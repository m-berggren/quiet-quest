package quietquest.app;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MQTTHandler {
    private final String HOST = "ee9e926915b64224a7bc895977db4ae9.s2.eu.hivemq.cloud";
    private final String USERNAME = "QuietQuest";
    private final String PASSWORD = "Quietquest1";

    private final String TOPIC_MOTION = "sensor/motion";
    private final String TOPIC_USONIC = "sensor/distance";
    private final UIUpdater uiUpdater;
    private final Mqtt5Client client;

    // Information used from this website:
    // https://console.hivemq.cloud/clients/java-hivemq?uuid=ee9e926915b64224a7bc895977db4ae9

    public MQTTHandler(UIUpdater uiUpdater) {
        this.uiUpdater = uiUpdater;

        client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(HOST)
                .serverPort(8883)
                .sslWithDefaultConfig()
                .automaticReconnectWithDefaultConfig()
                .build();
    }

    public void setup() {
        client.toBlocking().connectWith()
                .simpleAuth()
                .username(USERNAME)
                .password(UTF_8.encode(PASSWORD))
                .applySimpleAuth()
                .send();

        subscribe();
    }

    private void subscribe() {
        client.toAsync().subscribeWith()
                .topicFilter(TOPIC_MOTION)
                .callback(this::handleMessage)
                .send();
    }

    private void handleMessage(Mqtt5Publish publish) {
        ByteBuffer buffer = publish.getPayload().orElse(ByteBuffer.allocate(0));
        String messageContent = UTF_8.decode(buffer).toString();
        System.out.println("Received message on topic " + publish.getTopic() + ": " + messageContent);
        uiUpdater.updateUI(messageContent);
    }

    public void publishMessage(String topic, String message) {
        client.toAsync().publishWith()
                .topic(topic)
                .payload(UTF_8.encode(message))
                .send()
                .whenComplete((mqtt5Publish, throwable) -> {
                    if (throwable != null) {
                        System.err.println("Error Publishing: " + throwable.getMessage());
                    } else {
                        System.out.println("Message Published Successfully");
                    }
                });
    }

    public void start() {
        setup();
    }
}
