package ti_lab4.utils;

import javafx.stage.FileChooser;
import ti_lab4.dto.FileSignatureDto;
import ti_lab4.dto.InputDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileUtil {
    private final String SIGNATURE_LABEL = "{SIGNATURE}";

    public InputDto readFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = new File("D:\\bsuir\\Ти отчеты\\lab4\\test.txt");
        //File file = fileChooser.showOpenDialog(null);
        if (file == null) return null;

        try (InputStream is = new FileInputStream(file)) {
            byte[] allBytes = is.readAllBytes();
            String content = new String(allBytes, StandardCharsets.UTF_8);

            int sigPos = content.lastIndexOf(SIGNATURE_LABEL);

            String input;
            List<Byte> dataBytes = new ArrayList<>();
            Optional<FileSignatureDto> signature = Optional.empty();

            if (sigPos != -1) {
                input = content.substring(0, sigPos);

                byte[] textBytes = input.getBytes(StandardCharsets.UTF_8);
                for (byte b : textBytes) {
                    dataBytes.add(b);
                }

                String sigPart = content.substring(sigPos + SIGNATURE_LABEL.length()).trim();
                signature = parseSignature(sigPart);
            } else {
                input = content;
                for (byte b : allBytes) {
                    dataBytes.add(b);
                }
            }

            return new InputDto(input, dataBytes, signature);
        }
    }

    private Optional<FileSignatureDto> parseSignature(String sigStr) {
        String[] parts = sigStr.split("\\s+");
        if (parts.length < 2) return Optional.empty();

        try {
            return Optional.of(new FileSignatureDto(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
            ));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public void writeFile(List<Byte> plainBytesArray, int r, int s) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        //File file = fileChooser.showSaveDialog(null);
        File file = new File("D:\\bsuir\\Ти отчеты\\lab4\\out.txt"); // для тестирования

        if (file != null) {
            byte[] bytes = new byte[plainBytesArray.size()];
            for (int i = 0; i < plainBytesArray.size(); i++) {
                bytes[i] = plainBytesArray.get(i);
            }

            String signatureLine = String.format("%s %d %d", SIGNATURE_LABEL, r, s);

            try (OutputStream os = new FileOutputStream(file);
                 Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                writer.write(new String(bytes, StandardCharsets.UTF_8));
                writer.write(System.lineSeparator());
                writer.write(signatureLine);
            }
        }
    }
}

