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
    private Timestamp startTime;
    private Timestamp endTime;

    public Quest(String title, String description, ArrayList<Activity> activities) {
        this.title = title;
        this.description = description;
        this.activities = activities;
        this.mqttHandler = MQTTHandler.getInstance();
    }

    public void startActivity() {
        if (activities.isEmpty()) {
            return;
        }
        if (activities.getFirst() instanceof PomodoroTimer) {
            Activity activity = activities.getFirst();
            activity.start(); // Mqtt publish happens inside recursive function
        } else {
            // May include publish message from Task later
        }
    }

    // Getters
    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public QuestType getType(){
        if (activities.getFirst() instanceof PomodoroTimer) {
            return QuestType.POMODORO;
        } else {
            return QuestType.TASK;
        }
    }

    public Timestamp getStartTime(){
        return startTime;
    }

    public Timestamp getEndTime(){
        return endTime;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(Timestamp timestamp){
        this.startTime = timestamp;
    }

    public void setEndTime(Timestamp timestamp){
        this.endTime = timestamp;
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





