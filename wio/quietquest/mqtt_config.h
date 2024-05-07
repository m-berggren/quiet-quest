#ifndef MQTT_CONFIG_H                               // Makes sure header file only runs once
#define MQTT_CONFIG_H

#include <WiFi.h>                                   // Wifi library
#include <PubSubClient.h>                           // Library used for connecting to MQTT
#include "credentials.h"                            // SSID and PASSWORD information
#include "utils.h"                                  // Stored global variables
#include "Tune.h"                                   // Using questStart from Tune class

// ==========================* CONSTANTS *===========================

#define PORT                1883                    // Standard port for connecting to broker
#define SERVER              "broker.hivemq.com"     // Standard server for connecting to broker
#define LOOP_LIMIT          10                      // Limits the amount of times while loop will be called

// Publishing sensor data
#define TOPIC_PUB_MOTION        "/quietquest/sensor/motion"
#define TOPIC_PUB_DISTANCE      "/quietquest/sensor/distance"
#define TOPIC_PUB_QUEST         "/quietquest/sensor/connect"
#define TOPIC_PUB_LIGHT         "/quietquest/sensor/light"

// Subscribing to quests & tasks
#define TOPIC_SUB_QUEST_START   "/quietquest/application/start"
#define TOPIC_SUB_QUEST_END     "/quietquest/application/end"

// ==========================* VARIABLES *===========================

extern WiFiClient wifiClient; 
extern PubSubClient client;                         // Extern tells compiler these variables will be defined in cpp file

extern bool QUEST_RUNS;                             // Decides whether sensors are active or not

// ==========================* METHODS *=============================

extern void setupNetwork();
void setupWifi();
void setupMqtt();
extern bool wifiConnected();
extern bool mqttConnected();
extern void checkConnections();
extern void callback(char* topic, byte* payload, unsigned int length);

#endif                                              // MQTT_CONFIG_H