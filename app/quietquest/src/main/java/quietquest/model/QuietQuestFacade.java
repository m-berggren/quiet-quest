package quietquest.model;

import java.util.HashMap;

public class QuietQuestFacade {
    private final QuestManager questManager = new QuestManager();
    private final User user;
    private final Database database;

    public QuietQuestFacade(User user, Database database) {
        this.user = user;
        this.database = database;
    }

    public User getUser() {
        return user;
    }

    public Database getDatabase() {
        return database;
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

    public void saveBoxOpenTimes(Quest quest) {
        database.saveBoxOpenTimes(user.getUsername(), quest.getID());
    }

}
