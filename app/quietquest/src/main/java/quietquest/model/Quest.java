package quietquest.model;

import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Quest {

    // Attributes of Quest
    private String title;
    private String description;
    private ArrayList<Activity> activities;

    // Constructor
    public Quest(String title, String description, ArrayList<Activity> activities) {
        this.title = title;
        this.description = description;
        this.activities = activities;
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

    @Override
    public String toString() {
        return title;
    }

}





