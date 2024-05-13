package quietquest.model;

public interface Activity {
    public void completeTask();

    public void start();

    public void end();

    public QuestType getType();
}


