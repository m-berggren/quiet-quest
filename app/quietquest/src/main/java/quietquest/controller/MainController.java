package quietquest.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import quietquest.QuietQuestMain;
import quietquest.model.QuietQuestFacade;

import java.io.IOException;

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
    private BorderPane mainPane;
    @FXML
    private VBox menuVBox;

    public MainController() {
        this.quietQuestFacade = new QuietQuestFacade();
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

    public void loadView(String view) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(QuietQuestMain.class.getResource(view));
            Parent node = fxmlLoader.load();
            BaseController baseController = fxmlLoader.getController();
            baseController.setMainController(this);
            mainPane.setCenter(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
