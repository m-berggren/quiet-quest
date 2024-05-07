package quietquest.controller;

import quietquest.model.*;
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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.quietQuestFacade = mainController.getQuietQuestFacade();
        afterMainController();
    }

    /**
     * Hook that runs after setting main controller. Default is no action.
     */
    protected void afterMainController() {
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
    mainController.loadView(FxmlFile.SHOW_QUEST);
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
