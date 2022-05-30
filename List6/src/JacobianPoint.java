import java.math.BigInteger;

public class JacobianPoint extends Point{

    public BigInteger z;

    public JacobianPoint(BigInteger x, BigInteger y, BigInteger z){
        if (x.equals(BigInteger.ZERO) && y.equals(BigInteger.ZERO) && z.equals(BigInteger.ZERO)) {
            throw new ArithmeticException();
        } else {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    @Override
    public boolean equals(Object q) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
