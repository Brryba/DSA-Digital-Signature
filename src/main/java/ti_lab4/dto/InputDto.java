package ti_lab4.dto;

import java.util.List;
import java.util.Optional;

public record InputDto(String input, List<Byte> inputLetters, Optional<FileSignatureDto> fileSignature) {

}
