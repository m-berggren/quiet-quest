package quietquest.utility;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import javafx.application.Platform;
import quietquest.controller.UIUpdater;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MQTTHandler {

    private final String HOST = "broker.hivemq.com";
    private final String SUB_TOPICS = "/quietquest/sensor/#";
    private final String PUB_TOPICS = "/quietquest/application/#";

    private final UIUpdater uiUpdater;
    private final Mqtt5AsyncClient client;

    // Information used from this website:
    // https://console.hivemq.cloud/clients/java-hivemq?uuid=ee9e926915b642241a7bc895977db4ae9
    // For testing and building we are using a public MQTT broker by HiveMQ

    public MQTTHandler(UIUpdater uiUpdater) {
        this.uiUpdater = uiUpdater;
        client = Mqtt5Client.builder()
                .serverHost(HOST)
                .buildAsync();
    }

    public void connect() {
        client.connectWith().send();
    }

    public void connect(String pubTopic, String pubMessage) {
        client.connectWith()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable == null) {
                        publishMessage(pubTopic, pubMessage);
                    }
                });
    }

    public void disconnect() {
        client.disconnect();
    }

    public void subscribe() {
        client.subscribeWith()
                .topicFilter(SUB_TOPICS)
                .callback(this::handleMessage)// Method reference
                // Can use lambda as well, like so: m -> this.handleMessage(m)
                .send();
    }

    private void handleMessage(Mqtt5Publish subMessage) {
        ByteBuffer buffer = subMessage.getPayload().orElse(ByteBuffer.allocate(0));
        String messageContent = UTF_8.decode(buffer).toString();
        String topic = subMessage.getTopic().toString();

        System.out.println("Received message on topic " + subMessage.getTopic() + ": " + messageContent);

        String[] topicParts = topic.split("/");
        if (topicParts.length > 3) { // making sure to only handle topics with 3 parts (first '/' creates empty segment)
            String sensorType = topicParts[3]; // assume sensor type is the 4th element

            Platform.runLater(() -> {
                switch (sensorType) {
                    case "connect" -> handleConnectionStatusData(messageContent);
                    case "light" -> handleLightSensorData(messageContent);
                    case "motion" -> handleMotionSensorData(messageContent);
                    case "distance" -> handleUltrasonicSensorData(messageContent);
                    default -> System.out.println("Unknown sensor type: " + sensorType);
                }
            });
        } else {
            System.out.println("Invalid topic structure: " + topic);
        }
    }

    private void handleConnectionStatusData(String data) {
        boolean connectionStatus = Integer.parseInt(data) == 1;
        uiUpdater.updateConnectionStatusUI(connectionStatus);
    }

    private void handleLightSensorData(String data) {
        int lightValue = Integer.parseInt(data);
        uiUpdater.updateLightSensorUI(lightValue);
    }

    private void handleMotionSensorData(String data) {
        boolean motionDetected = Integer.parseInt(data) == 1;
        uiUpdater.updateMotionSensorUI(motionDetected);
    }

    private void handleUltrasonicSensorData(String data) {
        int ultrasonicValue = Integer.parseInt(data);
        uiUpdater.updateUltrasonicSensorUI(ultrasonicValue);
    }

    public void publishMessage(String topic, String message) {
        client.toAsync().publishWith()
                .topic(topic)
                .payload(UTF_8.encode(message))
                .send()
                .whenComplete((mqtt5Publish, throwable) -> { // When published handle error or send confirmation
                    if (throwable != null) {
                        System.err.println("Error Publishing: " + throwable.getMessage());
                    } else {
                        System.out.println("Message Published Successfully");
                    }
                });
    }
}
