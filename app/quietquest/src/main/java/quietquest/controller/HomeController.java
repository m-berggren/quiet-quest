package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import quietquest.model.BadgeManager;
import quietquest.model.PomodoroTimer;
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
    private ImageView theJourneyBeginsImage;
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
    private BadgeManager badgeManager;

    @Override
    protected void afterMainController() throws SQLException {
        quests = quietQuestFacade.getQuests();
        badgeManager = new BadgeManager();
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

    /**
     * Displays all badges.
     * Badges that have been unlocked by the user appear in color, all other badges appear in black-and-white.
     * Each badge image color is determined by the corresponding method in BadgeManager.
     */
    public void displayBadges() throws SQLException {
        database.connect();
        ArrayList<Quest> quests = database.getAllQuests(user);
        ArrayList<PomodoroTimer> pomodoroQuests = database.getAllPomodoroQuests(user);
        database.disconnect();

        theJourneyBeginsImage.setImage(badgeManager.getTheJourneyBeginsImage(quests));
        apprenticeImage.setImage(badgeManager.getApprenticeImage(quests));
        questConquerorImage.setImage(badgeManager.getQuestConquerorBadge(quests));
        questWarriorImage.setImage(badgeManager.getQuestWarriorImage(quests));
        speedyWitchImage.setImage(badgeManager.getSpeedyWitchImage(quests));
        timeWizardImage.setImage(badgeManager.getTimeQizardImage(pomodoroQuests));
        focusWarriorImage.setImage(badgeManager.getFocusWarriorImage(quests));
        ultimateQuestMasterImage.setImage(badgeManager.getUltimateQuestMasterImage(quests));
    }

}
