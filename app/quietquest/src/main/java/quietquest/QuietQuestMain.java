package quietquest;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import quietquest.controller.LogInController;
import quietquest.model.Database;
import quietquest.utility.FxmlFile;
import quietquest.utility.MQTTHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;


public class QuietQuestMain extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Database database = new Database();
        MQTTHandler mqttHandler = new MQTTHandler();
		MediaPlayer mediaPlayer = setMediaPlayer();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlFile.LOG_IN));
        Parent root = loader.load();
        LogInController logInController = loader.getController();
		logInController.initialize(database, mqttHandler, mediaPlayer); // Passes database & mqttHandler object until MainController is reached
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                database.closeConnection();
                mqttHandler.disconnect();
				closeMediaPlayer(mediaPlayer);
                Platform.exit(); // Specific to JavaFX, terminates the runtime
				System.exit(0);
            }
        });
        stage.setScene(scene);
        stage.setTitle("Quest App");
        stage.show();
    }

	private MediaPlayer setMediaPlayer() {
		String fileName = "/music/main-sound.mp3";
		URL path = getClass().getResource(fileName);
		try {
			Media media = new Media(path.toString());
			return new MediaPlayer(media);
		} catch (MediaException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void closeMediaPlayer(MediaPlayer mediaPlayer) {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.setOnEndOfMedia(new Runnable() {
				@Override
				public void run() {
					mediaPlayer.dispose();
				}
			});
		}
	}

    public static void main(String[] args) {
        launch();
    }
}