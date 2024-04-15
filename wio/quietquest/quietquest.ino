#include <WiFi.h>
#include "rpcWiFi.h"
#include "TFT_eSPI.h"
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include "Ultrasonic.h"
#include <string.h>

#define PIR_MOTION_SENSOR D0
Ultrasonic ultrasonic(0);

TFT_eSPI tft;

//Update these values corresponding to your network
const char *ssid = "lianiphone";    //wifi network name
const char *password = "12345678";  //wifi network password


//MQTT server
const char *mqtt_server = "broker.hivemq.com";

WiFiClient wifiClient;
PubSubClient client(wifiClient);


//Topics
const char *TOPIC = "QuietQuest";
const char *TOPIC_PUB_MOTION = "/quietquest/sensor/motion";
const char *TOPIC_PUB_DISTANCE = "/quietquest/sensor/distance";
const char *TOPIC_SUB_QUEST = "/quietquest/application/start";
const char *TOPIC_PUB_QUEST = "/quietquest/sensor/connect";


void callback(char *topic, byte *payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");

  char *txt = (char *)malloc(length + 1);
  memcpy(txt, payload, length);
  txt[length] = '\0';

  if (strcmp(topic, TOPIC_SUB_QUEST) == 0) {
    show(txt);
    delay(2000);
  }

  free(txt);
  Serial.println();
}

void reconnect() {
  while (!client.connected()) {
    WiFi.begin(ssid, password);
    Serial.print("Attempting MQTT connection...");
    //Create a random client ID
    String clientID = "ESP8266Client-";
    clientID += String(random(0xffff), HEX);
    //try to connect
    if (client.connect(clientID.c_str())) {
      Serial.println("connected");

      tft.setCursor(20, 150);
      tft.print("Terminal is connected to App");
      Serial.println("Published connection message successfully!");
      Serial.print("Subscribed to: ");
      Serial.println(TOPIC_SUB_QUEST);
      client.subscribe(TOPIC_SUB_QUEST);


    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void clear(uint32_t color) {
  tft.fillScreen(color);
  tft.setTextColor(TFT_BLACK);
}

void show(char *text) {
  tft.setTextSize(2);
  tft.drawString(text, 20, 20);
}

void setup() {

  // initiate tft screen
  tft.begin();
  tft.setRotation(3);

  pinMode(PIR_MOTION_SENSOR, INPUT);
  Serial.begin(9600);

  //Serial.begin(115200);
  Serial.print("Attempting to connect to SSID:");
  Serial.println(ssid);
  WiFi.mode(WIFI_STA);
  delay(1000);
  WiFi.begin(ssid, password);
  clear(TFT_BLUE);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
    show("Not connected to WiFi");
    WiFi.begin(ssid, password);
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

    clear(TFT_PURPLE);
    long RangeInCentimeters = ultrasonic.MeasureInCentimeters();
    char msgBuffer[50];
    sprintf(msgBuffer, "The distance to the phone is %ld cm", RangeInCentimeters);
    client.publish(TOPIC_PUB_DISTANCE, msgBuffer);
    tft.setCursor(20, 60);
    tft.print(msgBuffer);

    Serial.printf("The distance to obstacles in front is: %ld cm\n", RangeInCentimeters);

    if (digitalRead(PIR_MOTION_SENSOR)) {
      client.publish(TOPIC_PUB_MOTION, "Motion is detected. Someone is nearby.");
      tft.setCursor(20, 100);
      tft.print("Motion is detected.");
      Serial.println("Motion is detected.");
    } else {
      tft.setCursor(20, 100);
      tft.print("Searching for motion");
      client.publish(TOPIC_PUB_MOTION, "Searching for motion");
      Serial.println("PIR Motion Sensor: Searching for motion");
    }


    if (client.connected()) {
      tft.setCursor(20, 150);
      client.publish(TOPIC_PUB_QUEST, "Wio Terminal is connected");
      tft.print("Broker is connected");
    } else {
      tft.setCursor(20, 150);
      tft.print("Broker connection is lost");
    }

    if (WiFi.status() != WL_CONNECTED) {
      tft.setCursor(20, 180);
      tft.print("Wifi connection is lost");
 
    } else {
      tft.setCursor(20, 180);
      tft.print("Wifi is connected");
    }

    delay(1000);
  }
}
