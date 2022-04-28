import java.math.BigInteger;

public class ProjectivePoint extends Point{

    public BigInteger z;

    public ProjectivePoint(BigInteger x, BigInteger y, BigInteger z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object q) {
        if (q instanceof ProjectivePoint) {
            ProjectivePoint qq = (ProjectivePoint) q;
            return (x.equals(qq.x)) && (y.equals(qq.y)) && (z.equals(qq.z));
        } else {
            return false;
        }
    }

    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

}
