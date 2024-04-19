package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import quietquest.utility.FxmlFile;

import java.io.IOException;

public class StartController extends BaseController {
    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    private Label welcomeText;

    @FXML
    private Button helloButton;



    @FXML
    protected void onNewQuestButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = getFxmlLoader(FxmlFile.CREATE_QUEST);
        root = loader.load();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    protected void onQuestListButtonClick(ActionEvent event) throws IOException {
        FXMLLoader  loader = getFxmlLoader(FxmlFile.QUEST_LIST);
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}