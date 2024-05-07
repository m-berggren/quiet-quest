package quietquest.model;

import java.sql.Timestamp;

public class Task implements Activity {
    private String description;
    private Timestamp startTime;
    private Timestamp endTime;
    private boolean completionState;

    public Task(String description) {
        this.description = description;
        this.startTime = null;
        this.endTime = null;
        this.completionState = false;
    }

    public Task(String description, Timestamp startTime, Timestamp endTime, boolean completionState) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.completionState = completionState;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public boolean getCompletionState() {
        return completionState;
    }

    public void setCompletionState(boolean completionState) {
        this.completionState = completionState;
    }
    public String toString() {
        return description;
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

}
