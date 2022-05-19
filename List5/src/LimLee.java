import java.math.BigInteger;
import java.util.Arrays;

public class LimLee {
    private LimLeeParameters params;
    private EllipticCurve ec;
    private Point[][] precomputedPoints;
    public int onlineSquares;
    public int onlineMults;

    /**
     * Construct an instance of Lim-Lee exponentiation algorithm on elliptic curves.
     * 
     * @param params Precomputed algorithm parameters
     * @param ec     Elliptic curve for the operations
     */
    public LimLee(LimLeeParameters params, EllipticCurve ec) {
        this.params = params;
        this.ec = ec;
        // size: v * 2^h, preload with zero at inf
        precomputedPoints = new AffinePoint[params.v + 1][(1 << params.h)];
        for (Point[] row : precomputedPoints) {
            Arrays.fill(row, ec.getZeroAtInfinity());
        }

    }

    /**
     * Precompute points for the exponentiation. The number of points is equal to
     * the storage parameter.
     * 
     * @throws Exception If there's an error in the EC operation and the point
     *                   generated is not on the curve.
     */
    public void precomputePoints() throws Exception {
        // u -> [1, 2^h)
        // v -> [0, v)
        for (int u = 1; u < (int) Math.pow(2.0, params.h); u++) {

            int a = params.a;
            int b = params.b;
            int v = params.v;

            // At j = 0 calculate r_i's (g^2^ia)
            // Initialize binary representation of u
            String binaryU = Integer.toBinaryString(u);
            binaryU = String.format("%" + (params.h) + "s", binaryU).replaceAll(" ", "0");
            Point result = null;

            // Go through every bit of u
            for (int i = 0; i < params.h; i++) {
                // If bit at i is 1, then calculate g^(2^ia) = 2^ia * BP -> i*a doublings
                // lsb
                if (binaryU.charAt(params.h - 1 - i) == '1') {
                    Point partialSum = ec.getBasepoint();
                    for (int ii = 0; ii < i * a; ii++) {
                        partialSum = ec.doublePoint(partialSum);
                    }
                    result = result == null ? partialSum : ec.addPoints(result, partialSum);
                }
            }
            precomputedPoints[0][u] = result;

            for (int j = 1; j < v; j++) {

                // Double the point at G[0][u] j*b times
                result = precomputedPoints[0][u];

                for (int ii = 0; ii < j * b; ii++) {
                    result = ec.doublePoint(result);
                }
                precomputedPoints[j][u] = result;

            }
        }
    }

    /**
     * Calculate Y = e * BP, where BP is a basepoint of the elliptic curve ec.
     * 
     * @param e Exponent / scalar
     * @return Point Y
     * @throws Exception If there's an error in the EC operation and the point
     *                   generated is not on the curve
     */
    public Point fastPow(BigInteger e) throws Exception {

        // Reset counters
        onlineSquares = onlineMults = 0;

        // lsb order
        String eBinary = new StringBuilder(e.toString(2)).reverse().toString();

        // Neutral element (internet said it's the zero at inf)
        Point r = ec.getZeroAtInfinity();

        for (int k = params.b - 1; k >= 0; k--) {

            r = ec.doublePoint(r);
            onlineSquares++;

            // Calculate I_jk
            for (int j = params.v - 1; j >= 0; j--) {
                int ijk = 0;

                for (int i = 0; i < params.h; i++) {

                    // Read correct bit from block e_ijk
                    int currentBit = i * params.a + j * params.b;

                    if (checkIfKExists(i, j, k)) {
                        currentBit += k;
                    } else
                        continue;

                    if (currentBit >= eBinary.length()) {
                        System.out.println("(i:" + i + ",j:" + j + ",k:" + k + ")");
                    }

                    if (eBinary.charAt(currentBit) == '1') {
                        ijk += (int) Math.pow(2.0, i);
                    }
                }

                r = ec.addPoints(r, precomputedPoints[j][ijk]);
                onlineMults++;

            }

        }

        return r;

    }

    /**
     * Check if a given bit k belongs to a block e_ij
     * 
     * @param i Index of a big block
     * @param j Index of a subblock
     * @param k Index of specific bit in that subblock
     * @return true if such bit exists; false if it doesn't
     */
    private boolean checkIfKExists(int i, int j, int k) {
        // length of a last blocks form i = 1 to h - 2
        int bLast0 = (params.a - (params.v - 1) * params.b);

        return (i < params.h - 1 && (j < params.v - 1 || (j == params.v - 1 && k <= bLast0 - 1)))
                || (i == params.h - 1 && (j < params.vLast - 1 || (j == params.vLast - 1 && k <= params.bLast - 1)));
    }

}
