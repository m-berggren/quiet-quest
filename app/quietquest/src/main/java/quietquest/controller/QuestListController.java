package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import quietquest.model.Activity;
import quietquest.model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class QuestListController extends BaseController {
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
    private ListView<String> taskListView;
    @FXML
    private Label questTypeLabel;
    @FXML
    private Label focusLabel;
    @FXML
    private Label breakLabel;
    @FXML
    private Label intervalsLabel;

    private HashMap<String, Quest> quests;
    private Quest selectedQuest;

    @Override
    public void afterMainController() throws SQLException {
        selectedQuest = null;
        quests = quietQuestFacade.getQuests();
        displayQuests();
        setSelectedQuest();
    }

    public void onGoToQuestClick(ActionEvent event) throws IOException {
        showQuest(selectedQuest);
    }

    /**
     * Displays the current user's list of created quests in a ListView.
     */
    public void displayQuests() throws SQLException {
        if (quietQuestFacade != null) {
            database.connect();
            ArrayList<Quest> questsList = database.getAllQuests(user);
            database.disconnect();

            //quests = quietQuestFacade.getQuests();
            ObservableList<Quest> questList = FXCollections.observableArrayList(questsList);
            questListView.setItems(questList);
            questListView.setCellFactory(param -> new ListCell<Quest>() {
                @Override
                protected void updateItem(Quest item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getTitle() == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
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
                    quietQuestFacade.setQuestSelection(selectedQuest);
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
                    quietQuestFacade.deleteQuest(selectedQuest.getTitle());
                    quietQuestFacade.resetQuestSelection();
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
        if (selectedQuest != null) {
            titleField.setText(selectedQuest.getTitle());
            descriptionField.setText(selectedQuest.getDescription());
            if(selectedQuest.getType() == QuestType.TASK){
                questTypeLabel.setText("TASKS");
                pomodoroAnchorPane.setVisible(false);
                taskAnchorPane.setVisible(true);

                ObservableList<String> tasks = FXCollections.observableArrayList();
                for(Activity activity : selectedQuest.getActivities()){
                    if(activity instanceof Task){
                        tasks.add(((Task) activity).toString());
                    }
                }
                taskListView.setItems(tasks);
            } else if (selectedQuest.getType() == QuestType.POMODORO){
                questTypeLabel.setText("POMODORO");
                taskAnchorPane.setVisible(false);
                pomodoroAnchorPane.setVisible(true);
                PomodoroTimer pomodoro = (PomodoroTimer) selectedQuest.getActivities().getFirst();
                focusLabel.setText("Focus time: " + pomodoro.getFocusTime());
                breakLabel.setText("Break time: " + pomodoro.getBreakTime());
                intervalsLabel.setText("Intervals: " + pomodoro.getIntervals());
            }
        } else {
            System.out.println("selectedQuest is null");
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
    }

    public void onSaveButtonClick() {
        String newTitle = titleField.getText();
        titleField.setText(newTitle);
        selectedQuest.setTitle(newTitle);

        String newDescription = descriptionField.getText();
        descriptionField.setText(newDescription);
        selectedQuest.setDescription(newDescription);

        editButton.setDisable(false);
        saveButton.setDisable(true);
        titleField.setEditable(false);
        descriptionField.setEditable(false);
        questListView.setDisable(false);
    }
}
