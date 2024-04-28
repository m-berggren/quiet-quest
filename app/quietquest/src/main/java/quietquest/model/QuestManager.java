package quietquest.model;

import java.util.HashMap;

public class QuestManager {
  private HashMap<String, Quest> quests;
  private Quest questSelection;

  private HashMap<String, Task> tasks;

  private Task taskSelection;

  public QuestManager() {
    quests = new HashMap<>();
    tasks = new HashMap<>();
  }

  public void addQuest(Quest quest) {
    quests.put(quest.getTitle(), quest);
    System.out.println("Added: " + quest.getTitle()); //for us to get confirmation when a quest is saved
  }
  public void addTask(Task task){
    tasks.put(task.getTasks(), task);
  }
  public HashMap<String, Quest> getQuests() {
    return quests;
  }
  public HashMap<String,Task> getTasks(){
    return tasks;
  }

  public void deleteQuest(String title) {
    quests.remove(title);
  }
  public void deleteTask(String task){
    tasks.remove(task);
  }

  public void setQuestSelection(Quest quest) {
    questSelection = quest;
    System.out.println("Selected: " + quest);
  }

  public void setTaskSelection(Task tasks){
    taskSelection = tasks;
  }

  public Quest getQuestSelection() {
    return questSelection;
  }

  public Task getTaskSelection(){return taskSelection;}


  public void resetQuestSelection() {
    questSelection = null;
    System.out.println("Quest Selection is set to null.");
  }
  public void resetTaskSelection(){
    taskSelection = null;
  }
}
