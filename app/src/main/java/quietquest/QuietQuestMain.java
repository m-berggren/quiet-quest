package quietquest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quietquest.controller.QuestListController;
import quietquest.model.QuestManager;

import java.io.IOException;


public class QuietQuestMain extends Application {
    public static QuestManager questManager;

    @Override
    public void start(Stage stage) throws IOException {
        questManager = new QuestManager();
        FXMLLoader fxmlLoader = new FXMLLoader(QuietQuestMain.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1510, 760);
        stage.setTitle("Quiet Quest");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            QuestListController questListController = fxmlLoader.getController();
            questListController.disconnectMqtt();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}