package quietquest.app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable, UIUpdater {
    private Parent root;
    private Stage stage;
    private Scene scene;
    @FXML
    private Label welcomeText;

    @FXML
    private Button helloButton;
    @FXML
    private Label mqttMessage;

    @FXML
    private Label mqttDistanceMessage;

    public MQTTHandler mqttClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pass the controller instance to the MQTT subscriber
        mqttClient = new MQTTHandler(this);
        mqttClient.start(); // Start listening for MQTT messages*/

    }

    @FXML
    protected void onHelloButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource("/quietquest/app/create-quest-view.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handlePublishAction() {
        String message = "Hello from JavaFX";
        mqttClient.publishMessage("sensor/motion", message);
    }

    @Override
    public void updateUI(String message) {
        Platform.runLater(() -> {
            if ("Hello from JavaFX".equals(message)) {
                mqttMessage.setText("Works!");
                mqttMessage.getStyleClass().clear();
                mqttMessage.getStyleClass().add("label-all-green");

            } else if ("Hi people are coming".equals(message)) {
                mqttMessage.setText(message);
                mqttMessage.getStyleClass().clear();
                mqttMessage.getStyleClass().add("label-all-green");

            } else if ("LIAN ADD HERE".equals(message)) {
                mqttDistanceMessage.setText("");
                mqttDistanceMessage.getStyleClass().clear();
                mqttDistanceMessage.getStyleClass().add("label-all-green");

            } else if("Sensor is watching".equals(message)) {
                mqttMessage.setText(message);
                mqttMessage.getStyleClass().clear();
                mqttMessage.getStyleClass().add("label-all-red");

            } else {
                mqttMessage.getStyleClass().clear();
                mqttMessage.setText(message);
            }
        });
    }
}