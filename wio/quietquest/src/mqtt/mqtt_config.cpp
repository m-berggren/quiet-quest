#include "mqtt_config.h"                                            // Linked header file

WiFiClient wifiClient; 
PubSubClient client(wifiClient);
boolean QUEST_RUNS = false;

// ==========================* CALLBACK METHOD *=====================

/**
 * TODO
 * @param topic
 * @param payload
 * @param length
 */
void callback(char* topic, byte* payload, unsigned int length) {
    Serial.print(String("Message arrived [") + topic + "]:"); // Combining topic of type char* and the string of const char[] with aid of String()

    char *txt = (char *)malloc(length + 1);
    memcpy(txt, payload, length);
    txt[length] = '\0';

    Serial.println(txt);
    free(txt);

    if (strcmp(topic, TOPIC_SUB_QUEST_START) == 0) {
        QUEST_RUNS = true;
        if(strcmp(txt, "Your quest has started") == 0) {
            // TODO: implement to show text on terminal
            int beats[] = { 1, 1, 1, 4, 10 };
            questStart.playTune(5, "cegC ", beats, 100);
        } else if (strcmp(txt, "You have completed your quest!") == 0) {
            // TODO: implement to show text on terminal
            int beats[] = { 3, 3, 3, 7, 10 };
            questStop.playTune(5, "bgec ", beats, 100);
        } else if (strcmp(txt, "You have completed a task!") == 0) {
            // TODO: implement to show text on terminal
            int beats[] = { 1, 8, 10 };
            taskStop.playTune(3, "cc ", beats, 100);
        }
    } else if (strcmp(topic, TOPIC_SUB_QUEST_END) == 0) {
        QUEST_RUNS = false;
    }
}

// ==========================* NETWORK *=============================

/**
 * TODO
*/
void setupNetwork() {
    setupWifi(); // Uses Wifi.begin
    setupMqtt(); //
}

/**
 * TODO
*/
void setupWifi() {
    // Combining SSID of type char* and the string of const char[] with aid of String()
    Serial.println(String("Attempting to connect to SSID: ") + SSID);
    while (!wifiConnected()) {
        delay(500);
        Serial.print(". . . connecting to Wifi");
        WiFi.begin(SSID, PASSWORD);
    }

    Serial.print("WiFi connected with IP address: ");
    Serial.println(WiFi.localIP());
}

// ==========================* MQTT *================================

/**
 * TODO
*/
void setupMqtt() {
    Serial.print("Attempting MQTT connection...");

    //Create a random client ID
    String clientID = "QuietQuest-terminal-";
    clientID += String(random(0xffff), HEX);
    int iter_count = 0;

    while (!client.connected() && iter_count <= LOOP_LIMIT) { // Restrict maximum amount of retries
        //try to connect
        Serial.println(String("Failed, returned: ") + client.state());
        Serial.println("Trying again ...");
        client.connect(clientID.c_str());                                   // Attempts to connect client to MQTT broker
        delay(3000);
    }
    client.subscribe(TOPIC_SUB_QUEST_START);                                      // Starts subscribing to topics
    client.subscribe(TOPIC_SUB_QUEST_END);
    Serial.println("Connected to broker.");
}

// ==========================* CHECKS *==============================

/**
 * TODO
 * @return
 */
bool wifiConnected() {
    if(WiFi.status() == WL_CONNECTED) {
      return true;
    }
    return false;
}

/**
 * TODO
 * @return
 */
bool mqttConnected() {
    if(client.connected()) {
        return true;
    }
    return false;
}

/**
 * TODO
 */
void checkConnections() {
    if (!wifiConnected()) {
    setupWifi();
    }

    if (!mqttConnected()) {
        setupMqtt();
    }

    client.loop();
}
