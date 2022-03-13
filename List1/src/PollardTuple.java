import java.math.BigInteger;

public class PollardTuple {
    private BigInteger x;
    private BigInteger a;
    private BigInteger b;

    public PollardTuple(BigInteger x, BigInteger a, BigInteger b) {
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

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public BigInteger getB() {
        return b;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }

    public String toString() {
        return "(x: " + x.toString() + ", a:" + a.toString() + ", b:" + b.toString() + ")";
    }
}
