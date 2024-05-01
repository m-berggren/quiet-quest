package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import quietquest.QuietQuestMain;
import quietquest.model.Database;
import quietquest.model.User;
import quietquest.utility.FxmlFile;

import java.io.IOException;
import java.sql.SQLException;

public class CreateUserController extends BaseController{
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button saveButton;


    public void onSaveClick(ActionEvent event) throws SQLException, IOException {
        User user = new User(usernameTextField.getText(), passwordField.getText());
        Database database = new Database();
        if (database.createUser(user)) {
            System.out.println("Successfully created user.");
            loadFxml("create-quest-view.fxml", event);
        } else {
            System.out.println("Something went wrong.");
        }
        database.disconnect();
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
