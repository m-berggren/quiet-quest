package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.ImageView;
import quietquest.QuietQuestMain;
import quietquest.model.Quest;
import quietquest.model.QuestManager;
import quietquest.utility.MQTTHandler;

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

    private QuestManager questManager;
    private HashMap<String, Quest> quests;

    public void tasksList(){

        ListView<Quest> listView = new ListView<>();
        Quest = questManager.getQuestSelection();
        listView.getQuest().add(Quest) = (false);
        Quest.onProperty().addListener();

    }



   public void message(ActionEvent event){
       String [] TaskMotivation = {"Good Job!", "One step closer to a nap", "You can do it!", "Awesome job!"};
       String QuestMotivation = "";
      if(TaskCheckBox.isSelected()) {
          for(int i = 0; i < TaskMotivation.length; i++){
          QuestMotivation = TaskMotivation[i];
      }
          MotivationMessage.setVisible(true);
          MotivationMessage.setText(QuestMotivation);
      }
   }

    public void onTickTaskClick(ActionEvent event) {
        //Tanya: Audio alert
    }

    public void onStartQuestClick(ActionEvent event) {
        //Tanya: Audio alert
    }


}
