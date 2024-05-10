package quietquest.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.model.*;
import quietquest.utility.FxmlFile;
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
    private VBox menuVBox;

    private Database database;
    private MQTTHandler mqttHandler;

    // ==============================* INITIALIZATION METHODS *=======================

    public void initialize(User user, Database database, MQTTHandler mqttHandler) throws SQLException {
        this.database = database;
        this.mqttHandler = mqttHandler;
        this.quietQuestFacade = new QuietQuestFacade(user, database, mqttHandler);
        setMainController(this);
    }

    @Override
    protected void afterMainController() throws SQLException {
        super.afterMainController();
    }

    // ==============================* VIEW MANAGEMENT *====================================

    public void onHomeButtonClick() {
        showHome();
    }

    /**
     *
     */
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
        loadOnLogout(event);
    }

    public void onQuitButtonClick(ActionEvent event) {
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

    public void loadView(String view) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(QuietQuestMain.class.getResource(view));
            Parent node = fxmlLoader.load();
            BaseController baseController = fxmlLoader.getController();
            baseController.setMainController(this);
            mainPane.setCenter(node);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

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