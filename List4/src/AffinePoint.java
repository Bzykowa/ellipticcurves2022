import java.math.BigInteger;

public class AffinePoint extends Point{

    public AffinePoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object q) {
        if (q instanceof AffinePoint) {
            AffinePoint qq = (AffinePoint) q;
            return (x.equals(qq.x)) && (y.equals(qq.y));
        } else {
            return false;
        }
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
