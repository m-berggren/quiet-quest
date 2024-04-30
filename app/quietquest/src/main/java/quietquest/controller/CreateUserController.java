package quietquest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

public class CreateUserController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button saveButton;


    public void onSaveClick(ActionEvent event) {
        //set created_at current datetime
        //set app_sound ON
        //set sensor_sound ON
        //set desk_mode OFF
        //insert user into database
        //log in user and go to start page
    }
}
