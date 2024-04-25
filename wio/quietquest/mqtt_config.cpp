#include "mqtt_config.h"                                            // Linked header file

WiFiClient wifiClient;                                               // Initializes Wifi client
PubSubClient client(wifiClient);                                     // Used to subscribe and loop connection

// Topics for publishing payload data
const char* TOPIC_PUB_MOTION    = "/quietquest/sensor/motion";
const char* TOPIC_PUB_DISTANCE  = "/quietquest/sensor/distance";
const char* TOPIC_PUB_QUEST     = "/quietquest/sensor/connect";
const char* TOPIC_PUB_LIGHT     = "/quietquest/sensor/light";

// Topics for subscribing to data
const char* TOPIC_SUB_QUEST     = "/quietquest/application/start";

/**
 * TODO
*/
void checkMqttAndWifiConnections() {
  if (!wifiConnected) {
    setupWifi();
  }

  if (!mqttConnected()) {
    setupClient();
  }

  client.loop();

}

/**
 * TODO
*/
void subscribeToTopics() {
  // Start subscribing
  Serial.println(String("Subscribed to: ") + TOPIC_SUB_QUEST);
  client.subscribe(TOPIC_SUB_QUEST);
  

}

/**
 * TODO
*/
void setupClient() {
  Serial.print("Attempting MQTT connection...");

  //Create a random client ID
  String clientID = "QuietQuest-terminal-";
  clientID += String(random(0xffff), HEX);

  while (!client.connected()) {
    //try to connect
    Serial.println(String("Failed, returned: ") + client.state());
    Serial.println("Trying again ...");
    client.connect(clientID.c_str());                                    // Attempts to connect client to MQTT broker
  }
     
  Serial.println("Connected to broker.");
  subscribeToTopics();                                                   // Starts subscribing to topics
}

/**
 * TODO
*/
void setupWifi() {
  Serial.println(String("Attempting to connect to SSID: ") + SSID); // Combining SSID of type char* and the string of const char[] with aid of String()


  while (!wifiConnected()) {
    delay(500);
    Serial.print(". . . connecting to Wifi");
    WiFi.begin(SSID, PASSWORD);
  }

  Serial.print("WiFi connected with IP address: ");
  Serial.println(WiFi.localIP());
}

/**
 * TODO
*/
bool wifiConnected() {
  if(WiFi.status() == WL_CONNECTED) {
    return true;
  }
  return false;
}

bool mqttConnected() {
  if(client.connected()) {
    return true;
  }
  return false;
}

/**
 * TODO
*/
void callback(char *topic, byte *payload, unsigned int length) {
  Serial.print(String("Message arrived [") + topic + "]:"); // Combining topic of type char* and the string of const char[] with aid of String()

  char *txt = (char *)malloc(length + 1);
  memcpy(txt, payload, length);
  txt[length] = '\0';

  if (strcmp(topic, TOPIC_SUB_QUEST) == 0) {
    //show(txt);
    delay(2000);
  }

  //free(txt);
  Serial.println();
}
