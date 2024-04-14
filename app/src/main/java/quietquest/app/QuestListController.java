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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class QuestListController implements Initializable, UIUpdater {
    @FXML
    private Button deleteButton;
    @FXML
    private ListView<String> questListView;
    @FXML
    private ToggleButton subscribeButton;
    @FXML
    private Label mqttMessage;
    @FXML
    private Label mqttDistanceMessage;

    private FXMLLoader loader;
    private Parent root;
    private Stage stage;
    private Scene scene;
    private QuestManager questManager;
    private HashMap<String, Quest> quests;
    public MQTTHandler mqttClient;


    public void initialize(URL arg0, ResourceBundle arg1) {
        questManager = QuietQuestMain.questManager;
        quests = questManager.getQuests();
        mqttClient = new MQTTHandler(this);
        displayQuests();
        setSelectedQuest();
        //scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
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

    @FXML
    private void onSubscribeButtonClick() {
        if (subscribeButton.isSelected()) {
            mqttClient.connect(); // Connect to MQTT broker
            mqttClient.subscribe();

        } else {
            mqttClient.disconnect();

            mqttMessage.getStyleClass().clear();
            mqttMessage.setText("");
        }
    }
    @FXML
    private void onManualPublishClick() {
        String message = "Hello from JavaFX";
        mqttClient.publishMessage("/quietquest/sensor/motion", message);
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
