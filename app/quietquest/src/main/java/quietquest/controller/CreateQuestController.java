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
import javafx.scene.layout.Pane;
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
    private TextField taskField;
    @FXML
    private Button deleteTaskButton;
    @FXML
    private Pane popupPane;
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
    @FXML
    private Button addNewTaskButton;

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

    /**
     * Show current task list in the taskListView.
     */
    public void showTaskList() {
        taskListView.getItems().clear();
        taskListView.getItems().addAll(data);
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
       /* taskListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
            @Override
            public void changed(ObservableValue<? extends Task> observableValue, Task oldValue, Task newValue) {
            }
        });

        */
    }

    /**
     * New task added to task list and displayed in right side task list view.
     */
    public void addNewTask() {
        String newTaskTitle = taskField.getText();
        if (!newTaskTitle.isEmpty()) {
            Task newTask = new Task(newTaskTitle);
            tasks.add(newTask);
            data.add(newTask);
            taskField.clear();
        }
    }

    /**
     * Delete selected task from task list and remove from task list view too.
     */
    public void deleteTask() {
        Task currentTask = taskListView.getSelectionModel().getSelectedItem();
        quietQuestFacade.deleteTask(currentTask);
        showTaskList();
    }

    /**
     * Creates a quest upon clicking "Save" button.
     */
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
            System.out.println("quest: " + quest.getTitle());
            System.out.println("tasks: " + quest.getTasks());
            showCreateQuest();
        }
    }

    /**
     * Method that makes error/success message pop-up window appear.
     */
    public void showMessage(String message, String smallMessage){
        popupPane.setDisable(false);
        popupPane.setVisible(true);
        popupTextbox.setVisible(true);
        okayButton.setVisible(true);
        popupText.setVisible(true);
        popupText.setText(message);
        popupSmallText.setVisible(true);
        popupSmallText.setText(smallMessage);
        // disable other fields and buttons
        saveQuestButton.setDisable(true);
        clearButton.setDisable(true);
        goToQuestsButton.setDisable(true);
        titleField.setDisable(true);
        descriptionField.setDisable(true);
        taskField.setDisable(true);
        addNewTaskButton.setDisable(true);
        taskListView.setDisable(true);
        deleteTaskButton.setDisable(true);
    }

    /**
     * Error/success message pop-up window disappears upon clicking "Okay" button.
     * @param event
     * @throws IOException
     */
    public void onOkayButtonClick (ActionEvent event) throws IOException {
        popupPane.setVisible(false);
        popupPane.setDisable(true);
        popupTextbox.setVisible(false);
        popupText.setVisible(false);
        popupSmallText.setVisible(false);
        okayButton.setVisible(false);
        // enable other fields and buttons
        saveQuestButton.setDisable(false);
        clearButton.setDisable(false);
        goToQuestsButton.setDisable(false);
        titleField.setDisable(false);
        descriptionField.setDisable(false);
        taskField.setDisable(false);
        addNewTaskButton.setDisable(false);
        taskListView.setDisable(false);
        deleteTaskButton.setDisable(false);

    }

    /**
     * Clear all fields upon clicking "Clear" button.
     */
    @FXML
    public void clearAllFields () throws IOException {
        titleField.clear();
        descriptionField.clear();
        taskField.clear();
        taskListView.getItems().clear();
    }

    /**
    * Go to "Quest List" by clicking "List" button.
    */
    public void onGoToQuests() {
        showQuestList();
     }
}
