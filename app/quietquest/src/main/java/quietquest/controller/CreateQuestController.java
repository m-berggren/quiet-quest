package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import quietquest.model.Quest;

import java.io.IOException;
import java.util.ArrayList;

public class CreateQuestController extends BaseController {
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
    private Shape popupTextbox;
    @FXML
    private Button okayButton;
    @FXML
    private Text popupText;
    @FXML
    private Text popupSmallText;

  /**
   * Add up to 3 tasks to the quest.
   */
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

  /**
   * Delete first task from quest.
   */
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

  /**
   * Delete second task from quest.
   */
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

  /**
   * Delete third task from quest.
   */
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
          showMessage("Don't forget to name your quest!",
                  "Quests must have a title. Do not leave this field empty.");
          //----------------------if quest title is already taken---------------------------
        } else if (quietQuestFacade.getQuests().containsKey(titleField.getText())) {
          showMessage("Give your quest a unique title",
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
          quietQuestFacade.addQuest(quest);

          // display popup message for successful quest creation:
          showMessage("Quest saved successfully!", "Create a new quest now or check all your quests on the Quest List page.");
        }
      } catch (Exception e) {
        showMessage("Oops, something went wrong...", "Try creating your quest again");
      }
    }

    // error message pop-up:
    public void showMessage(String message, String smallMessage) {
        popupTextbox.setVisible(true);
        okayButton.setVisible(true);
        popupText.setVisible(true);
        popupText.setText(message);
        popupSmallText.setVisible(true);
        popupSmallText.setText(smallMessage);
    }

    // exit error message pop-up by clicking "Okay" button:
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
        deleteThirdTask();
        deleteSecondTask();
        deleteFirstTask();
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
