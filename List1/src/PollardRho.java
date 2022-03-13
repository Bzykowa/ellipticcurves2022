import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Class implementing Pollard-Rho algorithm for discrete logarithm. 
 */
public class PollardRho {

    private BigInteger p;
    private BigInteger g;
    private BigInteger y;
    private BigInteger q;

    private final BigInteger ZERO = BigInteger.valueOf(0);
    private final BigInteger ONE = BigInteger.valueOf(1);
    private final BigInteger TWO = BigInteger.valueOf(2);

    private int i = 0; // Race loop counter
    private SecureRandom random = new SecureRandom();

    /**
     * Main contructor
     * 
     * @param p Safe prime that p = 2p' + 1, where p,p' are primes;
     * @param g Generator of <g> where g = g'^2 mod p = 1
     * @param y g^x mod p
     */
    public PollardRho(BigInteger p, BigInteger g, BigInteger y) {
        this.p = p;
        this.g = g;
        this.y = y;
        q = (p.subtract(ONE)).divide(TWO); // ord(g) = q = (p - 1) / 2
    }

    /**
     * Step function in Pollard-Rho algorithm
     * 
     * @param i a three-element tuple (x,a,b) where x = (g^a)*(g^b) mod p
     * @return PollardTuple (x1,a1,b1)
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
                b1 = (i.getB().add(ONE)).mod(q);
                break;
            }
            case 1: {
                x1 = (i.getX().multiply(g)).mod(p);
                a1 = (i.getA().add(ONE)).mod(q);
                b1 = i.getB();
                break;
            }
            case 2: {
                x1 = i.getX().modPow(TWO,p);
                a1 = (i.getA().multiply(TWO)).mod(q);
                b1 = (i.getB().multiply(TWO)).mod(q);
                break;
            }
            default: {
                x1 = ZERO;
                a1 = ZERO;
                b1 = ZERO;
                break;
            }
        }

        return new PollardTuple(x1, a1, b1);
    }

    /**
     * Loops algorithm steps until t.x is equal to h.x. Hare is moving twice as fast
     * as tortoise.
     * 
     * @param t Tortoise tuple
     * @param h Hare tuple
     * @return Array containing two tuples after processing them with function f
     */
    private PollardTuple[] race(PollardTuple t, PollardTuple h) {
        do {
            i += 1;
            t = f(t);
            h = f(f(h));

        } while (!(t.getX().mod(p).compareTo(h.getX().mod(p)) == 0));

        PollardTuple[] result = { t, h };
        return result;
    }

    /**
     * Function that checks whether it is possible to calculate final x from
     * previously evaluated tortoise and hare tuples
     * 
     * @param t tortoise tuple
     * @param h hare tuple
     * @return True if t.b != h.b (mod q) False otherwise
     */
    private boolean verifyRaceResult(PollardTuple t, PollardTuple h) {
        return !((t.getB().mod(q)).compareTo(h.getB().mod(q)) == 0);
    }

    /**
     * Function calculating x in g^x = y (mod p)
     * @return x
     */
    public BigInteger solveX() {

        i = 0;
        PollardTuple tortoise = new PollardTuple(ONE, ZERO, ZERO);
        PollardTuple hare = new PollardTuple(ONE, ZERO, ZERO);

        PollardTuple[] raceResults = race(tortoise, hare);

        while (!verifyRaceResult(raceResults[0], raceResults[1])) {
            BigInteger a = BigInteger.valueOf(random.nextLong(q.longValue()));
            BigInteger b = BigInteger.valueOf(random.nextLong(q.longValue()));
            BigInteger x = (g.modPow(a, p)).multiply(y.modPow(b, p));
            tortoise = new PollardTuple(x, a, b);
            hare = new PollardTuple(x, a, b);

            raceResults = race(tortoise, hare);
        }

        //t.a - h.a
        BigInteger a1 = raceResults[0].getA().subtract(raceResults[1].getA());
        //h.b - t.b
        BigInteger b1 = raceResults[1].getB().subtract(raceResults[0].getB());

        System.out.println("The computation took " + i + " loops");

        return a1.multiply(b1.modInverse(q)).mod(q);
    }
}
