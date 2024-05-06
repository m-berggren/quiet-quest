package quietquest.model;

import quietquest.utility.MQTTHandler;

import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Quest {

    // Attributes of Quest
    private String title;
    private String description;
    private ArrayList<Activity> activities;
    private MQTTHandler mqttHandler;

    // Constructor
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





