package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.model.Quest;
import quietquest.model.QuestManager;
import quietquest.utility.FxmlFile;

import java.io.IOException;
import java.util.ArrayList;

public class CreateQuestController extends BaseController {
    @FXML
    private Button saveQuestButton;
    @FXML
    private Button goToQuestsButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField taskFieldOne;
    @FXML
    private TextField taskFieldTwo;
    @FXML
    private TextField taskFieldThree;
    @FXML
    private Button deleteTaskButton1;
    @FXML
    private Button deleteTaskButton2;
    @FXML
    private Button deleteTaskButton3;
    @FXML
    private ImageView createQuestImageView;
    @FXML
    private Shape errorTextbox;
    @FXML
    private Button okayButton;
    @FXML
    private Text errorText;
    @FXML
    private Text errorSmallText;

    private Parent root;
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;

    public static QuestManager questManager = QuietQuestMain.questManager;

    // add new tasks to quest (up to 3):
    public void addNewTask() {
        if (!taskFieldOne.getText().isEmpty()) {
            taskFieldTwo.setVisible(true);
            deleteTaskButton2.setVisible(true);
        }
        if (!taskFieldTwo.getText().isEmpty()) {
            taskFieldThree.setVisible(true);
            deleteTaskButton3.setVisible(true);
        }
    }

    // delete first task from quest:
    public void deleteFirstTask() {
        if (!taskFieldOne.getText().isEmpty()) {

            if (!taskFieldTwo.getText().isEmpty()) { // if there is a second task already
                taskFieldOne.setText(taskFieldTwo.getText()); // move second task desc up
            } else { // if there is no second task
                taskFieldOne.setText("");
            }
            deleteSecondTask(); // clear second task and make invisible
        }
    }

    // delete second task from quest:
    public void deleteSecondTask() {
        if (!taskFieldThree.getText().isEmpty()) { // if there is a third task already
            taskFieldTwo.setText(taskFieldThree.getText()); // move third task up
        } else { // if there is no third task
            taskFieldTwo.setText("");
            taskFieldTwo.setVisible(false);
            deleteTaskButton2.setVisible(false);
        }
        deleteThirdTask(); // clear third task and make invisible
    }

    // delete third task from quest:
    public void deleteThirdTask() {
        taskFieldThree.setText("");
        taskFieldThree.setVisible(false);
        deleteTaskButton3.setVisible(false);
    }

    // create a quest by clicking "Save Quest" button:
    public void createQuest() throws Exception {
        try {
            //----------------------if quest title field is left empty------------------------
            if (titleField.getText().isEmpty()) {
                showError("Don't forget to name your quest!",
                        "Quests must have a title. Do not leave this field empty.");
                //----------------------if quest title is already taken---------------------------
            } else if (questManager.getQuests().containsKey(titleField.getText())) {
                showError("Give your quest a unique title",
                        "Each quest must have a unique title.");
                //----------------------if everything good with title, create quest---------------
            } else {
                String title = titleField.getText();
                String description = descriptionField.getText();
                ArrayList<String> tasks = new ArrayList<>();
                tasks.add(taskFieldOne.getText());
                tasks.add(taskFieldTwo.getText());
                tasks.add(taskFieldThree.getText());

                Quest quest = new Quest(title, description, tasks);
                //quests.put(title, quest);
                questManager.addQuest(quest);
            }
        }
        catch (Exception e) {
            showError("Oops, something went wrong...", "Try creating your quest again");
        }
    }

    // error message pop-up:
    public void showError(String message, String smallMessage){
        errorTextbox.setVisible(true);
        okayButton.setVisible(true);
        errorText.setVisible(true);
        errorText.setText(message);
        errorSmallText.setVisible(true);
        errorSmallText.setText(smallMessage);
    }

    // exit error message pop-up by clicking "Okay" button:
    public void onOkayButtonClick () throws IOException {
        errorTextbox.setVisible(false); // error message pop-up appears
        errorText.setVisible(false);
        errorSmallText.setVisible(false);
        okayButton.setVisible(false);
    }

    // cancel quest creation by clicking "Cancel" button:
    @FXML
    public void cancelQuestCreation (ActionEvent event) throws IOException {
        // loader = getFxmlLoader(FxmlFile.START);
        // loadLoader(loader, event);
        loadLoader(FxmlFile.START, event);
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
