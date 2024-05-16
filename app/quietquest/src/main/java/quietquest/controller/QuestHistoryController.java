package quietquest.controller;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import quietquest.model.Activity;
import quietquest.model.Quest;
import quietquest.model.Task;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class QuestHistoryController extends BaseController {
	@FXML
	public ListView<Quest> quesHistoryListView;
	@FXML
	public Text titleHeader;
	@FXML
	private AnchorPane questlistAnchorPane;
	@FXML
	private Label titleLabel;

	@FXML
	private Button goToStatisticsButton;
	@FXML
	private AnchorPane viewInfoAnchorPane;
	@FXML
	private TextField titleField;
	@FXML
	private Text descriptionHeader;
	@FXML
	private TextArea descriptionField;
	@FXML
	private Text tasksHeader;
	@FXML
	private ListView<Activity> activityListView;

	private ArrayList<Quest> questArrayList;

	@Override
	public void afterMainController() throws SQLException {
		questArrayList = getAllQuests();
		displayQuests();
	}

	/**
	 * Changes the difference of two Timestamps into a string of HH:mm:ss.
	 *
	 * @param quest Quest object to get time data from.
	 * @return String of HH:mm:ss.
	 */
	private String getTimeDiffHMS(Quest quest) {
		Timestamp startTime = quest.getStartTime();
		Timestamp endTime = quest.getCompleteTime();

		LocalDateTime startDateTime = startTime.toLocalDateTime();
		LocalDateTime endDateTime = endTime.toLocalDateTime();

		Duration duration = Duration.between(startDateTime, endDateTime);
		long hours = duration.toHours();
		long minutes = duration.toMinutes() % 60;
		long seconds = duration.toSeconds() % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public void displayQuests() {

		//Get a list of quest with quest completionStatus as true
		ArrayList<Quest> completedQuestList = new ArrayList<>();
		for (Quest quest : questArrayList) {
			if (quest.getCompletionState()) {
				completedQuestList.add(quest);
			}
		}
		if (completedQuestList.isEmpty()) {
			titleLabel.setText("No completed quests found.");
		}
		//sort completedQuestList by completion time most recent first
		completedQuestList.sort((q1, q2) -> q2.getCompleteTime().compareTo(q1.getCompleteTime()));
		ObservableList<Quest> questItems = FXCollections.observableArrayList(completedQuestList);

		// Set the items on the questHistoryListView
		quesHistoryListView.setItems(questItems);

		// Set the cell factory to display the titles of quests
		quesHistoryListView.setCellFactory(lv -> new ListCell<Quest>() {
			@Override
			protected void updateItem(Quest quest, boolean empty) {
				super.updateItem(quest, empty);
				if (empty || quest == null || quest.getStartTime() == null || quest.getCompleteTime() == null) {
					setText(null);
					setGraphic(null);
				} else {
					// Creating a custom layout for the list cell
					VBox vBox = new VBox(5); // Vertical box with spacing
					Label titleLabel = new Label(quest.getTitle());
					titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
					Label startTimeLabel = new Label("    Start Time: " + formatTime(quest.getStartTime()));
					Label completionTimeLabel = new Label("    Completed Time: " + formatTime(quest.getCompleteTime()));
					//Show time spent calculated from the difference between the timestamp start time and completion time in minutes

					String timeSpent = getTimeDiffHMS(quest);
					Label timeSpentLabel = new Label("    Time Spent: " + timeSpent);
					Label boxOpenTimesLabel = new Label("    Box Open Times: " + quest.getBoxOpenTimes());

					String averageBoxOpenInterval = quest.getBoxOpenTimes() == 0 ? "00:00:00" : getTimeDiffHMS(quest);
					Label averageBoxOpenIntervalLabel = new Label("    Average Box Open Interval: " + averageBoxOpenInterval);

					vBox.getChildren().addAll(titleLabel, startTimeLabel, completionTimeLabel, timeSpentLabel, boxOpenTimesLabel, averageBoxOpenIntervalLabel);
					setGraphic(vBox);
				}
			}
		});


		// Setup listener for item selection to update quest details
		quesHistoryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Quest>() {
			@Override
			public void changed(ObservableValue<? extends Quest> observable, Quest oldValue, Quest newValue) {
				if (newValue != null) {
					updateQuestDetails(newValue);
				}
			}
		});
	}

	/**
	 * Handles the event when the statistics button is clicked.
	 */
	public void onStatisticsClick() {
		goToStatisticsButton.setOnAction(event -> {
			showStatistics();
		});
	}

	/**
	 * Updates the quest details in the view.
	 * @param quest is the selected Quest object in quest list view.
	 */
	private void updateQuestDetails(Quest quest) {
		titleField.setEditable(false);
		titleField.setText(quest.getTitle());
		descriptionField.setEditable(false);
		descriptionField.setText(quest.getDetail());
		if (activityListView == null) {
			System.out.println("activityListView is null");
		} else {
			setupActivityListView(quest);
		}
	}

	/**
	 * Sets up the activity list view with the activities from the selected quest.
	 * @param quest is the selected Quest object in quest list view.
	 */
	private void setupActivityListView(Quest quest) {
		ObservableList<Activity> taskItems = FXCollections.observableArrayList(quest.getActivities());
		activityListView.setItems(taskItems);
		activityListView.setCellFactory(lv -> new ListCell<Activity>() {
			@Override
			protected void updateItem(Activity activity, boolean empty) {
				super.updateItem(activity, empty);
				if (empty || activity == null) {
					setText(null);
				} else {
					setText(activity instanceof Task ? ((Task) activity).getDescription() : "No description available");
				}
			}
		});
	}
}

