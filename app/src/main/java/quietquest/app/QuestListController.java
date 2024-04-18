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
import javafx.scene.text.Text;
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
    @FXML
    private TextField titleField;
    @FXML
    private Text descriptionHeader;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Text tasksHeader;
    @FXML
    private TextField taskFieldOne;
    @FXML
    private TextField taskFieldTwo;
    @FXML
    private TextField taskFieldThree;
    @FXML
    private Button startButton;
    @FXML
    private Button editButton;
    @FXML
    private Button completeButton;
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

    //
    public void showSelected() {
        // show quest/task details on the right side:
        titleField.setVisible(true);
        descriptionHeader.setVisible(true);
        descriptionField.setVisible(true);

        Quest selectedItem = QuestManager.getQuestSelection();
        if (selectedItem instanceof Quest) { // if we selected a Quest, it should have its Tasks listed too
            tasksHeader.setVisible(true);
            taskFieldOne.setVisible(true);
            taskFieldTwo.setVisible(true);
            taskFieldThree.setVisible(true);

            Quest currentQuest = (Quest) selectedItem;
            titleField.setText(currentQuest.getTitle());
            descriptionField.setText(currentQuest.getDescription());
            // set text for task fields too
        /*
        } else {  // if we selected a Task, no task list should show up
            Task currentTask = selectedItem;
            titleField.setText(selectedItem.getTitle());
            descriptionField.setText(selectedItem.getDescription());
        */
        }
    }

    // edit selected quest by clicking "Edit" button:
    public void setEditable(){
        titleField.setEditable(true);
        descriptionField.setEditable(true);
    }

    // save quest/task details by clicking "Save" button:
    public void onSaveButtonClick() {
        // save title and make non-editable:
        String newTitle = titleField.getText();
        titleField.setText(newTitle);
        Quest.setTitle(newTitle);
        titleField.setEditable(false);

        // save description and make non-editable:
        String newDescription = descriptionField.getText();
        descriptionField.setText(newDescription);
        Quest.setDescription(newDescription);
        descriptionField.setEditable(false);
        //taskField.setEditable(false);
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

            } else if ("Motion is detected. Someone is nearby.".equals(message)) {
                mqttMotionMessage.setText(message);
                mqttMotionMessage.getStyleClass().clear();
                mqttMotionMessage.getStyleClass().add("label-all-green");

            } else if("Searching for motion".equals(message)) {
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
