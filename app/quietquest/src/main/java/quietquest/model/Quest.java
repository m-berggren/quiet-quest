package quietquest.model;

import quietquest.utility.MQTTHandler;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Quest {
    private String title;
    private String description;
    private ArrayList<Activity> activities;
    private MQTTHandler mqttHandler;
    private boolean completionState;
    private Timestamp startTime;
    private Timestamp endTime;
    private int boxOpenTimes;

    public Quest(String title, String description, ArrayList<Activity> activities) {
        this.title = title;
        this.description = description;
        this.activities = activities;
        this.mqttHandler = MQTTHandler.getInstance();
        this.completionState = false;
        this.startTime = null;
        this.endTime = null;
        this.boxOpenTimes = 0;
    }

    public Quest(String title, String description, ArrayList<Activity> activities, boolean completionState, Timestamp startTime, Timestamp completeTime, int boxOpenTimes) {
        this.title = title;
        this.description = description;
        this.activities = activities;
        this.mqttHandler = MQTTHandler.getInstance();
        this.completionState = completionState;
        this.startTime = startTime;
        this.endTime = completeTime;
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
                task.setStartTime(new Timestamp(System.currentTimeMillis()));
            }
        }
    }

    public void endActivity() {
        if (activities.isEmpty()) {
            return;
        }
        if (activities.getFirst() instanceof PomodoroTimer) {
            Activity activity = activities.getFirst();
            activity.end(); // Will end recursive PomodoroTimer calls
        } else if (activities.getFirst() instanceof Task) {
            // Update tasks with end time
            for (Activity activity : activities) {
                Task task = (Task) activity;
                task.setEndTime(new Timestamp(System.currentTimeMillis()));
            }
        }
    }

    // Getters
    public String getTitle() {
        return this.title;
    }

    public boolean getCompletionState() {
        return completionState;
    }

    public String getDescription() {
        return this.description;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public int getBoxOpenTimes() {
        return boxOpenTimes;
    }

    public QuestType getType(){
        if (activities.getFirst() instanceof PomodoroTimer) {
            return QuestType.POMODORO;
        } else {
            return QuestType.TASK;
        }
    }

    public Timestamp getEndTime(){
        return endTime;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndTime(Timestamp timestamp){
        this.endTime = timestamp;
    }

    public void setStartTime(Timestamp timestamp){
        this.startTime = timestamp;
    }

    public void setBoxOpenTimes(int boxOpenTimes) {
        this.boxOpenTimes = boxOpenTimes;
    }

    public void setCompletionState(boolean completionState) {
        this.completionState = completionState;
    }

    @Override
    public String toString() {
        return title;
    }

}





