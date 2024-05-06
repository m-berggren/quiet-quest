package quietquest.model;

import java.util.HashMap;

public class QuietQuestFacade {
    private final QuestManager questManager = new QuestManager();

    public QuietQuestFacade() {
    }

    public HashMap<String, Quest> getQuests() {
        return questManager.getQuests();
    }

    public void addQuest(Quest quest) {
        questManager.addQuest(quest);
    }

    public void deleteQuest(String title) {
        questManager.deleteQuest(title);
    }

    public void setQuestSelection(Quest quest) {
        questManager.setQuestSelection(quest);
    }

    public Quest getQuestSelection() {
        return questManager.getQuestSelection();
    }

    public void resetQuestSelection() {
        questManager.resetQuestSelection();
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

}
