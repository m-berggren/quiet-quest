package quietquest.controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;
import quietquest.model.*;
import quietquest.utility.MQTTHandler;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


public class QuestController extends BaseController implements UIUpdater, Callback<ListView<Activity>, ListCell<Activity>> {
    @FXML
    private AnchorPane taskAnchorPane;
    @FXML
    private AnchorPane pomodoroAnchorPane;
    @FXML
    private Button startQuestButton;
    @FXML
    private Button completeQuestButton;
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label mqttMotionMessage;
    @FXML
    private Label mqttDistanceMessage;
    @FXML
    private Label mqttConnectionMessage;
    @FXML
    private Label mqttLightMessage;
    @FXML
    private Label motivationalMessage;
    @FXML
    private Label focusLabel;
    @FXML
    private Label breakLabel;
    @FXML
    private Label intervalsLabel;
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Label questTypeLabel;
    private Activity currentActivity;
    private Quest currentQuest;

    private final String PUB_TOPIC_START = "/quietquest/application/start";
    private final String PUB_TOPIC_TASK_DONE = "/quietquest/application/task_done";
    private final String PUB_TOPIC_END = "/quietquest/application/end";
    private String[] message;

    /*
    public void initialize(URL arg0, ResourceBundle arg1) {
        mqttHandler.setUIUpdater(this);
    }
    */

    @Override
    protected void afterMainController() {
        mqttHandler.setUIUpdater(this);

        currentQuest = quietQuestFacade.getQuestSelection();
        titleLabel.setText(currentQuest.getTitle());
        descriptionLabel.setText(currentQuest.getDescription());

        if(currentQuest.getType() == QuestType.TASK){
            taskAnchorPane.setVisible(true);
            pomodoroAnchorPane.setVisible(false);
            questTypeLabel.setText("TASKS");

            ObservableList<Task> tasks = FXCollections.observableArrayList();
            for(Activity activity : currentQuest.getActivities()){
                if(activity instanceof Task){
                    tasks.add((Task) activity);
                }
            }
            taskListView.setItems(tasks);
        } else if(currentQuest.getType() == QuestType.POMODORO){
            taskAnchorPane.setVisible(false);
            pomodoroAnchorPane.setVisible(true);
            questTypeLabel.setText("POMODORO");
        }

        setSelectedTask();
    }

    private void setSelectedTask() {
        taskListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Activity>() {
            @Override
            public void changed(ObservableValue<? extends Activity> observableValue, Activity oldValue, Activity newValue) {
                if (newValue != null) {
                    currentActivity = newValue;
                    System.out.println(currentActivity);
                }
            }
        });

    }

    @Override
    public ListCell<Activity> call(ListView<Activity> param) {
        return new ListCell<>() {
            @Override
            public void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);
                if (empty || activity == null) {
                    setText(null);
                    setGraphic(null);
                    motivationalMessage.setVisible(false);
                } else if (activity instanceof Task) {
                    Task task = (Task) activity;
                    CheckBox checkBox = new CheckBox(task.getTask());
                    checkBox.setSelected(task.isCompleted());
                    checkBox.setOnAction(event -> {
                        task.setCompleted(checkBox.isSelected());
                        if (checkBox.isSelected()) {
                            showMessage();
                        }
                    });
                    setGraphic(checkBox);
                    motivationalMessage.setVisible(false);
                } else if (activity instanceof PomodoroTimer) {
                    PomodoroTimer timer = (PomodoroTimer) activity;
                    setText(timer.toString());
                    setGraphic(null);
                }
            }
        };
    }

    public void showMessage() {
        message = new String[]{"Good Job!", "Amazing!", "One step closer to a nap", "You can do it!", "Wow! Is there anything you can´t do?"};
        Random random = new Random();
        int index = random.nextInt(message.length);
        String setMessage = message[index];
        motivationalMessage.setText(setMessage);
        motivationalMessage.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            motivationalMessage.setVisible(false);
        }));
        timeline.play();
    }

    public void onTickTaskClick(ActionEvent event) {
        String message = "You have completed a task!";
        mqttHandler.publishMessage(PUB_TOPIC_TASK_DONE, message);
    }

    public void onStartQuestClick(ActionEvent event) {
        String message = "Your quest has started.";
        mqttHandler.connect(PUB_TOPIC_START, message); // Connect to MQTT broker & publish
        mqttHandler.subscribe(); // Subscribe

        currentQuest.startActivity(); // Starts quest, mqtt pub & sub
    }

    public void onCompleteQuestClick(ActionEvent event) {
        currentQuest.endActivity(); // Publishes last message
        mqttHandler.publishMessage(PUB_TOPIC_END, "Your quest has ended.");
        mqttHandler.disconnect();

        // Remove or change later:
        mqttConnectionMessage.getStyleClass().clear();
        mqttConnectionMessage.setText("");

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