import java.math.BigInteger;

public class ProjectivePoint extends Point {

    public BigInteger z;

    public ProjectivePoint(BigInteger x, BigInteger y, BigInteger z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object q) {
        if (q instanceof ProjectivePoint) {
            ProjectivePoint qq = (ProjectivePoint) q;
            // U1 = Y_2 * Z_1
            BigInteger U1 = qq.y.multiply(z);
            // U2 = Y_1 * Z_2
            BigInteger U2 = y.multiply(qq.z);
            // V1 = X_2 * Z_1
            BigInteger V1 = qq.x.multiply(z);
            // V2 = X_1 * Z_2
            BigInteger V2 = x.multiply(qq.z);

            return V1.equals(V2) && U1.equals(U2);
        } else {
            return false;
        }
    }

    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

    public boolean isInfinity() {
        return x.equals(BigInteger.ZERO) && y.equals(BigInteger.ONE);
    }

}
