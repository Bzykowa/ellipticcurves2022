import java.math.BigInteger;

public class JacobianPoint extends Point {

    public BigInteger z;

    public JacobianPoint(BigInteger x, BigInteger y, BigInteger z) {
        if (x.equals(BigInteger.ZERO) && y.equals(BigInteger.ZERO) && z.equals(BigInteger.ZERO)) {
            throw new ArithmeticException();
        } else {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    @Override
    public boolean equals(Object q) {
        if (q instanceof JacobianPoint) {
            JacobianPoint qq = (JacobianPoint) q;
            // U1 = Y_2 * Z_1 ^ 3
            BigInteger U1 = qq.y.multiply(z.pow(3));
            // U2 = Y_1 * Z_2 ^ 3
            BigInteger U2 = y.multiply(qq.z.pow(3));
            // V1 = X_2 * Z_1 ^ 2
            BigInteger V1 = qq.x.multiply(z.pow(2));
            // V2 = X_1 * Z_2 ^ 2
            BigInteger V2 = x.multiply(qq.z.pow(2));

            return V1.equals(V2) && U1.equals(U2);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

    public boolean isInfinity() {
        return x.equals(BigInteger.ZERO) && z.equals(BigInteger.ZERO);
    }

}
