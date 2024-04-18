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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
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
    @FXML
    private Pane warningPane;
    @FXML
    private Rectangle warningTextbox;
    @FXML
    private Button okayButton;
    @FXML
    private Button keepEditingButton;
    @FXML
    private Text warningText;
    @FXML
    private Text warningSmallText;
    @FXML
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
        currentQuest = questManager.getQuestSelection();

        // show quest details on the right side:
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

        // if fields are actively being edited, warning pop-up appears:
        if (titleField.isEditable()) {
            showWarning("Your changes will not be saved", "Are you sure you want to proceed?");
            setSelectedUneditable();
        } else {
            setSelectedUneditable();
            doNotShowWarning();
            // pre-fill quest details:
            titleField.setText(currentQuest.getTitle());
            descriptionField.setText(currentQuest.getDescription());
            taskFieldOne.setText(currentQuest.getTask(0));
            taskFieldTwo.setText(currentQuest.getTask(1));
            taskFieldThree.setText(currentQuest.getTask(2));
        }
    }

    // warning message pop-up:
    public void showWarning(String message, String smallMessage){
        warningPane.setDisable(false);
        warningTextbox.setVisible(true);
        okayButton.setVisible(true);
        keepEditingButton.setVisible(true);
        warningText.setVisible(true);
        warningText.setText(message);
        warningSmallText.setVisible(true);
        warningSmallText.setText(smallMessage);
        // if warning/error pop-up message is on screen, other quests become non-clickable:
        questListView.setDisable(true);
    }

    // discard edits and show new quest selection when by clicking okayButton:
    public void onOkayButtonClick() {
        doNotShowWarning();
        showSelected();
    }

    // stay on same quest in editing mode by clicking keepEditingButton:
    public void onKeepEditingClick() {
        doNotShowWarning();
        setEditable();
    }

    public void doNotShowWarning() {
        warningPane.setDisable(true);
        warningTextbox.setVisible(false);
        okayButton.setVisible(false);
        keepEditingButton.setVisible(false);
        warningText.setVisible(false);
        warningSmallText.setVisible(false);
        // quest list is clickable:
        questListView.setDisable(false);
    }

    // edit selected quest by clicking editButton:
    public void setEditable(){
        titleField.setEditable(true);
        descriptionField.setEditable(true);
        taskFieldOne.setEditable(true);
        taskFieldTwo.setEditable(true);
        taskFieldThree.setEditable(true);
    }

    // quest fields are non-editable if editButton is not explicitly clicked:
    public void setSelectedUneditable() {
        titleField.setEditable(false);
        descriptionField.setEditable(false);
        taskFieldOne.setEditable(false);
        taskFieldTwo.setEditable(false);
        taskFieldThree.setEditable(false);
    }

    // save quest/task details by clicking saveButton:
    public void onSaveButtonClick() {
        // save title:
        String newTitle = titleField.getText();
        titleField.setText(newTitle);
        currentQuest.setTitle(newTitle);

        // save description:
        String newDescription = descriptionField.getText();
        descriptionField.setText(newDescription);
        currentQuest.setDescription(newDescription);

        // save tasks:
        String newTaskOne = taskFieldOne.getText();
        taskFieldOne.setText(newTaskOne);
        currentQuest.setTask(0, newTaskOne);

        String newTaskTwo = taskFieldTwo.getText();
        taskFieldTwo.setText(newTaskTwo);
        currentQuest.setTask(1, newTaskTwo);

        String newTaskThree = taskFieldThree.getText();
        taskFieldThree.setText(newTaskThree);
        currentQuest.setTask(2, newTaskThree);

        // make fields non-editable:
        setSelectedUneditable();
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
