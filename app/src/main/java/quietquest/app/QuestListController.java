package quietquest.app;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class QuestListController implements Initializable, UIUpdater {
    @FXML
    private Button deleteButton;
    @FXML
    private ListView<String> questListView;
    @FXML
    private ToggleButton subscribeButton;
    @FXML
    private Label mqttMotionMessage;
    @FXML
    private Label mqttDistanceMessage;
    @FXML
    private Label mqttConnectionMessage;


    private FXMLLoader loader;
    private Parent root;
    private Stage stage;
    private Scene scene;
    private QuestManager questManager;
    private HashMap<String, Quest> quests;
    private MQTTHandler mqttClient;


    public void initialize(URL arg0, ResourceBundle arg1) {
        questManager = QuietQuestMain.questManager;
        quests = questManager.getQuests();
        mqttClient = new MQTTHandler(this);
        displayQuests();
        setSelectedQuest();
    }

    public void displayQuests(){
        questListView.getItems().addAll(quests.keySet());
    }

    private void setSelectedQuest(){
        questListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                String selectedKey = questListView.getSelectionModel().getSelectedItem();
                questManager.setQuestSelection(quests.get(selectedKey));
            }
        });
    }

    public void onDeleteQuest(ActionEvent event) throws IOException {
        loader = new FXMLLoader(QuietQuestMain.class.getResource("/quietquest/app/delete-quest-view.fxml"));
        loadLoader(loader, event);
    }

    public void loadLoader(FXMLLoader loader, ActionEvent event) throws IOException {
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void disconnectMqtt() {
        mqttClient.disconnect();
    }

    @FXML
    private void onSubscribeButtonClick() {
        if (subscribeButton.isSelected()) {
            mqttClient.connect(); // Connect to MQTT broker
            mqttClient.subscribe(); // Subscribe
        } else {
            mqttClient.disconnect();
            mqttConnectionMessage.getStyleClass().clear();
            mqttConnectionMessage.setText("");
        }
    }

    @FXML
    private void onManualPublishClick() {
        String message = "Your quest has started";
        mqttClient.publishMessage("/quietquest/application/start", message);
    }

    @Override
    public void updateUI(String message) {
        Platform.runLater(() -> {
            if (message.contains("Wio")) {
                mqttConnectionMessage.setText(message);
                mqttConnectionMessage.getStyleClass().clear();
                mqttConnectionMessage.getStyleClass().add("label-all-green");

            } else if ("Hi people are coming".equals(message)) {
                mqttMotionMessage.setText(message);
                mqttMotionMessage.getStyleClass().clear();
                mqttMotionMessage.getStyleClass().add("label-all-green");

            } else if("Sensor is watching".equals(message)) {
                mqttMotionMessage.setText(message);
                mqttMotionMessage.getStyleClass().clear();
                mqttMotionMessage.getStyleClass().add("label-all-red");

            } else if (message.contains("distance")) {
                mqttDistanceMessage.setText(message);
                mqttDistanceMessage.getStyleClass().clear();
                mqttDistanceMessage.getStyleClass().add("label-all-green");

            } else {
                mqttMotionMessage.getStyleClass().clear();
                mqttMotionMessage.setText(message);
            }
        });
    }
}
