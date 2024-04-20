package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import quietquest.QuietQuestMain;
import quietquest.model.Quest;
import quietquest.model.QuestManager;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class QuestController {
    @FXML
    private Button startQuestButton;
    @FXML
    private Button tickTaskButton;
    @FXML
    private ListView tasksListView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descLabel;
    @FXML
    private CheckBox TaskCheckBox;

    @FXML
    private Label MotivationMessage;


   public void message(ActionEvent event,MotivationMessage){
      if(TaskCheckBox.isSelected()) {

          for (int i = 0; i < 5; i++) {
              QuestManager

      }
   }






    public void onTickTaskClick(ActionEvent event) {
        //Tanya: Audio alert
    }

    public void onStartQuestClick(ActionEvent event) {
        //Tanya: Audio alert
    }


}
