import java.math.BigInteger;

/**
 * Class representing an elliptic curve E_a,b(F_p) of order q with its basepoint
 */
public class EllipticCurve {

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
        if (basepoint instanceof AffinePoint) {
            return o;

        } else if (basepoint instanceof ProjectivePoint) {
            return new ProjectivePoint(BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO);
        } else {
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
            if (pp.equals((AffinePoint) zeroAtInfinity)) {
                return qPoint;
            } else if (qq.equals((AffinePoint) zeroAtInfinity)) {
                return pPoint;
            }

            // If p_x = q_x result is zero at infinity or if p = q do doubling
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
            BigInteger y = (pp.y.negate()).add(alpha.multiply(pp.x.subtract(x))).mod(p);

            return new AffinePoint(x, y);
        } else if (pPoint instanceof ProjectivePoint) {

            ProjectivePoint pp = (ProjectivePoint) pPoint;
            ProjectivePoint qq = (ProjectivePoint) qPoint;

            // If any point is zero at infinity
            if (pp.equals((ProjectivePoint) zeroAtInfinity)) {
                return qPoint;
            } else if (qq.equals((ProjectivePoint) zeroAtInfinity)) {
                return pPoint;
            }

            // If p_x = q_x result is zero at infinity or if p = q do doubling
            if (pp.x.equals(qq.x)) {
                if (pp.equals(qq)) {
                    return doublePoint(pPoint);
                } else {
                    return zeroAtInfinity;
                }
            }

            // W = Z_1Z_2
            BigInteger W = pp.z.multiply(qq.z).mod(p);
            // U1 = X_1Z_2
            BigInteger U1 = pp.x.multiply(qq.z).mod(p);
            // U2 = X_2Z_1
            BigInteger U2 = qq.x.multiply(pp.z).mod(p);
            // S1 = Y_1Z_2
            BigInteger S1 = pp.y.multiply(qq.z).mod(p);
            // S2 = Y_2Z_1
            BigInteger S2 = qq.y.multiply(pp.z).mod(p);
            // P = U_2 - U_1
            BigInteger P = U2.subtract(U1).mod(p);
            // R = S_2 - S_1
            BigInteger R = S2.subtract(S1).mod(p);
            // x3 = P(WR^2 - (U_1 + U_2)P^2)
            BigInteger x3 = P.multiply(
                    W.multiply(R.pow(2)).subtract(U1.add(U2).multiply(P.pow(2)))).mod(p);
            // y3 = [R(-2WR^2 + 3(U_1 + U_2)P^2) - (S_1 + S_2)P^3]/2
            BigInteger y3 = ((R.multiply(BigInteger.valueOf(-2).multiply(W).multiply(R.pow(2))
                    .add(BigInteger.valueOf(3).multiply(U1.add(U2)).multiply(P.pow(2)))))
                    .subtract(S1.add(S2).multiply(P.pow(3)))).multiply(BigInteger.TWO.modInverse(p)).mod(p);
            // z3 = (P^3)W
            BigInteger z3 = P.pow(2).multiply(W).mod(p);

            return new ProjectivePoint(x3, y3, z3);
        } else {
            return zeroAtInfinity;
        }
    }

    /**
     * Double the point on this elliptic curve
     * 
     * @param Point pPoint
     * @return 2*pPoint which is the same as pPoint + pPoint on elliptic curve
     */
    public Point doublePoint(Point pPoint) {

        if (pPoint instanceof AffinePoint) {

            AffinePoint pp = (AffinePoint) pPoint;
            // p_y = 0 or p = zero at inf
            if (pp.y.equals(BigInteger.ZERO) || pp.equals((AffinePoint) zeroAtInfinity)) {
                return zeroAtInfinity;
            }

            // (3*p_x^2 + a)/2*p_y
            BigInteger alpha = (((BigInteger.valueOf(3)).multiply(pp.x.modPow(BigInteger.TWO, p))).add(a))
                    .multiply(((BigInteger.TWO).multiply(pp.y)).modInverse(p)).mod(p);

            // x = alpha^2 - 2*p_x (mod p)
            BigInteger x = alpha.modPow(BigInteger.TWO, p).subtract((BigInteger.TWO).multiply(pp.x)).mod(p);

            // y = -p_y + alpha * (p_x - x) (mod p)
            BigInteger y = (pp.y.negate()).add(alpha.multiply(pp.x.subtract(x))).mod(p);

            return new AffinePoint(x, y);
        } else if (pPoint instanceof ProjectivePoint) {

            ProjectivePoint pp = (ProjectivePoint) pPoint;
            // p_y = 0 or p = zero at inf
            if (pp.y.equals(BigInteger.ZERO) || pp.equals((ProjectivePoint) zeroAtInfinity)) {
                return zeroAtInfinity;
            }

            // W = 3(X_1)^2 + a(Z_1)^2
            BigInteger W = BigInteger.valueOf(3).multiply(pp.x.modPow(BigInteger.TWO, p))
                    .add(a.multiply(pp.z).modPow(BigInteger.TWO, p)).mod(p);
            // S = 2Y_1Z_1
            BigInteger S = BigInteger.TWO.multiply(pp.y).multiply(pp.z).mod(p);
            // B = 2SX_1Y_1
            BigInteger B = BigInteger.TWO.multiply(pp.x).multiply(pp.y).mod(p);
            // h = W^2 - 2B
            BigInteger h = W.modPow(BigInteger.TWO, p).subtract(BigInteger.TWO.multiply(B)).mod(p);
            // x3 = hS
            BigInteger x3 = h.multiply(S).mod(p);
            // y3 = W(B - h) - 2(SY_1)^2
            BigInteger y3 = W.multiply(B.subtract(h))
                    .subtract(BigInteger.TWO.multiply(S.multiply(pp.y).modPow(BigInteger.TWO, p))).mod(p);
            // z3 = S^3
            BigInteger z3 = S.modPow(BigInteger.valueOf(3), p);

            return new ProjectivePoint(x3, y3, z3);
        } else {
            return zeroAtInfinity;
        }
    }

    /**
     * Scalar multiplication of a point on elliptic curve
     * 
     * @param n
     * @param point
     * @return point times n
     */
    public Point scalarMultiply(BigInteger n, Point point) {
        // Write scalar as binary number
        String nBinary = n.toString(2);
        // exponent of 2 at index 0
        int exp = nBinary.length() - 1;
        Point result = null;

        for (int i = 0; i < nBinary.length(); i++) {
            /*
             * If nBinary[i] == '1' double the point exp times (exp = nBinary.length()-1-i;
             * exponent for 2 at this place in binary representation) else do nothing
             */
            if (nBinary.charAt(i) == '1') {
                Point partialSum = point;
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
