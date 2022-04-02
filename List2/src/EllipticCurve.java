import java.math.BigInteger;

/**
 * Class representing an elliptic curve E_a,b(F_p) of order q with its basepoint
 */
public class EllipticCurve {

    private ECPoint basepoint;
    private BigInteger a;
    private BigInteger b;
    private BigInteger p;
    private BigInteger q;
    private ECPoint zeroAtInfinity;

    /**
     * Main constructor of class
     * 
     * @param a         Paramater a in equation y^2 = x^3 + a*x + b
     * @param b         Paramater b in equation y^2 = x^3 + a*x + b
     * @param p         Size of finite field
     * @param q         Order of the curve
     * @param basepoint Basepoint of the curve
     */
    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p, BigInteger q, ECPoint basepoint) {
        this.setA(a);
        this.setB(b);
        this.setP(p);
        this.setQ(q);
        this.setBasepoint(basepoint);
        zeroAtInfinity = calculateZeroAtInfinity();
    }

    public BigInteger getQ() {
        return q;
    }

    private void setQ(BigInteger q) {
        this.q = q;
    }

    public BigInteger getP() {
        return p;
    }

    private void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getB() {
        return b;
    }

    private void setB(BigInteger b) {
        this.b = b;
    }

    public BigInteger getA() {
        return a;
    }

    private void setA(BigInteger a) {
        this.a = a;
    }

    public ECPoint getBasepoint() {
        return basepoint;
    }

    private void setBasepoint(ECPoint basepoint) {
        this.basepoint = basepoint;
    }

    public ECPoint getZeroAtInfinity() {
        return zeroAtInfinity;
    }

    /**
     * As there is no infinity in BigInteger let's generate point of the curve to be
     * a point of reference for zero at infinity.
     * 
     * @return point off the curve to serve as zero at infinity
     */
    private ECPoint calculateZeroAtInfinity() {

        ECPoint o = new ECPoint(BigInteger.ZERO, BigInteger.ZERO);

        // y^2 (mod p) = x^3 + ax + b (mod p)
        while (o.getY().modPow(BigInteger.TWO, p).equals(o.getX().pow(3).add(a.multiply(o.getX())).add(b).mod(p))) {

            // o(x,y) = o(x,y+1)
            o.setY(o.getY().add(BigInteger.ONE));
        }

        return o;
    }

    /**
     * Add two different points from this elliptic curve
     * 
     * @param pPoint
     * @param qPoint
     * @return pPoint + qPoint on elliptic curve
     */
    public ECPoint addPoints(ECPoint pPoint, ECPoint qPoint) {

        // If any point is zero at infinity
        if (pPoint.equals(zeroAtInfinity)) {
            return qPoint;
        } else if (qPoint.equals(zeroAtInfinity)) {
            return pPoint;
        }

        // If p_x = q_x result is zero at infinity
        if (pPoint.getX().equals(qPoint.getX())) {
            return zeroAtInfinity;
        }

        //(q_y-p_y)/(q_x-p_x)
        BigInteger alpha = (qPoint.getY().subtract(pPoint.getY())).divide(qPoint.getX().subtract(pPoint.getX()));
        // x = alpha^2 - p_x - q_x (mod p)
        BigInteger x = alpha.pow(2).subtract(pPoint.getX()).subtract(qPoint.getX()).mod(p);
        // y = -p_y + alpha * (p_x - x) (mod p)
        BigInteger y = pPoint.getY().negate().add(alpha.multiply(pPoint.getX().subtract(x))).mod(p);
        
        return new ECPoint(x, y);
    }

    /**
     * Double the point on this elliptic curve
     * 
     * @param pPoint
     * @return
     */
    public ECPoint doublePoint(ECPoint pPoint) {
        // TODO implement doubling
        return pPoint;
    }

}
