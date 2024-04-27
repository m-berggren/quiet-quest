package quietquest.model;

import java.util.ArrayList;

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
    }
    public void addTask(String task) {
        this.tasks.add(task);
    }
    public void removeTask() {
        this.tasks.remove();
    }
    public String getTask(int index) {
        return tasks.get(index);
    }*/

    public void completeTask() {

    }
}
