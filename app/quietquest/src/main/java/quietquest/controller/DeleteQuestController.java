package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import quietquest.model.Quest;

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

  public void onCancel(ActionEvent event) {
    showQuestList();
  }

  public void onConfirm(ActionEvent event) {
    quietQuestFacade.deleteQuest(quest.getTitle());
    quietQuestFacade.resetQuestSelection();
    showQuestList();
  }
}
