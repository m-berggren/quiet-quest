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

public class StartController implements Initializable {
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

    }

    @FXML
    protected void onNewQuestButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource("/quietquest/app/create-quest-view.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}