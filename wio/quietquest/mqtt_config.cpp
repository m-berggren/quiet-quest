#include "mqtt_config.h"                                            // Linked header file

WiFiClient wifiClient; 
PubSubClient client(wifiClient);

/**
 * TODO
*/
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print(String("Message arrived [") + topic + "]:"); // Combining topic of type char* and the string of const char[] with aid of String()

  char *txt = (char *)malloc(length + 1);
  memcpy(txt, payload, length);
  txt[length] = '\0';

  Serial.println(txt);
  free(txt);

  if (strcmp(topic, TOPIC_SUB_QUEST) == 0) {
    // TODO
  }
}

/**
 * TODO
*/
void setupNetwork() {
  setupWifi(); // Uses Wifi.begin 
  setupMqtt(); // 
}


// Network-related
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

// MQTT-related

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
    client.connect(clientID.c_str());                                    // Attempts to connect client to MQTT broker
    delay(3000);
  }
     
  Serial.println("Connected to broker.");                                // Starts subscribing to topics
}

// Checks

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
void checkConnections() {
  if (!wifiConnected()) {
    setupWifi();
  }

  if (!mqttConnected()) {
    setupMqtt();
  }

  client.loop();

}
