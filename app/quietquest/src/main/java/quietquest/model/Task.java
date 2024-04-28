package quietquest.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MultipleSelectionModel;

public class Task implements Activity{
    private String tasks;

    public Task(String tasks) {
        this.tasks = tasks;


    }
    //getter
    public String getTasks() {
        return this.tasks;
    }

    //Setter
    public void setTasks(String tasks){
        this.tasks = tasks;
    }
    /*public void setTask(int index, String task) {
        this.tasks.set(index, task);
    }*/
    /*public void addTask(String task) {
        this.tasks.add(task);
    }
    public void removeTask(MultipleSelectionModel<String> selectionModel) {
        this.tasks.remove();
    }
    public String getTask(int index) {
        return tasks.get(index);
    }*/

    public String toString() {
        return tasks;
    }
}
