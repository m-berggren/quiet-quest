package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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


    public void onTickTaskClick(ActionEvent event) {
        //Tanya: Audio alert
    }

    public void onStartQuestClick(ActionEvent event) {
        //Tanya: Audio alert
    }


}
