module lab_four_ti {
    requires javafx.controls;
    requires javafx.fxml;


    opens ti_lab4 to javafx.fxml;
    exports ti_lab4;
    exports ti_lab4.controller;
    opens ti_lab4.controller to javafx.fxml;
    exports ti_lab4.dsa_digital_signature;
    opens ti_lab4.dsa_digital_signature to javafx.fxml;
    exports ti_lab4.utils;
    opens ti_lab4.utils to javafx.fxml;
}