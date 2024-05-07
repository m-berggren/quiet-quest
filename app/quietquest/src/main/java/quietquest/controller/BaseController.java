package quietquest.controller;

import quietquest.model.Database;
import quietquest.model.Quest;
import quietquest.model.QuietQuestFacade;
import quietquest.model.User;
import quietquest.utility.FxmlFile;
import quietquest.utility.MQTTHandler;

import java.sql.SQLException;

public abstract class BaseController {
    protected final MQTTHandler mqttHandler;
    protected Database database;
    protected User user;
    private MainController mainController;
    protected QuietQuestFacade quietQuestFacade;

    public BaseController() {
        mqttHandler = MQTTHandler.getInstance();
    }

    public void setMainController(MainController mainController) throws SQLException {
        this.mainController = mainController;
        this.quietQuestFacade = mainController.getQuietQuestFacade();
        afterMainController();
    }

    /**
     * Hook that runs after setting main controller. Default is no action.
     */
    protected void afterMainController() throws SQLException {
    }

    public Database getDatabase() {
        return database;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void showHome() {
        mainController.loadView(FxmlFile.HOME);
    }

    public void showCreateQuest() {
        mainController.loadView(FxmlFile.CREATE_QUEST);
    }

    public void showQuestList() {
        mainController.loadView(FxmlFile.QUEST_LIST);
    }

    public void showQuest(Quest quest) {
        mainController.loadQuestController(quest);
    }

    public void showHistory() {
        mainController.loadView(FxmlFile.HISTORY);
    }

    public void showStatistics() {
        mainController.loadView(FxmlFile.STATISTICS);
    }

    public void showSettings() {
        mainController.loadView(FxmlFile.SETTINGS);
    }

    public void showHelp() {
        mainController.loadView(FxmlFile.HELP);
    }

    public void showLogIn() {
        mainController.loadView(FxmlFile.LOG_IN);
    }

    public QuietQuestFacade getQuietQuestFacade() {
        return this.quietQuestFacade;
    }
}
