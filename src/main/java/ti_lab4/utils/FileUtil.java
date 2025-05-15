package ti_lab4.utils;

import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileUtil {
    public List<Short> readFile(boolean iEncoding) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (InputStream is = new FileInputStream(file)) {
                byte[] bytes = is.readAllBytes();
                if (!iEncoding) {
                    List<Short> shorts = new ArrayList<>();
                    for (int i = 0; i < bytes.length - 1; i += 2) {
                        short value = (short) (((bytes[i] & 0xFF) << 8) | (bytes[i + 1] & 0xFF));
                        shorts.add(value);
                    }
                    if (bytes.length % 2 != 0) {
                        short lastValue = (short) ((bytes[bytes.length - 1] & 0xFF) << 8);
                        shorts.add(lastValue);
                    }
                    return shorts;
                }
                return IntStream.range(0, bytes.length)
                        .mapToObj(i -> (short) bytes[i])
                        .collect(Collectors.toList());
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

