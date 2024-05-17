package quietquest.controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import quietquest.model.*;
import javafx.scene.control.ListView;
import quietquest.model.PomodoroUIUpdater;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

import static quietquest.utility.MQTTTopics.*;


public class QuestController extends BaseController implements UIUpdater, PomodoroUIUpdater, Callback<ListView<Activity>, ListCell<Activity>> {
	@FXML
	private AnchorPane taskAnchorPane;
	@FXML
	private AnchorPane pomodoroInfoAnchorPane;
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
	private Label pomodoroStatusLabel;
	@FXML
	private Label startTimeLabel;
	@FXML
	private Label endTimeLabel;
	@FXML
	private Label questTypeLabel;
	private Activity currentActivity;
	private Quest currentQuest;

    private String[] message;
    private ObservableList<Activity> activities;
	private static QuestController activeController;
	private PomodoroTimer pomodoroTimer;

	public void initialize(Quest quest) {
		currentQuest = quest;
		System.out.println(quest.hashCode());

		/* This section retrieves the PomodoroTimer from the selected Quest, if it has one, and adds the implementation
		 * of Update to a list in that class. Every time the pomodoro then changes state it will update every observer
		 * present in that list. */
		if (currentQuest.getActivities() != null && !currentQuest.getActivities().isEmpty()) {
			Activity activity = currentQuest.getActivities().getFirst();
			if (activity instanceof PomodoroTimer) {
				pomodoroTimer = (PomodoroTimer) activity;
				pomodoroTimer.addObserver(this);
			}
		}
		// Add listener to handle view closure properly on a JavaFX thread
		Platform.runLater(() -> {
			Stage stage = (Stage) titleLabel.getScene().getWindow(); // Could be any FXML object defined here
			stage.setOnHidden(event -> onDestroy());
		});

		/* Sets this instance as THE controller. It is a static object so there only exists one. Every time it updates
		 * to be the latest QuestController opened. */
		setActiveController(this);
	}

	/**
	 * Synchronized method to set the activeController. The param object will be the next updated static object. This
	 * if important because the {@link #update(String) } checks the activeController and only allow that controller to
	 * publish and update any data. Without this solution all observers would publish and update information.
	 * <p>
	 * Synchronized in the method means that only one thread at a time can execute this block of code. This addition
	 * was added to handle errors with multiple observers that may use concurrent calls in the QuestController to
	 * prevent thread interference and consistency problems.
	 *
	 * @param controller is this QuestController.
	 */
	private static synchronized void setActiveController(QuestController controller) {
		activeController = controller;
	}

	/**
	 * Called when the view is destroyed. Removes this controller as an observer from the PomodoroTimer.
	 */
	@FXML
	public void onDestroy() {
		if (pomodoroTimer != null) {
			pomodoroTimer.removeObserver(this);
		}
	}

	@Override
	protected void afterMainController() {
		activityListView.setCellFactory(this);
		activities = FXCollections.observableArrayList(currentQuest.getActivities());
		activityListView.setItems(activities);

		quietQuestFacade.setUIUpdater(this);
		titleLabel.setText(currentQuest.getTitle());
		descriptionLabel.setText(currentQuest.getDetail());
		startTimeLabel.setText("Start: " + formatTime(currentQuest.getStartTime()));
		endTimeLabel.setText("End: " + formatTime(currentQuest.getCompleteTime()));
		motivationalAnchorPane.setVisible(false);

		// Set visibility and type labels based on the first activity type in the list
		if (currentQuest.getActivities().isEmpty()) {
			taskAnchorPane.setVisible(true);
			pomodoroInfoAnchorPane.setVisible(false);
			questTypeLabel.setText("TASKS");
		} else {
			if (currentQuest.getActivities().getFirst().getType() == QuestType.TASK) {
				taskAnchorPane.setVisible(true);
				pomodoroInfoAnchorPane.setVisible(false);
				questTypeLabel.setText("TASKS");
			} else if (currentQuest.getActivities().getFirst().getType() == QuestType.POMODORO) {
				taskAnchorPane.setVisible(false);
				pomodoroInfoAnchorPane.setVisible(true);
				questTypeLabel.setText("POMODORO");
			}
		}
		// Handle button states based on the current quest's state
		if (currentQuest.getId() != quietQuestFacade.getCurrentRunningQuestId() && quietQuestFacade.getCurrentRunningQuestId() != -1) {
			startQuestButton.setDisable(true);
			completeQuestButton.setDisable(true);
			activityListView.setDisable(true);
		} else {
			if (currentQuest.getStartTime() == null) {
				completeQuestButton.setDisable(true);
				activityListView.setDisable(true);
			} else {
				startQuestButton.setDisable(true);
			}
		}

		setSelectedTask();
	}

	/**
	 * Sets the selected task in the list view.
	 */
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

