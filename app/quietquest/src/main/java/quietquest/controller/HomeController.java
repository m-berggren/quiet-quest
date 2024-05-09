package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import quietquest.model.Quest;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class HomeController extends BaseController {
    @FXML
    private ListView<Quest> questListView;
    @FXML
    private Label currentQuestLabel;
    @FXML
    private Button continueQuestButton;
    @FXML
    private Button viewListButton;
    @FXML
    private Button createQuestButton;
    private HashMap<String, Quest> quests;
    private Quest currentQuest;

    @Override
    protected void afterMainController() throws SQLException {
        quests = quietQuestFacade.getQuests();
        displayCurrentQuest();
    }
    public void onContinueQuestClick(ActionEvent event) {
        showQuest(currentQuest);
    }
    public void onCreateQuestClick(ActionEvent event) {
        showCreateQuest();
    }
    public void onViewListClick(ActionEvent event) {
        showQuestList();
    }

    /**
     * Displays the currently ongoing quest.
     * If no ongoing quests found,
     */
    public void displayCurrentQuest() throws SQLException {
        database.connect();
        ArrayList<Quest> questsList = database.getAllQuests(user);
        database.disconnect();
        // Get the ongoing quest by checking that start time is not null and quest is not completed.
        for (Quest quest : questsList) {
            if (quest.getStartTime() != null && !quest.getCompletionState()) {
                currentQuest = quest;
            }
        }
        if (currentQuest != null) {
            currentQuestLabel.setText(currentQuest.getTitle());
            continueQuestButton.setVisible(true);
            viewListButton.setVisible(false);
            createQuestButton.setVisible(false);
        } else {
            continueQuestButton.setVisible(false);
            viewListButton.setVisible(true);
            createQuestButton.setVisible(true);
        }
    }

}
