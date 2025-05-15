package ti_lab4.dsa_digital_signature;

public class DSASignatureMaker {
    public int countR(int p, int q) {
        return p * q;
    }

    public int countREuler(int p, int q) {
        return (p - 1) * (q - 1);
    }

    //Расширенный алгоритм Евклида
    public int countOpenKey(int rEuler, int closedKey) {
        int d0 = rEuler, d1 = closedKey, x0 = 1, x1 = 0, y0 = 0, y1 = 1;
        while (d1 > 1) {
            int q = d0 / d1;
            int d2 = d0 % d1;
            int x2 = x0 - q * x1;
            int y2 = y0 - q * y1;
            d0 = d1;
            d1 = d2;
            x0 = x1;
            x1 = x2;
            y0 = y1;
            y1 = y2;
        }

        if (y1 < 0) {
            return y1 + rEuler;
        }
        return y1;
    }

    private int fastModularExponentiation(int num, int exponent, int mod) {
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



//    public List<Short> encodeSymbols(List<Short> inputTextArray, int openKey, int r) {
//        return inputTextArray.stream()
//                .map(sym -> fastModularExponentiation((short) (sym & 0xFF), openKey, r))
//                .collect(Collectors.toList());
//    }
//
//    public List<Short> decodeSymbols(List<Short> inputTextArray, int closedKey, int r) {
//        return inputTextArray.stream()
//                .map(sym -> fastModularExponentiation(sym, closedKey, r))
//                .collect(Collectors.toList());
//    }

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

    //public int countHash()
}