package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import quietquest.model.*;

import java.util.ArrayList;


public class QuestListController extends BaseController {
	@FXML
	private TextField newTaskText;
	@FXML
	private Button deleteTask;
	@FXML
	private Button addTask;
	@FXML
	private AnchorPane questAnchorPane;
	@FXML
	private Button goToQuestButton;
	@FXML
	private Label titleLabelRight;
	@FXML
	private AnchorPane taskAnchorPane;
	@FXML
	private AnchorPane pomodoroAnchorPane;
	@FXML
	private ListView<Quest> questListView;
	@FXML
	private TextField titleField;
	@FXML
	private TextArea descriptionField;
	@FXML
	private Button saveButton;
	@FXML
	private Button editButton;
	@FXML
	private ListView<Task> taskListView;
	@FXML
	private Label questTypeLabel;
	@FXML
	private Label focusLabel;
	@FXML
	private Label breakLabel;
	@FXML
	private Label intervalsLabel;

	private ArrayList<Quest> quests;
	private Quest selectedQuest;

	@Override
	public void afterMainController() {
		selectedQuest = null;
		quests = quietQuestFacade.getAllQuests();
		displayQuests();
		setSelectedQuest();
	}

	public void onGoToQuestClick() {
		showQuest(selectedQuest);
	}

	/**
	 * Displays the current user's list of created quests in a ListView.
	 */
	public void displayQuests() {
		if (quietQuestFacade != null) {
			//only show quests that are not completed
			ArrayList<Quest> uncompletedQuests = new ArrayList<>();
			for (Quest quest : quests) {
				if (!quest.getCompletionState()) {
					uncompletedQuests.add(quest);
				}
			}

			ObservableList<Quest> questList = FXCollections.observableArrayList(uncompletedQuests);
			questListView.setItems(questList);
			questListView.setCellFactory(param -> new ListCell<Quest>() {
				@Override
				protected void updateItem(Quest item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null || item.getTitle() == null) {
						setText(null);
					} else {
						setText(item.getTitle());
						if (item.getId() == quietQuestFacade.getCurrentQuestId()) {
							setText(item.getTitle() + " (In Progress)");
							setStyle("-fx-font-weight: bold");
						}
					}
				}
			});
		}
	}


	/**
	 * Sets the selectedQuest property to the selected item in a ListView, and calls showSelectedQuest()
	 * to display Quest information in the UI.
	 */
	private void setSelectedQuest() {
		questListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Quest>() {
			@Override
			public void changed(ObservableValue<? extends Quest> observable, Quest oldValue, Quest newValue) {
				if (newValue != null) {
					selectedQuest = newValue;
					showSelectedQuest();
				}
			}
		});

	}

	public void onDeleteQuest(ActionEvent event) {
		if (selectedQuest != null) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Delete Quest");
			alert.setHeaderText("Confirm Deletion");
			alert.setContentText("Are you sure you want to delete this quest?");
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					quietQuestFacade.deleteQuest(selectedQuest);
					clearQuestDetails();
					showQuestList();
				} else {
					alert.close();
				}
			});
		}
	}

	private void clearQuestDetails() {
		titleField.clear();
		descriptionField.clear();
		taskListView.getItems().clear();
	}

	/**
	 * Displays quest information depending on the type of quest that is currently the selectedQuest:
	 * "task" or "pomodoro".
	 */
	public void showSelectedQuest() {
		taskAnchorPane.setVisible(false);
		taskListView.getItems().clear();
		if (selectedQuest != null) {
			titleField.setText(selectedQuest.getTitle());
			descriptionField.setText(selectedQuest.getDetail());
			if (!selectedQuest.getActivities().isEmpty()) {
				questTypeLabel.setText("TASKS");
				pomodoroAnchorPane.setVisible(false);
				taskAnchorPane.setVisible(true);
				// Display tasks in taskListView
				ObservableList<Task> taskList = FXCollections.observableArrayList();
				for (Activity act : selectedQuest.getActivities()) {
					if (act instanceof Task) {
						taskList.add((Task) act);
					}
				}
				taskListView.setItems(taskList);
				taskAnchorPane.setDisable(true);
			} else {
				Activity activity = getActivitiesFromQuest(selectedQuest).getFirst();
				if (activity.getType() == QuestType.TASK) {
					questTypeLabel.setText("TASKS");
					pomodoroAnchorPane.setVisible(false);
					taskAnchorPane.setVisible(true);


					// Display tasks in taskListView
					ObservableList<Task> taskList = FXCollections.observableArrayList();
					for (Activity act : selectedQuest.getActivities()) {
						if (act instanceof Task) {
							taskList.add((Task) act);
						}
					}
					taskListView.setItems(taskList);
					taskAnchorPane.setDisable(true);

				} else if (activity.getType() == QuestType.POMODORO) {
					questTypeLabel.setText("POMODORO");
					taskAnchorPane.setVisible(false);
					pomodoroAnchorPane.setVisible(true);

					PomodoroTimer pomodoro = (PomodoroTimer) activity;
					focusLabel.setText("Focus time: " + pomodoro.getFocusTime());
					breakLabel.setText("Break time: " + pomodoro.getBreakTime());
					intervalsLabel.setText("Intervals: " + pomodoro.getInterval());

				}
			}
		}
	}

	/**
	 * Makes the quest title and description editable.
	 */
	public void onEditClick() {
		saveButton.setDisable(false);
		editButton.setDisable(true);
		titleField.setEditable(true);
		descriptionField.setEditable(true);
		questListView.setDisable(true);
		taskAnchorPane.setVisible(true);
		taskAnchorPane.setDisable(false);
		taskListView.setDisable(false);


	}

	public void onSaveButtonClick() {
		String newTitle = titleField.getText();
		titleField.setText(newTitle);
		selectedQuest.setTitle(newTitle);

		String newDescription = descriptionField.getText();
		descriptionField.setText(newDescription);
		selectedQuest.setDetail(newDescription);

		editButton.setDisable(false);
		saveButton.setDisable(true);
		titleField.setEditable(false);
		descriptionField.setEditable(false);
		questListView.setDisable(false);


		Quest newQuest = new Quest(quietQuestFacade.getUser(), newTitle, newDescription);
		quietQuestFacade.updateQuest(selectedQuest, newQuest);
		showSelectedQuest();
		questListView.refresh();
	}

	public void onAddTask() {
		String newTaskDescription = newTaskText.getText();
		if (selectedQuest != null && !newTaskDescription.isEmpty()) {
			Task newTask = new Task(newTaskDescription); // Generate a unique ID for the task
			selectedQuest.getActivities().add(newTask);
			quietQuestFacade.createTask(selectedQuest, newTask);
			newTaskText.clear();
			showSelectedQuest();

		}
	}

	public void onDeleteTask() {
		Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
		if (selectedTask != null && selectedQuest != null) {
			selectedQuest.getActivities().remove(selectedTask);
			quietQuestFacade.deleteTask(selectedTask);
			taskListView.getItems().remove(selectedTask);
			showSelectedQuest();
		}
	}
}
