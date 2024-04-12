package quietquest.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeleteQuestController implements Initializable{
    @FXML
    private Label questSelectionLabel;
    private FXMLLoader loader;
    private Parent root;
    private Stage stage;
    private Scene scene;
    private Quest quest;
    private QuestManager questManager;

    public void initialize(URL arg0, ResourceBundle arg1) {
        questManager = QuietQuestMain.questManager;
        quest = questManager.getQuestSelection();
        questSelectionLabel.setText(quest.getTitle());
    }

    public void onCancel(ActionEvent event) throws IOException {
        loader = new FXMLLoader(QuietQuestMain.class.getResource("/quietquest/app/quest-list-view.fxml"));
        loadLoader(loader, event);
    }

    public void onConfirm(ActionEvent event) throws IOException {
        questManager.deleteQuest(quest.getTitle());
        questManager.resetQuestSelection();

        loader = new FXMLLoader(QuietQuestMain.class.getResource("/quietquest/app/quest-list-view.fxml"));
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
