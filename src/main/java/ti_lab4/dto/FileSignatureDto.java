package ti_lab4.dto;

public record FileSignatureDto(int r, int s) {
    @Override
    public String toString() {
        return "r = " + r + ", s = " + s;
    }
}
