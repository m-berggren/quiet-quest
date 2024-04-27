package quietquest.controller;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import quietquest.QuietQuestMain;
import quietquest.model.Quest;
import quietquest.model.QuestManager;
import quietquest.utility.MQTTHandler;


import java.net.URL;
import java.util.ResourceBundle;


public class QuestController extends BaseController implements Initializable, UIUpdater {
    @FXML
    private Button startQuestButton;
    @FXML
    private Button tickTaskButton;
    @FXML
    private Button  completeQuestButton;
    @FXML
    private ListView tasksListView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descLabel;

    @FXML
    private ToggleButton subscribeButton;
    @FXML
    private Label mqttMotionMessage;
    @FXML
    private Label mqttDistanceMessage;
    @FXML
    private Label mqttConnectionMessage;
    @FXML
    private Label mqttLightMessage;

    private MQTTHandler mqttClient;

    private String selectedTask;

    public void initialize(URL arg0, ResourceBundle arg1) {
        mqttClient = new MQTTHandler(this);
    }


    @Override
    protected void afterMainController() {
        Quest quest = quietQuestFacade.getQuestManager().getQuestSelection();
        titleLabel.setText(quest.getTitle());
        descLabel.setText(quest.getDescription());
        tasksListView.getItems().addAll(quest.getTasks());
        setSelectedTask();
    }

    private void setSelectedTask(){
        tasksListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                selectedTask = (String)tasksListView.getSelectionModel().getSelectedItem();
            }
        });
    }


    public void onTickTaskClick(ActionEvent event) {
        String message = "You have completed a task!";
        mqttClient.publishMessage("/quietquest/application/start", message);
    }


    public void onStartQuestClick(ActionEvent event) {
        String message = "Your quest has started";
        mqttClient.publishMessage("/quietquest/application/start", message);
    }

    public void onCompleteQuestClick(ActionEvent event) {
        String message = "You have completed your quest!";
        mqttClient.publishMessage("/quietquest/application/start", message);
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


    @Override
    public void updateConnectionStatusUI(boolean connectionStatus) {
        if (connectionStatus) {
            mqttConnectionMessage.setText("Connected.");
            mqttConnectionMessage.getStyleClass().clear();
            mqttConnectionMessage.getStyleClass().add("label-all-green");
        } else {
            mqttConnectionMessage.setText("Not connected.");
            mqttConnectionMessage.getStyleClass().clear();
            mqttConnectionMessage.getStyleClass().add("label-all-red");
        }
    }
    @Override
    public void updateLightSensorUI(int lightValue) {
        mqttLightMessage.setText("Light value: " + lightValue);
        mqttLightMessage.getStyleClass().clear();

        if (lightValue > 50) {
            mqttLightMessage.getStyleClass().add("label-all-red");
        } else if (lightValue > 30) {
            mqttLightMessage.getStyleClass().add("label-all-yellow");
        } else {
            mqttLightMessage.getStyleClass().add("label-all-green");
        }
    }

    @Override
    public void updateMotionSensorUI(boolean motionDetected) {
        if (motionDetected) {
            mqttMotionMessage.setText("Motion detected.");
            mqttMotionMessage.getStyleClass().clear();
            mqttMotionMessage.getStyleClass().add("label-all-red");
        } else {
            mqttMotionMessage.setText("Motion not detected.");
            mqttMotionMessage.getStyleClass().clear();
            mqttMotionMessage.getStyleClass().add("label-all-green");
        }
    }

    @Override
    public void updateUltrasonicSensorUI(int distanceValue) {
        mqttDistanceMessage.setText("Distance to obstacle: " + distanceValue + " cm.");
        mqttDistanceMessage.getStyleClass().clear();

        if (distanceValue > 100) {
            mqttDistanceMessage.getStyleClass().add("label-all-green");
        } else if (distanceValue > 50) {
            mqttDistanceMessage.getStyleClass().add("label-all-yellow");
        } else {
            mqttDistanceMessage.getStyleClass().add("label-all-red");
        }
    }
}
