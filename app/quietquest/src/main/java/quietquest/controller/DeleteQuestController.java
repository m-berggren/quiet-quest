package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.model.Quest;
import quietquest.model.QuestManager;
import quietquest.utility.FxmlFile;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeleteQuestController extends BaseController implements Initializable {
  @FXML
  private Label questSelectionLabel;
  private Quest quest;

  public void initialize(URL arg0, ResourceBundle arg1) {
    quest = quietQuestFacade.getQuestSelection();
    questSelectionLabel.setText(quest.getTitle());
  }

    public void onCancel(ActionEvent event) throws IOException {
        loader = getFxmlLoader(FxmlFile.QUEST_LIST);
        loadLoader(loader, event);
    }

  public void onConfirm(ActionEvent event) {
    quietQuestFacade.deleteQuest(quest.getTitle());
    quietQuestFacade.resetQuestSelection();
    showQuestList();
  }
}
