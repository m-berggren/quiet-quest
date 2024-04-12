module quietquest.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.hivemq.client.mqtt;

    opens quietquest.app to javafx.fxml;
    exports quietquest.app;
}