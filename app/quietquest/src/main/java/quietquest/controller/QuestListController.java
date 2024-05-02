package quietquest.controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
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
    private ListView<String> questListView;
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
    private ArrayList< Task> tasks;

    private Task currentTask;

    private ObservableList<Task> data;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        currentQuest = null;
        currentTask = null;
    }

    @Override
    public void afterMainController() {
        quests = quietQuestFacade.getQuests();
        tasks = quietQuestFacade.getTasks();
        displayQuests();
        setSelectedQuest();
        currentQuest = null; // set to null to avoid another quest's details being shown
        //tasks = quietQuestFacade.getTasks();
        displayTasks();
        //setSelectedTasks();
        //currentTask = null;
    }

    public void onGoToQuestClick(ActionEvent event) throws IOException {
        showQuest(currentQuest);
    }

    public void displayQuests() {
        questListView.getItems().addAll(quests.keySet());
    }

    public void displayTasks(){
        taskListView.getItems().addAll();
        data = FXCollections.observableArrayList(tasks);
        taskListView.setItems(data);
        showTaskList(currentTask);}

// show task list
public void showTaskList(Task currentTask) {
    taskListView.getItems().clear();
    taskListView.getItems().addAll(data);
    taskListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // can only select 1 task at a time
    taskListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
        @Override
        public void changed(ObservableValue<? extends Task> observableValue, Task oldValue, Task newValue) {

        }

    });showSelected();
}

    private void setSelectedQuest() {
        questListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                String selectedKey = questListView.getSelectionModel().getSelectedItem();
                quietQuestFacade.setQuestSelection(quests.get(selectedKey));

            }
        });showSelected();
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
        // if fields are actively being edited, warning pop-up appears:
        if (titleField.isEditable()) {
            showWarning("Your changes will not be saved", "Are you sure you want to proceed?");
        } else {
            currentQuest = quietQuestFacade.getQuestSelection();
            currentTask = quietQuestFacade.getTaskSelection();
            if(currentQuest !=null){

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
            doNotShowWarning();
            // pre-fill quest details:
            titleField.setText(currentQuest.getTitle());
            descriptionField.setText(currentQuest.getDescription());
            showTaskList(currentTask);
            }else{//error handling
            }
        }
    }

    // show task list view details:
    /*public void showTaskList(Task currentTask) {
        this.currentTask = quietQuestFacade.getTaskSelection();
        taskListView.getItems().clear();
        taskListView.getItems().addAll(quietQuestFacade.getTaskSelection().getTasks());
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // can select multiple tasks at a time
    }*/

    // warning message pop-up:
    public void showWarning(String message, String smallMessage) {
        warningTextbox.setVisible(true);
        okayButton.setVisible(true);
        okayButton.setDisable(false);
        keepEditingButton.setVisible(true);
        keepEditingButton.setDisable(false);
        warningText.setVisible(true);
        warningText.setText(message);
        warningSmallText.setVisible(true);
        warningSmallText.setText(smallMessage);
        // if warning pop-up message is on screen, other quests, tasks, and buttons become non-clickable:
        questListView.setDisable(true);
        questListView.setEditable(false);
        taskListView.setDisable(true);
        editButton.setDisable(true);
        saveButton.setDisable(true);
        completeButton.setDisable(true);
    }

    // discard edits and show new quest selection when by clicking okayButton:
    public void onOkayButtonClick() {
        doNotShowWarning();
        setSelectedUneditable();
        showSelected();
    }

    // stay on same quest in editing mode by clicking keepEditingButton:
    public void onKeepEditingClick() {
        doNotShowWarning();
        setEditable();
        taskListView.setDisable(false);
    }

    public void doNotShowWarning() {
        warningTextbox.setVisible(false);
        okayButton.setVisible(false);
        keepEditingButton.setVisible(false);
        warningText.setVisible(false);
        warningSmallText.setVisible(false);
        // quest list and task list are clickable:
        questListView.setDisable(false);
        taskListView.setDisable(false);
    }

    // edit selected quest by clicking editButton:
    public void setEditable(){
        // set all quest details editable and enable related buttons:
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
        // set quest list view disabled so that other quest cannot be selected:
        questListView.setDisable(true);
    }

    // quest detail fields are non-editable if editButton is not explicitly clicked:
    public void setSelectedUneditable() {
        titleField.setEditable(false); // title field uneditable
        descriptionField.setEditable(false); // description field uneditable
        taskListView.setDisable(true); // task list view is un-clickable
        taskField.setEditable(false); // new task field uneditable
        deleteTaskButton.setDisable(true); // delete task button disabled
        addNewTaskButton.setDisable(true); // add task button disabled
        questListView.setDisable(false); // quest list view is clickable
    }

    // add task to task list:
    public void addNewTask() {
        String newTaskTitle = taskField.getText();// update current task to what is in the field
        if (!newTaskTitle.isEmpty()) {
            Task newTask = new Task(newTaskTitle);
            quietQuestFacade.addTasks(newTask); // add current task to task list
            showTaskList(currentTask);
            taskField.clear(); // clear text field after adding task to task list
        }
        System.out.println("tasks now: " + currentTask.getTasks());
    }

    // delete selected task from task list:
    public void deleteFirstTask() {
        quietQuestFacade.deleteTask(taskListView.getSelectionModel().getSelectedItem());
        showTaskList(currentTask); // reload task list information so that it displays updated information
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


        currentQuest.setTasks(tasks);

        // make fields non-editable:
        setSelectedUneditable();
    }



}
