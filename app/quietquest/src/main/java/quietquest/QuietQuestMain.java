package quietquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
 import quietquest.model.Database;
import quietquest.utility.FxmlFile;

import java.io.IOException;
import java.sql.SQLException;

public class QuietQuestMain extends Application {
  @Override
  public void start(Stage stage) throws IOException, SQLException {
    new Database();
    FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlFile.LOG_IN));
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