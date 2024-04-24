#include <WiFi.h>
#include "rpcWiFi.h"
#include "TFT_eSPI.h"
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include "Ultrasonic.h"
#include <string.h>
#include "credentials.h" // include header where SSID and PASSWORD are defined
#include "utils.h"
#include "mqtt_config.h"

#define PIR_MOTION_SENSOR PIN_WIRE_SCL
Ultrasonic ultrasonic(PIN_WIRE_SCL);

TFT_eSPI tft;


//MQTT server
const char *mqtt_server = "broker.hivemq.com";

WiFiClient wifiClient;
PubSubClient client(wifiClient);

// Initializing




void setup() {
  pinMode(D0, INPUT);
  pinMode(PIN_WIRE_SCL, INPUT);

  // initiate tft screen
  tft.begin();
  tft.setRotation(1);

  pinMode(PIR_MOTION_SENSOR, INPUT);
  Serial.begin(9600);

  //Serial.begin(115200);
  Serial.print("Attempting to connect to SSID:");
  Serial.println(SSID);
  WiFi.mode(WIFI_STA);
  delay(1000);
  WiFi.begin(SSID, PASSWORD);
  clear(TFT_BLUE);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
    show("Not connected to WiFi");
    WiFi.begin(SSID, PASSWORD);
  }
  clear(TFT_PURPLE);
  Serial.print("WiFi connected with IP address: ");
  Serial.println(WiFi.localIP());
  show("Connected to WiFi");
  delay(500);

  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  } else {
    client.loop();

  bool isWifiConnected = wifiConnected();
  bool isMqttConnected = mqttConnected(); 

  // Reading
  int motionReading = digitalRead(PIR_MOTION_SENSOR);
  int lightReading = analogRead(D0); // read the raw value from light_sensor

  // Parsing
  char* wifiStatus = parseDigitalValue(isWifiConnected);
  char* mqttStatus = parseDigitalValue(isMqttConnected);
  char* motionResult = parseDigitalValue(motionReading);
  long distanceResult = ultrasonic.MeasureInCentimeters();
  int lightResult = mapToPercentage(lightReading);

  // Printing to serial monitor
  Serial.printf("    Wifi connected: %s\n", wifiStatus);
  Serial.printf("    MQTT connected: %s\n", mqttStatus);
  Serial.printf("   Motion detected: %s\n", motionResult);
  Serial.printf("    Light measured: %i\n", lightResult);
  Serial.printf("Distance to object: %ld\n", distanceResult);

  // Publishing
  client.publish(TOPIC_PUB_QUEST, mqttStatus)
  client.publish(TOPIC_PUB_MOTION, motionResult);
  client.publish(TOPIC_PUB_LIGHT, toString(lightResult));
  client.publish(TOPIC_PUB_DISTANCE, toString(distanceResult));
  
  // Drawing on LCD screen




    delay(1000);
  }
}
