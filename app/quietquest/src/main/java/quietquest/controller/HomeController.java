package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import quietquest.model.Quest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
    @FXML
    private ImageView firstQuestImage;
    @FXML
    private ImageView apprenticeImage;
    @FXML
    private ImageView speedyWitchImage;
    @FXML
    private ImageView questConquerorImage;
    @FXML
    private ImageView timeWizardImage;
    @FXML
    private ImageView focusWarriorImage;
    @FXML
    private ImageView questWarriorImage;
    @FXML
    private ImageView ultimateQuestMasterImage;
    private HashMap<String, Quest> quests;
    private Quest currentQuest;

    @Override
    protected void afterMainController() throws SQLException {
        quests = quietQuestFacade.getQuests();
        displayCurrentQuest();
        displayBadges();
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
     * If no ongoing quests found, directs the user to take another action.
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

    private Image theJourneyBeginsBW = new Image(getClass().getResourceAsStream("/images/theJourneyBeginsBW.png"));
    private Image apprenticeBW = new Image(getClass().getResourceAsStream("/images/apprenticeBW.png"));
    private Image speedyWitchBW = new Image(getClass().getResourceAsStream("/images/speedyWitchBW.png"));
    private Image questConquerorBW = new Image(getClass().getResourceAsStream("/images/questConquerorBW.png"));
    private Image timeWizardBW = new Image(getClass().getResourceAsStream("/images/timeWizardBW.png"));
    private Image focusWarriorBW = new Image(getClass().getResourceAsStream("/images/focusWarriorBW.png"));
    private Image questWarriorBW = new Image(getClass().getResourceAsStream("/images/questWarriorBW.png"));
    private Image ultimateQuestMasterBW = new Image(getClass().getResourceAsStream("/images/ultimateQuestMasterBW.png"));

    public void displayBadges() {
        firstQuestImage.setImage(theJourneyBeginsBW);
        apprenticeImage.setImage(apprenticeBW);
        speedyWitchImage.setImage(speedyWitchBW);
        questConquerorImage.setImage(questConquerorBW);
        timeWizardImage.setImage(timeWizardBW);
        focusWarriorImage.setImage(focusWarriorBW);
        questWarriorImage.setImage(questWarriorBW);
        ultimateQuestMasterImage.setImage(ultimateQuestMasterBW);
    }

}
