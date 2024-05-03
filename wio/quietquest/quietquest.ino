#include <TFT_eSPI.h>
#include <Ultrasonic.h>
#include <string.h>
#include <ChainableLED.h>
#include "utils.h"
#include "mqtt_config.h"
#include "pins.h"
#include "display_config.h"

// Initializations
Ultrasonic ultrasonic(PIN_WIRE_SCL);
ChainableLED led(LED_CLK_PIN, LED_DATA_PIN, NUM_LEDS);

bool isBoxClosed;
String wifiStatus;
String mqttStatus;
String motionStatus;
int lightValue;
long distanceValue;
String boxStatus;

void setup() {
    pinMode(LIGHT_PIN, INPUT);                                                //
    pinMode(MOTION_PIN, INPUT);                                               //
    pinMode(DISTANCE_PIN, INPUT);                                             //
    pinMode(WIO_BUZZER, OUTPUT);

    Serial.begin(BAUD_RATE);                                                  //

    led.init();                                                               // Initialize LED

    displaySetup();                                                           // Initiates tft screen

    client.setServer(SERVER, PORT);
    setupNetwork();                                                           // Setup Wifi and MQTT broker connection
    client.setCallback(callback);                                             // Callback method is in MQTT config file
}

void loop() {

    // ==========================* SENSORS *=========================
    // Reading and interpreting sensor data

    int motionReading = digitalRead(MOTION_PIN);                          // Motion sensor gives 0 or 1
    int lightReading = mapToPercentage(analogRead(LIGHT_PIN));            // Maps 0-1023 light value to 0-100
    long distanceReading = ultrasonic.MeasureInCentimeters();             //

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
        led.setColorHSL(0, 1, 0.95, 0.1);                                     // (Red) box opened
    } else {
        isBoxClosed = true;
        led.setColorHSL(0, 0.37, 1, 0.01);                                    // (Blue-green) box unopened
    }

    // ==========================* MQTT & LCD *======================
    // Prints to serial monitor and LCD screen if interval has passed

    if (isTimeToUpdate()) {
        wifiStatus = wifiConnected() ? "Yes" : "No";
        mqttStatus = mqttConnected() ? "Yes" : "No";
        motionStatus = motionReading ? "Yes" : "No";
        lightValue = lightReading;
        distanceValue = distanceReading;
        boxStatus = isBoxClosed ? "Yes" : "No";


        Serial.printf("    Wifi connected: %s\n", wifiStatus.c_str());      //
        Serial.printf("    MQTT connected: %s\n", mqttStatus.c_str());      //
        Serial.printf("   Motion detected: %s\n", motionStatus.c_str());    //
        Serial.printf("    Light measured: %d\n", lightValue);              //
        Serial.printf("Distance to object: %ld\n", distanceValue);          //
        Serial.printf("     Is box closed: %s\n", boxStatus.c_str());       //
        Serial.println("================================");

        drawOnScreen(wifiStatus, mqttStatus, motionStatus, lightValue, distanceValue, boxStatus);

        // Publishes if connection to broker exists
        if (mqttConnected()) {
            client.publish(TOPIC_PUB_QUEST, "1");                           // Publishes '1' if mqttConnected
            client.publish(TOPIC_PUB_MOTION, toString(motionReading));      // Publishes '1' or '0'
            client.publish(TOPIC_PUB_LIGHT, toString(lightReading));        // Publishes int value as String
            client.publish(TOPIC_PUB_DISTANCE, toString(distanceReading));  // Publishes int value as String
        }
    }

    // Checks Wifi and keeps looping MQTT connection
    checkConnections();
}
