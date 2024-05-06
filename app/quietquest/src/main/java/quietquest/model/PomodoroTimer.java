package quietquest.model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import quietquest.utility.MQTTHandler;

import java.util.Timer;
import java.util.TimerTask;

public class PomodoroTimer implements Activity {
    private final Timer timer;
    private final int focusTime;
    private final int breakTime;
    private int intervals;
    private boolean isBreak;
    private final MQTTHandler mqttHandler;
    private final String PUB_TOPIC = "/quietquest/application/start";
    private final int milliSeconds = 1_000; // Default should be 60_000

    // Ideas from https://egandunning.com/projects/timemanagement-timer.html
    // and https://www.geeksforgeeks.org/java-util-timer-class-java/

    public PomodoroTimer(int focusTime, int breakTime, int intervals) {
        this.timer = new Timer();
        this.focusTime = focusTime;
        this.breakTime = breakTime;
        this.intervals = intervals;
        this.isBreak = false;
        this.mqttHandler = MQTTHandler.getInstance();
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
        scheduleNextTask();
    }

    /**
     *
     */
    private void scheduleNextTask() {
        if (intervals <= 0) { // Need a stop for recursive function
            timer.cancel();
            alert("Pomodoro session complete!");
            mqttHandler.publishMessage(PUB_TOPIC, "pomodoro_finished");
            // mqttClient.disconnect(); // Maybe implement disconnect here?
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
        timer.schedule(task, delay);
    }

    /**
     *
     */
    public void completeTask() {
        String taskType = isBreak ? "Break" : "Focus";
        alert(taskType + "interval complete!");

        if (!isBreak) {
            intervals--;
        }
        isBreak = !isBreak;
        scheduleNextTask();
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
        return String.format("Focus Time: %d\nBreak Time: %d\nIntervals: %d\n", focusTime, breakTime, intervals);
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
