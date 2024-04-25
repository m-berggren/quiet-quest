#include "TFT_eSPI.h"
#include "Ultrasonic.h"
#include <string.h>
#include "utils.h"
#include "mqtt_config.h"
#include "pins.h"

// Initializations
#define PIR_MOTION_SENSOR PIN_WIRE_SCL
Ultrasonic ultrasonic(PIN_WIRE_SCL);

TFT_eSPI tft;

void setup() {
  pinMode(LIGHT_PIN, INPUT);
  pinMode(MOTION_PIN, INPUT);
  pinMode(DISTANCE_PIN, INPUT);

  Serial.begin(BAUD_RATE);

  // initiate tft screen
  tft.begin();
  tft.setRotation(1);                         // Screen turns upside-down, 3 is standard rotation

  client.setServer(SERVER, PORT);             //
  client.setCallback(callback);               //
}

void loop() {

  // Reading and interpreting sensor data
  int motionReading = digitalRead(MOTION_PIN);                                     // Motion sensor gives 0 or 1
  int lightReading = mapToPercentage(analogRead(LIGHT_PIN));                       //
  long distanceReading = ultrasonic.MeasureInCentimeters();                        //
  // TODO: Add Chainable RGB Led and sound

  // Logic for led light & sound
  // TODO


  // Prints to serial monitor if interval has passed
  if (isTimeToUpdate()) {
    Serial.printf("    Wifi connected: %s\n", wifiConnected() ? "Yes" : "No");    //
    Serial.printf("    MQTT connected: %s\n", mqttConnected() ? "Yes" : "No");    //
    Serial.printf("   Motion detected: %s\n", motionReading ? "Yes" : "No");      //
    Serial.printf("    Light measured: %d\n", lightReading);                      //
    Serial.printf("Distance to object: %ld\n", distanceReading);                  //
  }

  // Updates LCD screen if interval has passed"
  // TODO

  // Publishes if connection to broker exists
  if (mqttConnected()) {
    client.publish(TOPIC_PUB_QUEST, "1");                                         // Publishes '1' if mqttConnected
    client.publish(TOPIC_PUB_MOTION, toString(motionReading));                    // Publishes '1' or '0'
    client.publish(TOPIC_PUB_LIGHT, toString(lightReading));                      // Publishes int value as String
    client.publish(TOPIC_PUB_DISTANCE, toString(distanceReading));                // Publishes int value as String
  }

  // Check connections
  checkMqttAndWifiConnections();                                                  // 
}
