package quietquest.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateQuestController {
    @FXML
    private Button saveQuestButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField taskDescriptionField;
    private Parent root;
    private Stage stage;
    private Scene scene;
    private HashMap<String, Quest> quests;


    // create a quest by clicking "Save Quest" button:
    public void createQuest() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        ArrayList<String> tasks = new ArrayList<>();
        tasks.add(taskDescriptionField.getText());

        Quest quest = new Quest(title, description, tasks);
        quests.put(title, quest);
    }

    // cancel quest creation by clicking "Cancel" button:
    @FXML
    protected void cancelQuestCreation (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource("/quietquest/app/hello-view.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
