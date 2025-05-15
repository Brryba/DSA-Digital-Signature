package ti_lab4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ti_lab4.dsa_digital_signature.DSASignatureMaker;
import ti_lab4.dto.InputDto;
import ti_lab4.utils.FileUtil;
import ti_lab4.utils.InputValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML private TextField qField;
    @FXML private TextField pField;
    @FXML private TextField hField;
    @FXML private TextField xField;
    @FXML private TextField kField;

    @FXML private TextField gField;
    @FXML private TextField yField;
    @FXML private TextField rField;
    @FXML private TextField sField;
    @FXML private TextField hashField;
    @FXML private TextField fileSignature;
    @FXML private TextArea textField;
    @FXML private TextArea hashBytesArea;

    @FXML private Button calculateBtn;
    @FXML private Button verifyBtn;
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;

    private final InputValidator validator = new InputValidator();
    private final DSASignatureMaker dsaMaker = new DSASignatureMaker();
    private final FileUtil fileUtil = new FileUtil();
    private List<Byte> fileBytes = new ArrayList<>();
    private int fileS, fileR, r, s;

    @FXML
    private void initialize() {
        calculateBtn.setOnAction(event -> handleCalculate());
        verifyBtn.setOnAction(event -> handleVerify());
        openMenuItem.setOnAction(event -> handleOpen());
        saveMenuItem.setOnAction(event -> handleSave());
    }

    private void handleCalculate() {
        int q, p, h, x, k;
        try {
            q = Integer.parseInt(qField.getText());
            p = Integer.parseInt(pField.getText());
            h = Integer.parseInt(hField.getText());
            x = Integer.parseInt(xField.getText());
            k = Integer.parseInt(kField.getText());
        } catch (NumberFormatException e) {
            showError("Все введенные числа должны быть целыми!");
            return;
        }

        try {
            validator.validateAll(q, p, h, x, k);
            saveMenuItem.setDisable(false);


            int g = dsaMaker.countG(p, q, h);
            int y = dsaMaker.countOpenKeyY(g, x, p);
            List<Integer> hashes = dsaMaker.countHash(fileBytes, q);
            int lastHash = hashes.getLast();
            this.r = dsaMaker.countR(g, k, p, q);
            this.s = dsaMaker.countS(k, lastHash, x, r, q);


            validator.validateRAndS(r, s);
            gField.setText(String.valueOf(g));
            yField.setText(String.valueOf(y));
            hashBytesArea.setText(String.valueOf(hashes));
            hashField.setText(String.valueOf(lastHash));
            rField.setText(String.valueOf(r));
            sField.setText(String.valueOf(s));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            clearFormOutputs();
        }
    }

    private void handleVerify() {
        // TODO: реализовать проверку
    }

    private void handleOpen() {
        InputDto inputDto;
        try {
            inputDto = fileUtil.readFile();
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
                .map(aByte -> (short) (aByte & 0xFF)).toList().toString());
        verifyBtn.setDisable(inputDto.fileSignature().isEmpty());
        calculateBtn.setDisable(false);

        if (inputDto.fileSignature().isPresent()) {
            fileR = inputDto.fileSignature().get().r();
            fileS = inputDto.fileSignature().get().s();
        }
    }

    private void handleSave() {
        try {
            fileUtil.writeFile(fileBytes, r, s);
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
        r = s = fileS = fileR = 0;
        saveMenuItem.setDisable(true);
    }
}