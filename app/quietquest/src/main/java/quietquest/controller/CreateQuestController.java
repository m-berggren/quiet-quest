package quietquest.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import quietquest.model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateQuestController extends BaseController implements Initializable {
    @FXML
    private Button saveQuestButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button goToQuestsButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField taskField;
    @FXML
    private Button deleteTaskButton;
    @FXML
    private Pane popupPane;
    @FXML
    private Shape popupTextbox;
    @FXML
    private Button okayButton;
    @FXML
    private Text popupText;
    @FXML
    private Text popupSmallText;
    @FXML
    private Button addNewTaskButton;
    @FXML
    private Button addNewPomodoroButton;
    @FXML
    private TextField focusTextField;
    @FXML
    private TextField breakTextField;
    @FXML
    private TextField intervalTextField;
    @FXML
    private Slider focusSlider;
    @FXML
    private Slider breakSlider;
    @FXML
    private Slider intervalSlider;
    @FXML
    private TabPane activityTabPane;
    @FXML
    private ListView<Activity> activityListView;

    private ObservableList<Activity> activityObservableList;
    private Tab lastSelectedTab;
    private User user;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        configureSliderListener();
        tabChangeListener();

        activityObservableList = FXCollections.observableArrayList();
        activityListView.setItems(activityObservableList);
    }

    @Override
    protected void afterMainController() throws SQLException {
        this.user = getUser();
        super.afterMainController();
    }

    private void configureSliderListener() {
        // Add a listener to the Slider's value property
        focusSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Rounds down value to intervals of 5. For example: 24 / 5 rounded to 4.0 and multiplied by 5
                double roundedValue = Math.round(newValue.doubleValue() / 5) * 5;
                focusSlider.setValue(roundedValue); // Updates slider position
                focusTextField.setText(String.valueOf((int) roundedValue)); // Updates text by intervals of 10
            }
        });
        breakSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double roundedValue = Math.round(newValue.doubleValue() / 5) * 5;
                breakSlider.setValue(roundedValue); // Updates slider position
                breakTextField.setText(String.valueOf((int) roundedValue)); // Updates text by intervals of 5
            }
        });
        intervalSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int intValue = newValue.intValue();
                intervalSlider.setValue(intValue);
                intervalTextField.setText(String.valueOf(intValue));
            }
        });

    }

    private void tabChangeListener() {
        activityTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if (newTab != lastSelectedTab) {
                    // The tab has changed so method executes
                    onTabChange(oldTab, newTab);
                }
            }
        });
        // Initialize lastSelectedTab to the current selected tab
        lastSelectedTab = activityTabPane.getSelectionModel().getSelectedItem();
    }

    private void onTabChange(Tab oldTab, Tab newTab) {
        if (!activityListView.getItems().isEmpty()) {
            Platform.runLater(() -> {
                ConfirmationAlert alert = new ConfirmationAlert();
                alert.setTitle("Confirmation");
                alert.setHeaderText("Switching between tabs will erase all prior data.");
                alert.setContentText("Do you want to continue?:");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get().getText().equals("No")) {
                    activityTabPane.getSelectionModel().select(oldTab);
                } else {
                    lastSelectedTab = newTab;
                    activityListView.getItems().clear();
                }
            });
        } else {
            lastSelectedTab = newTab;
        }
    }

    /**
     *
     */
    public void createTask() {
        String taskDescription = taskField.getText();
        if (!taskDescription.isEmpty()) {
            Task newTask = new Task(taskDescription);
            activityObservableList.add(newTask);
            taskField.clear();
        }
    }

    /**
     *
     */
    public void createPomodoroTimer() {
        int focusTime = Integer.parseInt(focusTextField.getText());
        int breakTime = Integer.parseInt(breakTextField.getText());
        int intervals = Integer.parseInt(intervalTextField.getText());

        PomodoroTimer newPomodoro = new PomodoroTimer(focusTime, breakTime, intervals);
        if (!activityObservableList.isEmpty() && activityObservableList.getFirst() instanceof PomodoroTimer) {
            ConfirmationAlert alert = new ConfirmationAlert();
            alert.setTitle("Confirmation");
            alert.setHeaderText("This will update existing Pomodoro activity.");
            alert.setContentText("Do you want to continue?:");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get().getText().equals("Yes")) {
                activityObservableList.clear();
                activityObservableList.add(newPomodoro);
            }
        } else {
            activityObservableList.add(newPomodoro);
        }
    }

    /**
     * Delete selected activity from activity list and remove from activity list view too.
     */
    public void deleteActivity() {
        Activity currentActivity = activityListView.getSelectionModel().getSelectedItem();
        activityObservableList.remove(currentActivity);
    }

    /**
     * Creates a quest upon clicking "Save" button.
     */
    public void createQuest(ActionEvent event) throws Exception {
        //if quest title field is left empty:
        if (titleField.getText().isEmpty()) {
            showMessage("Don't forget to name your quest!",
                    "Quests must have a title. Do not leave this field empty.");
        /* You can now save quests with the same title
            //if quest title is already taken:
        } else if (quietQuestFacade.getQuests().containsKey(titleField.getText())) {
            showMessage("Give your quest a unique title",
                    "Each quest must have a unique title.");
            //if everything good with title, create quest:
         */
        } else {
            String title = titleField.getText();
            String description = descriptionField.getText();
            ArrayList<Activity> activities = new ArrayList<>(activityListView.getItems());
            Quest quest = new Quest(user, title, description, activities);
            quietQuestFacade.createQuest(quest, activities);
            showCreateQuest();
        }
    }

    /**
     * Method that makes error/success message pop-up window appear.
     */
    public void showMessage(String message, String smallMessage) {
        popupPane.setDisable(false);
        popupPane.setVisible(true);
        popupTextbox.setVisible(true);
        okayButton.setVisible(true);
        popupText.setVisible(true);
        popupText.setText(message);
        popupSmallText.setVisible(true);
        popupSmallText.setText(smallMessage);
        // Disable other fields and buttons
        saveQuestButton.setDisable(true);
        clearButton.setDisable(true);
        goToQuestsButton.setDisable(true);
        titleField.setDisable(true);
        descriptionField.setDisable(true);
        taskField.setDisable(true);
        addNewTaskButton.setDisable(true);
        addNewPomodoroButton.setDisable(true);
        //taskListView.setDisable(true);
        deleteTaskButton.setDisable(true);
    }

    /**
     * Error/success message pop-up window disappears upon clicking "Okay" button.
     *
     * @param event
     * @throws IOException
     */
    public void onOkayButtonClick(ActionEvent event) throws IOException {
        popupPane.setVisible(false);
        popupPane.setDisable(true);
        popupTextbox.setVisible(false);
        popupText.setVisible(false);
        popupSmallText.setVisible(false);
        okayButton.setVisible(false);
        // enable other fields and buttons
        saveQuestButton.setDisable(false);
        clearButton.setDisable(false);
        goToQuestsButton.setDisable(false);
        titleField.setDisable(false);
        descriptionField.setDisable(false);
        taskField.setDisable(false);
        addNewTaskButton.setDisable(false);
        addNewPomodoroButton.setDisable(false);
        //taskListView.setDisable(false);
        deleteTaskButton.setDisable(false);
    }

    /**
     * Clear all fields upon clicking "Clear" button.
     */
    @FXML
    public void clearAllFields() throws IOException {
        titleField.clear();
        descriptionField.clear();
        taskField.clear();
        activityObservableList.clear();
    }

    /**
     * Go to "Quest List" by clicking "List" button.
     */
    public void onGoToQuests() {
        activityObservableList.clear();
        //quietQuestFacade.clearActivities();
        showQuestList();
    }
}
