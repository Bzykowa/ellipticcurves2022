import java.math.BigInteger;

public class ProjectivePoint {

    public BigInteger x;
    public BigInteger y;
    public BigInteger z;

    public ProjectivePoint(BigInteger x, BigInteger y, BigInteger z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(ProjectivePoint q) {
        return (x.equals(q.x)) && (y.equals(q.y)) && z.equals(q.z);
    }

    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

}
