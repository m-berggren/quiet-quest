package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.utility.FxmlFile;

import java.io.IOException;

public class LogInController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button logInButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Pane popupPane;
    @FXML
    private Rectangle popupTextbox;
    @FXML
    private Text popupText;
    @FXML
    private Text popupSmallText;
    @FXML
    private Button tryAgainButton;
    @FXML
    private Button popupSignUpButton;

    private String username;
    private String password;

    public void onLogInClick() {

    }
    public void onSignUpClick(ActionEvent event) throws IOException {
        loadFxml("create-user-view.fxml", event);
    }
    public void onTryAgainClick() {

    }

    private void loadFxml(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.MAIN));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // adding CSS styling option
        stage.setScene(scene);
        stage.show();

        MainController controller = loader.getController();
        controller.loadView(fxmlFile);
    }

}
