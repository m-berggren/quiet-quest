package quietquest.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Quest {

    // Attributes of Quest
    private String title;
    private String description;
    private ArrayList<Task> tasks = new ArrayList<>();


    // Constructors
    public Quest() {
    }

    public Quest(String title, String description, ArrayList<Task> tasks) {
        this.title = title;
        this.description = description;
        this.tasks = tasks;
    }

    // Getters
    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }


    @Override
    public String toString() {
        return title;
    }

}





