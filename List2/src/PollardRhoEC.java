import java.math.BigInteger;

/**
 * Class implementing Pollard-rho method to solve ECDLP, Y = s * P (find the s),
 * on elliptic curve E_a,b(F_p) represented as y^2 = x^3 + a*x + b over finite
 * field F_p and of order q.
 */
public class PollardRhoEC {
    private EllipticCurve curve;
    private ECPoint bigY;

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

        if (r.getX().compareTo(partitionPoint) == -1) {
            return 0;
        } else if (r.getX().compareTo(partitionPoint.multiply(BigInteger.TWO)) == -1) {
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
                // P + R_i
                r1 = curve.addPoints(curve.getBasepoint(), i.getR());
                // a_i + 1 (mod q)
                a1 = i.getA().add(BigInteger.ONE).mod(curve.getQ());
                // b_i (mod q)
                b1 = i.getB().mod(curve.getQ());
                break;
            }
            case 1: {
                // 2 * R_i
                r1 = curve.doublePoint(i.getR());
                // 2 * a_i (mod q)
                a1 = BigInteger.TWO.multiply(i.getA()).mod(curve.getQ());
                // 2 * b_i (mod q)
                b1 = BigInteger.TWO.multiply(i.getB()).mod(curve.getQ());
                break;
            }
            case 2: {
                // Y + R_i
                r1 = curve.addPoints(bigY, i.getR());
                // a_i (mod q)
                a1 = i.getA().mod(curve.getQ());
                // b_i + 1 (mod q)
                b1 = i.getB().add(BigInteger.ONE).mod(curve.getQ());
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

}
