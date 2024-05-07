package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.model.Database;
import quietquest.model.QuietQuestFacade;
import quietquest.model.User;
import quietquest.utility.FxmlFile;

import java.io.IOException;
import java.sql.SQLException;

public class MainController extends BaseController {
    @FXML
    private VBox sideMenu;
    @FXML
    private Button homeButton;
    @FXML
    private Button createQuestButton;
    @FXML
    private Button questListButton;
    @FXML
    private Button logOutButton;

    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox menuVBox;

    public MainController() {
    }

    public void initialize(User user, Database database) throws SQLException {
        setUser(user);
        setDatabase(database);
        this.quietQuestFacade = new QuietQuestFacade(user, database);
        setMainController(this);
    }

    public void onHomeButtonClick() {
        showHome();
    }

    public void onCreateQuestButtonClick() {
        showCreateQuest();
    }

    public void onQuestListButtonClick() {
        showQuestList();
    }

    public void onLogOutButtonClick(ActionEvent event) throws IOException, SQLException {
        loadOnLogout(event);
    }

    public void loadView(String view) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(QuietQuestMain.class.getResource(view));
            Parent node = fxmlLoader.load();
            BaseController baseController = fxmlLoader.getController();
            baseController.setUser(user);
            baseController.setDatabase(database);
            baseController.setMainController(this);
            mainPane.setCenter(node);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadOnLogout(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.LOG_IN));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // adding CSS styling option
        stage.setScene(scene);
        stage.show();
        database.disconnect();
        mqttHandler.disconnect();
    }
}
