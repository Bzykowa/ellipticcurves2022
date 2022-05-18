public class App {

    private static final int S_100 = 100;
    private static final int S_5000 = 5000;

    private static int a = 0;
    private static int b = 0;

    /**
     * Calculate optimal a,b for the Lim-Lee based on length of the exponent and the
     * storage bound.
     * 
     * @param l Bit length of the exponent
     * @param S Storage bound
     */
    private static void findOptimalParameters(int l, int S) {

    }

    /**
     * Calculate how many multiplication operations will Lim-Lee take with the
     * selected exonent split parameters.
     * 
     * @param h     Number of big blocks
     * @param a     Bit length of a big block for h-1 big blocks
     * @param aLast Bit length of the last big block
     * @return Estimated number of multiplication required for the Lim-Lee
     */
    private static double multiplicationCost(int h, int a, int aLast) {
        // 1 << h = 2^h
        int firstPart = ((1 << (h - 1)) - 1) * (a - aLast);
        int secondPart = (((1 << h) - 1)) * (aLast - 1);
        return (firstPart / (1 << (h - 1))) + (secondPart / (1 << h));
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
    private static int storageCost(int h, int v, int vLast) {
        // 1 << h = 2^h
        int firstParentheses = ((1 << h) - 1) * vLast;
        int secondParentheses = (1 << (h - 1)) - 1;
        int thirdParentheses = v - vLast;
        return firstParentheses + (secondParentheses * thirdParentheses);
    }

    public static void main(String[] args) throws Exception {

    }
}
