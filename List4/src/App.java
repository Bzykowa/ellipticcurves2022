import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class App {

    private final static EllipticCurve ec40_a = new EllipticCurve(new BigInteger("819542760399"),
            new BigInteger("450925713588"),
            new BigInteger("928695305977"), new BigInteger("928696393343"),
            new AffinePoint(new BigInteger("712862527867"), new BigInteger("341290948763")));

    private final static EllipticCurve ec10_a = new EllipticCurve(new BigInteger("253"), new BigInteger("364"),
            new BigInteger("647"), new BigInteger("643"),
            new AffinePoint(new BigInteger("373"), new BigInteger("298")));

    private final static EllipticCurve ec60_a = new EllipticCurve(new BigInteger("44076867910191103"),
            new BigInteger("605586970250934463"), new BigInteger("1139907280243627543"),
            new BigInteger("1139907280255984973"),
            new AffinePoint(new BigInteger("245862712227340576"), new BigInteger("970269412551424561")));
    private final static EllipticCurve ec40_p = new EllipticCurve(new BigInteger("819542760399"),
            new BigInteger("450925713588"),
            new BigInteger("928695305977"), new BigInteger("928696393343"),
            new ProjectivePoint(new BigInteger("712862527867"), new BigInteger("341290948763"), BigInteger.ONE));

    private final static EllipticCurve ec10_p = new EllipticCurve(new BigInteger("253"), new BigInteger("364"),
            new BigInteger("647"), new BigInteger("643"),
            new ProjectivePoint(new BigInteger("373"), new BigInteger("298"), BigInteger.ONE));

    private final static EllipticCurve ec60_p = new EllipticCurve(new BigInteger("44076867910191103"),
            new BigInteger("605586970250934463"), new BigInteger("1139907280243627543"),
            new BigInteger("1139907280255984973"),
            new ProjectivePoint(new BigInteger("245862712227340576"), new BigInteger("970269412551424561"),
                    BigInteger.ONE));

    public static void main(String[] args) throws Exception {

        SecureRandom random = new SecureRandom();
        EllipticCurve test_a = null;
        EllipticCurve test_p = null;

        System.out.println("10/40/60");

        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        in.close();

        switch (input) {
            case "10": {
                test_a = ec10_a;
                test_p = ec10_p;
                break;
            }
            case "40": {
                test_a = ec40_a;
                test_p = ec40_p;
                break;
            }
            case "60": {
                test_a = ec60_a;
                test_p = ec60_p;
                break;
            }
            default: {
                test_a = ec10_a;
                test_p = ec10_p;
                input = "10";
            }
        }

        BigInteger s = BigInteger.valueOf(random.nextLong(test_a.getQ().longValue()));
        AffinePoint bigYA = (AffinePoint) test_a.scalarMultiply(s, test_a.getBasepoint());
        ProjectivePoint bigYP = (ProjectivePoint) test_p.scalarMultiply(s, test_p.getBasepoint());

        System.out.println(input + " bit s: " + s.toString());
        System.out.println("Y " + input + " bit (affine): " + bigYA.toString());
        System.out.println("Y " + input + " bit (projective): " + bigYP.toString());

        PollardRhoEC pA = new PollardRhoEC(test_a, bigYA);
        PollardRhoEC pP = new PollardRhoEC(test_p, bigYP);

        long startA = System.nanoTime();
        BigInteger ssA = pA.solveS();
        long endA = System.nanoTime();
        long startP = System.nanoTime();
        BigInteger ssP = pP.solveS();
        long endP = System.nanoTime();

        System.out.println("Calculated " + input + " bit s (affine): " + ssA.toString());
        System.out.println("Elapsed (affine): " + (endA - startA) + "ns");
        System.out.println("Calculated " + input + " bit s (projective): " + ssP.toString());
        System.out.println("Elapsed (projective): " + (endP - startP) + "ns");

    }
}
