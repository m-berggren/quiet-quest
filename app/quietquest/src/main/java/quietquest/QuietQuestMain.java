package quietquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quietquest.utility.FxmlFile;

import java.io.IOException;

public class QuietQuestMain extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlFile.START));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Quest App");
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}