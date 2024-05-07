package quietquest.model;

public class Task implements Activity {
    private String task;
    private boolean completed;

    public Task() {

    }

    public Task(String tasks) {
        this.task = tasks;
    }

    //getter
    public String getTask() {
        return this.task;
    }

    //Setter
    public void setTasks(String tasks) {
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

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
