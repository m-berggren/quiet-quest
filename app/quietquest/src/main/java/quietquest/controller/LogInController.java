package quietquest.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private Database database;
    private MQTTHandler mqttHandler;

    /**
     * Load start page upon clicking "Log In" if user credentials match what is in the database.
     * If inputted credentials are incorrect, show a popup message.
     */

    public void initialize(Database database, MQTTHandler mqttHandler) {
        this.database = database;
        this.mqttHandler = mqttHandler;
    }
    public void onLogInClick(ActionEvent event) throws IOException, SQLException {
        username = usernameField.getText();
        password = passwordField.getText();
        if (database.checkIfUsernameExists(username) && database.checkIfPasswordCorrect(username, password)) {
            User user = database.getUserByUsername(username);
            //loadStartController(event, user, database, mqttHandler); // Passing User and Database as parameters to use in application
            loadHomeController(event, user, database, mqttHandler); // Passing User and Database as parameters to use in application
        } else { // Username does not exist OR wrong password
            showPopup();
        }
    }

    private void loadStartController(ActionEvent event, User user, Database database, MQTTHandler mqttHandler) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.START));
        Parent root = loader.load();
        StartController startController = loader.getController();
        startController.initialize(user, database, mqttHandler); // Important to pass these objects to the controller
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Load home page upon clicking "Log In" if user credentials match what is in the database.
     * @param event is the event triggers
     * @param user is the user object
     * @param database is the database object
     * @param mqttHandler is the MQTTHandler object
     * @throws IOException if IO exception occurs
     * @throws SQLException if SQL exception occurs
     */
    private void loadHomeController(ActionEvent event, User user, Database database, MQTTHandler mqttHandler) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.MAIN));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // adding CSS styling option
        stage.setScene(scene);
        stage.show();

        MainController controller = loader.getController();
        controller.initialize(user, database, mqttHandler);
        controller.loadView(FxmlFile.HOME);
    }

    /**
     * Allow user to press "Enter" key to log in
     *
     * @param event is the event triggers
     */
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
     * @param event is the event triggers
     */
    public void onCreateUserClick(ActionEvent event) throws IOException {
        loadCreateUserController(event, database, mqttHandler);
    }

    /**
     * Method used to load the page specified
     *
     * @param event the event that triggers the loading of the fxml file
     */
    private void loadCreateUserController(ActionEvent event, Database database, MQTTHandler mqttHandler) throws IOException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(FxmlFile.CREATE_USER));
        Parent root = loader.load();
        CreateUserController createUserController = loader.getController();
        createUserController.initialize(database, mqttHandler);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // adding CSS styling option
        stage.setScene(scene);
        stage.show();
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

    public void onQuitButtonClick(ActionEvent event) {
        mqttHandler.disconnect();
        database.closeConnection();
        Platform.exit();
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
}
