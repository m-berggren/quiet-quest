package quietquest.model;

import javafx.scene.image.Image;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BadgeManager {
    private final Image theJourneyBeginsBW;
    private final Image apprenticeBW;
    private final Image speedyWitchBW;
    private final Image questConquerorBW;
    private final Image timeWizardBW;
    private final Image focusWarriorBW;
    private final Image questWarriorBW;
    private final Image ultimateQuestMasterBW;

    private final Image theJourneyBegins;
    private final Image apprentice;
    private final Image speedyWitch;
    private final Image questConqueror;
    private final Image timeWizard;
    private final Image focusWarrior;
    private final Image questWarrior;
    private final Image ultimateQuestMaster;

    public BadgeManager() {
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

    private Image loadImage(String path) {
        return new Image(getClass().getResourceAsStream(path));
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
     * Calculates whether any quest in the list has been completed within the specified time limit.
     * @param quests ArrayList<Quest> the list of quests
     * @param timeLimit long value of the time limit in milliseconds
     * @return boolean value returns true if at least one quest has been completed in one hour or less
     */
    private boolean hasQuestCompletedWithinTime(ArrayList<Quest> quests, long timeLimit) {
        for (Quest quest : quests) {
            if (quest.getCompletionState() && quest.getEndTime().getTime() - quest.getStartTime().getTime() <= timeLimit) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates whether any pomodoro-type quest in the list has the minimum number of intervals.
     * @param pomodoroQuests ArrayList<Quest> the list of pomodoro-style quests
     * @param intervalCount int value of the minimum number of intervals needed
     * @return boolean value returns true if at least one quest has been completed in one hour or less
     */
    public boolean meetsIntervalCount(ArrayList<PomodoroTimer> pomodoroQuests, int intervalCount) {
        for (PomodoroTimer pomodoro : pomodoroQuests) {
            if (pomodoro.getIntervals() >= intervalCount) {
                return true;
            }
        }
        return false;
    }

    /**
     * The following methods are used to determine the correct image that should be displayed for each badge.
     * Locked badges are displayed in black-and-white, unlocked are displayed in color.
     *
     * @param quests ArrayList<Quest> the list of quests associated with the user
     * @return Image either the black-and-white or color version of the image
     */

    public Image getTheJourneyBeginsImage(ArrayList<Quest> quests) {
        if (!quests.isEmpty()) { // created at least 1 quest
            return theJourneyBegins;
        }
        return theJourneyBeginsBW;
    }

    public Image getApprenticeImage(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 1) { // completed at least 1 quest
            return apprentice;
        }
        return apprenticeBW;
    }

    public Image getQuestConquerorBadge(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 3) { // completed at least 3 quests
            return questConqueror;
        }
        return questConquerorBW;
    }

    public Image getQuestWarriorImage(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 10) { // completed at least 10 quests
            return questWarrior;
        }
        return questWarriorBW;
    }

    public Image getFocusWarriorImage(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 25) { // completed at least 25 quests
            return focusWarrior;
        }
        return focusWarriorBW;
    }

    public Image getUltimateQuestMasterImage(ArrayList<Quest> quests) {
        int completedQuestsCount = countCompletedQuests(quests);
        if (completedQuestsCount >= 100) { // completed at least 100 quests
            return ultimateQuestMaster;
        }
        return ultimateQuestMasterBW;
    }

    public Image getSpeedyWitchImage(ArrayList<Quest> quests) {
        boolean completedWithinHour = hasQuestCompletedWithinTime(quests, 60000);
        if (completedWithinHour) { // at least one quest completed within 1 hour of starting it
            return speedyWitch;
        }
        return speedyWitchBW;
    }

    public Image getTimeQizardImage(ArrayList<PomodoroTimer> pomodoroQuests) {
        boolean hasMinIntervals = meetsIntervalCount(pomodoroQuests, 4);
        if (hasMinIntervals) {
            return timeWizard;
        }
        return timeWizardBW;
    }

}