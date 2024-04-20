package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.utility.FxmlFile;

import java.io.IOException;

public class StartController {
  @FXML
  private Label welcomeText;

  @FXML
  private Button helloButton;

  @FXML
  protected void onNewQuestButtonClick(ActionEvent event) throws IOException {
    loadFxml(FxmlFile.CREATE_QUEST, event);
  }

  @FXML
  protected void onQuestListButtonClick(ActionEvent event) throws IOException {
    loadFxml(FxmlFile.QUEST_LIST, event);
  }

  private void loadFxml(String fxmlFile, ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.MAIN));
    Parent root = loader.load();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

    MainController controller = loader.getController();
    controller.loadView(fxmlFile);
  }
}