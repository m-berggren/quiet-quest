package quietquest.model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import quietquest.utility.MQTTHandler;

import java.util.Timer;
import java.util.TimerTask;

public class PomodoroTimer implements Activity {
    private Timer timer;
    private int questId;
    private final int focusTime;
    private final int breakTime;
    private int interval;
    private boolean isBreak;
    private boolean isInterrupted;
    private final String PUB_TOPIC = "/quietquest/application/pomodoro_done";
    private final int milliSeconds = 1_000; // Default is 60_000

    // Ideas from https://egandunning.com/projects/timemanagement-timer.html
    // and https://www.geeksforgeeks.org/java-util-timer-class-java/

    public PomodoroTimer(int focusTime, int breakTime, int interval) {
        this.timer = new Timer();
        this.focusTime = focusTime;
        this.breakTime = breakTime;
        this.interval = interval;
        this.isBreak = false;
        this.isInterrupted = false;
    }

    public PomodoroTimer(int questId, int focusTime, int breakTime, int interval) {
        this.timer = new Timer();
        this.questId = questId;
        this.focusTime = focusTime;
        this.breakTime = breakTime;
        this.interval = interval;
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

    /**
     * Creates milliseconds from minutes
     *
     * @param minutes to convert
     * @return milliseconds
     */
    private int toMilliseconds(int minutes) {
        return minutes * milliSeconds;
    }

    @Override
    public void start() {
        //mqttHandler.publishMessage(PUB_TOPIC, "pomodoro_started");
        scheduleNextTask(isInterrupted);
    }

    public int getQuestId() {
        return questId;
    }

    public QuestType getType() {
        return QuestType.POMODORO;
    }
    /**
     *
     */
    private void scheduleNextTask(boolean isInterrupted) {
        if (interval <= 0 || isInterrupted) { // Need a stop for recursive function, isInterrupted through manual exit
            stopPomodoro();
            return;
        }

        TimerTask task;
        int delay;

        if (isBreak) {
            task = new BreakTime(this);
            delay = toMilliseconds(breakTime);
        } else {
            task = new FocusTime(this);
            delay = toMilliseconds(focusTime);
        }
        if (!isInterrupted) {
            timer.schedule(task, delay);
        }
    }

    /**
     *
     */
    public void completeTask() {
        if (isInterrupted) {
            stopPomodoro();
            return;
        }

        String taskType = isBreak ? "Break" : "Focus";
        alert(taskType + "interval complete!");

        if (!isBreak) {
            interval--;
        }
        isBreak = !isBreak;
        scheduleNextTask(isInterrupted);
    }

    public void end() {
        isInterrupted = true;
        stopPomodoro();
    }

    public void stopPomodoro() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        alert("Pomodoro session complete!");
        //mqttHandler.publishMessage(PUB_TOPIC, "pomodoro_finished");
    }

    private void alert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pomodoro Timer");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }

    @Override
    public String toString() {
        return String.format("Focus Time: %d\nBreak Time: %d\nIntervals: %d\n", focusTime, breakTime, interval);
    }

    private class FocusTime extends TimerTask {
        private final PomodoroTimer session;

        private FocusTime(PomodoroTimer session) {
            this.session = session;
        }

        @Override
        public void run() {
            session.completeTask();
        }
    }

    private class BreakTime extends TimerTask {
        private final PomodoroTimer session;

        private BreakTime(PomodoroTimer session) {
            this.session = session;
        }

        @Override
        public void run() {
            session.completeTask();
        }
    }

}
