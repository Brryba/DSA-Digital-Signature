package ti_lab4.dsa_digital_signature;

import java.util.List;
import java.util.stream.Collectors;

public class DSACipher {
    public int countR(int p, int q) {
        return p * q;
    }

    public int countREuler(int p, int q) {
        return (p - 1) * (q - 1);
    }

    private short fastModularExponentiation(short num, int exponent, int mod) {
        long result = 1;
        long base = num % mod;

        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = (result * base) % mod;
            }
            base = (base * base) % mod;
            exponent >>= 1;
        }

        return (short) result;
    }



    public List<Short> encodeSymbols(List<Short> inputTextArray, int openKey, int r) {
        return inputTextArray.stream()
                .map(sym -> fastModularExponentiation((short) (sym & 0xFF), openKey, r))
                .collect(Collectors.toList());
    }

    public List<Short> decodeSymbols(List<Short> inputTextArray, int closedKey, int r) {
        return inputTextArray.stream()
                .map(sym -> fastModularExponentiation(sym, closedKey, r))
                .collect(Collectors.toList());
    }
}
