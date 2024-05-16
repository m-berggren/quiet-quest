package quietquest.controller;

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.model.Database;
import quietquest.model.User;
import quietquest.utility.FxmlFile;
import quietquest.utility.MQTTHandler;

import java.io.IOException;
import java.sql.SQLException;

public class StartController extends BaseController {
    private User user;
    private Database database;
    private MQTTHandler mqttHandler;
	private MediaPlayer mediaPlayer;

    public void initialize(User user, Database database, MQTTHandler mqttHandler) {
        this.user = user;
        this.database = database;
        this.mqttHandler = mqttHandler;
		this.mediaPlayer = quietQuestFacade.getMediaPlayer();
    }

    @FXML
    private Label welcomeLabel;

    @Override
    protected void afterMainController() throws SQLException {
    }

    @FXML
    protected void onNewQuestButtonClick(ActionEvent event) throws IOException, SQLException {
        loadFxml(FxmlFile.CREATE_QUEST, event);
    }

    @FXML
    protected void onQuestListButtonClick(ActionEvent event) throws IOException, SQLException {
        loadFxml(FxmlFile.QUEST_LIST, event);
    }

    @FXML
    protected void onHomeClick(ActionEvent event) throws SQLException, IOException {
        loadFxml(FxmlFile.HOME, event);
    }

    private void loadFxml(String fxmlFile, ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.MAIN));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // adding CSS styling option
        stage.setScene(scene);
        stage.show();

        MainController controller = loader.getController();
		controller.initialize(user, database, mqttHandler, mediaPlayer);
        controller.loadView(fxmlFile);
    }
}