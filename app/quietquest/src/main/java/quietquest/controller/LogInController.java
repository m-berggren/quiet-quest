package quietquest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class LogInController extends BaseController {
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
    public void onSignUpClick() {
        // direct to sign-up page through facade
    }
    public void onTryAgainClick() {

    }


}
