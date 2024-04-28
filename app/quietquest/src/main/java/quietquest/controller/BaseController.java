package quietquest.controller;

import quietquest.model.Quest;
import quietquest.model.QuietQuestFacade;
import quietquest.model.Task;
import quietquest.utility.FxmlFile;

public abstract class BaseController {
  private MainController mainController;
  protected QuietQuestFacade quietQuestFacade;


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

  public void showStart() {
    mainController.loadView(FxmlFile.START);
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

  public void showStatistics() {
    mainController.loadView(FxmlFile.STATISTICS);
  }

  public void showSettings() {
    mainController.loadView(FxmlFile.SETTINGS);
  }

  public void showHelp() {
    mainController.loadView(FxmlFile.HELP);
  }

  public void logout() {
  }

  public QuietQuestFacade getQuietQuestFacade() {
    return this.quietQuestFacade;
  }
}
