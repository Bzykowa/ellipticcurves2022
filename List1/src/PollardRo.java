import java.math.BigInteger;

public class PollardRo {

    private BigInteger p;
    BigInteger g;
    BigInteger y;

    private PollardTuple f(PollardTuple i) {

        int s = i.getX().mod(BigInteger.valueOf(3)).intValue();
        BigInteger x1;
        long a1;
        long b1;

        switch (s) {
            case 0: {
                x1 = i.getX().multiply(y).mod(p);
                break;
            }
            case 1: {
                x1 = i.getX().multiply(g).mod(p);
                break;
            }
            case 2: {
                x1 = i.getX().pow(2).mod(p);
                break;
            }
        }

        return i;
    }
}
