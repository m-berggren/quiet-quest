package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import quietquest.QuietQuestMain;
import javafx.scene.Parent;

import java.io.IOException;

public abstract class BaseController {
  public static FXMLLoader getFxmlLoader(String fxmlFile) {
    return new FXMLLoader(QuietQuestMain.class.getResource(fxmlFile));
  }

  public void loadLoader(String fxmlFile, ActionEvent event) throws IOException {
    Parent root = getFxmlLoader(fxmlFile).load();
    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
  }
}
