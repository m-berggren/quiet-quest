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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

import static javafx.geometry.Pos.CENTER;

public class StatisticsController extends BaseController {


	private ArrayList<Quest> quests;
	private final int millisecondInHour = 3600000;
	@FXML
	private ScrollPane chartContainerPane;
	@FXML
	private VBox chartContainer;
	@FXML
	private Label statisticsTitle;

	/**
	 * This method is called after the main controller has been initialized.
	 */
	public void afterMainController() {
		quests = getAllQuests();
		displayStatistics();
	}

	/**
	 * Get all complete quests from the database
	 *
	 * @return a number of all completed quests
	 */
	public int getCompletedQuestsNumber() {
		int completedQuests = 0;
		for (Quest quest : quests) {
			if (quest.getCompletionState()) {
				completedQuests++;
			}
		}
		return completedQuests;
	}

	/**
	 * Get all in progress quests from the database
	 *
	 * @return number of all in progress quests
	 */
	public int getInProgressQuestsNumber() {
		int inProgressQuests = 0;
		for (Quest quest : quests) {
			if (!quest.getCompletionState() && quest.getStartTime() != null) {
				inProgressQuests++;
			}
		}
		return inProgressQuests;
	}

	/**
	 * Get all unstarted quests from the database
	 *
	 * @return number of all unstarted quests
	 */
	public int getUnstartedQuestNumber() {
		int unstartedQuestNumber = 0;
		for (Quest quest : quests) {
			if (!quest.getCompletionState() && quest.getStartTime() == null) {
				unstartedQuestNumber++;
			}
		}
		return unstartedQuestNumber;
	}

	/**
	 * Get the number of all created quests
	 *
	 * @return number of all created quests
	 */
	public int getCreatedQuestsNumber() {
		return quests.size();
	}

	/**
	 * Get the average open box times on all complete quests
	 *
	 * @return the average open box times on all complete quests
	 */
	public int getAverageOpenBoxTimes() {
		int totalOpenBoxTimes = 0;
		for (Quest quest : quests) {
			if (quest.getCompletionState() && quest.getStartTime() != null && quest.getCompleteTime() != null) {
				totalOpenBoxTimes += quest.getBoxOpenTimes();
			}
		}
		if (quests.isEmpty()) {
			return 0;
		} else {
			return totalOpenBoxTimes / quests.size();
		}

	}

