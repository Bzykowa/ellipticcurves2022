import java.util.ArrayList;

public class LimLee {
    private LimLeeParameters params;
    private EllipticCurve ec;
    private AffinePoint[][] precomputedPoints;

    public LimLee(LimLeeParameters params, EllipticCurve ec) {
        this.params = params;
        this.ec = ec;
        // size: v * 2^h
        precomputedPoints = new AffinePoint[params.v + 1][(1 << params.h) - 1];
    }

    public void precomputePoints() {
        // u -> [1, 2^h)
        // v -> [0, v)
        for (int u = 0; u < (1 << params.h) - 1; u++) {

            int a = params.a;
            int b = params.b;
            int v = params.v;

            // Last row of the exponent so use correct parameters.
            if (u >= (1 << (params.h - 1)) - 1) {
                a = params.aLast;
                b = params.bLast;
                v = params.vLast;
            }

            for (int j = 0; j < v; j++) {

                AffinePoint result = null;

                // At j = 0 calculate r_i's (g^2^ia)
                if (j == 0) {
                    // Initialize binary representation of u
                    String binaryU = Integer.toBinaryString(u + 1);

                    // Go through every bit of u
                    for (int i = 0; i < binaryU.length(); i++) {
                        // If bit at i is 1, then calculate g^(2^ia) = 2^ia * BP -> i*a doublings
                        if (binaryU.charAt(i) == '1') {
                            AffinePoint partialSum = (AffinePoint) ec.getBasepoint();
                            for (int ii = 0; ii < i * a; ii++) {
                                partialSum = (AffinePoint) ec.doublePoint(partialSum);
                            }
                            result = result == null ? partialSum : (AffinePoint) ec.addPoints(result, partialSum);
                        }
                    }
                    precomputedPoints[j][u] = result;

                }
                // Double the point at G[0][u] j*b times
                else {
                    result = precomputedPoints[0][u];
                    for (int ii = 0; ii < j * b; ii++) {
                        result = (AffinePoint) ec.doublePoint(result);
                    }

                }
            }
        }
    }

}
