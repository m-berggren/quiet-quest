package quietquest.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
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
        tasksListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()).setCellFactory(CheckBoxListCell.forListView(Callback<onCheckbox, ObservableValue<Boolean>>());
            property = new CallBack<String, ObservableValue<Boolean>>();
            ObservableValue<Boolean> call; {
                return onProperty.on();
            } }
    }

public static class onCheckbox {
    private final BooleanProperty on = new SimpleBooleanProperty();
    private final StringProperty name = new SimpleStringProperty();

    public onCheckbox(String name, boolean on){
        setName(name);
        setOn(on);
    }
    public final StringProperty nameProperty () {
        return name;
    }

    public final String getName () {
        return this.nameProperty().get();
    }

    public final void setName ( final String name){
        this.nameProperty().set(name);
    }
    public final BooleanProperty onProperty () {
        return this.on;
    }

    public final boolean isOn () {
        return this.onProperty().get();
    }

    public final void setOn ( final boolean on){
        this.onProperty().set(on);
    }
    @Override
    public String toString() {
        return getName();
    }
    public void launch(String[] args) {
        launch(args);
    }
}






    public void onTickTaskClick(ActionEvent event) {
        //Tanya: Audio alert
    }


    public void onStartQuestClick(ActionEvent event) {
        //Tanya: Audio alert
    }

}
