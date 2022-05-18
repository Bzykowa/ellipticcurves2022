import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class App {

    private final static EllipticCurve ec10_a = new EllipticCurve(new BigInteger("253"), new BigInteger("364"),
            new BigInteger("647"), new BigInteger("643"),
            new AffinePoint(new BigInteger("373"), new BigInteger("298")));
    private final static EllipticCurve ec15_a = new EllipticCurve(new BigInteger("9030"),
            new BigInteger("11170"),
            new BigInteger("19423"), new BigInteger("19373"),
            new AffinePoint(new BigInteger("12411"), new BigInteger("15784")));
    private final static EllipticCurve ec20_a = new EllipticCurve(new BigInteger("360572"),
            new BigInteger("550596"),
            new BigInteger("568187"), new BigInteger("568363"),
            new AffinePoint(new BigInteger("281708"), new BigInteger("330842")));
    private final static EllipticCurve ec25_a = new EllipticCurve(new BigInteger("14311794"),
            new BigInteger("20151129"),
            new BigInteger("25069619"), new BigInteger("25073423"),
            new AffinePoint(new BigInteger("6708142"), new BigInteger("18333283")));
    private final static EllipticCurve ec30_a = new EllipticCurve(new BigInteger("25537879"),
            new BigInteger("251919295"),
            new BigInteger("701253127"), new BigInteger("701289311"),
            new AffinePoint(new BigInteger("436848809"), new BigInteger("675062066")));

    private final static EllipticCurve ec10_p = new EllipticCurve(new BigInteger("253"), new BigInteger("364"),
            new BigInteger("647"), new BigInteger("643"),
            new ProjectivePoint(new BigInteger("373"), new BigInteger("298"), BigInteger.ONE));

    private final static EllipticCurve ec15_p = new EllipticCurve(new BigInteger("9030"),
            new BigInteger("11170"),
            new BigInteger("19423"), new BigInteger("19373"),
            new ProjectivePoint(new BigInteger("12411"), new BigInteger("15784"), BigInteger.ONE));
    private final static EllipticCurve ec20_p = new EllipticCurve(new BigInteger("360572"),
            new BigInteger("550596"),
            new BigInteger("568187"), new BigInteger("568363"),
            new ProjectivePoint(new BigInteger("281708"), new BigInteger("330842"), BigInteger.ONE));
    private final static EllipticCurve ec25_p = new EllipticCurve(new BigInteger("14311794"),
            new BigInteger("20151129"),
            new BigInteger("25069619"), new BigInteger("25073423"),
            new ProjectivePoint(new BigInteger("6708142"), new BigInteger("18333283"), BigInteger.ONE));
    private final static EllipticCurve ec30_p = new EllipticCurve(new BigInteger("25537879"),
            new BigInteger("251919295"),
            new BigInteger("701253127"), new BigInteger("701289311"),
            new ProjectivePoint(new BigInteger("436848809"), new BigInteger("675062066"), BigInteger.ONE));

    public static void main(String[] args) throws Exception {

        SecureRandom random = new SecureRandom();
        EllipticCurve test_a = null;
        EllipticCurve test_p = null;

        System.out.println("10/15/20/25/30");

        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        in.close();

        switch (input) {
            case "10": {
                test_a = ec10_a;
                test_p = ec10_p;
                break;
            }
            case "15": {
                test_a = ec15_a;
                test_p = ec15_p;
                break;
            }
            case "20": {
                test_a = ec20_a;
                test_p = ec20_p;
                break;
            }
            case "25": {
                test_a = ec25_a;
                test_p = ec25_p;
                break;
            }
            case "30": {
                test_a = ec30_a;
                test_p = ec30_p;
                break;
            }
            default: {
                test_a = ec25_a;
                test_p = ec25_p;
                input = "test";
            }
        }

        if (input.equals("test")) {

            int correctAnswers = 0;
            for (int i = 0; i < 100; i++) {
                BigInteger s = BigInteger.valueOf(random.nextLong(test_a.getQ().longValue()));
                AffinePoint bigYA = (AffinePoint) test_a.scalarMultiply(s, test_a.getBasepoint());
                ProjectivePoint bigYP = (ProjectivePoint) test_p.scalarMultiply(s, test_p.getBasepoint());
                AffinePoint bigYPConverted = test_p.toAffine(bigYP);

                System.out.println("Affine: " + bigYA.toString() + "; Projective: " + bigYP.toString()
                        + "; Converted to affine: " + bigYPConverted.toString());

                if (bigYA.equals(bigYPConverted)) {
                    correctAnswers++;
                }
            }
            System.out.println("Tests passed: " + correctAnswers);
            System.out.println("Tests failed: " + (100 - correctAnswers));

        } else {
            BigInteger s = BigInteger.valueOf(random.nextLong(test_a.getQ().longValue()));
            AffinePoint bigYA = (AffinePoint) test_a.scalarMultiply(s, test_a.getBasepoint());
            ProjectivePoint bigYP = (ProjectivePoint) test_p.scalarMultiply(s, test_p.getBasepoint());

            System.out.println(input + " bit s: " + s.toString());
            System.out.println("Y " + input + " bit (affine): " + bigYA.toString());
            System.out.println("Y " + input + " bit (projective): " + bigYP.toString());

            PollardRhoEC pA = new PollardRhoEC(test_a, bigYA);
            PollardRhoEC pP = new PollardRhoEC(test_p, bigYP);

            long startA = System.currentTimeMillis();
            BigInteger ssA = pA.solveS();
            long endA = System.currentTimeMillis();
            long startP = System.currentTimeMillis();
            BigInteger ssP = pP.solveS();
            long endP = System.currentTimeMillis();

            System.out.println("Calculated " + input + " bit s (affine): " + ssA.toString());
            System.out.println("Calculated " + input + " bit s (projective): " + ssP.toString());
            System.out.println("Elapsed (affine): " + (endA - startA) + "ms");
            System.out.println("Elapsed (projective): " + (endP - startP) + "ms");
        }

    }
}
