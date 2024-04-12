package quietquest.app;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class QuestListController implements Initializable {
    @FXML
    private Button deleteButton;
    @FXML
    private ListView<String> questListView;

    private QuestManager questManager;

    private HashMap<String, Quest> quests;

    private FXMLLoader loader;
    private Parent root;
    private Stage stage;
    private Scene scene;

    public void initialize(URL arg0, ResourceBundle arg1) {
        questManager = QuietQuestMain.questManager;
        quests = questManager.getQuests();
        displayQuests();
        setSelectedQuest();
    }
    public void displayQuests(){
        questListView.getItems().addAll(quests.keySet());
    }

    private void setSelectedQuest(){
        questListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                String selectedKey = questListView.getSelectionModel().getSelectedItem();
                questManager.setQuestSelection(quests.get(selectedKey));
            }
        });
    }

    public void onDeleteQuest(ActionEvent event) throws IOException {
        loader = new FXMLLoader(QuietQuestMain.class.getResource("/quietquest/app/delete-quest-view.fxml"));
        loadLoader(loader, event);
    }

    public void loadLoader(FXMLLoader loader, ActionEvent event) throws IOException {
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
