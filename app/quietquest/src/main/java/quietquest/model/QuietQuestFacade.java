package quietquest.model;

import quietquest.controller.UIUpdater;
import quietquest.utility.MQTTHandler;

import java.sql.SQLException;
import java.util.ArrayList;

public class QuietQuestFacade {
    protected final MQTTHandler mqttHandler;
    protected final Database database;
    protected final User user;

    // ==============================* CONSTRUCTOR *========================================

    /**
     * Constructs a new instance of QuietQuestFacade that acts as a bridge separating controllers
     * from the model in the application and attempts to adhere to the MVC (Model-View-Controller)
     * architecture. It manages the MQTT handler, user session and connections to database.
     *
     * @param user     is the User object to store login information, used for database querying.
     * @param database is the single Database object that handles all query operations.
     */
    public QuietQuestFacade(User user, Database database, MQTTHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
        this.user = user;
        this.database = database;
    }

    // ==============================* DATABASE MANAGER *===================================

    /**
     * @return
     */
    public ArrayList<Quest> getAllQuests() {
        return database.getAllQuests(user);
    }

    public void createQuest(Quest quest, ArrayList<Activity> activities) {
        database.createQuest(quest, activities);
    }

    /**
     * @param quest
     */
    public void startQuest(Quest quest, PomodoroUIUpdater pomodoroObserver) {
        database.startQuest(quest);

        ArrayList<Activity> activities = database.getActivitiesFromQuest(quest);
        // Quests can be created without an activity so list may be empty
        if (activities.isEmpty()) {
            return;
        }

        if (activities.getFirst() instanceof PomodoroTimer pomodoro) {
            pomodoro.setPomodoroObserver(pomodoroObserver);
            pomodoro.start();
        }
    }

    /**
     * @return
     */
    public ArrayList<PomodoroTimer> getAllPomodoroQuests() throws SQLException {
        return database.getAllPomodoroQuests(user);
    }

    /**
     * @param quest
     */
    public void completeQuest(Quest quest) {
        database.completeQuest(quest);

        ArrayList<Activity> activities = quest.getActivities(); //database.getActivitiesFromQuest(quest);
        // Quests can be created without an activity so list may be empty
        if (activities.isEmpty()) {
            return;
        }
        if (activities.getFirst() instanceof PomodoroTimer pomodoro) {
            pomodoro.end();
        }
    }

    /**
     * @param quest
     * @return
     */
    public ArrayList<Activity> getActivitiesFromQuest(Quest quest) {
        return database.getActivitiesFromQuest(quest);
    }

    public void updateTask(Task currTask, Task updTask) {
        database.updateTask(currTask, updTask);
    }

    public void updateQuest(Quest currQuest, Quest updQuest) {
        database.updateQuest(currQuest, updQuest);
    }

    public void updateTaskCompletionStateInDb(Task task) {
        database.updateTaskCompletionState(task);
    }

    public void updateTaskEndTimeInDb(Task task) {
        database.updateTaskEndTime(task);
    }

    public void deleteQuest(Quest quest) {
        database.deleteQuest(quest);
    }

    public void saveBoxOpenTimes(Quest currentQuest) {
        database.saveBoxOpenTimes(user.getUsername() , currentQuest.getId());
    }

    // update task
    // update pomodoroTimer
    // or update activity
    // delete activity

    // ==============================* MQTT MANAGER *=======================================

    /**
     * Start subscribing to all sensor data from terminal.
     */
    public void subscribeMqtt() {
        mqttHandler.subscribe();
    }

    /**
     * Methos to stop subscribing to sensor data from terminal.Used if a Quest is completed or when pausing during a break in
     * PomodoroTimer.
     */
    public void unsubscribeMqtt() {
        mqttHandler.unsubscribe();
    }

    /**
     * Publishes topic data and payload to MQTT Broker for terminal to subscribe to.
     * @param topic a string of the topic
     * @param message a string of the payload message
     */
    public void publishMqttMessage(String topic, String message) {
        mqttHandler.publishMessage(topic, message);
    }

    /**
     * @param uiUpdater
     */
    public void setUIUpdater(UIUpdater uiUpdater) {
        mqttHandler.setUIUpdater(uiUpdater);
    }

    /**
     *
     */
    public void disconnectMqtt() {
        mqttHandler.disconnect();
    }

    // ==============================* QUEST MANAGER *======================================


    // ==============================* GETTERS & SETTERS *==================================

    public User getUser() {
        return user;
    }

}
