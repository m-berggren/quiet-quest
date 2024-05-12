package quietquest.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import quietquest.model.Quest;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.ArrayList;

import static javafx.geometry.Pos.*;

public class StatisticsController extends BaseController {


  private ArrayList<Quest> quests;
  @FXML
  private ScrollPane chartContainerPane;
  @FXML
  private VBox chartContainer;
  @FXML
  private Label statisticsTitle;
  @FXML
  public VBox statisticsLabel;

    public void afterMainController() {
        quests = getAllQuests();
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
      if (!quest.getCompletionState() && quest.getStartTime() != null) {
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
      if (quest.getCompletionState() && quest.getStartTime()!=null && quest.getCompleteTime()!=null) {
        totalOpenBoxTimes += quest.getBoxOpenTimes();
      }
    }
    if (quests.isEmpty()) {
      return 0;
    } else {
      return totalOpenBoxTimes / quests.size();
    }

  }

  public int getAverageOpenBoxInterval() {
    int totalQuestTimeSpent = 0;
    int totalBoxOpenTimes = 0;
    int averageOpenBoxInterval = 0;
    for (Quest quest : quests) {
      if (quest.getCompletionState() && quest.getStartTime()!=null && quest.getCompleteTime()!=null) {
        int timeSpent = (int) ((quest.getCompleteTime().getTime() - quest.getStartTime().getTime()) / 60000);
        totalQuestTimeSpent += timeSpent;
        totalBoxOpenTimes += quest.getBoxOpenTimes() + 1;
      }
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
      if (quest.getCompletionState() && quest.getStartTime()!=null && quest.getCompleteTime()!=null) {
        int timeSpent = (int) ((quest.getCompleteTime().getTime() - quest.getStartTime().getTime()) / 60000);
        totalQuestTimeSpent += timeSpent;
      }
    }
    if (quests.isEmpty()) {
      return 0;
    } else {
      return totalQuestTimeSpent / quests.size();
    }
  }


  private void displayStatistics() {
    chartContainerPane.setContent(chartContainer);
    chartContainer.getChildren().clear();
    //ADD LABELS SHOWING SOME MAJOR STATISTICS
    Label createdQuestsLabel = new Label("Number of All Created Quests: " + getCreatedQuestsNumber());
    createdQuestsLabel.setStyle("-fx-font-size: 16");
    Label averageOpenBoxTimesLabel = new Label("Average Open Box Times on All Complete Quests: " + getAverageOpenBoxTimes());
    averageOpenBoxTimesLabel.setStyle("-fx-font-size: 16;");
    Label averageOpenBoxIntervalLabel = new Label("Average Open Box Interval on All Complete Quests: " + getAverageOpenBoxInterval());
    averageOpenBoxIntervalLabel.setStyle("-fx-font-size: 16;");
    Label averageTimeSpentOnQuestLabel = new Label("Average Time Spent on All Quests: " + getAverageTimeSpentOnQuest());
    averageTimeSpentOnQuestLabel.setStyle("-fx-font-size: 16;");
    //Add charts and space between charts
    chartContainer.setSpacing(80);
    chartContainer.getChildren().add(statisticsLabel);

    VBox allQuests = new VBox();
    allQuests.setAlignment(CENTER);
    allQuests.getChildren().add(createQuestStatusPieChart());
    allQuests.getChildren().add(createdQuestsLabel);

    VBox completedQuests = new VBox();
    completedQuests.setAlignment(CENTER);
    completedQuests.getChildren().add(createCompletedQuestsBarChart());

    VBox averageTimeSpent = new VBox();
    averageTimeSpent.setAlignment(CENTER);
    averageTimeSpent.getChildren().add(createAverageTimeSpentBarChart());
    averageTimeSpent.getChildren().add(averageTimeSpentOnQuestLabel);

    VBox averageOpenBoxTimes = new VBox();
    averageOpenBoxTimes.setAlignment(CENTER);
    averageOpenBoxTimes.getChildren().add(createAverageOpenBoxTimesChart());
    averageOpenBoxTimes.getChildren().add(averageOpenBoxTimesLabel);

    VBox averageOpenBoxInterval = new VBox();
    averageOpenBoxInterval.setAlignment(CENTER);
    averageOpenBoxInterval.getChildren().add(createAverageOpenBoxIntervalsChart());
    averageOpenBoxInterval.getChildren().add(averageOpenBoxIntervalLabel);


    chartContainer.getChildren().add(allQuests);
    chartContainer.getChildren().add(completedQuests);
    chartContainer.getChildren().add(averageTimeSpent);
    chartContainer.getChildren().add(averageOpenBoxTimes);
    chartContainer.getChildren().add(averageOpenBoxInterval);

  }


