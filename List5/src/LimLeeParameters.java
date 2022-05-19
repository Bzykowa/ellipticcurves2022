import java.math.BigInteger;

public class LimLeeParameters {
    public int a;
    public int b;
    public int l;
    public int h;
    public int v;
    public int aLast;
    public int bLast;
    public int vLast;
    public BigInteger storage;
    public BigInteger squarings;
    public BigInteger multiplications;

    /**
     * Create an instance of Lim-Lee parameters. Calculate all necessary parameters
     * and prepare operations and storage estimations.
     * 
     * @param a Bit length of a big block
     * @param b Bit length of a small block
     * @param l Bit length of the exponent
     */
    public LimLeeParameters(int a, int b, int l) {
        this.a = a;
        this.b = b;
        this.l = l;

        // Int division ceiling hack (divide ints normally with floor and add 1 if there
        // was a remainder)
        h = l / a + ((l % a == 0) ? 0 : 1);
        v = a / b + ((a % b == 0) ? 0 : 1);
        aLast = l - a * (h - 1);
        vLast = aLast / b + ((aLast % b == 0) ? 0 : 1);
        bLast = aLast - b * (vLast - 1);

        squarings = BigInteger.valueOf(b).subtract(BigInteger.ONE);
        multiplications = multiplicationCost();
        storage = storageCost();

    }

    /**
     * Calculate how many multiplication operations will Lim-Lee take with the
     * selected exponent split parameters.
     * 
     * @param h     Number of big blocks
     * @param a     Bit length of a big block for h-1 big blocks
     * @param aLast Bit length of the last big block
     * @return Estimated number of multiplication required for the Lim-Lee
     */
    private BigInteger multiplicationCost() {
        BigInteger firstPart = (BigInteger.TWO.pow(h - 1).subtract(BigInteger.ONE))
                .multiply(BigInteger.valueOf(a).subtract(BigInteger.valueOf(aLast)));
        BigInteger secondPart = (BigInteger.TWO.pow(h).subtract(BigInteger.ONE))
                .multiply(BigInteger.valueOf(aLast).subtract(BigInteger.ONE));
        return (firstPart.divide(BigInteger.TWO.pow(h - 1))).add(secondPart.divide(BigInteger.TWO.pow(h)));
    }

    /**
     * Calculate how much precomputed values will be in the Lim-Lee algorithm with
     * the selected exponent split parameters.
     * 
     * @param h     Number of big blocks
     * @param v     Number of subblocks for h-1 big blocks
     * @param vLast Number of subblocks in the last big block
     * @return Estimated number of precomputed values for the Lim-Lee
     */
    private BigInteger storageCost() {
        // 1 << h = 2^h
        BigInteger firstParentheses = (BigInteger.TWO.pow(h).subtract(BigInteger.ONE))
                .multiply(BigInteger.valueOf(vLast));
        BigInteger secondParentheses = BigInteger.TWO.pow(h - 1).subtract(BigInteger.ONE);
        BigInteger thirdParentheses = BigInteger.valueOf(v).subtract(BigInteger.valueOf(vLast));
        return firstParentheses.add(secondParentheses.multiply(thirdParentheses));
    }

}
