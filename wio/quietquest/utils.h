#ifndef UTILS_H     //
#define UTILS_H

#include <stdio.h>                          // Import needed for sprintf()
#include <Arduino.h>                        // Import needed for map() and millis()

#define BAUD_RATE   9600                    // Common data rate in bits per second
#define PORT        1883                    // Standard port for connecting to broker
#define SERVER      "broker.hivemq.com"     // Standard server for connectiong to broker
#define MAX_READING 1023                    // Max reading for light sensor
#define MIN_READING 0                       // Min reading value
#define WIDTH       320                     // Define the width of the Wio terminal display
#define HEIGHT      240                     // Define the height of the Wio terminal display
#define INTERVAL    1000                    // Interval in ms to determine when to do prints and update LCD

extern bool isTimeToUpdate();
extern int mapToPercentage(int lightValue);
extern bool isTimeToUpdate();
extern char* toString(int integer);

#endif