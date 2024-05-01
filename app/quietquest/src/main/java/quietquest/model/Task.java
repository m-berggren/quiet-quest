package quietquest.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MultipleSelectionModel;

public class Task implements Activity{
    private String task;
    private boolean completed;
    public Task(){

    }
    public Task(String tasks) {
        this.task = tasks;


    }
    //getter
    public String getTasks() {
        return this.task;
    }

    //Setter
    public void setTasks(String tasks){
        this.task = tasks;
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
        return task;
    }



    @Override
    public void completeTask() {

    }


    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
