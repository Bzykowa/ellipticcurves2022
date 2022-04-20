import java.math.BigInteger;
import java.security.SecureRandom;

public class App {
    public static void main(String[] args) throws Exception {

        SecureRandom random = new SecureRandom();

        // Generate s > 2^99 (and s <= 2^200 for testing purposes)
        int exp = random.nextInt(101) + 100;
        BigInteger s = new BigInteger(exp, 20, random);
        // Check the last bit to ensure s is odd
        s = s.testBit(s.bitLength() - 1) ? s : s.add(BigInteger.ONE);

        // Generate t >= 150 (and t <= 250 for testing purposes)
        exp = random.nextInt(101) + 150;

        // Calculate p as 2^t · s + 1
        BigInteger p = (BigInteger.TWO).pow(exp).multiply(s).add(BigInteger.ONE);

        // Generate test values
        int aBitLength = (p.bitLength() / 2);
        BigInteger a = new BigInteger(aBitLength, -1, random);
        BigInteger aSquared = a.modPow(BigInteger.TWO, p);

        // Test sqrt implementation
        ModSqrt solver = new ModSqrt(p);
        BigInteger a1 = solver.sqrtModP(aSquared);

        String result = (a1.compareTo(a) == 0) ? "Result correct" : "Result incorrect";
        System.out.println(result);
        System.out.println("a_0: " + a.toString());
        System.out.println("a_1: " + a.toString());
    }
}
