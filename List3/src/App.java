import java.math.BigInteger;
import java.security.SecureRandom;

public class App {
    public static void main(String[] args) throws Exception {

        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.ZERO;
        BigInteger s = BigInteger.ZERO;
        int exp = 0;

        //Ensure that p is prime
        while (!(p.isProbablePrime(30))) {
            // Generate s > 2^99 (and s <= 2^200 for testing purposes)
            exp = random.nextInt(101) + 100;
            s = new BigInteger(exp, 20, random);
            // Check the last bit to ensure s is odd
            s = s.testBit(0) ? s : s.add(BigInteger.ONE);

            // Generate t >= 150 (and t <= 250 for testing purposes)
            exp = random.nextInt(101) + 150;

            // Calculate p as 2^t · s + 1
            p = (BigInteger.TWO).pow(exp).multiply(s).add(BigInteger.ONE);
        }

        // Generate test values
        int aBitLength = (p.bitLength() / 2);
        BigInteger a = new BigInteger(aBitLength, 0, random);
        BigInteger aSquared = a.modPow(BigInteger.TWO, p);

        // Test sqrt implementation
        ModSqrt solver = new ModSqrt(p, s, exp);
        BigInteger a1 = solver.sqrtModP(aSquared);

        String result = (a1.compareTo(a) == 0) ? "Result correct" : "Result incorrect";
        System.out.println(result);
        System.out.println("a_0: " + a.toString());
        System.out.println("a_1: " + a.toString());
    }
}
