module org.example.quietquest {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.quietquest to javafx.fxml;
    exports org.example.quietquest;
}