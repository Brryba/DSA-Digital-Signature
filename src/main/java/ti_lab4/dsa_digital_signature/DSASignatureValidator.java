package ti_lab4.dsa_digital_signature;

import ti_lab4.dto.DsaParams;
import ti_lab4.dto.DsaValidationResult;

import java.util.List;

public class DSASignatureValidator {
    private final DSASignatureMaker dsaMaker = new DSASignatureMaker();

    public DsaValidationResult validate(DsaParams params, int fileS, int fileR, List<Byte> fileBytes) {
        int q = params.q(), p = params.p(), h = params.h(), x = params.x();
        int g = dsaMaker.countG(p, q, h);
        int y = dsaMaker.countOpenKeyY(g, x, p);

        List<Integer> hashes = dsaMaker.countHash(fileBytes, q);
        int hash = hashes.getLast();

        int w = dsaMaker.fastModularExponentiation(fileS, q - 2, q);          // w = s^(-1) mod q
        int u1 = (hash * w) % q;           // u1 = h(M) * w mod q
        int u2 = (fileR * w) % q;              // u2 = r * w mod q
        int v = (dsaMaker.fastModularExponentiation(g, u1, p)
                * dsaMaker.fastModularExponentiation(y, u2, p)
                % p) % q;  // v = (g^u1 * y^u2 mod p) mod q

        return new DsaValidationResult(g, y, hash, w, u1, u2, v);
    }
}
