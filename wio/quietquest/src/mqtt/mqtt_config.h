#ifndef MQTT_CONFIG_H                                               // Makes sure header file only runs once
#define MQTT_CONFIG_H

#include <WiFi.h>                                                   // Wifi library
#include <PubSubClient.h>                                           // Library used for connecting to MQTT
#include "../credentials/credentials.h"                             // SSID and PASSWORD information
#include "../utilities/utils.h"                                     // Stored global variables
#include "../audio/tune.hpp"                                        // Using questStart from Tune class
#include "../display/display_config.h"                              // Used to clear screen & update static values

// ==========================* CONSTANTS *===========================
// For added safety constexpr <type> could be used over #define (macro), but it is not deemed needed in this project

#define PORT                                1883                    // Standard port for connecting to broker
#define SERVER                              "broker.hivemq.com"     // Standard server for connecting to broker
#define LOOP_LIMIT                          10                      // Limits the amount of times while loop is called

// Publishing sensor data
#define TOPIC_PUB_MOTION                    "/quietquest/sensor/motion"
#define TOPIC_PUB_DISTANCE                  "/quietquest/sensor/distance"
#define TOPIC_PUB_QUEST                     "/quietquest/sensor/connect"
#define TOPIC_PUB_LIGHT                     "/quietquest/sensor/light"

// Subscribing to quests & tasks
#define TOPIC_SUB_QUEST_START               "/quietquest/app/quest/start"
#define TOPIC_SUB_QUEST_END                 "/quietquest/app/quest/end"
#define TOPIC_SUB_TASK_END                  "/quietquest/app/task/end"
#define TOPIC_SUB_POMODORO_INTERVAL         "/quietquest/app/pomodoro/interval"

// ==========================* VARIABLES *===========================

extern WiFiClient wifiClient; 
extern PubSubClient client;                         // Extern tells compiler these variables will be defined in cpp file

extern bool QUEST_RUNS;                             // Decides whether sensors are active or not
extern bool POMODORO_BREAK;                         // A quick break is allowed before terminal

// ==========================* METHODS *=============================

extern void setupNetwork();
void setupWifi();
void setupMqtt();
extern bool wifiConnected();
extern bool mqttConnected();
extern void checkConnections();
extern void callback(char* topic, byte* payload, unsigned int length);

#endif