package quietquest.controller;

import quietquest.model.Quest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class StatisticsController extends BaseController{
  private ArrayList<Quest> quests;

  public void afterMainController() throws SQLException {
    database.connect();
    quests = database.getAllQuests(user);
    database.disconnect();


  }

}
