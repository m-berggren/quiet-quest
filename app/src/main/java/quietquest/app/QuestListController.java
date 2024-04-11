package quietquest.app;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;


import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class QuestListController implements Initializable {
    @FXML
    private Button deleteButton;

    @FXML
    private ListView<String> questListView;

    private Quest selectedQuest;

    private QuestManager questManager;

    private HashMap<String, Quest> quests;

    private String selectedKey;

    public void initialize(URL arg0, ResourceBundle arg1) {
        //Replace with "real" quests
        //quests.put("Monty Python", "Holy Grail");
        //quests.put("Mr Bean", "Save Baby");
        //quests.put("Frodo", "Destroy Ring");
        questManager = QuietQuestMain.questManager;
        quests = questManager.getQuests();
        displayQuests();
    }
    public void displayQuests(){
        questListView.getItems().addAll(quests.keySet());
    }

    private void setSelectedQuest(){
        questListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                selectedKey = questListView.getSelectionModel().getSelectedItem();
                selectedQuest = quests.get(selectedKey);
                System.out.println("Selected: " + selectedQuest.toString());
            }
        });
    }

    public void onDeleteQuest(){

    }
}
