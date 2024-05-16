package quietquest.utility;

public class MQTTTopics {
	// All topics used for publishing and subscribing
    public static final String TOPIC_PUB_QUEST_START = "/quietquest/app/quest/start";
    public static final String TOPIC_PUB_QUEST_END = "/quietquest/app/quest/end";
    public static final String TOPIC_PUB_TASK_DONE = "/quietquest/app/task/end";
    public static final String TOPIC_PUB_POMODORO_INTERVAL = "/quietquest/app/pomodoro/interval";
    public static final String TOPIC_SUB_ALL = "/quietquest/sensor/#";
}
