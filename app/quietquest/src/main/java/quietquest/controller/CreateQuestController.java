package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.model.Quest;
import quietquest.model.QuestManager;
import quietquest.utility.FxmlFile;
import quietquest.utility.MQTTHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CreateQuestController extends BaseController implements Initializable {
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
    private ListView<String> taskListView;
    @FXML
    private Text allTasksText;

    private Parent root;
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;

    public QuestManager questManager = QuietQuestMain.questManager;
    private ArrayList<String> tasks;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        tasks = new ArrayList<>();
        showTaskList();
    }

    // show task list if not empty:
    public void showTaskList() {
        taskListView.getItems().clear();
        taskListView.getItems().addAll(tasks);
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // can only select 1 task at a time
        taskListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            }
        });
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
        String newTask = taskFieldOne.getText(); // update current task to what is in the field
        if (!newTask.isEmpty()) {
            tasks.add(newTask); // add current task to task list
            showTaskList(); // reload task list view so that it displays updated information
            taskFieldOne.clear(); // clear text field after adding task to task list
        }
    }

    // delete selected task from task list:
    public void deleteFirstTask() {
        tasks.remove(taskListView.getSelectionModel().getSelectedIndex());
        showTaskList(); // reload task list view so that it displays updated information
    }

    // create a quest by clicking "Save Quest" button:
    public void createQuest() throws Exception {
        try {
            //if quest title field is left empty:
            if (titleField.getText().isEmpty()) {
                showMessage("Don't forget to name your quest!",
                        "Quests must have a title. Do not leave this field empty.");
                //if quest title is already taken:
            } else if (questManager.getQuests().containsKey(titleField.getText())) {
                showMessage("Give your quest a unique title",
                        "Each quest must have a unique title.");
                //if everything good with title, create quest:
            } else {
                String title = titleField.getText();
                String description = descriptionField.getText();
                Quest quest = new Quest(title, description, tasks);
                // add quest to quest list:
                questManager.addQuest(quest);
                // display popup message for successful quest creation:
                showMessage("Quest saved successfully!", "Create a new quest now or check all your quests on the Quest List page.");
                clearAllFields();
                // clear task list before creating a new quest:
                tasks.clear();
            }
        }
        catch (Exception e) {
            showMessage("Oops, something went wrong...", "Try creating your quest again");
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
    public void onOkayButtonClick () throws IOException {
        popupTextbox.setVisible(false); // error message pop-up appears
        popupText.setVisible(false);
        popupSmallText.setVisible(false);
        okayButton.setVisible(false);
    }

    // clear all fields by clicking "Clear" button:
    @FXML
    public void clearAllFields () throws IOException {
        titleField.setText("");
        descriptionField.setText("");
        taskFieldOne.setText("");
    }

    // go to "Quest List" by clicking "See quests" button:
    public void onGoToQuests(ActionEvent event) throws IOException {
        // loader = getFxmlLoader(FxmlFile.QUEST_LIST);
        // loadLoader(loader, event);
        loadLoader(FxmlFile.QUEST_LIST, event);

        // stage.setOnCloseRequest(action -> {
        //    QuestListController questListController = loader.getController();
        //    questListController.disconnectMqtt();
        // });
    }

    public void loadLoader(FXMLLoader loader, ActionEvent event) throws IOException {
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        // Set a style
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.show();
    }

}
