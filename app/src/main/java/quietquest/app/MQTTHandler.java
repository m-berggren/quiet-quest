package quietquest.app;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MQTTHandler {
    private final String HOST = "broker.hivemq.com";
    private final String SUB_TOPICS = "/quietquest/sensor/#";
    private final UIUpdater uiUpdater;
    private final Mqtt5AsyncClient client;

    // Information used from this website:
    // https://console.hivemq.cloud/clients/java-hivemq?uuid=ee9e926915b64224a7bc895977db4ae9
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
                .whenComplete((mqtt5Publish, throwable) -> { // When published handle error or send confirmation
                    if (throwable != null) {
                        System.err.println("Error Publishing: " + throwable.getMessage());
                    } else {
                        System.out.println("Message Published Successfully");
                    }
                });
    }
}
