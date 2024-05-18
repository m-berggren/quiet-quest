package quietquest.model;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PomodoroTimer implements Activity {
	private final String FOCUS_TIME_START = "Focus time started";
	private final String BREAK_TIME_START = "Break time started";
	private final String BREAK_TIME_END = "Break time ended";
	private final String POMODORO_FINISH = "Pomodoro timer finished";
	private Timer timer; // Timer to schedule tasks
	private int questId;
	private final int focusTime;
	private final int breakTime;
	private final int interval;
	private ArrayList<PomodoroUIUpdater> observers;
	private final int MILLISECONDS = 1_000; // Used to convert minutes to milliseconds. 60_000 is the needed value.

	// Ideas from https://egandunning.com/projects/timemanagement-timer.html
	// and https://www.geeksforgeeks.org/java-util-timer-class-java/

	// ==============================* CONSTRUCTORS *=======================================

	/**
	 * Used for creating a container with the necessary information to pass into database. Not intended to use within
	 * application otherwise.
	 *
	 * @param focusTime the focus time in minutes.
	 * @param breakTime the break time in minutes.
	 * @param interval the number of intervals.
	 */
	public PomodoroTimer(int focusTime, int breakTime, int interval) {
		this.timer = new Timer();
		this.focusTime = focusTime;
		this.breakTime = breakTime;
		this.interval = interval;
		this.observers = new ArrayList<>();
	}

	/**
	 * Used to create full pomodoro_timer object when querying database.
	 *
	 * @param questId the quest ID.
	 * @param focusTime the focus time in minutes.
	 * @param breakTime the break time in minutes.
	 * @param interval the number of intervals.
	 */
	public PomodoroTimer(int questId, int focusTime, int breakTime, int interval) {
		this.timer = new Timer();
		this.questId = questId;
		this.focusTime = focusTime;
		this.breakTime = breakTime;
		this.interval = interval;
		this.observers = new ArrayList<>();
	}

	// ==============================* GETTERS & SETTERS *==================================

	public int getFocusTime() {
		return focusTime;
	}

	public int getBreakTime() {
		return breakTime;
	}

	public int getInterval() {
		return interval;
	}

	public int getQuestId() {
		return questId;
	}

	// ==============================* OBSERVER MANAGEMENT *================================
	/* The idea is that since each new entry to another view in the application will create a new stage (the
	 * PomodoroTimer cares about the QuestController and its update method), it should store an observer with every new
	 * creation and add it to the list of controllers. Similar with removal of observer, when the stage is hidden it
	 * gets removed from the list of observers, so it no longer notifies the update method in that specific controller.
	 */

	/**
	 * Adds an observer to the list of observers.
	 *
	 * @param observer being the PomodoroUIUpdater implemented in QuestController.
	 */
	public void addObserver(PomodoroUIUpdater observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	/**
	 * Removes an observer from the list of observers.
	 *
	 * @param observer the observer to remove.
	 */
	public void removeObserver(PomodoroUIUpdater observer) {
		observers.remove(observer);
	}

	// ==============================* INTERFACE METHODS *==================================

	/**
	 * Handles the start operation of the pomodoroTimer. Needs to start with 0 as the value increments on each
	 * subsequent call. Additional information of how the PomodoroTimer runs at {@link #runTimer(int)}.
	 */
	public void start() {
		if (timer != null) {
			runTimer(0);
		}
	}

	/**
	 * Ends the PomodoroTimer and notifies the observer. Used in {@link #runTimer(int)} when base case is true and then
	 * cancels the timer.
	 */
	public void end() {
		if (timer != null) {
			notifyPomodoroObserver(POMODORO_FINISH);
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public QuestType getType() {
		return QuestType.POMODORO;
	}

	// ==============================* TIMER MANAGEMENT *===================================

	/**
	 * Notifies the observer with a specific event message.
	 *
	 * If and observer is set the method uses {@link Platform#runLater(Runnable)} to make sure the update is done on
	 * a JavaFX thread.
	 *
	 * @param event the event message passed to observer.
	 */
	private void notifyPomodoroObserver(String event) {
		for (PomodoroUIUpdater observer : observers) {
			// Schedule update so that it runs on a JavaFX thread instead of the Timer
			Platform.runLater(() -> {
				observer.update(event);
			});
		}
	}

	/**
	 * Recursively schedules focus and break intervals until the specified number of intervals is reached.
	 * The method operates like this:
	 *
	 * - First, it checks if the current interval has reached the total number of intervals. If true, it
	 *   calls {@link #end()} to stop the timer and return.
	 * - Next, it calls {@link #startFocusTime()} to notify observers that the focus time has started.
	 * - A {@link TimerTask} is then scheduled for the focus period. When the focus period ends, the task checks
	 *   if more intervals remain. If more intervals remain, it calls {@link #startBreakTime(int)} to begin the break
	 *   period.
	 * - If the current interval is the last one, it skips the break and calls {@link #end()} to finish the timer.
	 *
	 * Once runTimer's base case is true for one of the recursive calls in {@link #startBreakTime(int)}, they will start
	 * returning until all is ended and PomodoroTimer is finished.
	 *
	 * @param currentInterval int that starts off at 0 and increments by 1 on each call.
	 */
	private void runTimer(int currentInterval) {
		if (currentInterval >= interval) {
			end();
			return;
		}

		// Notifies the observer, meaning update() method in QuestController runs on all stored observers
		startFocusTime();

		// Use of anonymous class for a one-time-use to override run() method
		TimerTask focusTask = new TimerTask() {
			@Override
			public void run() {
				if (timer != null) {
					if (currentInterval < interval - 1) { // Skip the last break as it is unnecessary
						startBreakTime(currentInterval);
					} else {
						end();
					}
				}
			}
		};

		/* Schedule method works similar to scheduleAtFixedRate in this use case as there will be no delays, each
		 *  focusTime and breakTime are fixed and cannot change. */
		timer.schedule(focusTask, toMilliseconds(focusTime));
	}

	/**
	 * Notifies the observer at {@link quietquest.controller.QuestController} that focus time has started. Called at the
	 * beginning of each focus period. The actual notification is handled by {@link #notifyPomodoroObserver(String)}.
	 */
	private void startFocusTime() {
		notifyPomodoroObserver(FOCUS_TIME_START);
	}

	/**
	 * Starts the break time after a focus time ends.
	 *
	 * This method notifies the observer that the break time has started, schedules a {@link TimerTask} for the
	 * end of the break and will recursively call {@link #runTimer(int)} to start the next focus time.
	 *
	 * @param currentInterval the current count. Increments after break time ends.
	 */
	private void startBreakTime(int currentInterval) {
		notifyPomodoroObserver(BREAK_TIME_START);
		TimerTask breakTask = new TimerTask() {
			@Override
			public void run() {
				if (timer != null) {
					notifyPomodoroObserver(BREAK_TIME_END);
					// After break has ended starts the next focus time
					runTimer(currentInterval + 1);
				}
			}
		};
		timer.schedule(breakTask, toMilliseconds(breakTime));
	}

	// ==============================* UTILITY *====================================

	/**
	 * Converts minutes to milliseconds.
	 *
	 * @param minutes to convert.
	 * @return equivalent time in milliseconds.
	 */
	private int toMilliseconds(int minutes) {
		return minutes * MILLISECONDS;
	}

	@Override
	public String toString() {
		return String.format("Focus Time: %d\nBreak Time: %d\nIntervals: %d\n", focusTime, breakTime, interval);
	}
}
