#include "TFT_eSPI.h"
#include "Ultrasonic.h"
#include <string.h>
#include "utils.h"
#include "mqtt_config.h"
#include "pins.h"
#include "display_config.h"
#include <ChainableLED.h>
#include "Tune.h"

// Initializations
Ultrasonic ultrasonic(PIN_WIRE_SCL);
ChainableLED led(LED_CLK_PIN, LED_DATA_PIN, NUM_LEDS);
bool isBoxClosed;

void setup() {
  pinMode(LIGHT_PIN, INPUT);                        //
  pinMode(MOTION_PIN, INPUT);                       //
  pinMode(DISTANCE_PIN, INPUT);                     //
  pinMode(WIO_BUZZER, OUTPUT);

  Serial.begin(BAUD_RATE);                          //
  
  // initialize LED
  led.init();

  // initiate tft screen
  displaySetup();                                   // Uses tft library and sets up display rotation 1
  
  client.setServer(SERVER, PORT);
  client.setCallback(callback);                     // Callback method used in cpp file for MQTT setup
  setupNetwork();                                   // Setup wifi and MQTT broker connection
  
}

void loop() {

  // Reading and interpreting sensor data
  int motionReading = digitalRead(MOTION_PIN);                                     // Motion sensor gives 0 or 1
  int lightReading = mapToPercentage(analogRead(LIGHT_PIN));                       // Maps 0-1023 light reading to percentage (0-100)
  long distanceReading = ultrasonic.MeasureInCentimeters();                        //
  // TODO: Add Chainable RGB Led and sound

  // Logic for led light & sound

  // Buzzer: Audio alert depending on proximity
  if (distanceReading < 16){
      int beats[] = {1, 1};
      shortRange.playTune(2, "X ", beats, 100);
  } else if (distanceReading < 31) {
      int beats[] = {1, 4};
      midRange.playTune(2, "a ", beats, 100);
  } else if (distanceReading < 51) {
      int beats[] = {2, 20};
      longRange.playTune(2, "a ", beats, 100);
  }

  // LED light
  if (lightReading > LIGHT_VALUE_THRESHOLD) {
    isBoxClosed = false;
    led.setColorHSL(0, 1, 0.95, 0.1);       // (Red) box opened
    } else {
      isBoxClosed = true;
    led.setColorHSL(0, 0.37, 1, 0.01);      // (Blue-green) box unopened
    }


  // Prints to serial monitor if interval has passed
  if (isTimeToUpdate()) {
    Serial.printf("    Wifi connected: %s\n", wifiConnected() ? "Yes" : "No");    //
    Serial.printf("    MQTT connected: %s\n", mqttConnected() ? "Yes" : "No");    //
    Serial.printf("   Motion detected: %s\n", motionReading ? "Yes" : "No");      //
    Serial.printf("    Light measured: %d\n", lightReading);                      //
    Serial.printf("Distance to object: %ld\n", distanceReading);                  //
    Serial.printf("     Is box closed: %s\n", isBoxClosed ? "Yes" : "No");        //
    Serial.println();
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

  // Check wifi and MQTT connections
  checkConnections();                                                             // 
}
