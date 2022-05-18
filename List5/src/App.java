import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class App {

    private static final int S_100 = 100;
    private static final int S_5000 = 5000;
    // As defined in NIST Guide
    private static final EllipticCurve P521 = new EllipticCurve(BigInteger.valueOf(-3), new BigInteger(
            "051953eb9618e1c9a1f929a21a0b68540eea2da725b99b315f3b8b489918ef109e156193951ec7e937b1652c0bd3bb1bf073573df883d2c34f1ef451fd46b503f00",
            16),
            new BigInteger(
                    "6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057151"),
            new BigInteger(
                    "6864797660130609714981900799081393217269435300143305409394463459185543183397655394245057746333217197532963996371363321113864768612440380340372808892707005449"),
            new AffinePoint(new BigInteger(
                    "c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66",
                    16),
                    new BigInteger(
                            "11839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650",
                            16)));

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

        int l = 512;
        findOptimalParameters(l, S_100);

        System.out.println("For l = " + l + " optimal params are:");
        System.out.println("a = " + optimalParameters.a);
        System.out.println("b = " + optimalParameters.b);
        System.out.println("storage = " + optimalParameters.storage);
        //System.out.println("h = " + optimalParameters.h);
        //System.out.println("v = " + optimalParameters.v);

    }
}
