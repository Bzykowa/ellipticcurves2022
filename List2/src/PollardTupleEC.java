import java.math.BigInteger;

public class PollardTupleEC {

    private ECPoint r;
    private BigInteger a;
    private BigInteger b;

    public PollardTupleEC(ECPoint r, BigInteger a, BigInteger b) {
        this.setR(r);
        this.setA(a);
        this.setB(b);
    }

    public BigInteger getB() {
        return b;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public ECPoint getR() {
        return r;
    }

    public void setR(ECPoint r) {
        this.r = r;
    }

    public boolean equals(PollardTupleEC i) {
        return (r.equals(i.getR())) && (a.equals(i.getA())) && (b.equals(i.getB()));
    }

    public String toString() {
        return r.toString() + ", " + a + ", " + b;
    }

}