	/**
	 * Get the average open box interval on all complete quests
	 *
	 * @return the average open box interval on all complete quests
	 */
	public int getAverageOpenBoxInterval() {
		int totalQuestTimeSpent = 0;
		int totalBoxOpenTimes = 0;
		for (Quest quest : quests) {
			if (quest.getCompletionState() && quest.getStartTime() != null && quest.getCompleteTime() != null) {
				int timeSpent = (int) ((quest.getCompleteTime().getTime() - quest.getStartTime().getTime()) / millisecondInHour);
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

	/**
	 * Get the average time spent on all quests
	 *
	 * @return the average time spent on all quests
	 */
	public int getAverageTimeSpentOnQuest() {
		int totalQuestTimeSpent = 0;
		for (Quest quest : quests) {
			if (quest.getCompletionState() && quest.getStartTime() != null && quest.getCompleteTime() != null) {
				int timeSpent = (int) ((quest.getCompleteTime().getTime() - quest.getStartTime().getTime()) / millisecondInHour);
				totalQuestTimeSpent += timeSpent;
			}
		}
		if (quests.isEmpty()) {
			return 0;
		} else {
			return totalQuestTimeSpent / quests.size();
		}
	}

	/**
	 * Display the statistics on the statistics page
	 */
	private void displayStatistics() {
		if (quests.isEmpty()) {
			statisticsTitle.setText("No Quests Found");
			chartContainer.getChildren().clear();
		} else {
			chartContainerPane.setContent(chartContainer);
			chartContainer.getChildren().clear();
			//ADD LABELS SHOWING SOME MAJOR STATISTICS
			Label createdQuestsLabel = new Label("Number of All Created Quests: " + getCreatedQuestsNumber());
			createdQuestsLabel.setStyle("-fx-font-size: 16");
			Label averageOpenBoxTimesLabel = new Label("Average Open Box Times on All Complete Quests: " + getAverageOpenBoxTimes());
			averageOpenBoxTimesLabel.setStyle("-fx-font-size: 16;");
			Label averageOpenBoxIntervalLabel = new Label("Average Open Box Interval on All Complete Quests(in hours): " + getAverageOpenBoxInterval());
			averageOpenBoxIntervalLabel.setStyle("-fx-font-size: 16;");
			Label averageTimeSpentOnQuestLabel = new Label("Average Time Spent on All Quests(in hours): " + getAverageTimeSpentOnQuest());
			averageTimeSpentOnQuestLabel.setStyle("-fx-font-size: 16;");
			//Add charts and space between charts
			chartContainer.setSpacing(100);

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
	}

	/**
	 * Create a pie chart showing the status of all quests
	 *
	 * @return a pie chart showing the status of all quests
	 */
	private PieChart createQuestStatusPieChart() {
		ObservableList<PieChart.Data> pieChartData =
				FXCollections.observableArrayList(
						new PieChart.Data("Completed", getCompletedQuestsNumber()),
						new PieChart.Data("In Progress", getInProgressQuestsNumber()),
						new PieChart.Data("Unstarted", getUnstartedQuestNumber()));
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

	/**
	 * Create a bar chart showing the number of completed quests in the last 8 weeks
	 *
	 * @return a bar chart showing the number of completed quests in the last 8 weeks
	 */
	private BarChart<String, Number> createCompletedQuestsBarChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Number of Completed Quests");
		yAxis.setTickUnit(1);

		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle("Completed Quests in the Last 8 Weeks");

		barChart.setCategoryGap(50);
		barChart.setBarGap(2);
		barChart.setMinHeight(350);

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Completed Quests");

		Map<String, Integer> weeklyCompletions = calculateWeeklyCompletions();
		for (Map.Entry<String, Integer> entry : weeklyCompletions.entrySet()) {
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}
		barChart.getData().add(series);
		return barChart;
	}

	/**
	 * Calculate the number of completed quests in the last 8 weeks
	 *
	 * @return a map of the week number of completed quests in the last 8 weeks
	 */
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

	/**
	 * Create a bar chart showing the average time spent on quests in the last 8 weeks
	 *
	 * @return a bar chart showing the average time spent on quests in the last 8 weeks
	 */
	private BarChart<String, Number> createAverageTimeSpentBarChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Average Time Spent on Quests (hours)");
		yAxis.setTickUnit(30);

		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle("Average Time Spent on Quests in the Last 8 Weeks");
		//set min height to ensure the chart is displayed
		barChart.setMinHeight(350);
		barChart.setCategoryGap(50);
		barChart.setBarGap(2);

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Average Time Spent on Quests(hours)");

		Map<String, Double> averageQuestTimesPerWeek = calculateQuestAverageTimeSpent();
		for (Map.Entry<String, Double> entry : averageQuestTimesPerWeek.entrySet()) {
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}

		barChart.getData().add(series);
		return barChart;

	}

	/**
	 * Calculate the average time spent on quests in the last 8 weeks
	 *
	 * @return a map of the average time spent on quests in the last 8 weeks
	 */
	private Map<String, Double> calculateQuestAverageTimeSpent() {
		Map<LocalDate, List<Integer>> weeklyQuestTimes = new HashMap<>();
		LocalDate now = LocalDate.now(ZoneId.systemDefault());
		LocalDate eightWeeksAgo = now.minusWeeks(7); // Start of the period to consider
		for (Quest quest : quests) {
			if (quest.getCompleteTime() != null && quest.getStartTime() != null && quest.getCompletionState()) {
				LocalDate completionDate = toLocalDate(quest.getCompleteTime());
				if (completionDate.isAfter(eightWeeksAgo) || completionDate.equals(eightWeeksAgo)) {
					LocalDate startOfWeek = getStartOfWeek(completionDate);
					weeklyQuestTimes.putIfAbsent(startOfWeek, new ArrayList<>());
					int timeSpent = (int) ((quest.getCompleteTime().getTime() - quest.getStartTime().getTime()) / millisecondInHour);
					weeklyQuestTimes.get(startOfWeek).add(timeSpent);
				}
			}
		}
		return getStringDoubleMap(weeklyQuestTimes, eightWeeksAgo);
	}

	/**
	 * Calculate the average open box times per week
	 *
	 * @return a map of the average open box times in the last 8 weeks
	 */
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
		return getStringDoubleMap(weeklyBoxOpens, eightWeeksAgo);
	}


	/**
	 * Calculate the average open box intervals per week
	 *
	 * @return a map of the average open box intervals per week
	 */
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
					int interval = (int) ((quest.getCompleteTime().getTime() - quest.getStartTime().getTime()) / millisecondInHour);
					weeklyBoxIntervals.get(startOfWeek).add(interval);
				}
			}
		}
		return getStringDoubleMap(weeklyBoxIntervals, eightWeeksAgo);
	}

	/**
	 * Create a bar chart showing the average open box times in the last 8 weeks
	 *
	 * @return a bar chart showing the average open box times in the last 8 weeks
	 */
	private BarChart<String, Number> createAverageOpenBoxTimesChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Average Open Box Times Per Quest");

		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle("Average Open Box Times Per Quest in the Last 8 Weeks");
		barChart.setMinHeight(350);
		barChart.setCategoryGap(50);
		barChart.setBarGap(2);

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Average Open Box Times Per Quest");

		Map<String, Double> averageOpenBoxTimes = calculateAverageOpenBoxTimesPerWeek();
		for (Map.Entry<String, Double> entry : averageOpenBoxTimes.entrySet()) {
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}

		barChart.getData().add(series);
		return barChart;
	}


	/**
	 * Create a bar chart showing the average open box intervals in the last 8 weeks
	 *
	 * @return a bar chart showing the average open box intervals in the last 8 weeks
	 */
	private BarChart<String, Number> createAverageOpenBoxIntervalsChart() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Average Open Box Time Interval(hours)");

		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle("Average Open Box Time Interval in the Last 8 Weeks");
		barChart.setMinHeight(350);
		barChart.setCategoryGap(50);
		barChart.setBarGap(2);

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Average Open Box Time Interval(hours)");

		Map<String, Double> averageOpenBoxIntervals = calculateAverageOpenBoxIntervalsPerWeek();
		for (Map.Entry<String, Double> entry : averageOpenBoxIntervals.entrySet()) {
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}

		barChart.getData().add(series);
		return barChart;
	}

	/**
	 * Get the average of the values in the map
	 *
	 * @param weeklyMap     the map to create from
	 * @param eightWeeksAgo the date 8 weeks ago
	 * @return a map of the average values
	 */
	private Map<String, Double> getStringDoubleMap(Map<LocalDate, List<Integer>> weeklyMap, LocalDate eightWeeksAgo) {
		Map<String, Double> averagePerWeek = new LinkedHashMap<>();
		for (Map.Entry<LocalDate, List<Integer>> entry : weeklyMap.entrySet()) {
			int total = entry.getValue().stream().mapToInt(Integer::intValue).sum();
			double average = total / (double) entry.getValue().size();
			int weekOfYear = entry.getKey().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
			averagePerWeek.put("Week " + weekOfYear, average);
		}

		Map<String, Double> formattedAverageBoxOpens = new LinkedHashMap<>();
		for (int i = 0; i < 8; i++) {
			LocalDate date = eightWeeksAgo.plusWeeks(i);
			LocalDate startOfWeek = getStartOfWeek(date);
			int weekOfYear = startOfWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
			formattedAverageBoxOpens.put("Week " + weekOfYear, averagePerWeek.getOrDefault("Week " + weekOfYear, averagePerWeek.getOrDefault(startOfWeek, 0.0)));
		}

		return formattedAverageBoxOpens;
	}

	/**
	 * Convert a date to a local date
	 *
	 * @param date to convert
	 * @return the date as a local date
	 */
	private LocalDate toLocalDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * @param date the date to get the start of the week from
	 * @return the start date of the week
	 */
	private LocalDate getStartOfWeek(LocalDate date) {
		return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

}

