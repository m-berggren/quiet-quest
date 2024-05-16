package quietquest.model;

import javafx.application.Platform;

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
    private PomodoroUIUpdater pomodoroObserver;
    private final int milliSeconds = 1_000; // Default is 60_000

    // Ideas from https://egandunning.com/projects/timemanagement-timer.html
    // and https://www.geeksforgeeks.org/java-util-timer-class-java/

    /**
     * Used for creating a container with the necessary information to pass into database. Not intended to use within
     * application otherwise.
     */
    public PomodoroTimer(int focusTime, int breakTime, int interval) {
        this.timer = new Timer();
        this.focusTime = focusTime;
        this.breakTime = breakTime;
        this.interval = interval;
        this.pomodoroObserver = null;
    }

    /**
     * Used to create full pomodoro_timer object when querying database,
     */
    public PomodoroTimer(int questId, int focusTime, int breakTime, int interval) {
        this.timer = new Timer();
        this.questId = questId;
        this.focusTime = focusTime;
        this.breakTime = breakTime;
        this.interval = interval;
        this.pomodoroObserver = null;
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

    public void setPomodoroObserver(PomodoroUIUpdater pomodoroObserver) {
        this.pomodoroObserver = pomodoroObserver;
    }

	public int getQuestId() {
		return questId;
	}

	public QuestType getType() {
		return QuestType.POMODORO;
	}

	// ==============================* INTERFACE METHODS *====================================

	/**
	 * Handles the start operation of the pomodoroTimer. Needs to start with 0 as the value increments on each
	 * subsequent call.
	 */
	@Override
	public void start() {
		runTimer(0);
	}

	/**
	 * Ends the PomodoroTimer and notifies the observer. Used in {@link #runTimer(int)} when base case is true and then
	 * cancels the timer.
	 */
	@Override
	public void end() {
		notifyPomodoroObserver(POMODORO_FINISH);
		timer.cancel();
	}

	// ==============================* TIMER MANAGEMENT *===================================

	/**
	 * Notifies the observer with a specific event message.
	 *
	 * <p>If and observer is set the method uses {@link Platform#runLater(Runnable)} to make sure the update is done on
	 * a JavaFX thread.</p>
	 *
	 * @param event the event message passed to observer.
	 */
    private void notifyPomodoroObserver(String event) {
        if (pomodoroObserver != null) {
            // Schedule update so that it runs on a JavaFX thread instead of the Timer
            Platform.runLater(() -> {
                pomodoroObserver.update(event);
			});
		}
	}

	/**
	 * Recursively schedules focus and break intervals until the specified number of intervals is reached.
	 *
	 * <p>It starts off by notifying the controller {@link quietquest.controller.QuestController} that it should
	 * publish to the wio terminal. It then creates a TimerTask through an anonymous class where we only care about
	 * creating a one-time-use class with one override on run() method. It checks if currentInterval interval is smaller
	 * than interval -1, meaning that it should not run a break after the last focusTime has run.
	 * {@link #startBreakTime(int)} notifies to controller that break has started and after breakTime is over creates
	 * a new focus session but now with currentInterval + 1.</p>
	 *
	 * <p>The lambda expression could be specified as an anonymous class of Runnable that the Platform.runLater expects as
	 * parameter. Platform.runLater(Runnable) means that the runnable is posted to an event queue. While documentation
	 * states the application "should avoid flooding JavaFX with too many pending Runnables", but in the scope of this
	 * project this has not been issue.</p>
	 *
	 * <p>Once runTimer's base case is true for one of the recursive calls in {@link #startBreakTime(int)}, they will start
	 * returning until all is ended and PomodoroTimer is finished.</p>
     *
     * @param currentInterval int that starts off at 0 and increments by 1 on each call.
     */
    private void runTimer(int currentInterval) {
        if (currentInterval >= interval) {
            end();
            return;
		}

		startFocusTime();
		//
        TimerTask focusTask = new TimerTask() {
            @Override
            public void run() {
                if (currentInterval < interval - 1) { // Skip the last break as it is unnecessary
                    startBreakTime(currentInterval);
                } else {
                    Platform.runLater(() -> end()); // If last interval then we run end()
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
	 * <p>This method notifies the observer that the break time has started, schedules a {@link TimerTask} for the
	 * end of the break and will recursively call {@link #runTimer(int)} to start the next focus time.</p>
	 *
	 * @param currentInterval the current count. Increments after break time ends.
     */
    private void startBreakTime(int currentInterval) {
        notifyPomodoroObserver(BREAK_TIME_START);
        TimerTask breakTask = new TimerTask() {
            @Override
            public void run() {
                notifyPomodoroObserver(BREAK_TIME_END);
                // After break has ended starts the next focus time
                runTimer(currentInterval + 1);
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
		return minutes * milliSeconds;
	}

    @Override
    public String toString() {
        return String.format("Focus Time: %d\nBreak Time: %d\nIntervals: %d\n", focusTime, breakTime, interval);
    }
}
