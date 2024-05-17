package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import quietquest.model.Activity;
import quietquest.model.BadgeManager;
import quietquest.model.PomodoroTimer;
import quietquest.model.Quest;
import quietquest.utility.MenuButtonType;

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


    // ==============================* INITIALIZATION METHODS *===================================

    @Override
    protected void afterMainController() throws SQLException {
        badgeManager = new BadgeManager();
        displayCurrentQuest();
        displayBadges();
    }
    public void onContinueQuestClick(ActionEvent event) {
		if (currentQuest == null) {
			return;
		}
		Activity activity = currentQuest.getActivities().getFirst();
		if (activity instanceof PomodoroTimer) {
			Quest pomodoroQuest = quietQuestFacade.getPomodoroQuest();
			if (pomodoroQuest != null) {
				changeSelectedMenuButton(MenuButtonType.QUEST_LIST);
				showQuest(pomodoroQuest);
			} else {
				changeSelectedMenuButton(MenuButtonType.QUEST_LIST);
				showQuest(currentQuest);
			}
		} else {
			changeSelectedMenuButton(MenuButtonType.QUEST_LIST);
			showQuest(currentQuest);
		}
	}

    public void onCreateQuestClick(ActionEvent event) {
        changeSelectedMenuButton(MenuButtonType.CREATE_QUEST);
        showCreateQuest();
    }
    public void onViewListClick(ActionEvent event) {
        changeSelectedMenuButton(MenuButtonType.QUEST_LIST);
        showQuestList();
    }

    /**
     * Displays the currently ongoing quest.
     * If no ongoing quests found, directs the user to take another action.
     */
    public void displayCurrentQuest() throws SQLException {
        ArrayList<Quest> questsList = getAllQuests();
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
        ArrayList<Quest> quests = getAllQuests();
        ArrayList<PomodoroTimer> pomodoroQuests = getAllPomodoroQuests();

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