	/**
	 * Callback method to create a new ListCell for the ListView.
	 * @param param The single argument upon which the returned value should be
	 *      determined.
	 * @return The new ListCell.
	 */
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
					//if a checkbox is checker, then it cannot be unchecked
					checkBox.setDisable(task.getCompletionState());
					checkBox.setOnAction(event -> {
						if (checkBox.isSelected()) {
							showMessage();
							String message = "You have completed a task!";

							task.setEndTime(Timestamp.from(Instant.now()));
							task.setCompletionState(true);
							//Make checkbox unable to uncheck after user check it
							checkBox.setDisable(task.getCompletionState());
							quietQuestFacade.updateTaskEndTimeInDb(task);
							quietQuestFacade.updateTaskCompletionStateInDb(task);

							quietQuestFacade.publishMqttMessage(TOPIC_PUB_TASK_DONE, message);
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

	/**
	 * Overrides the update method in {@link PomodoroUIUpdater}.
	 * Used to update the UI through observer pattern.
	 *
	 * @param message the string to display.
	 */
	@Override
	public void update(String message) {
		if (this != activeController) {
			return; // Only one QuestController will handle publishing to the Wio terminal
		}

		final String FOCUS_TIME = "Focus time started";
		final String BREAK_TIME_START = "Break time started";
		final String BREAK_TIME_END = "Break time ended";
		final String POMODORO_END = "Pomodoro timer finished";

		// Need Platform.runLater() as a separate thread when Timer is already running
		Platform.runLater(() -> {
			switch (message) {
				case FOCUS_TIME -> {
					System.out.println(FOCUS_TIME);

                    pomodoroInfoAnchorPane.setVisible(true);
                    pomodoroStatusLabel.setText("Focus time now active");
                    pomodoroStatusLabel.setTextFill(Color.color(0.6, 0, 0));

					quietQuestFacade.publishMqttMessage(TOPIC_PUB_POMODORO_INTERVAL, FOCUS_TIME);
				}
				case BREAK_TIME_START -> {
					System.out.println(BREAK_TIME_START);

					pomodoroStatusLabel.setText("Break time now active");
					pomodoroStatusLabel.setTextFill(Color.color(0, 0.50, 0));

					quietQuestFacade.publishMqttMessage(TOPIC_PUB_POMODORO_INTERVAL, BREAK_TIME_START);
				}
				case BREAK_TIME_END -> {
					System.out.println(BREAK_TIME_END);
				}
				case POMODORO_END -> {
					System.out.println(POMODORO_END);

                    pomodoroInfoAnchorPane.setVisible(false);

					onCompletion();
				}
			}
		});
	}

	/**
	 * Shows a motivational message for 3 seconds.
	 */
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

	/**
	 * Starts the quest and sets the start time.
	 * @param event The event that triggered the method.
	 */
    public void onStartQuestClick(ActionEvent event) {
		currentQuest.setStartTime(Timestamp.from(Instant.now()));
        startTimeLabel.setText("Start: " + currentQuest.getStartTime());

		// Connects to MQTT as soon as page loads
		System.out.println("Starts quest");

		quietQuestFacade.startQuest(currentQuest, this);
		quietQuestFacade.publishMqttMessage(TOPIC_PUB_QUEST_START, "Your quest has started");
		quietQuestFacade.subscribeMqtt();

		startTimeLabel.setText("Start: " + formatTime(currentQuest.getStartTime()));

        startQuestButton.setDisable(true);
        completeQuestButton.setDisable(false);
		activityListView.setDisable(false);
    }

	/**
	 * Completes the quest and sets the end time.
	 * @param event The event that triggered the method.
	 * @throws SQLException If an SQL exception occurs.
	 */
    public void onCompleteQuestClick(ActionEvent event) throws SQLException {
        onCompletion();
        showMessage();
    }

    private void onCompletion() {
		//if not all checkbox is checked, then the quest cannot be completed
		for (Activity activity : currentQuest.getActivities()) {
			if (activity instanceof Task task) {
				if (!task.getCompletionState()) {
					String reminder = "You have not completed all tasks. Please complete all tasks before ending the quest.";
					Platform.runLater(() -> {
						showReminder(reminder);
					});
					return;
				}
			}
		}
		currentQuest.setCompleteTime(Timestamp.from(Instant.now()));
		endTimeLabel.setText("End: " + formatTime(currentQuest.getCompleteTime()));

		quietQuestFacade.completeQuest(currentQuest);
		quietQuestFacade.publishMqttMessage(TOPIC_PUB_QUEST_END, "Your quest has ended");
		quietQuestFacade.unsubscribeMqtt();

		mqttConnectionMessage.getStyleClass().clear();
		mqttConnectionMessage.setText("");
		mqttDistanceMessage.setVisible(false);
		mqttMotionMessage.setVisible(false);
		mqttLightMessage.setVisible(false);
		completeQuestButton.setDisable(true);
	}

	private void showReminder(String s) {
		//use a timeline to show the reminder for 3 seconds
		motivationalMessage.setText(s);
		motivationalAnchorPane.setVisible(true);
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
			motivationalAnchorPane.setVisible(false);
		}));
		timeline.play();
	}

	/**
	 * Updates the UI with the connection status.
	 * @param connectionStatus The connection status.
	 */
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

	/**
	 * Updates the UI with the light sensor value.
	 * @param lightValue The light sensor value 0-100.
	 */
	@Override
	public void updateLightSensorUI(int lightValue) {
		mqttLightMessage.setText("Light value: " + lightValue);
		mqttLightMessage.getStyleClass().clear();

		if (lightValue > 5) {
			quietQuestFacade.saveBoxOpenTimes(currentQuest);
		}
		if (lightValue > 50) {
			mqttLightMessage.getStyleClass().add("label-all-red");
		} else if (lightValue > 30) {
			mqttLightMessage.getStyleClass().add("label-all-yellow");
		} else {
			mqttLightMessage.getStyleClass().add("label-all-green");
		}
	}

	/**
	 * Updates the UI with the motion sensor value.
	 * @param motionDetected The motion sensor value.
	 */
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

	/**
	 * Updates the UI with the ultrasonic sensor value.
	 * @param distanceValue The ultrasonic sensor value.
	 */
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