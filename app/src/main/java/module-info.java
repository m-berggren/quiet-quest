module quietquest.app {
    requires javafx.controls;
    requires javafx.fxml;


    opens quietquest.app to javafx.fxml;
    exports quietquest.app;
}