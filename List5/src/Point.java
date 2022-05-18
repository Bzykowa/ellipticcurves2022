import java.math.BigInteger;

public abstract class Point {
    public BigInteger x;
    public BigInteger y;

    public abstract boolean equals(Object q);
    public abstract String toString();
}
