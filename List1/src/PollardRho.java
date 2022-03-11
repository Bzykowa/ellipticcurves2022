import java.math.BigInteger;

/**
 * Class implementing Pollard-Rho algorithm for discrete logarithm. It computes x such that g^x = y mod p
 */
public class PollardRho {

    private BigInteger p;
    private BigInteger g;
    private BigInteger y;
    private BigInteger q;

    /**
     * Main contructor
     * @param p Safe prime that p = 2q + 1, where p,q are primes;
     * @param g Generator of Fp*
     * @param y g^x mod p
     */
    public PollardRho(BigInteger p, BigInteger g, BigInteger y) {
        this.p = p;
        this.g = g;
        this.y = y;
        q = (p.subtract(new BigInteger("1"))).divide(new BigInteger("2"));   //ord(g) = q = (p - 1) / 2 
    }

    /**
     * Step function in Pollard-Rho algorithm
     * @param i a three-element tuple (x,a,b) where x = (g^a)*(g^b) mod p
     * @return
     */
    private PollardTuple f(PollardTuple i) {

        int s = i.getX().mod(BigInteger.valueOf(3)).intValue();
        BigInteger x1;
        BigInteger a1;
        BigInteger b1;

        switch (s) {
            case 0: {
                x1 = (i.getX().multiply(y)).mod(p);
                a1 = i.getA();
                b1 = (i.getB().add(new BigInteger("1"))).mod(q);
                break;
            }
            case 1: {
                x1 = (i.getX().multiply(g)).mod(p);
                a1 = (i.getA().add(new BigInteger("1"))).mod(q);
                b1 = i.getB();
                break;
            }
            case 2: {
                x1 = (i.getX().pow(2)).mod(p);
                a1 = (i.getA().multiply(new BigInteger("2"))).mod(q);
                b1 = (i.getB().multiply(new BigInteger("2"))).mod(q);
                break;
            }
            default: {
                x1 = new BigInteger("0");
                a1 = new BigInteger("0");
                b1 = new BigInteger("0");
                break;
            }
        }

        return new PollardTuple(x1,a1,b1);
    }
}
