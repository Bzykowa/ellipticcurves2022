import java.math.BigInteger;

public class AffinePoint{
    public BigInteger x;
    public BigInteger y; 

    public AffinePoint(BigInteger x, BigInteger y){
        this.x = x;
        this.y = y;
    }

    public boolean equals(AffinePoint q) {
        return (x.equals(q.x)) && (y.equals(q.y));
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
