package quietquest.controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;
import quietquest.model.*;
import javafx.scene.control.ListView;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Random;


public class QuestController extends BaseController implements UIUpdater, Callback<ListView<Activity>, ListCell<Activity>> {
    @FXML
    private AnchorPane taskAnchorPane;
    @FXML
    private AnchorPane pomodoroAnchorPane;
    @FXML
    private AnchorPane motivationalAnchorPane;
    @FXML
    private Button startQuestButton;
    @FXML
    private Button completeQuestButton;
    @FXML
    private ListView<Activity> activityListView;
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
    private boolean isQuestRunning = false;
    private ObservableList<Activity> activities;

    public void initialize(Quest quest) {
        currentQuest = quest;
    }

    @Override
    protected void afterMainController() {
        activityListView.setCellFactory(this);
        activities = FXCollections.observableArrayList(getActivitiesFromQuest(currentQuest));
        activityListView.setItems(activities);

        quietQuestFacade.setUIUpdater(this);
        titleLabel.setText(currentQuest.getTitle());
        descriptionLabel.setText(currentQuest.getDetail());
        startTimeLabel.setText("Start: " + currentQuest.getStartTime());
        endTimeLabel.setText("End: " + currentQuest.getCompleteTime());
        motivationalAnchorPane.setVisible(false);

        /*if(currentQuest.getType() == QuestType.TASK){
            taskAnchorPane.setVisible(true);
            pomodoroAnchorPane.setVisible(false);
            questTypeLabel.setText("TASKS");
        } else if(currentQuest.getType() == QuestType.POMODORO){
            taskAnchorPane.setVisible(false);
            pomodoroAnchorPane.setVisible(true);
            questTypeLabel.setText("POMODORO");
        }*/
        setSelectedTask();
    }

    private void setSelectedTask() {
        activityListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Activity>() {
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
                    motivationalAnchorPane.setVisible(false);
                } else if (activity instanceof Task task) {
                    CheckBox checkBox = new CheckBox(task.getDescription());
                    checkBox.setSelected(task.getCompletionState());
                    checkBox.setOnAction(event -> {
                        if (checkBox.isSelected() && isQuestRunning) {
                            showMessage();
                            String message = "You have completed a task!";

                            task.setEndTime(Timestamp.from(Instant.now()));
                            task.setCompletionState(true);
                            quietQuestFacade.updateTaskEndTimeInDb(task);
                            quietQuestFacade.updateTaskCompletionStateInDb(task);

                            quietQuestFacade.publishMqttMessage(PUB_TOPIC_TASK_DONE, message);
                        }
                    });
                    setGraphic(checkBox);
                    motivationalAnchorPane.setVisible(false);
                } else if (activity instanceof PomodoroTimer) {
                    PomodoroTimer timer = (PomodoroTimer) activity;
                    setText(timer.toString());
                    setGraphic(null);
                }
            }
        };
    }

    public void showMessage() {
        message = new String[]{"Good Job!", "Amazing!", "One step closer to a nap", "You can do it!",
                "Wow! Is there anything you can´t do?"};
        Random random = new Random();
        int index = random.nextInt(message.length);
        String setMessage = message[index];
        motivationalMessage.setText(setMessage);
        motivationalAnchorPane.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            motivationalAnchorPane.setVisible(false);
        }));
        timeline.play();
    }

    public void onStartQuestClick(ActionEvent event) {
        isQuestRunning = true;
        startTimeLabel.setText("Start: " + currentQuest.getStartTime());

        quietQuestFacade.startQuest(currentQuest);
        quietQuestFacade.connectMqtt(PUB_TOPIC_START, "Your quest has started.");
        quietQuestFacade.subscribeMqtt();

        startQuestButton.setDisable(true);
    }

    public void onCompleteQuestClick(ActionEvent event) throws SQLException {
        isQuestRunning = false;
        endTimeLabel.setText("End: " + currentQuest.getCompleteTime());

        quietQuestFacade.completeQuest(currentQuest);
        quietQuestFacade.publishMqttMessage(PUB_TOPIC_END, "Your quest has ended.");
        quietQuestFacade.disconnectMqtt();

        // Remove or change later:
        mqttConnectionMessage.getStyleClass().clear();
        mqttConnectionMessage.setText("");
        completeQuestButton.setDisable(true);
        showMessage();
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