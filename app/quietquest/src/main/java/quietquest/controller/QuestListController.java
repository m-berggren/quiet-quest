package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import quietquest.model.Quest;
import quietquest.model.Task;
import quietquest.utility.FxmlFile;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class QuestListController extends BaseController implements Initializable {
    @FXML
    private ListView<Quest> questListView;
    @FXML
    private TextField titleField;
    @FXML
    private Text descriptionHeader;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Text tasksHeader;
    @FXML
    private Button saveButton;
    @FXML
    private Button editButton;
    @FXML
    private Button completeButton;
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
    private FxmlFile view;
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private TextField taskField;
    @FXML
    private Button addNewTaskButton;
    @FXML
    private Button deleteTaskButton;
    private HashMap<String, Quest> quests;
    private Quest currentQuest;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        displayQuests();
    }

    @Override
    public void afterMainController() {
        quests = quietQuestFacade.getQuests();
        displayQuests();
        setSelectedQuest();
    }

    public void onGoToQuestClick(ActionEvent event) throws IOException {
        showQuest(currentQuest);
    }

    public void displayQuests() {
        if (quietQuestFacade != null) {
            quests = quietQuestFacade.getQuests();
            ObservableList<Quest> questList = FXCollections.observableArrayList(quests.values());
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

    private void setSelectedQuest() {
        questListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Quest>() {
            @Override
            public void changed(ObservableValue<? extends Quest> observable, Quest oldValue, Quest newValue) {
                if (newValue != null) {
                    currentQuest = newValue;
                    quietQuestFacade.setQuestSelection(currentQuest);
                    showSelected();
                }
            }
        });

    }

    public void onDeleteQuest(ActionEvent event) {
        if (currentQuest != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Quest");
            alert.setHeaderText("Confirm Deletion");
            alert.setContentText("Are you sure you want to delete this quest?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    quietQuestFacade.deleteQuest(currentQuest.getTitle());
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

        // Hide or disable UI components as necessary
        titleField.setVisible(false);
        descriptionHeader.setVisible(false);
        descriptionField.setVisible(false);
        tasksHeader.setVisible(false);
        taskListView.setVisible(false);
        saveButton.setVisible(false);
        editButton.setVisible(false);
        completeButton.setVisible(false);
    }

    //
    public void showSelected() {
        if (currentQuest != null) {

            // show quest details on the right side:
            // title details:
            titleField.setVisible(true);
            // description details:
            descriptionHeader.setVisible(true);
            descriptionField.setVisible(true);
            // task details:
            tasksHeader.setVisible(true);
            addNewTaskButton.setVisible(true);
            deleteTaskButton.setVisible(true);
            // quest edit/save/complete buttons:
            saveButton.setVisible(true);
            editButton.setVisible(true);
            completeButton.setVisible(true);
            // set fields uneditable:
            setSelectedUneditable();
            //    doNotShowWarning();
            // pre-fill quest details:
            titleField.setText(currentQuest.getTitle());
            descriptionField.setText(currentQuest.getDescription());
        } else {//error handling
        }
        // }
    }

    // Edit selected quest by clicking editButton:
    public void setEditable() {
        // Set all quest details editable and enable related buttons:
        titleField.setEditable(true);
        descriptionField.setEditable(true);
        taskListView.setDisable(false);
        taskListView.setEditable(true);
        taskField.setEditable(true);
        addNewTaskButton.setDisable(false);
        deleteTaskButton.setDisable(false);
        saveButton.setDisable(false);
        editButton.setDisable(false);
        completeButton.setDisable(false);
        // Set quest list view disabled so that other quest cannot be selected:
        questListView.setDisable(true);
    }

    // Quest detail fields are non-editable if editButton is not explicitly clicked:
    public void setSelectedUneditable() {
        titleField.setEditable(false); // title field uneditable
        descriptionField.setEditable(false); // description field uneditable
        taskListView.setDisable(true); // task list view is un-clickable
        taskField.setEditable(false); // new task field uneditable
        deleteTaskButton.setDisable(true); // delete task button disabled
        addNewTaskButton.setDisable(true); // add task button disabled
        questListView.setDisable(false); // quest list view is clickable
    }

    // Save quest/task details by clicking saveButton:
    public void onSaveButtonClick() {
        // save title:
        String newTitle = titleField.getText();
        titleField.setText(newTitle);
        currentQuest.setTitle(newTitle);

        // Save description:
        String newDescription = descriptionField.getText();
        descriptionField.setText(newDescription);
        currentQuest.setDescription(newDescription);

        // Save tasks:

        //currentQuest.setTasks(tasks);

        // Make fields non-editable:
        setSelectedUneditable();
    }


}
