package ti_lab4.dto;

import java.util.List;

public record SignatureResult(int g, int y, List<Integer> hashes, int r, int s) {
}
