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
    private int interval;
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

    /**
     * Creates milliseconds from minutes
     *
     * @param minutes to convert
     * @return milliseconds
     */
    private int toMilliseconds(int minutes) {
        return minutes * milliSeconds;
    }

    private void notifyPomodoroObserver(String event) {
        if (pomodoroObserver != null) {
            // Schedule update so that it runs on a JavaFX thread instead of the Timer
            Platform.runLater(() -> {
                pomodoroObserver.update(event);
            });
        }
    }

    /**
     * Handles the start operation of the pomodoroTimer.
     */
    @Override
    public void start() {
        runTimer(0);
    }

    /**
     * The logic of the timers are handled in {@link #startFocusTime} and {@link #startBreakTime}.
     *
     * @param currentInterval int that starts off at 0 and increments by 1 on each call.
     */
    private void runTimer(int currentInterval) {
        if (currentInterval >= interval) {
            end();
            return;
        }

        startFocusTime();
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

        // Schedule method appear to work better than scheduleAtFixedRate
        timer.schedule(focusTask, toMilliseconds(focusTime));
    }

    /**
     * This method only alerts the observer at {@link quietquest.controller.QuestController} to inform that focus time
     * has started. Publishing MQTT payload and showing UI update happens there.
     */
    private void startFocusTime() {
        notifyPomodoroObserver(FOCUS_TIME_START);
    }

    /**
     *
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

    public int getQuestId() {
        return questId;
    }

    public QuestType getType() {
        return QuestType.POMODORO;
    }

    @Override
    public void end() {
        notifyPomodoroObserver(POMODORO_FINISH);
        timer.cancel();
    }

    public void completeTask() {
    }


    @Override
    public String toString() {
        return String.format("Focus Time: %d\nBreak Time: %d\nIntervals: %d\n", focusTime, breakTime, interval);
    }

    /**
     *
     */
    private class focusTask extends TimerTask {
        private final int currentInterval;

        private focusTask(int currentInterval) {
            this.currentInterval = currentInterval;
        }

        @Override
        public void run() {
            if (currentInterval < interval - 1) { // Skip the last break as it is unnecessary
                startBreakTime(currentInterval);
            }
            runTimer(currentInterval + 1); // Schedules the next interval
        }
    }

    /**
     * Method is just utilized to notify the controller {@link quietquest.controller.QuestController} there is an
     * update. Could utilize an anonymous inner class but this adds clarity.
     */
    private class breakTask extends TimerTask {
        @Override
        public void run() {
            notifyPomodoroObserver("Break time ended");
        }
    }
}
