import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class App {

    private static final int S_100 = 100;
    private static final int S_5000 = 5000;
    // As defined in NIST Guide
    private static final BigInteger p = new BigInteger(
            "6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057151");
    private static final EllipticCurve P521 = new EllipticCurve(BigInteger.valueOf(-3).mod(p), new BigInteger(
            "1093849038073734274511112390766805569936207598951683748994586394495953116150735016013708737573759623248592132296706313309438452531591012912142327488478985984"),
            p,
            new BigInteger(
                    "6864797660130609714981900799081393217269435300143305409394463459185543183397655394245057746333217197532963996371363321113864768612440380340372808892707005449"),
            new AffinePoint(new BigInteger(
                    "2661740802050217063228768716723360960729859168756973147706671368418802944996427808491545080627771902352094241225065558662157113545570916814161637315895999846"),
                    new BigInteger(
                            "3757180025770020463545507224491183603594455134769762486694567779615544477440556316691234405012945539562144444537289428522585666729196580810124344277578376784")));

    private static LimLeeParameters optimalParameters;

    /**
     * Calculate optimal a,b for the Lim-Lee based on length of the exponent and the
     * storage bound.
     * 
     * @param l Bit length of the exponent
     * @param S Storage bound
     */
    private static void findOptimalParameters(int l, int S) {

        ArrayList<LimLeeParameters> possibleParams = new ArrayList<LimLeeParameters>();

        // Generate possible parameters and skip those exceeding the storage limit
        for (int a = 1; a <= l; a++) {
            for (int b = 1; b <= a; b++) {
                LimLeeParameters test = new LimLeeParameters(a, b, l);
                if (test.storage.compareTo(BigInteger.valueOf(S)) <= 0) {
                    possibleParams.add(test);
                }
            }
        }
        // Sort the array based on operations count
        Collections.sort(possibleParams, new Comparator<LimLeeParameters>() {
            public int compare(LimLeeParameters p1, LimLeeParameters p2) {
                BigInteger ops1 = p1.squarings.add(p1.multiplications);
                BigInteger ops2 = p2.squarings.add(p2.multiplications);

                return ops1.compareTo(ops2);
            }
        });

        optimalParameters = possibleParams.get(0);

    }

    public static void main(String[] args) throws Exception {

        // Magic number as a test value
        int l = 512;
        BigInteger e = new BigInteger(l, 1, new SecureRandom());
        findOptimalParameters(l, S_5000);

        // Print optimal parameters that will be used in the algorithm test run
        System.out.println("For l = " + l + " optimal params are:");
        System.out.println("a = " + optimalParameters.a);
        System.out.println("b = " + optimalParameters.b);
        System.out.println("storage = " + optimalParameters.storage);
        System.out.println("h = " + optimalParameters.h);
        System.out.println("v = " + optimalParameters.v);
        System.out.println("aLast = " + optimalParameters.aLast);
        System.out.println("bLast = " + optimalParameters.bLast);
        System.out.println("vLast = " + optimalParameters.vLast);

        // Test on a NIST Curve
        LimLee limlee = new LimLee(optimalParameters, P521);
        limlee.precomputePoints();
        AffinePoint Y = (AffinePoint) limlee.fastPow(e);
        AffinePoint testY = (AffinePoint) P521.scalarMultiply(e, P521.getBasepoint());

        System.out.println("Lim-Lee Y = " + Y.toString());
        System.out.println("Scalar  Y = " + testY.toString());

        // Print online operation costs
        System.out.println("Online squares = " + limlee.onlineSquares);
        System.out.println("Online multiplications = " + limlee.onlineMults);

    }
}
