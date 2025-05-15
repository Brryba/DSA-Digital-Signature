module lab_four_ti {
    requires javafx.controls;
    requires javafx.fxml;


    opens personal.tilab4 to javafx.fxml;
    exports personal.tilab4;
}