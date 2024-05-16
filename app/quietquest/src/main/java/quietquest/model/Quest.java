package quietquest.model;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Quest {
    private int id;
    private final int userId;
    private boolean completionState;
    private String title;
    private String detail;
    private Timestamp startTime;
    private Timestamp completeTime;
    private int boxOpenTimes;
    private final ArrayList<Activity> activityArrayList;

    // ==============================* CONSTRUCTOR *========================================

    /**
     * Used for creating a container with the necessary information to pass into database. Not intended to use within
     * application otherwise.
     */
    public Quest(User user, String title, String detail, ArrayList<Activity> activities) {
        this.userId = user.getId();
        this.title = title;
        this.detail = detail;
        this.activityArrayList = activities;
    }

    /**
     * Used to create full Quest object when querying database.
     */
    public Quest(int id, int userId, boolean completionState, String title, String detail, Timestamp startTime, Timestamp completeTime, int boxOpenTimes, ArrayList<Activity> activities) {
        this.id = id;
        this.userId = userId;
        this.completionState = completionState;
        this.title = title;
        this.detail = detail;
        this.startTime = startTime;
        this.completeTime = completeTime;
        this.boxOpenTimes = boxOpenTimes;
        this.activityArrayList = activities;
    }

    // ==============================* QUEST MANAGEMENT *===================================


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

	public ArrayList<Activity> getActivities() {
        return activityArrayList;
	}

    public void addActivity(Activity activity) {
		activityArrayList.add(activity);
    }

    public void removeActivity(Activity activity) {
		activityArrayList.remove(activity);
    }
}