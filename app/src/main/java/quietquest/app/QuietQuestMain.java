package quietquest.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class QuietQuestMain extends Application {
    public static QuestManager questManager;

    @Override
    public void start(Stage stage) throws IOException {
        questManager = new QuestManager();
        FXMLLoader fxmlLoader = new FXMLLoader(QuietQuestMain.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1510, 760);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        // Set a style
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public static void main(String[] args) {
        launch();
    }
}