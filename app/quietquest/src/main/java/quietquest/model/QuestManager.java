package quietquest.model;

import java.util.HashMap;

public class QuestManager {
  private HashMap<String, Quest> quests;
  private Quest questSelection;

  private HashMap<String, Task> tasks;

  public QuestManager() {
    quests = new HashMap<>();
  }

  public void addQuest(Quest quest) {
    quests.put(quest.getTitle(), quest);
    System.out.println("Added: " + quest.getTitle()); //for us to get confirmation when a quest is saved
  }

  public HashMap<String, Quest> getQuests() {
    return quests;
  }

  public void deleteQuest(String title) {
    quests.remove(title);
  }

  public void setQuestSelection(Quest quest) {
    questSelection = quest;
    System.out.println("Selected: " + quest);
  }

  public Quest getQuestSelection() {
    return questSelection;
  }


  public void resetQuestSelection() {
    questSelection = null;
    System.out.println("Quest Selection is set to null.");
  }
}
