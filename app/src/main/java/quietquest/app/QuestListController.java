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
import java.lang.reflect.Array;
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
    private Button saveButton;
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
    private Quest currentQuest;


    public void initialize(URL arg0, ResourceBundle arg1) {
        questManager = QuietQuestMain.questManager;
        quests = questManager.getQuests();
        mqttClient = new MQTTHandler(this);
        displayQuests();
        setSelectedQuest();
        currentQuest = null; // set to null to avoid another quest's details being shown
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
                showSelected();
            }
        });
    }

    public void onDeleteQuest(ActionEvent event) throws IOException {
        loader = new FXMLLoader(QuietQuestMain.class.getResource("/quietquest/app/delete-quest-view.fxml"));
        loadLoader(loader, event);
    }

    //
    public void showSelected() {
        // show quest details on the right side:
        currentQuest = questManager.getQuestSelection();

        titleField.setVisible(true);
        descriptionHeader.setVisible(true);
        descriptionField.setVisible(true);
        tasksHeader.setVisible(true);
        taskFieldOne.setVisible(true);
        taskFieldTwo.setVisible(true);
        taskFieldThree.setVisible(true);

        saveButton.setVisible(true);
        editButton.setVisible(true);
        completeButton.setVisible(true);

        // pre-fill quest details:
        titleField.setText(currentQuest.getTitle());
        descriptionField.setText(currentQuest.getDescription());
        taskFieldOne.setText(currentQuest.getTask(0));
        taskFieldTwo.setText(currentQuest.getTask(1));
        taskFieldThree.setText(currentQuest.getTask(2));
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
        currentQuest.setTitle(newTitle);
        titleField.setEditable(false);

        // save description and make non-editable:
        String newDescription = descriptionField.getText();
        descriptionField.setText(newDescription);
        currentQuest.setDescription(newDescription);
        descriptionField.setEditable(false);

        // save tasks and make non-editable:
        String newTaskOne = taskFieldOne.getText();
        taskFieldOne.setText(newTaskOne);
        currentQuest.setTask(0, newTaskOne);
        taskFieldOne.setEditable(false);

        String newTaskTwo = taskFieldTwo.getText();
        taskFieldTwo.setText(newTaskTwo);
        currentQuest.setTask(1, newTaskTwo);
        taskFieldTwo.setEditable(false);

        String newTaskThree = taskFieldThree.getText();
        taskFieldThree.setText(newTaskThree);
        currentQuest.setTask(2, newTaskThree);
        taskFieldThree.setEditable(false);

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
