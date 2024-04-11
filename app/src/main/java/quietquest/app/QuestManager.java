package quietquest.app;

import java.util.HashMap;

public class QuestManager {
    private HashMap<String, Quest> quests;

    public QuestManager(){
        quests = new HashMap<String, Quest>();
    }

    public void addQuest(Quest quest){
        quests.put(quest.getTitle(), quest);
        System.out.println("Added: " + quest.getTitle()); //for us to get confirmation when a quest is saved
    }

    public HashMap<String, Quest> getQuests(){
        return quests;
    }

    public boolean deleteQuest(String title){
        if(quests.containsKey(title)){
            quests.remove(title);
            return true;
        }

        return false;
    }
}
