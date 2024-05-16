package quietquest.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.model.*;
import quietquest.utility.*;

import java.sql.SQLException;
import java.io.IOException;
import java.util.Optional;

public class MainController extends BaseController {
    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox sideMenu;
    @FXML
    private ToggleButton homeButton;
    @FXML
    private ToggleButton createQuestButton;
    @FXML
    private ToggleButton questListButton;
    @FXML
    private ToggleButton questHistoryButton;
    @FXML
    private ToggleButton statisticsButton;
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
        mqttHandler.connect();

    }

    @Override
    protected void afterMainController() throws SQLException {
        super.afterMainController();
    }

    // ==============================* VIEW MANAGEMENT *====================================

    public void onHomeButtonClick() {
        toggleOneButtonOnly(MenuButtonType.HOME);
        showHome();
    }

    /**
     *
     */
    public void onCreateQuestButtonClick() {
        toggleOneButtonOnly(MenuButtonType.CREATE_QUEST);
        showCreateQuest();
    }

    public void onQuestListButtonClick() {
        toggleOneButtonOnly(MenuButtonType.QUEST_LIST);
        showQuestList();
    }

    public void onQuestHistoryButtonClick() {
        toggleOneButtonOnly(MenuButtonType.QUEST_HISTORY);
        showHistory();
    }

    public void onStatisticsButtonClick() {
        toggleOneButtonOnly(MenuButtonType.STATISTICS);
        showStatistics();
    }

    public void onLogOutButtonClick(ActionEvent event) throws IOException, SQLException {
        loadOnLogout(event);
    }

    public void toggleOneButtonOnly(MenuButtonType selectedType) {
        homeButton.setSelected(selectedType == MenuButtonType.HOME);
        createQuestButton.setSelected(selectedType == MenuButtonType.CREATE_QUEST);
        questListButton.setSelected(selectedType == MenuButtonType.QUEST_LIST);
        questHistoryButton.setSelected(selectedType == MenuButtonType.QUEST_HISTORY);
        statisticsButton.setSelected(selectedType == MenuButtonType.STATISTICS);
    }

    private void selectHomeButton() {
        homeButton.setSelected(true);
        createQuestButton.setSelected(false);
        questListButton.setSelected(false);
        questHistoryButton.setSelected(false);
        statisticsButton.setSelected(false);
    }

    private void toggleHomeButton() {
        homeButton.setSelected(true);
        createQuestButton.setSelected(false);
        questListButton.setSelected(false);
        questHistoryButton.setSelected(false);
        statisticsButton.setSelected(false);
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

            // Apply the stylesheet to the newly loaded view
            node.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            mainPane.setCenter(node);

            if(view.equals("quest-view.fxml")){
                mainPane.getStyleClass().clear();
                mainPane.getStyleClass().add("backgroundquest");
            }else{
              mainPane.getStyleClass().clear();
              mainPane.getStyleClass().add("backgroundmain");
            }
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