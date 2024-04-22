package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import quietquest.model.Quest;
import quietquest.model.QuestManager;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;


public class QuestController extends BaseController implements Initializable {
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

    /*public void tasksList(){

        ListView<Quest> listView = new ListView<>();
        Quest quest= quietQuestFacade.getQuestSelection();
        listView.getQuest().add(quest) = (false);
        Quest.onProperty().addListener();

    }*/



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

    private String selectedTask;

    public void initialize(URL arg0, ResourceBundle arg1) {
        //mqttClient = new MQTTHandler(this);
    }


    @Override
    protected void afterMainController() {
        Quest quest = quietQuestFacade.getQuestManager().getQuestSelection();
        titleLabel.setText(quest.getTitle());
        descLabel.setText(quest.getDescription());
        tasksListView.getItems().addAll(quest.getTasks());
        setSelectedTask();
    }

    private void setSelectedTask(){
        tasksListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                selectedTask = (String)tasksListView.getSelectionModel().getSelectedItem();
            }
        });
    }



    public void onTickTaskClick(ActionEvent event) {
        //Tanya: Audio alert
    }


    public void onStartQuestClick(ActionEvent event) {
        //Tanya: Audio alert
    }

}
