package quietquest;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import quietquest.controller.LogInController;
import quietquest.model.Database;
import quietquest.utility.FxmlFile;
import quietquest.utility.MQTTHandler;

import java.io.IOException;
import java.sql.SQLException;

public class QuietQuestMain extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Database database = new Database();
        MQTTHandler mqttHandler = new MQTTHandler();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlFile.LOG_IN));
        Parent root = loader.load();
        LogInController logInController = loader.getController();
        logInController.initialize(database, mqttHandler); // Passes database & mqttHandler object until MainController is reached
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                database.closeConnection();
                mqttHandler.disconnect();
                Platform.exit(); // Specific to JavaFX, terminates the runtime
            }
        });
        stage.setScene(scene);
        stage.setTitle("Quiet Quest");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}