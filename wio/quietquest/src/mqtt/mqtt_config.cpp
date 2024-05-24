#include "mqtt_config.h"                                            // Linked header file

WiFiClient wifiClient; 
PubSubClient client(wifiClient);
boolean QUEST_RUNS = false;
boolean POMODORO_BREAK = false;

// ==========================* CALLBACK METHOD *=====================

/**
 * Callback runs when a topic and payload is received from the application.
 * @param topic char* values that's the topic subscribed to.
 * @param payload byte*, the message that comes with payload.
 * @param length int value used to change bytes into a text.
 */
void callback(char* topic, byte* payload, unsigned int length) {
    // Combining topic of type char* and the string of const char[] with aid of String()
    Serial.print(String("Message arrived [") + topic + "]:");               // Not necessary but useful for tracing

    char *txt = (char *)malloc(length + 1);
    memcpy(txt, payload, length);
    txt[length] = '\0';

    if (strcmp(topic, TOPIC_SUB_QUEST_START) == 0) {
        QUEST_RUNS = true;
        int beats[] = { 1, 1, 1, 4, 10 };
        questStart.playTune(5, "cegC ", beats, 100);

    } else if (strcmp(topic, TOPIC_SUB_QUEST_END) == 0) {
        QUEST_RUNS = false;
        int beats[] = {3, 3, 3, 7, 10};
        questStop.playTune(5, "bgec ", beats, 100);
        drawStaticElements();
    }

    if (strcmp(topic, TOPIC_SUB_TASK_END) == 0) {
        int beats[] = { 1, 8, 10 };
        taskStop.playTune(3, "cc ", beats, 100);
    }

    if (strcmp(topic, TOPIC_SUB_POMODORO_INTERVAL) == 0) {
        if (strcmp(txt, "Break time started") == 0) {
            POMODORO_BREAK = true;
            int beats[] = { 1, 7, 10 };
            pomodoroInterval.playTune(3, "cd ", beats, 100);

        } else if(strcmp(txt, "Focus time started") == 0) {
            POMODORO_BREAK = false;
            int beats[] = { 1, 6, 10 };
            pomodoroInterval.playTune(3, "ce ", beats, 100);
        } else if(strcmp(txt, "Break time ended") == 0) {
            POMODORO_BREAK = false;
            int beats[] = { 1, 4, 10 };
            pomodoroInterval.playTune(3, "cg ", beats, 100);
        } else if (strcmp(txt, "Pomodoro timer finished") == 0) {
            POMODORO_BREAK = false;
            QUEST_RUNS = false;
        }
    }
    free(txt);
}

// ==========================* NETWORK *=============================

/**
 * Method that does the first setup in the arduino file.
*/
void setupNetwork() {
    setupWifi(); // Uses Wifi.begin
    setupMqtt(); //
}

/**
 * Setting up the wifi connection.
*/
void setupWifi() {
    // Combining SSID of type char* and the string of const char[] with aid of String()
    Serial.println(String("Attempting to connect to SSID: ") + SSID);       // Not necessary but useful for tracing
    while (!wifiConnected()) {
        delay(500);
        Serial.print(". . . connecting to Wifi");                           // Not necessary but useful information
        WiFi.begin(SSID, PASSWORD);
    }

    Serial.print("WiFi connected with IP address: ");                       // Not necessary but useful information
    Serial.println(WiFi.localIP());
}

// ==========================* MQTT *================================

/**
 * Method that sets up MQTT broker connection and subscribes to all topics.
*/
void setupMqtt() {
    Serial.print("Attempting MQTT connection...");                          // While not necessary it is useful for user

    //Create a random client ID
    String clientID = "QuietQuest-terminal-";
    clientID += String(random(0xffff), HEX);
    int iter_count = 0;

    while (!client.connected() && iter_count <= LOOP_LIMIT) {               // Restrict maximum amount of retries
        //try to connect
        Serial.println(String("Failed, returned: ") + client.state());      // Not necessary but useful for tracing
        Serial.println("Trying again ...");
        client.connect(clientID.c_str());                                   // Attempts to connect client to MQTT broker
        iter_count++;
        delay(3000);
    }

    // Start subscribing to topics
    client.subscribe(TOPIC_SUB_QUEST_START);
    client.subscribe(TOPIC_SUB_QUEST_END);
    client.subscribe(TOPIC_SUB_TASK_END);
    client.subscribe(TOPIC_SUB_POMODORO_INTERVAL);
    Serial.println("Connected to broker.");
}

// ==========================* CHECKS *==============================

/**
 * Checks if wifi is connected. Used in every loop() call in arduino file and within methods in this class.
 * @return boolean value, true if connected.
 */
bool wifiConnected() {
    if(WiFi.status() == WL_CONNECTED) {
      return true;
    }
    return false;
}

/**
 * Checks if mqtt connection is valid. Used in every loop() call in arduino file and within methods in this class.
 * @return boolean value, true if connected.
 */
bool mqttConnected() {
    if(client.connected()) {
        return true;
    }
    return false;
}

/**
 * Method that runs in the end of ever loop() in arduino file. If no wifi/mqtt connection then attempts to set it up.
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
