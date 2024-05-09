package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quietquest.QuietQuestMain;
import quietquest.model.User;
import quietquest.utility.FxmlFile;
import quietquest.model.Database;
import quietquest.utility.MQTTHandler;

import java.io.IOException;
import java.sql.SQLException;

public class LogInController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button logInButton;
    @FXML
    private Button createUserButton;
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
    private Button popupCreateUserButton;

    private String username;
    private String password;

    /**
     * Load start page upon clicking "Log In" if user credentials match what is in the database.
     * If inputted credentials are incorrect, show a popup message.
     */
    public void onLogInClick(ActionEvent event) throws IOException, SQLException {
        username = usernameField.getText();
        password = passwordField.getText();
        Database database = new Database();
        if (database.checkIfUsernameExists(username) && database.checkIfPasswordCorrect(username, password)) {
            User user = database.getUserByUsername(username);
            loadStartController(event, user, database); // Passing User and Database as parameters to use in application
        } else { // Username does not exist OR wrong password
            showPopup();
        }
    }

    private void loadStartController(ActionEvent event, User user, Database database) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.START));
        Parent root = loader.load();
        StartController startController = loader.getController();
        startController.initialize(user, database);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onEnterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            EventTarget target = event.getTarget();
            ActionEvent actionEvent = new ActionEvent(target, null);
            try {
                onLogInClick(actionEvent);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load sign-up page upon clicking "Create User Account" button
     *
     * @param event
     * @throws IOException
     */
    public void onCreateUserClick(ActionEvent event) throws IOException {
        try {
            loadFxml(FxmlFile.CREATE_USER, event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear username and password fields upon clicking "Try Again" button
     * Allow user to try again (popup message disappears)
     */
    public void onTryAgainClick() {
        usernameField.clear();
        passwordField.clear();
        doNotShowPopup();
    }

    /**
     * Show popup message
     * Disable all other buttons and text fields
     */
    public void showPopup() {
        // show popup elements
        popupPane.setDisable(false);
        popupPane.setVisible(true);
        popupText.setVisible(true);
        popupTextbox.setVisible(true);
        popupSmallText.setVisible(true);
        popupCreateUserButton.setVisible(true);
        tryAgainButton.setVisible(true);

        // disable regular buttons/text fields
        createUserButton.setDisable(true);
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
        popupCreateUserButton.setVisible(false);
        tryAgainButton.setVisible(false);

        // Enable regular buttons/text fields
        createUserButton.setDisable(false);
        logInButton.setDisable(false);
        usernameField.setDisable(false);
        passwordField.setDisable(false);
    }

    /**
     * Method used to load the page specified
     *
     * @param fxmlFile specifies the page that is loaded
     * @param event
     * @throws IOException
     */
    private void loadFxml(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // adding CSS styling option
        stage.setScene(scene);
        stage.show();
    }
}
