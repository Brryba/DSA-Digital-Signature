package ti_lab4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ti_lab4.dsa_digital_signature.DSASignatureMaker;
import ti_lab4.dto.DsaParams;
import ti_lab4.dto.InputDto;
import ti_lab4.utils.FileUtil;
import ti_lab4.utils.InputValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    private TextField qField;
    @FXML
    private TextField pField;
    @FXML
    private TextField hField;
    @FXML
    private TextField xField;
    @FXML
    private TextField kField;

    @FXML
    private TextField gField;
    @FXML
    private TextField yField;
    @FXML
    private TextField rField;
    @FXML
    private TextField sField;
    @FXML
    private TextField hashField;
    @FXML
    private TextField fileSignature;
    @FXML
    private TextArea textField;
    @FXML
    private TextArea hashBytesArea;
    @FXML
    private TextArea verificationResultArea;

    @FXML
    private Button calculateBtn;
    @FXML
    private Button verifyBtn;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;

    private final InputValidator validator = new InputValidator();
    private final DSASignatureMaker dsaMaker = new DSASignatureMaker();
    private final FileUtil fileUtil = new FileUtil();
    private List<Byte> fileBytes = new ArrayList<>();
    private int fileS, fileR;

    @FXML
    private void initialize() {
        calculateBtn.setOnAction(_ -> handleCalculate());
        openMenuItem.setOnAction(_ -> handleOpen());
        saveMenuItem.setOnAction(_ -> handleSave());
        verifyBtn.setOnAction(_ -> handleVerify());
    }

    private DsaParams readDsaParams(boolean kRequired) {
        int q = Integer.parseInt(qField.getText());
        int p = Integer.parseInt(pField.getText());
        int h = Integer.parseInt(hField.getText());
        int x = Integer.parseInt(xField.getText());
        int k = kRequired ? Integer.parseInt(kField.getText()) : -1;
        return new DsaParams(q, p, h, x, k, kRequired);
    }

    private DsaParams parseAndValidateInput(boolean kRequired) throws IllegalArgumentException {
        DsaParams params = readDsaParams(kRequired);
        validator.validateAll(params);
        return params;
    }

    private SignatureResult calculateSignature(DsaParams params) {
        int g = dsaMaker.countG(params.p(), params.q(), params.h());
        int y = dsaMaker.countOpenKeyY(g, params.x(), params.p());
        List<Integer> hashes = dsaMaker.countHash(fileBytes, params.q());
        int lastHash = hashes.getLast();

        int r = dsaMaker.countR(g, params.k(), params.p(), params.q());
        int s = dsaMaker.countS(params.k(), lastHash, params.x(), r, params.q());

        validator.validateRAndS(r, s);
        return new SignatureResult(g, y, hashes, r, s);
    }

    private void handleCalculate() {
        try {
            DsaParams params = parseAndValidateInput(true);
            saveMenuItem.setDisable(false);

            SignatureResult result = calculateSignature(params);
            updateUI(result);

        } catch (NumberFormatException e) {
            showError("Все введенные числа должны быть целыми!");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            clearFormOutputs();
        }
    }

    private void handleVerify() {
        try {
            DsaParams params = parseAndValidateInput(false);
            int q = params.q(), p = params.p(), h = params.h(), x = params.x();

            int g = dsaMaker.countG(p, q, h);
            int y = dsaMaker.countOpenKeyY(g, x, p);

            List<Integer> hashes = dsaMaker.countHash(fileBytes, q);
            int hash = hashes.getLast();

            int r = fileR;
            int s = fileS;

            int w = dsaMaker.fastModularExponentiation(s, q - 2, q);          // w = s^(-1) mod q
            int u1 = (hash * w) % q;           // u1 = h(M) * w mod q
            int u2 = (r * w) % q;              // u2 = r * w mod q
            int v = (dsaMaker.fastModularExponentiation(g, u1, p)
                    * dsaMaker.fastModularExponentiation(y, u2, p)
                    % p) % q;  // v = (g^u1 * y^u2 mod p) mod q


            verificationResultArea.setText(
                    "g = " + g + "  // g = h^((p-1)/q) mod p\n" +
                            "y = " + y + "  // y = g^x mod p\n" +
                            "hash = " + hash + "  // h(M)\n" +
                            "w = " + w + "  // w = s^(-1) mod q\n" +
                            "u1 = " + u1 + "  // u1 = hash * w mod q\n" +
                            "u2 = " + u2 + "  // u2 = r * w mod q\n" +
                            "v = " + v + "  // v = (g^u1 * y^u2 mod p) mod q\n" +
                            "r из файла = " + r +
                            "\nРезультат: " + (v == r ? "Подпись ВЕРНА" : "Подпись НЕВЕРНА")
            );

        } catch (NumberFormatException e) {
            showError("Все параметры должны быть целыми числами!");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    record SignatureResult(int g, int y, List<Integer> hashes, int r, int s) {
    }

    private void updateUI(SignatureResult result) {
        gField.setText(String.valueOf(result.g()));
        yField.setText(String.valueOf(result.y()));
        hashBytesArea.setText(String.valueOf(result.hashes()));
        hashField.setText(String.valueOf(result.hashes().getLast()));
        rField.setText(String.valueOf(result.r()));
        sField.setText(String.valueOf(result.s()));
    }

    private void handleOpen() {
        InputDto inputDto;
        try {
            inputDto = fileUtil.readFile();
            if (inputDto == null) return;
        } catch (IOException e) {
            showError(e.getMessage());
            return;
        }
        clearFormOutputs();
        this.fileBytes = inputDto.inputLetters();
        fileSignature.setText(inputDto.fileSignature().isPresent()
                ? String.valueOf(inputDto.fileSignature().get())
                : "В файле нет подписи или она была изменена и записана в неправильном формате");
        textField.setText(inputDto.input());
        textField.appendText("\n" + fileBytes.stream()
                .map(aByte -> (short) (aByte & 0xFF)).toList());
        verifyBtn.setDisable(inputDto.fileSignature().isEmpty());
        calculateBtn.setDisable(false);

        if (inputDto.fileSignature().isPresent()) {
            fileR = inputDto.fileSignature().get().r();
            fileS = inputDto.fileSignature().get().s();
        }
    }

    private void handleSave() {
        try {
            fileUtil.writeFile(fileBytes, Integer.parseInt(rField.getText()), Integer.parseInt(sField.getText()));
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void clearFormOutputs() {
        gField.setText("");
        yField.setText("");
        hashBytesArea.setText("");
        hashField.setText("");
        rField.setText("");
        sField.setText("");
        verificationResultArea.setText("");
        fileS = fileR = 0;
        saveMenuItem.setDisable(true);
    }
}