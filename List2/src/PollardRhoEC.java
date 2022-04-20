import java.math.BigInteger;

/**
 * Class implementing Pollard-rho method to solve ECDLP, Y = s * P (find the s),
 * on elliptic curve E_a,b(F_p) represented as y^2 = x^3 + a*x + b over finite
 * field F_p and of order q.
 */
public class PollardRhoEC {
    private EllipticCurve curve;
    private ECPoint bigY;
    private int i = 0; // iterations of algorithm counter

    /**
     * Main constructor of the class
     * 
     * @param curve Elliptic curve used in calculations
     * @param bigY  Y to solve ECDLP on
     */
    public PollardRhoEC(EllipticCurve curve, ECPoint bigY) {
        this.curve = curve;
        this.bigY = bigY;
    }

    /**
     * Function which partitions points of elliptic curve into 3 sets
     * 
     * @param r
     * @return 0 if in S_1, 1 if in S_2, 2 if in S_3
     */
    private int partition(ECPoint r) {

        if (r.equals(curve.getZeroAtInfinity())) {
            return 0;
        }

        BigInteger partitionPoint = curve.getQ().divide(BigInteger.valueOf(3));

        if (r.getY().compareTo(partitionPoint) == -1) {
            return 0;
        } else if (r.getY().compareTo(partitionPoint.multiply(BigInteger.TWO)) == -1) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * A step function in Pollard-rho method
     *
     * @param i PollardTupleEC (r, a, b) where r = a*P + b*Y
     * @return PollardTupleEC (r1, a1, b1)
     */
    private PollardTupleEC f(PollardTupleEC i) {
        ECPoint r1;
        BigInteger a1, b1;

        switch (partition(i.getR())) {
            case 0: {
                // Y + R_i
                r1 = curve.addPoints(bigY, i.getR());
                // a_i 
                a1 = i.getA();
                // b_i + 1(mod q)
                b1 = i.getB().add(BigInteger.ONE).mod(curve.getQ());
                break;
            }
            case 1: {
                // 2 * R_i
                r1 = curve.doublePoint(i.getR());
                // 2 * a_i (mod q)
                a1 = (BigInteger.TWO).multiply(i.getA()).mod(curve.getQ());
                // 2 * b_i (mod q)
                b1 = (BigInteger.TWO).multiply(i.getB()).mod(curve.getQ());
                break;
            }
            case 2: {
                // P + R_i
                r1 = curve.addPoints(curve.getBasepoint(), i.getR());
                // a_i + 1 (mod q)
                a1 = i.getA().add(BigInteger.ONE).mod(curve.getQ());
                // b_i 
                b1 = i.getB();
                break;
            }
            default: {
                r1 = curve.getZeroAtInfinity();
                a1 = BigInteger.ZERO;
                b1 = BigInteger.ZERO;
                break;
            }
        }

        return new PollardTupleEC(r1, a1, b1);
    }

    /**
     * Function solving ECDLP -> Y = s * P (find the s)
     * 
     * @return s, -1 if was unable to solve
     */
    public BigInteger solveS() {

        BigInteger a0 = BigInteger.ONE;
        BigInteger b0 = BigInteger.ZERO;
        i = 0;

        PollardTupleEC t = new PollardTupleEC(curve.getBasepoint(), a0, b0);
        PollardTupleEC h = new PollardTupleEC(curve.getBasepoint(), a0, b0);

        // race till collision
        do {
            i += 1;
            t = f(t);
            h = f(f(h));
        } while (!t.getR().equals(h.getR()));

        //System.out.println(t.toString());
        //System.out.println(h.toString());
        System.out.println("Algorithm took " + i + " iterations.");

        // if gcd(q, h_b - t_b) return (h_a - t_a)/(t_b - h_b) mod q else error so return -1
        return curve.getQ().gcd(h.getB().subtract(t.getB())).equals(BigInteger.ONE)
                ? (h.getA().subtract(t.getA())).multiply((t.getB().subtract(h.getB())).modInverse(curve.getQ())).mod(curve.getQ())
                : BigInteger.valueOf(-1);
    }

}
