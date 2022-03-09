import java.math.BigInteger;

public class PollardTuple {
    private BigInteger x;
    private long a;
    private long b;
    
    public PollardTuple(BigInteger x, long a, long b) {
        this.x = x;
        this.a = a;
        this.b = b;
    }
    public BigInteger getX() {
        return x;
    }
    public void setX(BigInteger x) {
        this.x = x;
    }
    public long getA() {
        return a;
    }
    public void setA(long a) {
        this.a = a;
    }
    public long getB() {
        return b;
    }
    public void setB(long b) {
        this.b = b;
    }
}
