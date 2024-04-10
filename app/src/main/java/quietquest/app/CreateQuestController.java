package quietquest.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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
    private HashMap<String, Quest> quests;


    // create a quest:
    public void createQuest() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        ArrayList<String> tasks = new ArrayList<>();
        tasks.add(taskDescriptionField.getText());

        Quest quest = new Quest(title, description, tasks);
        quests.put(title, quest);
    }


}
