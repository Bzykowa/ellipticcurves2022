import java.math.BigInteger;
import java.security.SecureRandom;

public class ModSqrt {

    // p = 2^t · s + 1
    private BigInteger p;
    // s > 2^99, odd number
    private BigInteger s;
    // t >= 150
    private int t;

    // Helpers to shorten code lines
    private BigInteger zero = BigInteger.ZERO;
    private BigInteger one = BigInteger.ONE;
    private BigInteger two = BigInteger.TWO;
    private SecureRandom random = new SecureRandom();

    /**
     * Main class constructor 
     * @param p a prime number equal to 2^t · s + 1
     * @param s an odd number bigger than 2^99 for exercise purposes
     * @param t a number greater or equal to 150 for exercise purposes
     */
    public ModSqrt(BigInteger p, BigInteger s, int t) {
        this.p = p;
        this.s = s;
        this.t = t;
    }

    /**
     * A function calculating square root of a in mod p
     * @param a a BigInteger to calculate a root of
     * @return  a square root of a
     */
    public BigInteger sqrtModP(BigInteger a) {

        BigInteger aInv = a.modInverse(p);
        // r = a^((s+1)/2)
        BigInteger r = a.modPow(s.add(one).divide(two), p);

        // Generate n - a non-quadratic residue in F_p
        // n is a NQR when n^((p-1)/2) is equal to -1 (p-1) (mod p)
        boolean isNQR = false;
        BigInteger n = zero;
        while (!isNQR) {
            int nLength = random.nextInt(2, p.bitLength());
            n = new BigInteger(nLength, 1, random);
            BigInteger temp = n.modPow(p.subtract(one).divide(two), p);
            isNQR = temp.compareTo(p.subtract(one)) == 0 ? true
                    : false;
        }

        // b = n^s (mod p)
        BigInteger b = n.modPow(s, p);

        // We need to find j such that aInv * (b^j * r)^2 is congruent to 1 (mod p)

        // j0 = (aInv * r^2)^(2^(t-2)) (mod p) -> if 1 the n j0 = 0 else if -1 (p-1) j0
        // = 1
        BigInteger j = (aInv.multiply(r.modPow(two, p))).modPow(two.pow(t - 2), p).compareTo(one) == 0 ? zero : one;

        // Calculate subsequent bits of j
        for (int gamma = 1; gamma < t - 1; gamma++) {
            BigInteger nextJ = (aInv.multiply((b.modPow(j, p).multiply(r)).modPow(two, p)))
                    .modPow(two.pow(t - gamma - 2), p).compareTo(one) == 0 ? zero : two.pow(gamma).mod(p);
            j = j.add(nextJ);
        }

        return b.modPow(j, p).multiply(r).mod(p);
    }

}
