#ifndef MQTT_CONFIG_H  // Makes sure mgtt.h it only runs once
#define MQTT_CONFIG_H

#include <WiFi.h>               // Wifi library
#include <rpcWiFi.h>            // Wifi library
#include <PubSubClient.h>       //
#include "credentials.h"        // SSID and PASSWORD information


extern WiFiClient wifiClient;           // Wifi client
extern PubSubClient client;             // Used to subscribe and loop connection, extern made available elsewhere

// Topics for publishing payload data
extern const char* TOPIC;
extern const char* TOPIC_PUB_MOTION;
extern const char* TOPIC_PUB_DISTANCE;
extern const char* TOPIC_PUB_QUEST;
extern const char* TOPIC_PUB_LIGHT;

// Topics for subscribing to data
extern const char *TOPIC_SUB_QUEST;

extern void checkMqttAndWifiConnections();
void setupWifi();
void setupClient();
extern bool wifiConnected();
extern bool mqttConnected();
extern void callback(char *topic, byte *payload, unsigned int length);

#endif          // Ends the check