import java.math.BigInteger;

/**
 * Class representing point on an elliptic curve in affine coordinates (x,y) 
 */
public class ECPoint{
    private BigInteger x;
    private BigInteger y;

    public ECPoint(BigInteger x, BigInteger y) {
        this.setX(x);
        this.setY(y);
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public boolean equals(ECPoint q){
        return (x.equals(q.getX())) && (y.equals(q.getY()));
    }
}