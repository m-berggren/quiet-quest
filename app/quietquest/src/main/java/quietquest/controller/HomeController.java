package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private Image theJourneyBeginsBW;
    private Image apprenticeBW;
    private Image speedyWitchBW;
    private Image questConquerorBW;
    private Image timeWizardBW;
    private Image focusWarriorBW;
    private Image questWarriorBW;
    private Image ultimateQuestMasterBW;

    private Image theJourneyBegins;
    private Image apprentice;
    private Image speedyWitch;
    private Image questConqueror;
    private Image timeWizard;
    private Image focusWarrior;
    private Image questWarrior;
    private Image ultimateQuestMaster;

    private HashMap<String, Quest> quests;
    private Quest currentQuest;

    @Override
    protected void afterMainController() throws SQLException {
        quests = quietQuestFacade.getQuests();
        loadImages();
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
     * Method to load a single image.
     * @param path Specifies the path of the image.
     * @return Returns the image loaded from the specified path.
     */
    private Image loadImage(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    /**
     * Method to load all images connected to the various badge types.
     */
    private void loadImages() {
        // Badge images in black-and-white
        theJourneyBeginsBW = loadImage("/images/theJourneyBeginsBW.png");
        apprenticeBW = loadImage("/images/apprenticeBW.png");
        speedyWitchBW = loadImage("/images/speedyWitchBW.png");
        questConquerorBW = loadImage("/images/questConquerorBW.png");
        timeWizardBW = loadImage("/images/timeWizardBW.png");
        focusWarriorBW = loadImage("/images/focusWarriorBW.png");
        questWarriorBW = loadImage("/images/questWarriorBW.png");
        ultimateQuestMasterBW = loadImage("/images/ultimateQuestMasterBW.png");
        // Badge images in color
        theJourneyBegins = loadImage("/images/theJourneyBegins.png");
        apprentice = loadImage("/images/apprentice.png");
        speedyWitch = loadImage("/images/speedyWitch.png");
        questConqueror = loadImage("/images/questConqueror.png");
        timeWizard = loadImage("/images/timeWizard.png");
        focusWarrior = loadImage("/images/focusWarrior.png");
        questWarrior = loadImage("/images/questWarrior.png");
        ultimateQuestMaster = loadImage("/images/ultimateQuestMaster.png");
    }

    /**
     * Displays all badges.
     * Badges that have already been unlocked by the user appear in color,
     * all other badges appear in black-and-white.
     */
    public void displayBadges() throws SQLException {
        database.connect();
        ArrayList<Quest> quests = database.getAllQuests(user);
        ArrayList<PomodoroTimer> pomodoroQuests = database.getAllPomodoroQuests(user);
        database.disconnect();

        displayTheJourneyBeginsBadge(quests);
        displayApprenticeBadge(quests);
        displaySpeedyWitchBadge(quests);
        displayQuestConquerorBadge(quests);
        displayTimeWizardBadge(pomodoroQuests);
        displayFocusWarriorBadge(quests);
        displayQuestWarriorBadge(quests);
        displayUltimateQuestMasterBadge(quests);
    }

    /**
     * Method to count the completed quests associated with the user.
     * @param quests ArrayList<Quest> quests associated with the user
     * @return int value of the number of quests in the list
     */
    private int countCompletedQuests(ArrayList<Quest> quests) {
        int count = 0;
        for (Quest quest : quests) {
            if (quest.getCompletionState()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates whether any quest associated with the user has been completed within one hour of starting it.
     * @param quests ArrayList<Quest> the list of quests associated with the user
     * @return boolean value returns true if at least one quest has been completed in one hour or less
     */
    private boolean hasQuestCompletedWithinHour(ArrayList<Quest> quests) {
        for (Quest quest : quests) {
            if (quest.getCompletionState() && quest.getEndTime().getTime() - quest.getStartTime().getTime() <= 60000) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for determining whether the user has at least one pomodoro-style quest that has at least 4 intervals.
     * @param pomodoroQuests ArrayList<PomodoroTimer> the list of pomodoro quests associated with the user
     * @return boolean value, returns true if there is at least one pomodoro with at least 4 intervals
     */
    private boolean hasFourOrMoreIntervals(ArrayList<PomodoroTimer> pomodoroQuests) {
        for (PomodoroTimer pomodoro : pomodoroQuests) {
            if (pomodoro.getIntervals() >= 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Display theJourneyBegins badge. Unlocked if at least 1 quest has been created.
     * @param quests ArrayList<Quest> the list of quests associated with the user
     */
    private void displayTheJourneyBeginsBadge(ArrayList<Quest> quests) {
        if (!quests.isEmpty()) {
            theJourneyBeginsImage.setImage(theJourneyBegins);
        } else {
            theJourneyBeginsImage.setImage(theJourneyBeginsBW);
        }
    }

    /**
     * Display apprentice badge. Unlocked if there is at least 1 completed quest.
     * @param quests ArrayList<Quest> the list of quests associated with the user
     */
    private void displayApprenticeBadge(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 1) {
            apprenticeImage.setImage(apprentice);
        } else {
            apprenticeImage.setImage(apprenticeBW);
        }
    }

    /**
     * Display questConqueror badge. Unlocked if at least 3 quests are completed.
     * @param quests ArrayList<Quest> the list of quests associated with the user
     */
    private void displayQuestConquerorBadge(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 3) {
            questConquerorImage.setImage(questConqueror);
        } else {
            questConquerorImage.setImage(questConquerorBW);
        }
    }

    /**
     * Display questWarrior badge. Unlocked if at least 10 quests are completed.
     * @param quests ArrayList<Quest> the list of quests associated with the user
     */
    private void displayQuestWarriorBadge(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 10) {
            questWarriorImage.setImage(questWarrior);
        } else {
            questWarriorImage.setImage(questWarriorBW);
        }
    }

    /**
     * Display focusWarrior badge. Unlocked if at least 25 quests are completed.
     * @param quests ArrayList<Quest> the list of quests associated with the user
     */
    private void displayFocusWarriorBadge(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 25) {
            focusWarriorImage.setImage(focusWarrior);
        } else {
            focusWarriorImage.setImage(focusWarriorBW);
        }
    }

    /**
     * Display ultimateQuestMaster badge. Unlocked if at least 100 quests are completed.
     * @param quests ArrayList<Quest> the list of quests associated with the user
     */
    private void displayUltimateQuestMasterBadge(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 100) {
            ultimateQuestMasterImage.setImage(ultimateQuestMaster);
        } else {
            ultimateQuestMasterImage.setImage(ultimateQuestMasterBW);
        }
    }

    /**
     * Display speedyWitch badge. Unlocked if at least one quest has been completed within 1 hour of starting it.
     * @param quests ArrayList<Quest> the list of quests associated with the user
     */
    private void displaySpeedyWitchBadge(ArrayList<Quest> quests) {
        boolean completedWithinHour = hasQuestCompletedWithinHour(quests);
        if (completedWithinHour) {
            speedyWitchImage.setImage(speedyWitch);
        } else {
            speedyWitchImage.setImage(speedyWitchBW);
        }
    }

    /**
     * Display timeWizard badge. Unlocked if at least one of the user's pomodoro quests has 4 or more intervals.
     * @param pomodoroQuests ArrayList<PomodoroTimer> the list of pomodoros associated with the user
     */
    private void displayTimeWizardBadge(ArrayList<PomodoroTimer> pomodoroQuests) {
        boolean hasFourOrMore = hasFourOrMoreIntervals(pomodoroQuests);
        if (hasFourOrMore) {
            timeWizardImage.setImage(timeWizard);
        } else {
            timeWizardImage.setImage(timeWizardBW);
        }
    }

}
