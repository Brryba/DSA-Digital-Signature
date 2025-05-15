package ti_lab4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ti_lab4.dsa_digital_signature.DSASignatureMaker;
import ti_lab4.dsa_digital_signature.DSASignatureValidator;
import ti_lab4.dto.DsaParams;
import ti_lab4.dto.DsaValidationResult;
import ti_lab4.dto.FileInput;
import ti_lab4.dto.SignatureResult;
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
    private final DSASignatureValidator dsaValidator = new DSASignatureValidator();
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
            DsaValidationResult validationParams = dsaValidator.validate(params, fileS, fileR, fileBytes);

            verificationResultArea.setText(
                    "g = " + validationParams.g() + "  // g = h^((p-1)/q) mod p\n" +
                            "y = " + validationParams.y() + "  // y = g^x mod p\n" +
                            "hash = " + validationParams.hash() + "  // h(M)\n" +
                            "w = " + validationParams.w() + "  // w = s^(-1) mod q\n" +
                            "u1 = " + validationParams.u1() + "  // u1 = hash * w mod q\n" +
                            "u2 = " + validationParams.u2() + "  // u2 = r * w mod q\n" +
                            "v = " + validationParams.v() + "  // v = (g^u1 * y^u2 mod p) mod q\n" +
                            "r из файла = " + fileR +
                            "\nРезультат: " + (validationParams.v() == fileR ? "Подпись ВЕРНА" : "Подпись НЕВЕРНА")
            );

        } catch (NumberFormatException e) {
            showError("Все параметры должны быть целыми числами!");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
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
        FileInput fileInput;
        try {
            fileInput = fileUtil.readFile();
            if (fileInput == null) return;
        } catch (IOException e) {
            showError(e.getMessage());
            return;
        }
        clearFormOutputs();
        this.fileBytes = fileInput.inputLetters();
        fileSignature.setText(fileInput.fileSignature().isPresent()
                ? String.valueOf(fileInput.fileSignature().get())
                : "В файле нет подписи или она была изменена и записана в неправильном формате");
        textField.setText(fileInput.input());
        textField.appendText("\n" + fileBytes.stream()
                .map(aByte -> (short) (aByte & 0xFF)).toList());
        verifyBtn.setDisable(fileInput.fileSignature().isEmpty());
        calculateBtn.setDisable(false);

        if (fileInput.fileSignature().isPresent()) {
            fileR = fileInput.fileSignature().get().r();
            fileS = fileInput.fileSignature().get().s();
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