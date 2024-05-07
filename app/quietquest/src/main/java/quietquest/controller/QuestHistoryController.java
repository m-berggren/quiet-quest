package quietquest.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import quietquest.model.Activity;
import quietquest.model.Quest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class QuestHistoryController extends BaseController {
    private HashMap<String, Quest> quests;

    @FXML
    private TreeView<String> questHistoryTree;

    @FXML
    private Label questHistoryLabel;


    @Override
    public void afterMainController() throws SQLException {
        quests = quietQuestFacade.getQuests();
        initializeQuestTreeView();
    }

    private void initializeQuestTreeView() {
        Quest rootQuest = new Quest("All Quests", "", new ArrayList<>()); // Assuming there is a simple constructor for names
        TreeItem<String> rootItem = new TreeItem<>(rootQuest.getTitle());
        rootItem.setExpanded(true);
        questHistoryTree.setRoot(rootItem);

        for (Quest quest : quests.values()) {
            TreeItem<String> questItem = new TreeItem<>(quest.getTitle());
            rootItem.getChildren().add(questItem);

            for (Activity task : quest.getActivities()) {
                TreeItem<String> taskItem = new TreeItem<>(task.toString());
                questItem.getChildren().add(taskItem);
            }
        }

        setupQuestTreeViewCellFactory();
    }

    private void setupQuestTreeViewCellFactory() {
        //cell factory to display quest title,start time and end time
        questHistoryTree.setCellFactory(tv -> new TreeCell<>() {
          @Override
          protected void updateItem(String text, boolean empty) {
            super.updateItem(text, empty);
            if (empty || text == null) {
              setText(null);
            } else {
              setText(text);
            }
          }
        });
    }
}