  private PieChart createQuestStatusPieChart() {
    ObservableList<PieChart.Data> pieChartData =
            FXCollections.observableArrayList(
                    new PieChart.Data("Completed", getCompletedQuestsNumber()),
                    new PieChart.Data("In Progress", getInProgressQuestsNumber()),
                    new PieChart.Data("Uncompleted", getUncompleteQuestNumber()));
    pieChartData.forEach(data -> data.nameProperty().bind(
            Bindings.concat(
                    data.getName(), ": ", data.pieValueProperty().asString("%.0f")
            ))
    );

    //show label of each part of pie chart
    PieChart pieChart = new PieChart(pieChartData);
    pieChart.setTitle("All Quest Status");
    pieChart.setMinSize(300, 300);
    return pieChart;
  }

  private BarChart<String, Number> createCompletedQuestsBarChart() {
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Number of Completed Quests");
    yAxis.setTickUnit(1);

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Completed Quests in the Last 8 Weeks");

    barChart.setCategoryGap(50);
    barChart.setBarGap(2);
    barChart.setMinHeight(300);

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
      if (quest.getCompletionState() && quest.getCompleteTime() != null) {
        LocalDate completionDate = toLocalDate(quest.getCompleteTime());
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

  private BarChart<String, Number> createAverageTimeSpentBarChart() {
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Average Time Spent on Quests (minutes)");
    yAxis.setTickUnit(30);

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Average Time Spent on Quests in the Last 8 Weeks");
    //set min height to ensure the chart is displayed
    barChart.setMinHeight(400);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Average Time Spent on Quests");

    Map<String, Double> averageQuestTimesPerWeek = calculateQuestAverageTimeSpent();
    for (Map.Entry<String, Double> entry : averageQuestTimesPerWeek.entrySet()) {
      series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }

    barChart.getData().add(series);
    return barChart;

  }

  private Map<String, Double> calculateQuestAverageTimeSpent() {
    Map<LocalDate, List<Integer>> weeklyQuestTimes = new HashMap<>();
    LocalDate now = LocalDate.now(ZoneId.systemDefault());
    LocalDate eightWeeksAgo = now.minusWeeks(7); // Start of the period to consider

    for (Quest quest : quests) {
      if (quest.getCompleteTime() != null && quest.getStartTime() != null) {
        LocalDate completionDate = toLocalDate(quest.getCompleteTime());
        if (completionDate.isAfter(eightWeeksAgo) || completionDate.equals(eightWeeksAgo)) {
          LocalDate startOfWeek = getStartOfWeek(completionDate);
          weeklyQuestTimes.putIfAbsent(startOfWeek, new ArrayList<>());
          int timeSpent = (int) ((quest.getCompleteTime().getTime() - quest.getStartTime().getTime()) / 60000);
          weeklyQuestTimes.get(startOfWeek).add(timeSpent);
        }
      }
    }

    Map<String, Double> averageQuestTimesPerWeek = new LinkedHashMap<>();
    for (Map.Entry<LocalDate, List<Integer>> entry : weeklyQuestTimes.entrySet()) {
      int total = entry.getValue().stream().mapToInt(Integer::intValue).sum();
      double average = total / (double) entry.getValue().size();
      int weekOfYear = entry.getKey().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
      averageQuestTimesPerWeek.put("Week " + weekOfYear, average);
    }

    Map<String, Double> formattedAverageQuestTimes = new LinkedHashMap<>();
    for (int i = 0; i < 8; i++) {
      LocalDate date = eightWeeksAgo.plusWeeks(i);
      LocalDate startOfWeek = getStartOfWeek(date);
      int weekOfYear = startOfWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
      formattedAverageQuestTimes.put("Week " + weekOfYear, averageQuestTimesPerWeek.getOrDefault("Week " + weekOfYear, averageQuestTimesPerWeek.getOrDefault(startOfWeek, 0.0)));
    }

    return formattedAverageQuestTimes;
  }

  private Map<String, Double> calculateAverageOpenBoxTimesPerWeek() {
    Map<LocalDate, List<Integer>> weeklyBoxOpens = new HashMap<>();
    LocalDate now = LocalDate.now(ZoneId.systemDefault());
    LocalDate eightWeeksAgo = now.minusWeeks(7);// Start of the period to consider

    for (Quest quest : quests) {
      if (quest.getCompletionState() && quest.getCompleteTime() != null && quest.getStartTime() != null) {
        LocalDate completionDate = toLocalDate(quest.getCompleteTime());
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

    Map<String, Double> formattedAverageBoxOpens = new LinkedHashMap<>();
    for (int i = 0; i < 8; i++) {
      LocalDate date = eightWeeksAgo.plusWeeks(i);
      LocalDate startOfWeek = getStartOfWeek(date);
      int weekOfYear = startOfWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
      formattedAverageBoxOpens.put("Week " + weekOfYear, averageBoxOpensPerWeek.getOrDefault("Week " + weekOfYear, averageBoxOpensPerWeek.getOrDefault(startOfWeek, 0.0)));
    }

    return formattedAverageBoxOpens;
  }

  private Map<String, Double> calculateAverageOpenBoxIntervalsPerWeek() {
    Map<LocalDate, List<Integer>> weeklyBoxIntervals = new HashMap<>();
    LocalDate now = LocalDate.now(ZoneId.systemDefault());
    LocalDate eightWeeksAgo = now.minusWeeks(7); // Start of the period to consider

    for (Quest quest : quests) {
      if (quest.getCompleteTime() != null && quest.getStartTime() != null && quest.getCompletionState()) {
        LocalDate completionDate = toLocalDate(quest.getCompleteTime());
        if (completionDate.isAfter(eightWeeksAgo) || completionDate.equals(eightWeeksAgo)) {
          LocalDate startOfWeek = getStartOfWeek(completionDate);
          weeklyBoxIntervals.putIfAbsent(startOfWeek, new ArrayList<>());
          int interval = (int) ((quest.getCompleteTime().getTime() - quest.getStartTime().getTime()) / 60000);
          weeklyBoxIntervals.get(startOfWeek).add(interval);
        }
      }
    }

    Map<String, Double> averageBoxIntervalsPerWeek = new LinkedHashMap<>();
    for (Map.Entry<LocalDate, List<Integer>> entry : weeklyBoxIntervals.entrySet()) {
      int total = entry.getValue().stream().mapToInt(x -> x).sum();
      double average = total / (double) entry.getValue().size();
      int weekOfYear = entry.getKey().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
      averageBoxIntervalsPerWeek.put("Week " + weekOfYear, average);
    }

    Map<String, Double> formattedAverageBoxIntervals = new LinkedHashMap<>();
    for (int i = 0; i < 8; i++) {
      LocalDate date = eightWeeksAgo.plusWeeks(i);
      LocalDate startOfWeek = getStartOfWeek(date);
      int weekOfYear = startOfWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
      formattedAverageBoxIntervals.put("Week " + weekOfYear, averageBoxIntervalsPerWeek.getOrDefault("Week " + weekOfYear, averageBoxIntervalsPerWeek.getOrDefault(startOfWeek, 0.0)));
    }
    return formattedAverageBoxIntervals;
  }

  private BarChart<String, Number> createAverageOpenBoxTimesChart() {
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Average Open Box Times");

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Average Open Box Times in the Last 8 Weeks");
    barChart.setMinHeight(300);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Average Open Box Times");

    Map<String, Double> averageOpenBoxTimes = calculateAverageOpenBoxTimesPerWeek();
    for (Map.Entry<String, Double> entry : averageOpenBoxTimes.entrySet()) {
      series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }

    barChart.getData().add(series);
    return barChart;
  }


  private BarChart<String, Number> createAverageOpenBoxIntervalsChart() {
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Average Open Box Interval");

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Average Open Box Interval in the Last 8 Weeks");
    barChart.setMinHeight(300);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Average Open Box Interval");

    Map<String, Double> averageOpenBoxIntervals = calculateAverageOpenBoxIntervalsPerWeek();
    for (Map.Entry<String, Double> entry : averageOpenBoxIntervals.entrySet()) {
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

