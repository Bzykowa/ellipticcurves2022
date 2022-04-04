import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class App {

    private final static EllipticCurve ec40 = new EllipticCurve(new BigInteger("819542760399"),
            new BigInteger("450925713588"),
            new BigInteger("928695305977"), new BigInteger("928696393343"),
            new ECPoint(new BigInteger("712862527867"), new BigInteger("341290948763")));

    private final static EllipticCurve ec10 = new EllipticCurve(new BigInteger("253"), new BigInteger("364"),
            new BigInteger("647"), new BigInteger("643"),
            new ECPoint(new BigInteger("373"), new BigInteger("298")));

    private final static EllipticCurve ec60 = new EllipticCurve(new BigInteger("44076867910191103"),
            new BigInteger("605586970250934463"), new BigInteger("1139907280243627543"),
            new BigInteger("1139907280255984973"),
            new ECPoint(new BigInteger("245862712227340576"), new BigInteger("970269412551424561")));

    public static void main(String[] args) throws Exception {

        SecureRandom random = new SecureRandom();
        EllipticCurve test = null;

        System.out.println("10/40/60");

        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        in.close();

        switch (input) {
            case "10": {
                test = ec10;
                break;
            }
            case "40": {
                test = ec40;
                break;
            }
            case "60": {
                test = ec60;
                break;
            }
            default: {
                test = ec10;
                input = "10";
            }
        }

        BigInteger s = BigInteger.valueOf(random.nextLong(test.getQ().longValue()));
        ECPoint bigY = test.scalarMultiply(s, test.getBasepoint());
        System.out.println(input + " bit s: " + s.toString());
        System.out.println("Y " + input + " bit: " + bigY.toString());
        PollardRhoEC p = new PollardRhoEC(test, bigY);
        BigInteger ss = p.solveS();
        System.out.println("Calculated " + input + " bit s: " + ss.toString());

    }
}
