package quietquest.model;

import quietquest.utility.MQTTHandler;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Quest {
    private int id;
    private final int userId;
    private boolean completionState;
    private String title;
    private String detail;
    private Timestamp startTime;
    private Timestamp completeTime;
    private int boxOpenTimes;

    // ==============================* CONSTRUCTOR *========================================

    /**
     * Used for creating a container with the necessary information to pass into database. Not intended to use within
     * application otherwise.
     */
    public Quest(User user, String title, String detail) {
        this.userId = user.getId();
        this.title = title;
        this.detail = detail;
    }

    /**
     * Used to create full Quest object when querying database.
     */
    public Quest(int id, int userId, boolean completionState, String title, String detail, Timestamp startTime, Timestamp completeTime, int boxOpenTimes) {
        this.id = id;
        this.userId = userId;
        this.completionState = completionState;
        this.title = title;
        this.detail = detail;
        this.startTime = startTime;
        this.completeTime = completeTime;
        this.boxOpenTimes = boxOpenTimes;
    }

    // ==============================* QUEST MANAGEMENT *===================================

    /*public void startQuest(Activity activity) {
        if (activity instanceof PomodoroTimer pomodoro) {
            pomodoro.start(); // Mqtt publish happens inside recursive function
        } else if (activity instanceof Task) {
            // Update startTime for all tasks
            for (Activity activity : activities) {
                Task task = (Task) activity;
                task.setStartTime(new Timestamp(System.currentTimeMillis()));
            }
        }
    }
     */

    /*public void endQuest() {
        if (activities.isEmpty()) {
            return;
        }
        if (activities.getFirst() instanceof PomodoroTimer) {
            Activity activity = activities.getFirst();
            activity.end(); // Will end recursive PomodoroTimer calls
        } else if (activities.getFirst() instanceof Task) {
            // Update tasks with end time
            for (Activity activity : activities) {
                Task task = (Task) activity;
                task.setEndTime(new Timestamp(System.currentTimeMillis()));
            }
        }
    }*/

    // ==============================* GETTERS & SETTERS *==============================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public boolean getCompletionState() {
        return completionState;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp timestamp) {
        this.startTime = timestamp;
    }

    public Timestamp getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Timestamp timestamp) {
        this.completeTime = timestamp;
    }

    public int getBoxOpenTimes() {
        return boxOpenTimes;
    }

    public void setBoxOpenTimes(int boxOpenTimes) {
        this.boxOpenTimes = boxOpenTimes;
    }

    // ==============================* UTILITY METHODS *====================================

    @Override
    public String toString() {
        return title;
    }
}