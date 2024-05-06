package quietquest.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import quietquest.controller.CreateQuestController;

public class ConfirmationAlert extends Alert {
    public ConfirmationAlert() {
        super(Alert.AlertType.CONFIRMATION);
        setDialogPane();
        setButtons();
    }

    public void setDialogPane() {
        // Apply the CSS file to the alert dialog window
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getStylesheets().add(
                CreateQuestController.class.getResource("/style.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }

    public void setButtons() {
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        this.getButtonTypes().setAll(yesButton, noButton);
    }
}
