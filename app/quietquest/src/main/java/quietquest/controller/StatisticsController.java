package quietquest.controller;

import quietquest.model.Quest;

import java.util.ArrayList;


public class StatisticsController extends BaseController{
    private ArrayList<Quest> quests;

    public void afterMainController() {
        quests = getAllQuests();
        displayStatistics();
  }

        
    }
  public int getCompletedQuestsNumber() {
    int completedQuests = 0;
    for (Quest quest : quests) {
      if (quest.getCompletionState()) {
        completedQuests++;
      }
    }
    return completedQuests;
  }

  public int getInProgressQuestsNumber() {
    int inProgressQuests = 0;
    for (Quest quest : quests) {
      if (!quest.getCompletionState()) {
        inProgressQuests++;
      }
    }
    return inProgressQuests;
  }

  public int getCreatedQuestsNumber() {
    return quests.size();
  }

  public int getAverageOpenBoxTimes() {
    int totalOpenBoxTimes = 0;
    for (Quest quest : quests) {
      totalOpenBoxTimes += quest.getBoxOpenTimes();
    }
    return totalOpenBoxTimes / quests.size();
  }

  public int getAverageOpenBoxInterval(){
    int totalQuestTimeSpent = 0;
    int totalBoxOpenTimes = 0;
    int averageOpenBoxInterval = 0;
    for (Quest quest : quests) {
      int timeSpent = (int) ((quest.getEndTime().getTime() - quest.getStartTime().getTime()) / 60000);
      totalQuestTimeSpent += timeSpent;
      totalBoxOpenTimes += quest.getBoxOpenTimes() + 1;
    }
    averageOpenBoxInterval = totalQuestTimeSpent / totalBoxOpenTimes;
    return totalQuestTimeSpent;
  }

  //Calculate the average time spent on a quest
  public int getAverageTimeSpentOnQuest() {
    int totalQuestTimeSpent = 0;
    for (Quest quest : quests) {
      int timeSpent = (int) ((quest.getEndTime().getTime() - quest.getStartTime().getTime()) / 60000);
      totalQuestTimeSpent += timeSpent;
    }
    return totalQuestTimeSpent / quests.size();
  }

  public void displayStatistics() {
    // Display above statistics in the view



  }
}
