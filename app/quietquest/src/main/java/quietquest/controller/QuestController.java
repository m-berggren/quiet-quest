package quietquest.controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Duration;
import quietquest.model.Quest;
import quietquest.model.Task;
import quietquest.utility.MQTTHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;



public class QuestController extends BaseController implements Initializable, UIUpdater, Callback<ListView<Task>, ListCell<Task>> {
    @FXML
    private Button startQuestButton;
    @FXML
    private Button completeQuestButton;
    @FXML
    private ListView<Task> tasksListView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descLabel;

    @FXML
    private ToggleButton subscribeButton;
    @FXML
    private Label mqttMotionMessage;
    @FXML
    private Label mqttDistanceMessage;
    @FXML
    private Label mqttConnectionMessage;
    @FXML
    private Label mqttLightMessage;
    @FXML
    private Label motivationalMessage;

    private MQTTHandler mqttClient;

    //private String selectedTask;
    private ArrayList<Task> tasks;
    private ObservableList<Task> data;

    private String []message;






    public void initialize(URL arg0, ResourceBundle arg1) {
        mqttClient = new MQTTHandler(this);
        tasksListView.setCellFactory(this);

    }


    @Override
    protected void afterMainController() {
        Quest quest = quietQuestFacade.getQuestManager().getQuestSelection();
        ArrayList<Task> tasks = quietQuestFacade.getQuestManager().getQuestSelection().getTasks();
        titleLabel.setText(quest.getTitle());
        //descLabel.setText(quest.getDescription());
        tasksListView.getItems().addAll(tasks);
        if(tasks != null) {
            data = FXCollections.observableArrayList(tasks);
            tasksListView.setItems(data);
        }
        setSelectedTask();
        displayTasks();



    }

    public void displayTasks(){
        if(tasks != null && !tasks.isEmpty()) {
            data = FXCollections.observableArrayList(tasks);
            tasksListView.setItems(data);
        }
       }



    private void setSelectedTask() {
        tasksListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
            @Override
            public void changed(ObservableValue<? extends Task> observableValue, Task oldValue, Task newValue) {
                if(newValue != null) {
                    tasksListView.getItems().clear();
                    tasksListView.getItems().add(newValue);
                }






            }
        });

    }
     @Override
    public ListCell<Task> call (ListView<Task> param){
        return new ListCell<>(){


            @Override
            public void updateItem(Task data,boolean empty){
                super.updateItem(data, empty);
                if (empty || data == null) {
                    setText(null);
                    setGraphic(null);
                    motivationalMessage.setVisible(false);
                } else {
                     CheckBox checkBox = new CheckBox(data.getTasks());
                     checkBox.setSelected(data.isCompleted());
                     checkBox.setOnAction(event -> {
                         data.setCompleted(checkBox.isSelected());

                         showMessage();
                     });
                     setGraphic(checkBox);
                     motivationalMessage.setVisible(false);
                 }
            }
        };






    }

    public void showMessage (){
        message = new String[]{"Good Job!", "Amazing!", "One step closer to a nap","You can do it!", "Wow! Is there anything you can´t do?"};
        Random random = new Random();
        int index = random.nextInt(message.length);
        String setMessage = message[index];
        motivationalMessage.setText(setMessage);
        motivationalMessage.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                motivationalMessage.setVisible(false);
            }));
            timeline.play();
    }






    public void onTickTaskClick(ActionEvent event) {
        String message = "You have completed a task!";
        mqttClient.publishMessage("/quietquest/application/start", message);
    }


    public void onStartQuestClick(ActionEvent event) {
        String message = "Your quest has started";
        mqttClient.publishMessage("/quietquest/application/start", message);
    }

    public void onCompleteQuestClick(ActionEvent event) {
        String message = "You have completed your quest!";
        mqttClient.publishMessage("/quietquest/application/start", message);
    }

    public void disconnectMqtt() {
        mqttClient.disconnect();
    }


    @FXML
    private void onSubscribeButtonClick() {
        if (subscribeButton.isSelected()) {
            mqttClient.connect(); // Connect to MQTT broker
            mqttClient.subscribe(); // Subscribe
        } else {
            mqttClient.disconnect();
            mqttConnectionMessage.getStyleClass().clear();
            mqttConnectionMessage.setText("");

        }
    }


    @Override
    public void updateConnectionStatusUI(boolean connectionStatus) {
        if (connectionStatus) {
            mqttConnectionMessage.setText("Connected.");
            mqttConnectionMessage.getStyleClass().clear();
            mqttConnectionMessage.getStyleClass().add("label-all-green");
        } else {
            mqttConnectionMessage.setText("Not connected.");
            mqttConnectionMessage.getStyleClass().clear();
            mqttConnectionMessage.getStyleClass().add("label-all-red");
        }
    }

    @Override
    public void updateLightSensorUI(int lightValue) {
        mqttLightMessage.setText("Light value: " + lightValue);
        mqttLightMessage.getStyleClass().clear();

        if (lightValue > 50) {
            mqttLightMessage.getStyleClass().add("label-all-red");
        } else if (lightValue > 30) {
            mqttLightMessage.getStyleClass().add("label-all-yellow");
        } else {
            mqttLightMessage.getStyleClass().add("label-all-green");
        }
    }

    @Override
    public void updateMotionSensorUI(boolean motionDetected) {
        if (motionDetected) {
            mqttMotionMessage.setText("Motion detected.");
            mqttMotionMessage.getStyleClass().clear();
            mqttMotionMessage.getStyleClass().add("label-all-red");
        } else {
            mqttMotionMessage.setText("Motion not detected.");
            mqttMotionMessage.getStyleClass().clear();
            mqttMotionMessage.getStyleClass().add("label-all-green");
        }
    }

    @Override
    public void updateUltrasonicSensorUI(int distanceValue) {
        mqttDistanceMessage.setText("Distance to obstacle: " + distanceValue + " cm.");
        mqttDistanceMessage.getStyleClass().clear();

        if (distanceValue > 100) {
            mqttDistanceMessage.getStyleClass().add("label-all-green");
        } else if (distanceValue > 50) {
            mqttDistanceMessage.getStyleClass().add("label-all-yellow");
        } else {
            mqttDistanceMessage.getStyleClass().add("label-all-red");
        }
    }

}