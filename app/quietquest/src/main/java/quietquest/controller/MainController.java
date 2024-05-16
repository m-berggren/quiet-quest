package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import quietquest.QuietQuestMain;
import quietquest.model.*;
import quietquest.utility.FxmlFile;
import quietquest.model.Database;
import quietquest.model.User;
import quietquest.model.Quest;
import java.net.URL;
import quietquest.utility.MQTTHandler;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Optional;

public class MainController extends BaseController {
    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox sideMenu;
    @FXML
    private Button homeButton;
    @FXML
    private Button createQuestButton;
    @FXML
    private Button questListButton;
    @FXML
    private Button questHistoryButton;
    @FXML
    private Button statisticsButton;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private VBox menuVBox;
    @FXML
    private Slider volumeSlider;
    private MediaPlayer mediaPlayer;

    private Database database;
    private MQTTHandler mqttHandler;

    // ==============================* INITIALIZATION METHODS *=======================

    public void initialize(User user, Database database, MQTTHandler mqttHandler) throws SQLException {
        this.database = database;
        this.mqttHandler = mqttHandler;
        this.quietQuestFacade = new QuietQuestFacade(user, database, mqttHandler);
        setMainController(this);
        mqttHandler.connect();

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
    }

    @Override
    protected void afterMainController() throws SQLException {
        super.afterMainController();
    }

    // ==============================* VIEW MANAGEMENT *====================================

    /**
     * The methods below redirect to the various pages inside the application,
     * using inherited methods from BaseController.
     */
    public void onHomeButtonClick() {
        showHome();
    }

    public void onCreateQuestButtonClick() {
        showCreateQuest();
    }

    public void onQuestListButtonClick() {
        showQuestList();
    }

    public void onQuestHistoryButtonClick() {
        showHistory();
    }

    public void onStatisticsButtonClick() {
        showStatistics();
    }

    public void onLogOutButtonClick(ActionEvent event) throws IOException, SQLException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        loadOnLogout(event);
    }

    /**
     * Plays the desired music file.
     * The chosen music file is declared as fileName and links to an mp3 audio file in the project directory.
     */
    public void onPlayButtonClick() {
        String fileName = "/music/main-sound.mp3";
        URL path = getClass().getResource(fileName);
        if (path != null) {
            Media media = new Media(path.toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
            playButton.setDisable(true);
            stopButton.setDisable(false);
        }
    }


    /**
     * Stops the music file from playing.
     */
    public void onStopButtonClick() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playButton.setDisable(false);
            stopButton.setDisable(true);
        }
    }

    /**
     * Exits the desktop application and disconnects from the database and MQTT handler.
     */
    public void onQuitButtonClick() {
        ConfirmationAlert alert = new ConfirmationAlert();
        alert.setTitle("Confirmation");
        alert.setHeaderText("This will exit the application.");
        alert.setContentText("Do you want to continue?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().getText().equals("Yes")) {
            mqttHandler.disconnect();
            database.closeConnection();
            Platform.exit();
        }
    }

    /**
     * Loads the desired view.
     * @param view String value of the desired view
     */
    public void loadView(String view) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(QuietQuestMain.class.getResource(view));
            Parent node = fxmlLoader.load();
            BaseController baseController = fxmlLoader.getController();
            baseController.setMainController(this);

            // Apply the stylesheet to the newly loaded view
            node.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            mainPane.setCenter(node);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the create quest view based on string in {@link FxmlFile}.
     * @param quest Quest value of the selected quest
     */
    public void loadQuestController(Quest quest) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.SHOW_QUEST));
            Parent node = fxmlLoader.load();
            QuestController questController = fxmlLoader.getController();
            questController.initialize(quest);
            questController.setMainController(this);
            mainPane.setCenter(node);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the login view based on string in {@link FxmlFile}.
     */
    public void loadOnLogout(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.LOG_IN));
        Parent root = loader.load();
        LogInController logInController = loader.getController();
        logInController.initialize(database, mqttHandler);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // adding CSS styling option
        stage.setScene(scene);
        stage.show();
    }
}