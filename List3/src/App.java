import java.math.BigInteger;
import java.security.SecureRandom;

public class App {

    private static BigInteger zero = BigInteger.ZERO;
    private static BigInteger one = BigInteger.ONE;
    private static BigInteger two = BigInteger.TWO;

    public static void main(String[] args) throws Exception {

        SecureRandom random = new SecureRandom();
        BigInteger p = zero;
        BigInteger s = zero;
        int exp = 0;

        // Ensure that p is prime
        while (!(p.isProbablePrime(30))) {
            // Generate s > 2^99 (and s <= 2^200 for testing purposes)
            exp = random.nextInt(99, 201);
            s = new BigInteger(exp, 20, random);
            // Check the last bit to ensure s is odd
            s = s.testBit(0) ? s : s.add(one);

            // Generate t >= 150 (and t <= 250 for testing purposes)
            exp = random.nextInt(150, 251);

            // Calculate p as 2^t · s + 1
            p = (two).pow(exp).multiply(s).add(one);
        }

        // Generate test values
        boolean isQR = false;
        int aLength = 0;
        BigInteger a = zero;
        BigInteger aSquared = zero;
        
        // Check if a^2 is 100% a quadratic residue
        // aSquared is QR when a^((p-1)/2) = 1 (mod p)
        while (!isQR) {
            aLength = random.nextInt(2, p.bitLength());
            a = new BigInteger(aLength, 0, random);
            aSquared = a.modPow(two, p);

            isQR = one.compareTo(aSquared.modPow(p.subtract(one).divide(two), p)) == 0 ? true : false;
        }

        // Test sqrt implementation
        ModSqrt solver = new ModSqrt(p, s, exp);
        BigInteger a1 = solver.sqrtModP(aSquared);

        // Result is correct when a1 or -a1 is equal to initial a
        String result = (a1.compareTo(a) == 0 || p.subtract(a1).compareTo(a) == 0) ? "Result correct"
                : "Result incorrect";
        System.out.println(result);
        System.out.println("a_0: " + a.toString());
        System.out.println("a_1: " + a1.toString());
        System.out.println("-a_1: " + p.subtract(a1).toString());
    }
}
