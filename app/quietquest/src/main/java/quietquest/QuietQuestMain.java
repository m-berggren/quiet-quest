package quietquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quietquest.controller.BaseController;
import quietquest.utility.FxmlFile;
import quietquest.model.QuestManager;

import java.io.IOException;

public class QuietQuestMain extends Application {
    public static QuestManager questManager;

    @Override
    public void start(Stage stage) throws IOException {
        questManager = new QuestManager();
        FXMLLoader loader = BaseController.getFxmlLoader(FxmlFile.START);
        Scene scene = new Scene(loader.load(), 1510, 760);
        stage.setTitle("Quiet Quest");
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    public static void main(String[] args) {
        launch();
    }
}