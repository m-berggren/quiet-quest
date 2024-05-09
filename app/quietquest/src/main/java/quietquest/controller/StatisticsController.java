package quietquest.controller;

import quietquest.model.Quest;

import java.util.ArrayList;


public class StatisticsController extends BaseController{
    private ArrayList<Quest> quests;

    public void afterMainController() {
        quests = getAllQuests();

        
    }
}
