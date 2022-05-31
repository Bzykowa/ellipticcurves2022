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

    // Helpers to shorten code lines
    private final BigInteger zero = BigInteger.ZERO;
    private final BigInteger one = BigInteger.ONE;
    private final BigInteger two = BigInteger.TWO;
    private final BigInteger three = BigInteger.valueOf(3);
    private final BigInteger four = BigInteger.valueOf(4);
    private final BigInteger eight = BigInteger.valueOf(8);

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

        AffinePoint o = new AffinePoint(zero, zero);

        // y^2 (mod p) = x^3 + ax + b (mod p)

        while (o.y.modPow(two, p).equals(o.x.pow(3).add(a.multiply(o.x)).add(b).mod(p))) {

            // o(x,y) = o(x,y+1)
            o.y = (o.y.add(one));
        }
        if (basepoint instanceof AffinePoint) {
            return o;

        } else if (basepoint instanceof ProjectivePoint) {
            return new ProjectivePoint(zero, one, zero);
        } else if (basepoint instanceof JacobianPoint) {
            return new JacobianPoint(zero, one, zero);
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

        if (pPoint instanceof AffinePoint && qPoint instanceof AffinePoint) {

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
            BigInteger x = (alpha.modPow(two, p)).subtract(pp.x).subtract(qq.x).mod(p);
            // y = -p_y + alpha * (p_x - x) (mod p)
            BigInteger y = (pp.y.negate()).add(alpha.multiply(pp.x.subtract(x))).mod(p);

            return new AffinePoint(x, y);
        } else if (pPoint instanceof ProjectivePoint && qPoint instanceof ProjectivePoint) {

            ProjectivePoint pp = (ProjectivePoint) pPoint;
            ProjectivePoint qq = (ProjectivePoint) qPoint;

            // If any point is zero at infinity
            if (pp.isInfinity()) {
                return qPoint;
            } else if (qq.isInfinity()) {
                return pPoint;
            }

            // U1 = Y_2 * Z_1
            BigInteger U1 = qq.y.multiply(pp.z).mod(p);
            // U2 = Y_1 * Z_2
            BigInteger U2 = pp.y.multiply(qq.z).mod(p);
            // V1 = X_2 * Z_1
            BigInteger V1 = qq.x.multiply(pp.z).mod(p);
            // V2 = X_1 * Z_2
            BigInteger V2 = pp.x.multiply(qq.z).mod(p);

            // If V1 = V2 result is zero at infinity or if U1 = U2 do doubling
            if (V1.equals(V2)) {
                if (U1.equals(U2)) {
                    return doublePoint(pPoint);
                } else {
                    return zeroAtInfinity;
                }
            }

            // U = U_1 - U_2
            BigInteger U = U1.subtract(U2).mod(p);
            // V = V_1 - V_2
            BigInteger V = V1.subtract(V2).mod(p);
            // W = Z_1 * Z_2
            BigInteger W = pp.z.multiply(qq.z).mod(p);
            // A = U^2 * W - V^3 - 2*V^2*V2
            BigInteger A = U.modPow(two, p).multiply(W).subtract(V.modPow(three, p))
                    .subtract(two.multiply(V.modPow(two, p)).multiply(V2)).mod(p);
            // x3 = VA
            BigInteger x3 = V.multiply(A).mod(p);
            // y3 = U * (V^2 * V2 - A) - V^3 * U2
            BigInteger y3 = U.multiply(V.modPow(two, p).multiply(V2).subtract(A))
                    .subtract(V.modPow(three, p).multiply(U2)).mod(p);
            // z3 = V^3 * W
            BigInteger z3 = V.modPow(three, p).multiply(W).mod(p);

            return new ProjectivePoint(x3, y3, z3);
        } else if (pPoint instanceof JacobianPoint && qPoint instanceof JacobianPoint) {

            JacobianPoint pp = (JacobianPoint) pPoint;
            JacobianPoint qq = (JacobianPoint) qPoint;

            // U1 = pp.x * qq.z^2
            BigInteger U1 = pp.x.multiply(qq.z.modPow(two, p)).mod(p);
            // U2 = qq.x * pp.z^2
            BigInteger U2 = qq.x.multiply(pp.z.modPow(two, p)).mod(p);
            // S1 = pp.y * qq.z^3
            BigInteger S1 = pp.y.multiply(qq.z.modPow(three, p)).mod(p);
            // S2 = qq.y * pp.z^3
            BigInteger S2 = qq.y.multiply(pp.z.modPow(three, p)).mod(p);

            // If U1 = U2 result is zero at infinity or if S1 = S2 do doubling
            if (U1.equals(U2)) {
                if (S1.equals(S2)) {
                    return doublePoint(pPoint);
                } else {
                    return zeroAtInfinity;
                }
            }

            // H = U2 - U1
            BigInteger H = U2.subtract(U1).mod(p);
            // R = S2 - S1
            BigInteger R = S2.subtract(S1).mod(p);
            // x3 = R^2 - H^3 - 2U1H^2
            BigInteger x3 = R.modPow(two, p).subtract(H.modPow(three, p))
                    .subtract(two.multiply(U1).multiply(H.modPow(two, p))).mod(p);
            // y3 = R(U1H^2 - x3) - S1H^3
            BigInteger y3 = R.multiply(U1.multiply(H.modPow(two, p)).subtract(x3))
                    .subtract(S1.multiply(H.modPow(three, p))).mod(p);
            // z3 = H * pp.z * qq.z
            BigInteger z3 = H.multiply(pp.z).multiply(qq.z).mod(p);

            return new JacobianPoint(x3, y3, z3);

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
            if (pp.y.mod(p).equals(zero) || pp.equals((AffinePoint) zeroAtInfinity)) {
                return zeroAtInfinity;
            }

            // (3*p_x^2 + a)/2*p_y
            BigInteger alpha = ((three.multiply(pp.x.modPow(two, p))).add(a))
                    .multiply(((two).multiply(pp.y)).modInverse(p)).mod(p);

            // x = alpha^2 - 2*p_x (mod p)
            BigInteger x = alpha.modPow(two, p).subtract(two.multiply(pp.x)).mod(p);

            // y = -p_y + alpha * (p_x - x) (mod p)
            BigInteger y = (pp.y.negate()).add(alpha.multiply(pp.x.subtract(x))).mod(p);

            return new AffinePoint(x, y);
        } else if (pPoint instanceof ProjectivePoint) {

            ProjectivePoint pp = (ProjectivePoint) pPoint;
            // p_y = 0 or p = zero at inf
            if (pp.y.mod(p).equals(zero) || pp.isInfinity()) {
                return zeroAtInfinity;
            }

            // W = 3X^2 + aZ^2
            BigInteger W = three.multiply(pp.x.modPow(two, p))
                    .add(a.multiply(pp.z.modPow(two, p))).mod(p);
            // S = YZ
            BigInteger S = pp.y.multiply(pp.z).mod(p);
            // B = XYS
            BigInteger B = S.multiply(pp.x).multiply(pp.y).mod(p);
            // h = W^2 - 8B
            BigInteger h = W.modPow(two, p).subtract(eight.multiply(B)).mod(p);
            // x3 = 2hS
            BigInteger x3 = h.multiply(S).multiply(two).mod(p);
            // y3 = W(4B - h) - 8(YS)^2
            BigInteger y3 = W.multiply(four.multiply(B).subtract(h))
                    .subtract(eight.multiply((S.multiply(pp.y)).modPow(two, p))).mod(p);
            // z3 = 8S^3
            BigInteger z3 = eight.multiply(S.modPow(three, p)).mod(p);

            return new ProjectivePoint(x3, y3, z3);
        } else if (pPoint instanceof JacobianPoint) {

            JacobianPoint pp = (JacobianPoint) pPoint;

            // pp.y = 0 or pp is zero at infinity
            if (pp.y.mod(p).equals(zero) || pp.isInfinity())
                return zeroAtInfinity;

            // S = 4XY^2
            BigInteger S = four.multiply(pp.x).multiply(pp.y.modPow(two, p)).mod(p);
            // M = 3X^2 + aZ^4
            BigInteger M = three.multiply(pp.x.modPow(two, p)).add(a.multiply(pp.z.modPow(four, p))).mod(p);
            // x3 = M^2 - 2S
            BigInteger x3 = M.modPow(two, p).add(two.multiply(S)).mod(p);
            // y3 = M(S - x3) - 8Y^4
            BigInteger y3 = M.multiply(S.subtract(x3)).subtract(eight.multiply(pp.y.modPow(four, p))).mod(p);
            // z3 = 2YZ
            BigInteger z3 = two.multiply(pp.y).multiply(pp.z).mod(p);

            return new JacobianPoint(x3, y3, z3);
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

    /**
     * Scalar multiplication P = k * G done in a double and add method presented
     * during the lecture.
     * 
     * @param k scalar
     * @param G point to multiply
     * @return k * G
     */
    public Point doubleAndAdd(BigInteger k, Point G) {

        // Write scalar as binary number
        String kBinary = k.toString(2);
        Point P = G;

        for (int i = 1; i < kBinary.length(); i++) {
            P = doublePoint(P);
            if (kBinary.charAt(i) == '1')
                P = addPoints(P, G);
        }

        return P;
    }

    /**
     * Transform a ProjectivePoint to AffinePoint.
     * 
     * @param pp
     * @return an affine coordinates version of pp
     */
    public AffinePoint toAffine(ProjectivePoint pp) {
        try {
            BigInteger zInv = pp.z.modInverse(p);
            return new AffinePoint(pp.x.multiply(zInv).mod(p), pp.y.multiply(zInv).mod(p));
        } catch (ArithmeticException e) {
            // Zero at infinity
            return new AffinePoint(zero, zero);
        }
    }

    /**
     * Transform a JacobianPoint to AffinePoint.
     * 
     * @param pp
     * @return an affine coordinates version of pp
     */
    public AffinePoint toAffine(JacobianPoint pp) {
        try {
            BigInteger z2Inv = pp.z.modPow(two.negate(), p);
            BigInteger z3Inv = pp.z.modPow(three.negate(), p);
            return new AffinePoint(pp.x.multiply(z2Inv).mod(p), pp.y.multiply(z3Inv).mod(p));
        } catch (ArithmeticException e) {
            // Zero at infinity
            return new AffinePoint(zero, zero);
        }
    }

    /**
     * Transform a AffinePoint to ProjectivePoint.
     * 
     * @param pp
     * @return a projective coordinates version of pp
     */
    public ProjectivePoint toProjective(AffinePoint pp) {
        return new ProjectivePoint(pp.x, pp.y, BigInteger.ONE);
    }

}
