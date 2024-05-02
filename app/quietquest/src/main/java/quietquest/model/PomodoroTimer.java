package quietquest.model;

import java.util.Timer;

public class PomodoroTimer implements Activity {
    private Timer timer;
    private int focusTime;
    private int breakTime;
    private int intervals;

    public PomodoroTimer(int focusTime, int breakTime, int intervals) {
        this.timer = new Timer();
        this.focusTime = toMilliseconds(focusTime);
        this.breakTime = toMilliseconds(breakTime);
        this.intervals = toMilliseconds(intervals);
    }

    /**
     * Creates milliseconds from minutes
     *
     * @param minutes to convert
     * @return milliseconds
     */
    private int toMilliseconds(int minutes) {
        return minutes * 60000;
    }

    public void completeTask() {
    }

}
