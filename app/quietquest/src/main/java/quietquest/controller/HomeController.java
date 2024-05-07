package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import quietquest.model.Quest;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class HomeController extends BaseController implements Initializable {
    @FXML
    private ListView<Quest> questListView;
    private HashMap<String, Quest> quests;
    private Quest currentQuest;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        try {
            displayQuests();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void afterMainController() throws SQLException {
        quests = quietQuestFacade.getQuests();
        displayQuests();
        setSelectedQuest();
    }
    public void onContinueQuestClick(ActionEvent event) {
        showQuest(currentQuest);
    }
    public void onCreateQuestClick(ActionEvent event) {
        showCreateQuest();
    }
    public void onGoToQuests(ActionEvent event) {
        showQuestList();
    }
    public void displayQuests() throws SQLException {
        if (quietQuestFacade != null) {
            database.connect();
            ArrayList<Quest> questsList = database.getAllQuests(user);
            database.disconnect();

            //quests = quietQuestFacade.getQuests();
            ObservableList<Quest> questList = FXCollections.observableArrayList(questsList);
            questListView.setItems(questList);
            questListView.setCellFactory(param -> new ListCell<Quest>() {
                @Override
                protected void updateItem(Quest item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getTitle() == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            });
        }
    }

    private void setSelectedQuest() {
        questListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Quest>() {
            @Override
            public void changed(ObservableValue<? extends Quest> observable, Quest oldValue, Quest newValue) {
                if (newValue != null) {
                    currentQuest = newValue;
                    quietQuestFacade.setQuestSelection(currentQuest);
                }
            }
        });

    }
}
