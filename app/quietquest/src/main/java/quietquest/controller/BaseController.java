package quietquest.controller;

import quietquest.model.*;
import quietquest.utility.FxmlFile;
import quietquest.utility.MenuButtonType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public abstract class BaseController {
	private MainController mainController;
	protected QuietQuestFacade quietQuestFacade;

	// ==============================* CONSTRUCTOR *========================================

	/**
	 * Initialization of mainController and quietQuestFacade happens in setMainController
	 */
	public BaseController() {
	}

	// ==============================* INITIALIZATION METHODS *=======================

	/**
	 * Hook that runs after setting main controller. Default is no action.
	 */
	protected void afterMainController() throws SQLException {
	}

	// ==============================* VIEW MANAGEMENT *====================================

	/**
	 * Loads the home view based on string in {@link FxmlFile}.
	 */
	public void showHome() {
		mainController.loadView(FxmlFile.HOME);
	}

	/**
	 * Loads the create quest view based on string in {@link FxmlFile}.
	 */
	public void showCreateQuest() {
		mainController.loadView(FxmlFile.CREATE_QUEST);
	}

	/**
	 * Loads the quest list view based on string in {@link FxmlFile}.
	 */
	public void showQuestList() {
		mainController.loadView(FxmlFile.QUEST_LIST);
	}

	/**
	 * Loads the quest view based on string in {@link FxmlFile}.
	 *
	 * @param quest is the selected Quest object in quest list view.
	 */
	public void showQuest(Quest quest) {
		mainController.loadQuestController(quest);
	}

	/**
	 * Loads the quest history view based on string in {@link FxmlFile}.
	 */
	public void showHistory() {
		mainController.loadView(FxmlFile.HISTORY);
	}

	/**
	 * Loads the statistics view based on string in {@link FxmlFile}.
	 */
	public void showStatistics() {
		mainController.loadView(FxmlFile.STATISTICS);
	}


	// ==============================* GETTERS & SETTERS *==============================

	public void setMainController(MainController mainController) throws SQLException {
		this.mainController = mainController;
		this.quietQuestFacade = mainController.getQuietQuestFacade();
		afterMainController();
	}

	public QuietQuestFacade getQuietQuestFacade() {
		return this.quietQuestFacade;
	}

	public ArrayList<Quest> getAllQuests() {
		return quietQuestFacade.getAllQuests();
	}

	public ArrayList<Activity> getActivitiesFromQuest(Quest quest) {
		return quest.getActivities();
	}

	public ArrayList<PomodoroTimer> getAllPomodoroQuests() throws SQLException {
		return quietQuestFacade.getAllPomodoroQuests();
	}

    public void changeSelectedMenuButton(MenuButtonType selectedType) {
        mainController.toggleOneButtonOnly(selectedType);
    }

    public User getUser() {
        return quietQuestFacade.getUser();
    }


	// ==============================* HELPER METHODS *====================================

	/**
	 * Formats a timestamp to a string.
	 *
	 * @param timestamp is the timestamp to be formatted.
	 * @return the formatted string.
	 */
	public String formatTime(Timestamp timestamp) {
		if (timestamp == null) {
			return "N/A";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(timestamp);
	}


}
