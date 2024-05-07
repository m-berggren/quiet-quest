package quietquest.model;

import quietquest.utility.MQTTHandler;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Quest {

    // Attributes of Quest
    private String title;
    private String description;
    private ArrayList<Activity> activities;
    private MQTTHandler mqttHandler;
    private boolean completionState;
    private Timestamp startTime;
    private Timestamp completeTime;
    private int boxOpenTimes;


    // Constructor
    public Quest(String title, String description, ArrayList<Activity> activities) {
        this.title = title;
        this.description = description;
        this.activities = activities;
        this.mqttHandler = MQTTHandler.getInstance();
        this.completionState = false;
        this.startTime = null;
        this.completeTime = null;
        this.boxOpenTimes = 0;

    }

    public Quest(String title, String description, ArrayList<Activity> activities, boolean completionState, Timestamp startTime, Timestamp completeTime, int boxOpenTimes) {
        this.title = title;
        this.description = description;
        this.activities = activities;
        this.mqttHandler = MQTTHandler.getInstance();
        this.completionState = completionState;
        this.startTime = startTime;
        this.completeTime = completeTime;
        this.boxOpenTimes = boxOpenTimes;
    }

    public void startActivity() {
        if (activities.isEmpty()) {
            return;
        }
        if (activities.getFirst() instanceof PomodoroTimer) {
            Activity activity = activities.getFirst();
            activity.start(); // Mqtt publish happens inside recursive function
        } else if (activities.getFirst() instanceof Task) {
            // Update startTime for all tasks
            for (Activity activity : activities) {
                Task task = (Task) activity;
                task.setStartTime(Timestamp.from(Instant.now()));
            }
        }
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public int getBoxOpenTimes() {
        return boxOpenTimes;
    }

    public void setBoxOpenTimes(int boxOpenTimes) {
        this.boxOpenTimes = boxOpenTimes;
    }

    public Timestamp getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Timestamp completeTime) {
        this.completeTime = completeTime;
    }

    public void setCompletionState(boolean completionState) {
        this.completionState = completionState;
    }

    public boolean getCompletionState() {
        return completionState;
    }

    // Getters
    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }


    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void endActivity() {
        if (activities.isEmpty()) {
            return;
        }
        if (activities.getFirst() instanceof PomodoroTimer) {
            Activity activity = activities.getFirst();
            activity.end(); // Will end recursive PomodoroTimer calls
        } else {
            // May include publish message from Task later
        }
    }

    @Override
    public String toString() {
        return title;
    }

}





