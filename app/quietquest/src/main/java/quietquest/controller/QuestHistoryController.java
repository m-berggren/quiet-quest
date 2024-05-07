package quietquest.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import quietquest.model.Quest;
import quietquest.model.Task;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.sql.Timestamp;

public class QuestHistoryController extends BaseController implements Initializable {
    private HashMap<String, Quest> quests;

    @FXML
    private TreeView<Quest> questHistoryTree;
    @FXML
    private Label questHistoryLabel;


    public void initialize(URL location, ResourceBundle resources) {
        initializeQuestTreeView();
    }

    private void initializeQuestTreeView() {
        Quest rootQuest = new Quest("All Quests", "", new ArrayList<>()); // Assuming there is a simple constructor for names
        TreeItem<Quest> rootItem = new TreeItem<>(rootQuest);
        rootItem.setExpanded(true);
        questHistoryTree.setRoot(rootItem);

        // Assume quests is already populated, perhaps in another initialization step or via a service
        for (Quest quest : quests.values()) {
            TreeItem<Quest> questItem = new TreeItem<>(quest);
            rootItem.getChildren().add(questItem);

            // Optionally, add tasks directly if not doing lazy loading
            for (Task task : quest.getTasks()) {
                TreeItem<Quest> taskItem = new TreeItem<>(taskAsQuest);
                questItem.getChildren().add(taskItem);
            }
        }

        setupQuestTreeViewCellFactory();
    }

    private void setupQuestTreeViewCellFactory() {
        //cell factory to display quest title,start time and end time
        questHistoryTree.setCellFactory(tv -> new TreeCell<Quest>() {
            //cell factory to display quest title,start time and end time
            @Override
            protected void updateItem(Quest quest, boolean empty) {
                super.updateItem(quest, empty);
                if (empty || quest == null) {
                    setText(null);
                } else {
                    setText(quest.getTitle() + " (" + formatTimestamp(quest.getStartTime()) + " - " + formatTimestamp(quest.getEndTime()) + ")");       

                }
            }

            private String formatTimestamp(Timestamp timestamp) {
                return (timestamp != null) ? timestamp.toString() : "N/A";

            }
        });
    }
}

