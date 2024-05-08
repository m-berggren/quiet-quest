package quietquest.controller;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import quietquest.model.Activity;
import quietquest.model.Quest;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class QuestHistoryController extends BaseController {
  @FXML
  public ListView<Quest> quesHistoryListView;
  @FXML
  private AnchorPane questlistAnchorPane;
  @FXML
  private Label titleLabel;

  @FXML
  private Button goToStatisticsButton;
  @FXML
  private AnchorPane viewInfoAnchorPane;
  @FXML
  private TextField titleField;
  @FXML
  private Text descriptionHeader;
  @FXML
  private TextArea descriptionField;
  @FXML
  private Text tasksHeader;
  @FXML
  private ListView<Activity> activityListView;

  private HashMap<String, Quest> quests;

  @Override
  public void afterMainController() throws SQLException {
    //quests = quietQuestFacade.getQuests();
    //System.out.println("Number of quests loaded: " + quests.size());
    displayQuests();
  }

  public void displayQuests() throws SQLException {
    // Convert the values from the HashMap to an ObservableList
    database.connect();
    ArrayList<Quest> questsList = database.getAllQuests(user);
    database.disconnect();
    //Get a list of quest with quest completionStatus as true
    ArrayList<Quest> completedQuestList = new ArrayList<>();
    for (Quest quest : questsList) {
      if (quest.getCompletionState()) {
        completedQuestList.add(quest);
      }
    }
    ObservableList<Quest> questItems = FXCollections.observableArrayList(completedQuestList);

    // Set the items on the questHistoryListView
    quesHistoryListView.setItems(questItems);

    // Set the cell factory to display the titles of quests
    quesHistoryListView.setCellFactory(lv -> new ListCell<Quest>() {
      @Override
      protected void updateItem(Quest quest, boolean empty) {
        super.updateItem(quest, empty);
        if (empty || quest == null) {
          setText(null);
          setGraphic(null);
        } else {
          // Creating a custom layout for the list cell
          VBox vBox = new VBox(5); // Vertical box with spacing
          Label titleLabel = new Label(quest.getTitle());
          titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

          Label startTimeLabel = new Label("Start Time: " + formatDate(quest.getStartTime()));
          Label completionTimeLabel = new Label("Completed Time: " + formatDate(quest.getCompleteTime()));

          vBox.getChildren().addAll(titleLabel, startTimeLabel, completionTimeLabel);
          setGraphic(vBox); // Set the VBox as the graphic of the list cell
        }
      }
    });


    // Setup listener for item selection to update quest details
    quesHistoryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Quest>() {
      @Override
      public void changed(ObservableValue<? extends Quest> observable, Quest oldValue, Quest newValue) {
        if (newValue != null) {
          updateQuestDetails(newValue);
        }
      }
    });
  }


  private String formatDate(Timestamp timestamp) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return format.format(timestamp);
  }

  public void onStatisticsClick() {
    goToStatisticsButton.setOnAction(event -> {
      showStatistics();
    });
  }

  private void updateQuestDetails(Quest quest) {
    titleField.setText(quest.getTitle());
    descriptionField.setText(quest.getDescription());
    if (activityListView == null) {
      System.out.println("activityListView is null");
    } else {
      setupActivityListView();
    }
  }

  private void setupActivityListView() {
    activityListView.setCellFactory(lv -> new ListCell<Activity>() {
      @Override
      protected void updateItem(Activity activity, boolean empty) {
        super.updateItem(activity, empty);
        setText(empty || activity == null ? "" : activity.toString());
      }
    });
  }
}

