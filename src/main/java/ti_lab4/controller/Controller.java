package ti_lab4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ti_lab4.dsa_digital_signature.DSACipher;
import ti_lab4.utils.InputValidator;

import java.io.File;

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
    @FXML private TextField extraField;

    @FXML private Button calculateBtn;
    @FXML private Button verifyBtn;
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;

    private InputValidator validator = new InputValidator();
    private DSACipher cipher = new DSACipher();

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
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }
    }

    private void handleVerify() {
        // TODO: реализовать проверку
    }

    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        // TODO: обработка открытия файла
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