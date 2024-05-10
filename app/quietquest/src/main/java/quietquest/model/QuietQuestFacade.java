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
    public void startQuest(Quest quest) {
        database.startQuest(quest);

        ArrayList<Activity> activities = database.getActivitiesFromQuest(quest);
        if (activities.getFirst() instanceof PomodoroTimer pomodoro) {
            pomodoro.start();
        }
    }

    /**
     * @param quest
     */
    public void completeQuest(Quest quest) {
        database.completeQuest(quest);

        ArrayList<Activity> activities = database.getActivitiesFromQuest(quest);
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

    public void updateTaskInDb(Task currTask, Task updTask) {
        database.updateTask(currTask, updTask);
    }

    public void updateQuestInDb(Quest currQuest, Quest updQuest) {
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

    // update task
    // update pomodoroTimer
    // or update activity
    // delete activity

    // ==============================* MQTT MANAGER *=======================================

    /**
     * @param pubTopic
     * @param pubMessage
     */
    public void connectMqtt(String pubTopic, String pubMessage) {
        mqttHandler.connect(pubTopic, pubMessage);
    }

    /**
     *
     */
    public void subscribeMqtt() {
        mqttHandler.subscribe();
    }

    /**
     * @param topic
     * @param message
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

    // create quest
    // delete quest
    // update quest


    // ==============================* QUEST MANAGER *======================================


    // ==============================* GETTERS & SETTERS *==================================

    public User getUser() {
        return user;
    }
}
