package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.QuietQuestMain;
import quietquest.model.Quest;
import quietquest.model.QuestManager;
import quietquest.model.Task;
import quietquest.utility.FxmlFile;
import quietquest.utility.MQTTHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CreateQuestController extends BaseController implements Initializable {
    @FXML
    private TextField taskDescriptionField;
    @FXML
    private Button saveQuestButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button goToQuestsButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField taskFieldOne;
    @FXML
    private Button deleteTaskButton1;
    @FXML
    private Shape popupTextbox;
    @FXML
    private Button okayButton;
    @FXML
    private Text popupText;
    @FXML
    private Text popupSmallText;
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private Text allTasksText;

    private Parent root;
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;

    private ArrayList <Task> tasks;
    private ObservableList<Task> data;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        tasks = new ArrayList<>();
        data = FXCollections.observableArrayList(tasks);
        taskListView.setItems(data);
        showTaskList();
    }

    // show task list
    public void showTaskList() {
        taskListView.getItems().clear();
        taskListView.getItems().addAll(data);
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // can only select 1 task at a time
        taskListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
            @Override
            public void changed(ObservableValue<? extends Task> observableValue, Task oldValue, Task newValue) {

            }

        });
        visibility();
    }

    //check if task list is not empty
    private void visibility(){
        if (!tasks.isEmpty()) {
            // if there are tasks in the list, make task list elements visible:
            allTasksText.setVisible(true);
            taskListView.setVisible(true);
            deleteTaskButton1.setVisible(true);
        }
        else {
            // if no tasks in the list, make task list elements invisible:
            allTasksText.setVisible(false);
            taskListView.setVisible(false);
            deleteTaskButton1.setVisible(false);
        }
    }

    // add task to task list:
    public void addNewTask() {
        String newTaskTitle = taskFieldOne.getText();// update current task to what is in the field
        if (!newTaskTitle.isEmpty()) {
            Task newTask = new Task(newTaskTitle);
            tasks.add(newTask);
            data.add(newTask);
            //quietQuestFacade.addTasks(newTask); // add current task to task list
            visibility(); // reload task list view so that it displays updated information
        }

    }



    // delete selected task from task list:
    public void deleteFirstTask() {
        //quietQuestFacade.deleteTask((taskListView.getItems().remove(tasks)));
        quietQuestFacade.deleteTask(taskListView.getSelectionModel().getSelectedItem());
        quietQuestFacade.resetQuestSelection();
        visibility();
    }




    // create a quest by clicking "Save Quest" button:
    public void createQuest(ActionEvent event) throws Exception {

            //if quest title field is left empty:
            if (titleField.getText().isEmpty()) {
                showMessage("Don't forget to name your quest!",
                        "Quests must have a title. Do not leave this field empty.");
                //if quest title is already taken:
            } else if (quietQuestFacade.getQuests().containsKey(titleField.getText())) {
                showMessage("Give your quest a unique title",
                        "Each quest must have a unique title.");
                //if everything good with title, create quest:
            } else {
                String title = titleField.getText();
                String description = descriptionField.getText();
                Quest quest = new Quest(title, description, tasks);
                // add quest to quest list:
                quietQuestFacade.addQuest(quest);
                // display popup message for successful quest creation:
                showMessage("Quest saved successfully!", "Create a new quest now or check all your quests on the Quest List page.");
                System.out.println("quest: " + quest.getTitle());
                System.out.println("tasks: " + quest.getTasks());
                showCreateQuest();
            }
    }

    // error/success message pop-up:
    public void showMessage(String message, String smallMessage){
        popupTextbox.setVisible(true);
        okayButton.setVisible(true);
        popupText.setVisible(true);
        popupText.setText(message);
        popupSmallText.setVisible(true);
        popupSmallText.setText(smallMessage);
    }

    // exit error/success message pop-up by clicking "Okay" button:
    public void onOkayButtonClick (ActionEvent event) throws IOException {
        popupTextbox.setVisible(false); // error message pop-up appears
        popupText.setVisible(false);
        popupSmallText.setVisible(false);
        okayButton.setVisible(false);
    }

    // clear all fields by clicking "Clear" button:
    @FXML
    public void clearAllFields () throws IOException {
        titleField.clear();
        descriptionField.clear();
        taskFieldOne.clear();
        taskListView.getItems().clear();
    }


  /**
   * Cancel quest creation by clicking "Cancel" button.
   */
  public void cancelQuestCreation(ActionEvent event) {
    showStart();
  }


  /**
   * Go to "Quest List" by clicking "See quests" button.
   */
  public void onGoToQuests(ActionEvent event) {
    showQuestList();
  }
}
