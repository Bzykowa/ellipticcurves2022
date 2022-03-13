import java.math.BigInteger;
import java.security.SecureRandom;

public class App {

    private static SecureRandom random = new SecureRandom();

    public static void main(String[] args) throws Exception {

        //Randomly choose p length between 40 and 59 bits
        int pLength = random.nextInt(40, 60);
        BigInteger p;
        BigInteger g;
        BigInteger y;

        // Find p that will satisfy p = 2p' + 1
        do {
            // BigInteger of pLength bits that is probably prime with probability in range
            // [1/2^20,1]
            BigInteger p1 = new BigInteger(pLength, 20, random);
            p = p1.multiply(BigInteger.TWO).add(BigInteger.ONE);
        } while (!p.isProbablePrime(20));
        System.out.println("p = " + p);

        // Find g that will satisfy g = g'^2 (mod p) != 1
        BigInteger g1;
        do {
            g1 = BigInteger.valueOf(random.nextLong(p.longValue()-2)+2);
            g = g1.modPow(BigInteger.TWO,p);
            //System.out.println("g1: "+ g1 + " g1^2 mod p: " + g.mod(p) + " p: " + p);
        } while (g.mod(p).compareTo(BigInteger.ONE) == 0);
        System.out.println("g = " + g + " g' = " + g1);

        //Generate y = g^x, where x is randomly chosen from [0, ord(g)]
        long initX = random.nextLong((p.subtract(BigInteger.ONE)).divide(BigInteger.TWO).longValue());
        y = g.modPow(BigInteger.valueOf(initX), p);
        System.out.println("y = " + y + " initX = " + initX);

        PollardRho pollard = new PollardRho(p, g, y);
        BigInteger x = pollard.solveX();
        System.out.println("X = " + x);

    }
}
