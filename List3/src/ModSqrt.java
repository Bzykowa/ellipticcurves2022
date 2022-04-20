import java.math.BigInteger;

public class ModSqrt {

    // p = 2^t · s + 1
    private BigInteger p;
    // s > 2^99, odd number
    private BigInteger s;
    // t >= 150
    private int t;

    public ModSqrt(BigInteger p, BigInteger s, int t) {
        this.p = p;
        this.s = s;
        this.t = t;
    }

    public BigInteger sqrtModP(BigInteger a) {
        // TODO implement
        BigInteger result = BigInteger.ZERO;
        return result;
    }

}
