package quietquest.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import quietquest.model.Quest;

import java.sql.SQLException;
import java.util.ArrayList;

public class HomeController extends BaseController {
    @FXML
    private ListView<Quest> questListView;
    private Quest currentQuest;
    

    // ==============================* INITIALIZATION METHODS *===================================

    /**
     * TODO: JavaDoc
     */
    @Override
    protected void afterMainController() throws SQLException {
        displayQuests();
        setSelectedQuest();
    }

    /**
     * TODO: JavaDoc
     */
    public void displayQuests() {
        ArrayList<Quest> questsList = getAllQuests();

        /*
        Tracks changes made to the elements in questsList and notifies the observer questObservableList.
        The FXML property questListView uses the ObservableList to refresh and update if any changes.
        The setCellFactory decides how the items (cells) shall be visualised and ListCell created via a lambda
        function. ListCell is able to display JavaFX text (among others) and updateItem is used to handle each cell.
         */
        ObservableList<Quest> questsObservableList = FXCollections.observableArrayList(questsList);
        questListView.setItems(questsObservableList);
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

    /**
     * TODO: JavaDoc
     */
    private void setSelectedQuest() {
        questListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Quest>() {
            @Override
            public void changed(ObservableValue<? extends Quest> observable, Quest oldValue, Quest newValue) {
                if (newValue != null) {
                    currentQuest = newValue;
                }
            }
        });
    }

    // ==============================* ACTION EVENTS *======================================

    /**
     * TODO: JavaDoc
     *
     * @param event
     */
    public void onContinueQuestClick(ActionEvent event) {
        showQuest(currentQuest);
    }

    /**
     * TODO: JavaDoc
     *
     * @param event
     */
    public void onCreateQuestClick(ActionEvent event) {
        showCreateQuest();
    }

    /**
     * TODO: JavaDoc
     *
     * @param event
     */
    public void onGoToQuests(ActionEvent event) {
        showQuestList();
    }
}
