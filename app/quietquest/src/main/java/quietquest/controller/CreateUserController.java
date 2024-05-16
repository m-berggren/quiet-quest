package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import quietquest.QuietQuestMain;
import quietquest.model.Database;
import quietquest.model.User;
import quietquest.utility.FxmlFile;
import quietquest.utility.MQTTHandler;

import java.io.IOException;
import java.sql.SQLException;

public class CreateUserController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button saveButton;
    @FXML
    private Label usernameExistsLabel;
    @FXML
    private Label passwordHintLabel;

    private Database database;
    private MQTTHandler mqttHandler;
	private MediaPlayer mediaPlayer;

	public void initialize(Database database, MQTTHandler mqttHandler, MediaPlayer mediaPlayer) {
        this.database = database;
        this.mqttHandler = mqttHandler;
		this.mediaPlayer = mediaPlayer;
    }

    /**
     * Directs back to the login page without saving new user into the database.
     *
     * @param event
     * @throws IOException
     */
    public void onBackClick(ActionEvent event) throws IOException {
        loadFxml(FxmlFile.LOG_IN, event, database, mqttHandler);
    }

    /**
     * Saves a new user to the database if a unique username and valid password are provided.
     *
     * @param event ActionEvent
     * @throws SQLException is the SQL exception thrown by the database
     * @throws IOException is the IO exception thrown by the FXMLLoader
     */
    public void onSaveClick(ActionEvent event) throws SQLException, IOException {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        boolean usernameOK = false;
        boolean passwordOK = false;

        if (database.checkIfUsernameExists(username)) {
            usernameExistsLabel.setVisible(true);
        } else {
            usernameOK = true;
            usernameExistsLabel.setVisible(false);
        }

        if (!passwordValid(password) || password.isEmpty()) {
            passwordHintLabel.setVisible(true);
        } else {
            passwordOK = true;
            passwordHintLabel.setVisible(false);
        }

        if (usernameOK && passwordOK) {
            if (database.createUser(username, password)) {
                System.out.println("Successfully created user.");
                loadFxml(FxmlFile.LOG_IN, event, database, mqttHandler);
            } else {
                System.out.println("Something went wrong.");
            }
        }
    }

    /**
     * Checks validity of new password.
     *
     * @param password String that should contain at least 8 characters where at least one must be a number.
     * @return True if password fulfills criteria, otherwise false.
     */
    public boolean passwordValid(String password) {
        boolean length = password.length() >= 8;
        boolean containsNumber = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                containsNumber = true;
                i = password.length();
            }
        }
        return length && containsNumber;
    }


    /**
     * Loads the fxml file and sets the controller for the log in page.
     * @param fxmlFile the fxml file to be loaded
     * @param event the event that triggers the loading of the fxml file
     * @param database the database
     * @param mqttHandler the mqtt handler
     * @throws IOException if IO exception occurs
     */
    private void loadFxml(String fxmlFile, ActionEvent event, Database database, MQTTHandler mqttHandler) throws IOException {
        FXMLLoader loader = new FXMLLoader(QuietQuestMain.class.getResource(fxmlFile));
        Parent root = loader.load();
        LogInController logInController = loader.getController();
		logInController.initialize(database, mqttHandler, mediaPlayer);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // adding CSS styling option
        stage.setScene(scene);
        stage.show();
    }
}
