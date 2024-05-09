package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import quietquest.model.Activity;
import quietquest.model.PomodoroTimer;
import quietquest.model.Quest;

import java.sql.Array;
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
    private Image theJourneyBeginsBW = new Image(getClass().getResourceAsStream("/images/theJourneyBeginsBW.png"));
    private Image theJourneyBegins = new Image(getClass().getResourceAsStream("/images/theJourneyBegins.png"));
    private Image apprenticeBW = new Image(getClass().getResourceAsStream("/images/apprenticeBW.png"));
    private Image apprentice = new Image(getClass().getResourceAsStream("/images/apprentice.png"));
    private Image speedyWitchBW = new Image(getClass().getResourceAsStream("/images/speedyWitchBW.png"));
    private Image speedyWitch = new Image(getClass().getResourceAsStream("/images/speedyWitch.png"));
    private Image questConquerorBW = new Image(getClass().getResourceAsStream("/images/questConquerorBW.png"));
    private Image questConqueror = new Image(getClass().getResourceAsStream("/images/questConqueror.png"));
    private Image timeWizardBW = new Image(getClass().getResourceAsStream("/images/timeWizardBW.png"));
    private Image timeWizard = new Image(getClass().getResourceAsStream("/images/timeWizard.png"));
    private Image focusWarriorBW = new Image(getClass().getResourceAsStream("/images/focusWarriorBW.png"));
    private Image focusWarrior = new Image(getClass().getResourceAsStream("/images/focusWarrior.png"));
    private Image questWarriorBW = new Image(getClass().getResourceAsStream("/images/questWarriorBW.png"));
    private Image questWarrior = new Image(getClass().getResourceAsStream("/images/questWarrior.png"));
    private Image ultimateQuestMasterBW = new Image(getClass().getResourceAsStream("/images/ultimateQuestMasterBW.png"));
    private Image ultimateQuestMaster = new Image(getClass().getResourceAsStream("/images/ultimateQuestMaster.png"));

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

    /**
     * Displays all badges.
     * Badges that have already been unlocked by the user appear in color,
     * all other badges appear in black-and-white.
     */
    public void displayBadges() throws SQLException {
        // starts by displaying all black-and-white images
        theJourneyBeginsImage.setImage(theJourneyBeginsBW);
        apprenticeImage.setImage(apprenticeBW);
        speedyWitchImage.setImage(speedyWitchBW);
        questConquerorImage.setImage(questConquerorBW);
        timeWizardImage.setImage(timeWizardBW);
        focusWarriorImage.setImage(focusWarriorBW);
        questWarriorImage.setImage(questWarriorBW);
        ultimateQuestMasterImage.setImage(ultimateQuestMasterBW);

        database.connect();
        ArrayList<Quest> quests = database.getAllQuests(user);
        ArrayList<PomodoroTimer> pomodoroQuests = database.getAllPomodoroQuests(user);
        database.disconnect();
        boolean completedWithinHour = false;
        ArrayList<Quest> completedQuests = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest.getCompletionState()) {
                completedQuests.add(quest);
                if ((quest.getEndTime().getTime() - quest.getStartTime().getTime()) <= 60000 ) {
                    completedWithinHour = true;
                }
            }
        }
        ArrayList<PomodoroTimer> minFourIntervalPomodoros = new ArrayList<>();
        for (PomodoroTimer pomodoro : pomodoroQuests) {
            if (pomodoro.getIntervals() >= 4) {
                minFourIntervalPomodoros.add(pomodoro);
            }
        }

        if (!minFourIntervalPomodoros.isEmpty()) { // unlock timeWizard if a pomodoro with 4 or more focus sessions exists
            timeWizardImage.setImage(timeWizard);
        }
        if (!quests.isEmpty()) { // unlock theJourneyBegins if at least 1 quest exists
            theJourneyBeginsImage.setImage(theJourneyBegins);
        }
        if (!completedQuests.isEmpty()) { // unlock apprentice if completed at least 1 quest
            apprenticeImage.setImage(apprentice);
        }
        if (completedQuests.size() >= 3) { // unlock questConqueror if completed at least 3 quests
            questConquerorImage.setImage(questConqueror);
        }
        if (completedQuests.size() >= 10) { // unlock questWarrior if completed at least 10 quests
            questWarriorImage.setImage(questWarrior);
        }
        if (completedQuests.size() >= 25) { // unlock focusWarrior if completed at least 25 quests
            focusWarriorImage.setImage(focusWarrior);
        }
        if (completedQuests.size() >= 100) { // unlock ultimateQuestMaster if completed at least 100 quests
            ultimateQuestMasterImage.setImage(ultimateQuestMaster);
        }
        if (completedWithinHour) { // unlock speedyWitch if at least 1 quest was completed within 1 hour
            speedyWitchImage.setImage(speedyWitch);
        }
    }

}
