package ti_lab4.dsa_digital_signature;

import java.util.ArrayList;
import java.util.List;

public class DSASignatureMaker {
    public int fastModularExponentiation(int num, int exponent, int mod) {
        long result = 1;
        long base = num % mod;

        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = (result * base) % mod;
            }
            base = (base * base) % mod;
            exponent >>= 1;
        }

        return (int) result;
    }

    public int countG(int p, int q, int h) throws IllegalArgumentException {
        int power = (p - 1) / q;
        int g = fastModularExponentiation(h, power, p);
        if (g <= 1) {
            throw new IllegalArgumentException("g <= 1. Введите другие числа");
        }
        return g;
    }

    public int countOpenKeyY(int g, int x, int p) {
        return fastModularExponentiation(g, x, p);
    }

    public List<Integer> countHash(List<Byte> numbers, int q) {
        int H0_HASH = 100 % q;
        List<Integer> result = new ArrayList<>(numbers.size() + 1);
        result.add(H0_HASH);
        int prev = H0_HASH;
        for (int num : numbers) {
            int cur = fastModularExponentiation(prev + num, 2, q);
            result.add(cur);
            prev = cur;
        }
        return result;
    }

    public int countR(int g, int k, int p, int q) {
        return fastModularExponentiation(g, k, p) % q;
    }

    public int countS(int k, int hash, int x, int r, int q) {
        return fastModularExponentiation(k, q - 2, q) * (hash + x * r) % q;
    }
}