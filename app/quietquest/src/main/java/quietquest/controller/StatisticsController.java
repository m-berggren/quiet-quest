package quietquest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import quietquest.model.Quest;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

public class StatisticsController extends BaseController {
  private ArrayList<Quest> quests;
  @FXML
  private VBox chartContainer;

  public void afterMainController() throws SQLException {
    database.connect();
    quests = database.getAllQuests(user);
    database.disconnect();
    displayStatistics();
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

  public int getUncompleteQuestNumber() {
    int uncompletedQuests = 0;
    for (Quest quest : quests) {
      if (!quest.getCompletionState()) {
        uncompletedQuests++;
      }
    }
    return uncompletedQuests;
  }

  public int getCreatedQuestsNumber() {
    return quests.size();
  }

  public int getAverageOpenBoxTimes() {
    int totalOpenBoxTimes = 0;
    for (Quest quest : quests) {
      totalOpenBoxTimes += quest.getBoxOpenTimes();
    }
    if (quests.size() == 0) {
      return 0;
    } else {
      return totalOpenBoxTimes / quests.size();
    }

  }

  public int getAverageOpenBoxInterval() {
    int totalQuestTimeSpent = 0;
    int totalBoxOpenTimes = 0;
    for (Quest quest : quests) {
      int timeSpent = (int) ((quest.getEndTime().getTime() - quest.getStartTime().getTime()) / 60000);
      totalQuestTimeSpent += timeSpent;
      totalBoxOpenTimes += quest.getBoxOpenTimes() + 1;
    }
    if (totalBoxOpenTimes == 0) {
      return 0;
    } else {
      return totalQuestTimeSpent / totalBoxOpenTimes;

    }
  }

  //Calculate the average time spent on a quest
  public int getAverageTimeSpentOnQuest() {
    int totalQuestTimeSpent = 0;
    for (Quest quest : quests) {
      int timeSpent = (int) ((quest.getEndTime().getTime() - quest.getStartTime().getTime()) / 60000);
      totalQuestTimeSpent += timeSpent;
    }
    if (quests.isEmpty()) {
      return 0;
    } else {
      return totalQuestTimeSpent / quests.size();
    }
  }

  private void displayStatistics() {
    chartContainer.getChildren().clear();
    chartContainer.getChildren().add(createQuestStatusPieChart());
    chartContainer.getChildren().add(createCompletedQuestsBarChart());
    //chartContainer.getChildren().add(createAverageTimeSpentLineChart());
    chartContainer.getChildren().add(createAverageOpenBoxTimesChart());
    //chartContainer.getChildren().add(createAverageOpenBoxIntervalsChart());
  }

  private PieChart createQuestStatusPieChart() {
    ObservableList<PieChart.Data> pieChartData =
            FXCollections.observableArrayList(
                    new PieChart.Data("Completed", getCompletedQuestsNumber()),
                    new PieChart.Data("In Progress", getInProgressQuestsNumber()),
                    new PieChart.Data("Uncompleted", getUncompleteQuestNumber()));
    return new PieChart(pieChartData);
  }

  private BarChart<String, Number> createCompletedQuestsBarChart() {
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Number of Completed Quests");
    //set x axis lable to color white
    xAxis.setStyle("-fx-tick-label-fill: black;");

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Completed Quests in the Last 8 Weeks");

    barChart.setCategoryGap(50);
    barChart.setBarGap(2);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Completed Quests");

    Map<String, Integer> weeklyCompletions = calculateWeeklyCompletions();
    for (Map.Entry<String, Integer> entry : weeklyCompletions.entrySet()) {
      series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }

    barChart.getData().add(series);
    return barChart;
  }


  private Map<String, Integer> calculateWeeklyCompletions() {
    Map<LocalDate, Integer> weeklyCompletionsMap = new HashMap<>();
    LocalDate now = LocalDate.now(ZoneId.systemDefault());
    LocalDate eightWeeksAgo = now.minusWeeks(7); // Start of the period to consider

    for (Quest quest : quests) {
      if (quest.getCompletionState() && quest.getEndTime() != null) {
        LocalDate completionDate = toLocalDate(quest.getEndTime());
        if (completionDate.isAfter(eightWeeksAgo) || completionDate.equals(eightWeeksAgo)) {
          LocalDate startOfWeek = getStartOfWeek(completionDate);
          weeklyCompletionsMap.put(startOfWeek, weeklyCompletionsMap.getOrDefault(startOfWeek, 0) + 1);
        }
      }
    }

    // Ensure data for exactly the last 8 weeks, even if no quests were completed in one or more of those weeks
    Map<String, Integer> formattedWeeklyCompletions = new LinkedHashMap<>();
    for (int i = 0; i < 8; i++) {
      LocalDate date = eightWeeksAgo.plusWeeks(i);
      LocalDate startOfWeek = getStartOfWeek(date);
      int weekOfYear = startOfWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
      formattedWeeklyCompletions.put("Week " + weekOfYear, weeklyCompletionsMap.getOrDefault(startOfWeek, 0));
    }

    return formattedWeeklyCompletions;
  }

  private Map<String, Double> calculateAverageOpenBoxTimesPerWeek() {
    Map<LocalDate, List<Integer>> weeklyBoxOpens = new HashMap<>();
    LocalDate now = LocalDate.now(ZoneId.systemDefault());
    LocalDate eightWeeksAgo = now.minusWeeks(7); // Start of the period to consider

    for (Quest quest : quests) {
      if (quest.getEndTime() != null) {
        LocalDate completionDate = toLocalDate(quest.getEndTime());
        if (completionDate.isAfter(eightWeeksAgo) || completionDate.equals(eightWeeksAgo)) {
          LocalDate startOfWeek = getStartOfWeek(completionDate);
          weeklyBoxOpens.putIfAbsent(startOfWeek, new ArrayList<>());
          weeklyBoxOpens.get(startOfWeek).add(quest.getBoxOpenTimes());
        }
      }
    }

    Map<String, Double> averageBoxOpensPerWeek = new LinkedHashMap<>();
    for (Map.Entry<LocalDate, List<Integer>> entry : weeklyBoxOpens.entrySet()) {
      int total = entry.getValue().stream().mapToInt(Integer::intValue).sum();
      double average = total / (double) entry.getValue().size();
      int weekOfYear = entry.getKey().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
      averageBoxOpensPerWeek.put("Week " + weekOfYear, average);
    }

    return averageBoxOpensPerWeek;
  }

  private Map<String, Double> calculateAverageOpenBoxIntervalsPerWeek() {
    Map<LocalDate, List<Integer>> weeklyBoxIntervals = new HashMap<>();
    LocalDate now = LocalDate.now(ZoneId.systemDefault());
    LocalDate eightWeeksAgo = now.minusWeeks(7); // Start of the period to consider

    for (Quest quest : quests) {
      if (quest.getEndTime() != null) {
        LocalDate completionDate = toLocalDate(quest.getEndTime());
        if (completionDate.isAfter(eightWeeksAgo) || completionDate.equals(eightWeeksAgo)) {
          LocalDate startOfWeek = getStartOfWeek(completionDate);
          weeklyBoxIntervals.putIfAbsent(startOfWeek, new ArrayList<>());
          int interval = (int) ((quest.getEndTime().getTime() - quest.getStartTime().getTime()) / 60000);
          weeklyBoxIntervals.get(startOfWeek).add(interval);
        }
      }
    }

    Map<String, Double> averageBoxIntervalsPerWeek = new LinkedHashMap<>();
    for (Map.Entry<LocalDate, List<Integer>> entry : weeklyBoxIntervals.entrySet()) {
      int total = entry.getValue().stream().mapToInt(Integer::intValue).sum();
      double average = total / (double) entry.getValue().size();
      int weekOfYear = entry.getKey().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
      averageBoxIntervalsPerWeek.put("Week " + weekOfYear, average);
    }

    return averageBoxIntervalsPerWeek;
  }

  private BarChart<String, Number> createAverageOpenBoxTimesChart() {
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Average Open Box Times");

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Average Open Box Times in the Last 8 Weeks");

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Average Open Box Times");

    Map<String, Double> averageOpenBoxTimes = calculateAverageOpenBoxTimesPerWeek();
    for (Map.Entry<String, Double> entry : averageOpenBoxTimes.entrySet()) {
      series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }

    barChart.getData().add(series);
    return barChart;
  }




  private LocalDate toLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  private LocalDate getStartOfWeek(LocalDate date) {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
  }

}

