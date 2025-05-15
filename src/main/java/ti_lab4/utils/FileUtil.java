package ti_lab4.utils;

import javafx.stage.FileChooser;
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
        //File file = fileChooser.showOpenDialog(null);
        // TODO: Use Chooser
        File file = new File("D:\\bsuir\\Ти отчеты\\lab4\\test.txt");

        if (file != null) {
            try (InputStream is = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(isr)) {

                String contentAsString = reader.lines().collect(Collectors.joining("\n"));
                int signatureIndex = contentAsString.lastIndexOf(SIGNATURE_LABEL);
                List<Byte> dataBytes;
                Optional<Integer> signature = Optional.empty();
                String input;

                if (signatureIndex != -1) {
                    input = contentAsString.substring(0, signatureIndex);
                    byte[] textBytes = input.getBytes(StandardCharsets.UTF_8);
                    dataBytes = IntStream.range(0, textBytes.length)
                            .mapToObj(i -> textBytes[i])
                            .collect(Collectors.toList());
                    String afterSignature = contentAsString.substring(signatureIndex + SIGNATURE_LABEL.length()).trim();
                    try {
                        if (!afterSignature.isEmpty()) {
                            String digits = afterSignature.replaceAll("\\D+", "");
                            if (!digits.isEmpty()) {
                                signature = Optional.of(Integer.parseInt(digits));
                            }
                        }
                    } catch (NumberFormatException _) {
                    }
                } else {
                    input = contentAsString;
                    byte[] allBytes = contentAsString.getBytes(StandardCharsets.UTF_8);
                    dataBytes = IntStream.range(0, allBytes.length)
                            .mapToObj(i -> allBytes[i])
                            .collect(Collectors.toList());
                }

                return new InputDto(input, dataBytes, signature);
            }
        }
        return null;
    }

    public void writeFile(List<Short> plainBytesArray, boolean isEncoding) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().addAll();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (OutputStream os = new FileOutputStream(file)) {
                if (isEncoding) {
                    for (Short b : plainBytesArray) {
                        os.write(b >> 8);
                        os.write(b);
                    }
                } else {
                    List<Byte> bytes = plainBytesArray.stream().
                            map(Short::byteValue)
                            .toList();
                    for (Byte b : bytes) {
                        os.write(b);
                    }
                }
            }
        }
    }
}

