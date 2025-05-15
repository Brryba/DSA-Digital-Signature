package ti_lab4.dto;

import java.util.List;
import java.util.Optional;

public record FileInput(String input, List<Byte> inputLetters, Optional<FileSignatureParams> fileSignature) {

}
