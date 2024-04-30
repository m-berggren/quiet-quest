package quietquest.model;

import java.util.ArrayList;
import java.util.HashMap;

public class QuietQuestFacade {
  private final QuestManager questManager = new QuestManager();

  public QuietQuestFacade() {
  }

  public HashMap<String, Quest> getQuests() {
    return questManager.getQuests();
  }

  public ArrayList<Task> getTasks(){return questManager.getTasks();}

  public void addQuest(Quest quest) {
    questManager.addQuest(quest);
  }

  public void addTasks(Task task){
    questManager.addTask(task);
  }

  public void deleteQuest(String title) {
    questManager.deleteQuest(title);
  }

  public void deleteTask(Task task){questManager.deleteTask(task);}

  public void setQuestSelection(Quest quest) {
    questManager.setQuestSelection(quest);
  }

  public void setTaskSelection(Task task) {questManager.setTaskSelection(task);}

  public Quest getQuestSelection() {
    return questManager.getQuestSelection();
  }

  public Task getTaskSelection(){return questManager.getTaskSelection();}

  public void resetQuestSelection() {
    questManager.resetQuestSelection();
  }

  public void resetTaskSelection(){questManager.resetTaskSelection();}

  public QuestManager getQuestManager() {
    return questManager;
  }

}
