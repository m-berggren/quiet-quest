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

    /**
     * Load start page upon clicking "Log In" if user credentials match what is in the database
     */
    public void onLogInClick(ActionEvent event) throws IOException {
        username = usernameField.getText();
        password = passwordField.getText();

        /*if (username && password match database) {
            loadFxml("start-view.fxml", event);
        } else {
            showPopup();
        }

         */
    }

    /**
     * Load sign-up page upon clicking "Sign Up" button
     * @param event
     * @throws IOException
     */
    public void onSignUpClick(ActionEvent event) throws IOException {
        loadFxml("create-user-view.fxml", event);
    }

    /**
     * Clear username and password fields upon clicking "Try Again" button. Does not show popup message
     */
    public void onTryAgainClick() {
        usernameField.clear();
        passwordField.clear();
        doNotShowPopup();
    }

    /**
     * Show popup message and disable all other buttons and text fields
     */
    public void showPopup() {
        // show popup elements
        popupPane.setDisable(false);
        popupPane.setVisible(true);
        popupText.setVisible(true);
        popupTextbox.setVisible(true);
        popupSmallText.setVisible(true);
        popupSignUpButton.setVisible(true);
        tryAgainButton.setVisible(true);

        // disable regular buttons/text fields
        signUpButton.setDisable(true);
        logInButton.setDisable(true);
        usernameField.setDisable(true);
        passwordField.setDisable(true);
    }

    /**
     * Make popup invisible and enable regular buttons and text fields
     */
    public void doNotShowPopup() {
        // do not show popup elements
        popupPane.setDisable(true);
        popupPane.setVisible(false);
        popupText.setVisible(false);
        popupTextbox.setVisible(false);
        popupSmallText.setVisible(false);
        popupSignUpButton.setVisible(false);
        tryAgainButton.setVisible(false);

        // enable regular buttons/text fields
        signUpButton.setDisable(false);
        logInButton.setDisable(false);
        usernameField.setDisable(false);
        passwordField.setDisable(false);
    }

    /**
     * Method used to load the page specified
     * @param fxmlFile specifies the page that is loaded
     * @param event
     * @throws IOException
     */
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
