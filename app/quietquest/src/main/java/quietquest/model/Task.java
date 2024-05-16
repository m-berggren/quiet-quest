package quietquest.model;

import java.sql.Timestamp;

public class Task implements Activity {
    private int id;
    private int questId;
    private String description;
    private Timestamp startTime;
    private Timestamp endTime;
    private boolean completionState;

    // ==============================* CONSTRUCTOR *========================================

    /**
     * Used for creating a container with the necessary information to pass into database. Not intended to use within
     * application otherwise.
     */
    public Task(String description) {
        this.description = description;
        this.startTime = null;
        this.endTime = null;
        this.completionState = false;
    }

    /**
     * Used to create full Task object when querying database,
     */
    public Task(int id, int questId, String description, Timestamp startTime, Timestamp endTime, boolean completionState) {
        this.id = id;
        this.questId = questId;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.completionState = completionState;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getQuestId() {
        return questId;
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

    public QuestType getType() {
        return QuestType.TASK;
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
