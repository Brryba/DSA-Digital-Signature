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

    @FXML private Button calculateBtn;
    @FXML private Button verifyBtn;
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;

    private final InputValidator validator = new InputValidator();
    private final DSASignatureMaker dsaMaker = new DSASignatureMaker();
    private final FileUtil fileUtil = new FileUtil();
    private List<Byte> fileBytes = new ArrayList<>();
    private int generatedSignature;
    private int existingSignature;

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
            int g = dsaMaker.countG(p, q, h);
            gField.setText(String.valueOf(g));
            int y = dsaMaker.countOpenKeyY(g, x, p);
            yField.setText(String.valueOf(y));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
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
        this.fileBytes = inputDto.inputLetters();
        fileSignature.setText(inputDto.fileSignature().isPresent()
                ? String.valueOf(inputDto.fileSignature().get())
                : "В файле не было подписи");
        textField.setText(inputDto.input());
        textField.appendText("\n" + fileBytes.stream()
                .map(aByte -> (short) (aByte & 0xFF)).toList().toString());
        verifyBtn.setDisable(inputDto.fileSignature().isEmpty());
        calculateBtn.setDisable(false);
        System.out.println(inputDto);
    }

    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        // TODO: обработка сохранения файла
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}