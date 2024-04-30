#ifndef MQTT_CONFIG_H           // Makes sure header file only runs once
#define MQTT_CONFIG_H

#include <WiFi.h>               // Wifi library
#include <PubSubClient.h>       // Library used for connectiong to MQTT
#include "credentials.h"        // SSID and PASSWORD information
#include "utils.h"              // Stored global variables
#include "Tune.h"               // Using questStart from Tune class

extern WiFiClient wifiClient; 
extern PubSubClient client;     // Extern tells the compiler these variables will be defined in cpp file

extern void setupNetwork();
void setupWifi();
void setupMqtt();
extern bool wifiConnected();
extern bool mqttConnected();
extern void checkConnections();
extern void callback(char* topic, byte* payload, unsigned int length);

#endif                          // MQTT_CONTROLLER_H