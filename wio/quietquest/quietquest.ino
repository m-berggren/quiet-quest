#include <WiFi.h>
#include "rpcWiFi.h"
#include "TFT_eSPI.h"
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include "Ultrasonic.h"
#include <string.h>
#include "credentials.h" // include header where SSID and PASSWORD are defined
#include <ChainableLED.h>
#include "Tune.h"

#define PIR_MOTION_SENSOR PIN_WIRE_SCL
Ultrasonic ultrasonic(PIN_WIRE_SCL);

TFT_eSPI tft;

//LED light
#define NUM_LEDS  1
ChainableLED led(2, 3, NUM_LEDS);

//outer threshold distance: 50 cm
const double OUTER_DISTANCE_THRESHOLD = 50.0;
//inner threshold distance: 15 cm
const double INNER_DISTANCE_THRESHOLD = 15.0;

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
const char *TOPIC_PUB_LIGHT = "/quietquest/sensor/light";


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
    WiFi.begin(SSID, PASSWORD);
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
  pinMode(D0, INPUT);
  pinMode(PIN_WIRE_SCL, INPUT);

  // initialize LED
  led.init();

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

    clear(TFT_PURPLE);

    // Ultrasonic Ranger sensor
    long RangeInCentimeters = ultrasonic.MeasureInCentimeters();
    char msgBuffer[50];
    snprintf(msgBuffer, sizeof(msgBuffer), "%ld", RangeInCentimeters);
    client.publish(TOPIC_PUB_DISTANCE, msgBuffer);
    tft.setCursor(20, 60);
    tft.print(msgBuffer);

    Serial.printf("The distance to obstacle in front is: %ld cm\n", RangeInCentimeters);

    // Buzzer: Audio alert depending on proximity
    if (RangeInCentimeters < 16){
        int beats[] = {1, 1};
        shortRange.playTune(2, "X ", beats, 100);
    } else if (RangeInCentimeters < 31) {
        int beats[] = {1, 4};
        midRange.playTune(2, "a ", beats, 100);
    } else if (RangeInCentimeters < 51) {
        int beats[] = {2, 20};
        longRange.playTune(2, "a ", beats, 100);
    }

    // Mini PIR Motion sensor
    if (digitalRead(PIR_MOTION_SENSOR) == HIGH) {
      client.publish(TOPIC_PUB_MOTION, "1");
      tft.setCursor(20, 100);
      tft.print("Motion is detected.");
      Serial.println("Motion is detected.");
    } else {
      tft.setCursor(20, 100);
      tft.print("Searching for motion");
      client.publish(TOPIC_PUB_MOTION, "0");
      Serial.println("Searching for motion");
    }

    // Light sensor
    int raw_light = analogRead(D0); // read the raw value from light_sensor
    int light = map(raw_light, 0, 1023, 0, 100); // map values so they stay between 0-100
    snprintf(msgBuffer, sizeof(msgBuffer), "%d", light);
 
    Serial.printf("Light level: %d\n", light);
    client.publish(TOPIC_PUB_LIGHT, msgBuffer);

    // LED light
    if (light > 15) {
    led.setColorHSL(0, 1, 0.95, 0.1); //(red) box opened
    } else {
    led.setColorHSL(0, 0.37, 1, 0.01); //(blue-green) box unopened
    }

    // Connection check
    if (client.connected()) {
      tft.setCursor(20, 150);
      client.publish(TOPIC_PUB_QUEST, "1");
      tft.print("Broker is connected");
    } else {
      client.publish(TOPIC_PUB_QUEST, "0");
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
