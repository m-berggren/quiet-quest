module quietquest {
  requires javafx.controls;
  requires javafx.fxml;
  requires com.hivemq.client.mqtt;
  requires jdk.jdi;
  requires java.sql;
  requires javafx.media;

  opens quietquest to javafx.fxml;
  exports quietquest;
  exports quietquest.model;
  opens quietquest.model to javafx.fxml;
  exports quietquest.utility;
  opens quietquest.utility to javafx.fxml;
  exports quietquest.controller;
  opens quietquest.controller to javafx.fxml;
}