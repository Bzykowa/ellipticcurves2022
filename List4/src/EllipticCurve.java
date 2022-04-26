import java.math.BigInteger;

/**
 * Class representing an elliptic curve E_a,b(F_p) of order q with its basepoint
 */
public class EllipticCurve<Point> {

    private Point basepoint;
    private BigInteger a;
    private BigInteger b;
    private BigInteger p;
    private BigInteger q;
    private Point zeroAtInfinity;

    /**
     * Main constructor of class
     * 
     * @param a         Paramater a in equation y^2 = x^3 + a*x + b
     * @param b         Paramater b in equation y^2 = x^3 + a*x + b
     * @param p         Size of finite field
     * @param q         Order of the curve
     * @param basepoint Basepoint of the curve
     */
    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p, BigInteger q, Point basepoint) {
        this.setA(a);
        this.setB(b);
        this.setP(p);
        this.setQ(q);
        this.setBasepoint(basepoint);
        zeroAtInfinity = calculateZeroAtInfinity();
    }

    /**
     * Get order of the elliptic curve
     * 
     * @return Order of E_a,b(F_p)
     */
    public BigInteger getQ() {
        return q;
    }

    private void setQ(BigInteger q) {
        this.q = q;
    }

    /**
     * Return size of finite field F_p over which is the elliptic curve
     * 
     * @return p in F_p
     */
    public BigInteger getP() {
        return p;
    }

    private void setP(BigInteger p) {
        this.p = p;
    }

    /**
     * Get paramater b in equation y^2 = x^3 + a*x + b
     * 
     * @return b in equation y^2 = x^3 + a*x + b
     */
    public BigInteger getB() {
        return b;
    }

    private void setB(BigInteger b) {
        this.b = b;
    }

    /**
     * Get paramater a in equation y^2 = x^3 + a*x + b
     * 
     * @return a in equation y^2 = x^3 + a*x + b
     */
    public BigInteger getA() {
        return a;
    }

    private void setA(BigInteger a) {
        this.a = a;
    }

    /**
     * Get basepoint of the elliptic curve
     * 
     * @return Point basepoint
     */
    public Point getBasepoint() {
        return basepoint;
    }

    private void setBasepoint(Point basepoint) {
        this.basepoint = basepoint;
    }

    /**
     * Get the Point representing zero at infinity
     * 
     * @return Point zero at infinity
     */
    public Point getZeroAtInfinity() {
        return zeroAtInfinity;
    }

    /**
     * As there is no infinity in BigInteger let's generate point off the curve to
     * be a point of reference for zero at infinity.
     * 
     * @return Point to serve as zero at infinity
     */
    private Point calculateZeroAtInfinity() {

        AffinePoint o = new AffinePoint(BigInteger.ZERO, BigInteger.ZERO);

        // y^2 (mod p) = x^3 + ax + b (mod p)

        while (o.y.modPow(BigInteger.TWO, p).equals(o.x.pow(3).add(a.multiply(o.x)).add(b).mod(p))) {

            // o(x,y) = o(x,y+1)
            o.y = (o.y.add(BigInteger.ONE));
        }

        //If basepoint is affine then 0 at inf is also analogously for projective coordinates
        if (basepoint instanceof AffinePoint) {
            return (Point) o;

        } else if (basepoint instanceof ProjectivePoint) {
            return (Point) new ProjectivePoint(o.x, o.y, BigInteger.ONE)
        } else{
            return null;
        }

    }

    /**
     * Add two different points from this elliptic curve
     * 
     * @param Point pPoint
     * @param Point qPoint
     * @return pPoint + qPoint on elliptic curve
     */
    public Point addPoints(Point pPoint, Point qPoint) {

        if (pPoint instanceof AffinePoint) {

            AffinePoint pp = (AffinePoint) pPoint;
            AffinePoint qq = (AffinePoint) qPoint;
            // If any point is zero at infinity
            if (pp.equals((AffinePoint)zeroAtInfinity)) {
                return qPoint;
            } else if (qq.equals((AffinePoint)zeroAtInfinity)) {
                return pPoint;
            }

            // If p_x = q_x result is zero at infinity
            if (pp.x.equals(qq.x)) {
                if (pp.y.equals(qq.y)) {
                    return doublePoint(pPoint);
                } else {
                    return zeroAtInfinity;
                }
            }

            // (q_y - p_y)/(q_x - p_x)
            BigInteger alpha = (qq.y.subtract(pp.y))
                    .multiply((qq.x.subtract(pp.x)).modInverse(p)).mod(p);
            // x = alpha^2 - p_x - q_x (mod p)
            BigInteger x = (alpha.modPow(BigInteger.TWO, p)).subtract(pp.x).subtract(qq.x).mod(p);
            // y = -p_y + alpha * (p_x - x) (mod p)
            BigInteger y = (pp.x.negate()).add(alpha.multiply(pp.x.subtract(x))).mod(p);

            return (Point) new AffinePoint(x, y);
        }
    }

    /**
     * Add two different points from this elliptic curve
     * 
     * @param ProjectivePoint pPoint
     * @param ProjectivePoint qPoint
     * @return pPoint + qPoint on elliptic curve
     */
    public ProjectivePoint addPoints(ProjectivePoint pPoint, ProjectivePoint qPoint) {
        return pPoint;
    }

    /**
     * Double the point on this elliptic curve
     * 
     * @param AffinePoint pPoint
     * @return 2*pPoint which is the same as pPoint + pPoint on elliptic curve
     */
    public AffinePoint doublePoint(AffinePoint pPoint) {

        // p_y = 0 or p = zero at inf
        if (pPoint.y.equals(BigInteger.ZERO) || pPoint.equals(zeroAtInfinity)) {
            return zeroAtInfinity;
        }

        // (3*p_x^2 + a)/2*p_y
        BigInteger alpha = (((BigInteger.valueOf(3)).multiply(pPoint.x.modPow(BigInteger.TWO, p))).add(a))
                .multiply(((BigInteger.TWO).multiply(pPoint.y)).modInverse(p)).mod(p);

        // x = alpha^2 - 2*p_x (mod p)
        BigInteger x = alpha.modPow(BigInteger.TWO, p).subtract((BigInteger.TWO).multiply(pPoint.y)).mod(p);

        // y = -p_y + alpha * (p_x - x) (mod p)
        BigInteger y = (pPoint.y.negate()).add(alpha.multiply(pPoint.x.subtract(x))).mod(p);

        return new AffinePoint(x, y);
    }

    /**
     * Double the point on this elliptic curve
     * 
     * @param pPoint
     * @return 2*pPoint which is the same as pPoint + pPoint on elliptic curve
     */
    public ProjectivePoint doublePoint(ProjectivePoint pPoint) {
        return pPoint;
    }

    /**
     * Scalar multiplication of a point on elliptic curve
     * 
     * @param n
     * @param point
     * @return
     */
    public AffinePoint scalarMultiply(BigInteger n, AffinePoint point) {
        // Write scalar as binary number
        String nBinary = n.toString(2);
        // exponent of 2 at index 0
        int exp = nBinary.length() - 1;
        AffinePoint result = null;

        for (int i = 0; i < nBinary.length(); i++) {
            /*
             * If nBinary[i] == '1' double the point exp times (exp = nBinary.length()-1-i;
             * exponent for 2 at this place in binary representation) else do nothing
             */
            if (nBinary.charAt(i) == '1') {
                AffinePoint partialSum = point;
                for (int j = 0; j < exp; j++) {
                    partialSum = doublePoint(partialSum);
                }

                result = result == null ? partialSum : addPoints(result, partialSum);
            }

            exp = exp - 1;
        }

        return result == null ? zeroAtInfinity : result;
    }

}
