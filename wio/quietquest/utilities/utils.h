#ifndef UTILS_H                             // Stops header file from running more than once
#define UTILS_H

#include <stdio.h>                          // Import needed for sprintf()
#include <Arduino.h>                        // Import needed for map() and millis()


// ==========================* CONSTANTS *===========================
// For added safety constexpr <type> could be used over #define (macro), but it is not deemed needed in this project

// General arduino file
#define BAUD_RATE                   9600    // Common data rate in bits per second
#define INTERVAL                    1000    // Interval in ms to determine when to do prints and update LCD

// Sensor-related
#define MAX_READING                 1023    // Max reading for light sensor
#define MIN_READING                 0       // Min reading value
#define OUTER_DISTANCE_THRESHOLD    50.0    // Outer threshold distance: 50 cm
#define INNER_DISTANCE_THRESHOLD    15.0    // Inner threshold distance: 15 cm
#define NUM_LEDS                    1       // Number of LEDs
#define LIGHT_VALUE_THRESHOLD       5       // Threshold for acceptable light value (when box is considered closed)

// Wio terminal screen
#define WIDTH                       320     // Define the width of the Wio terminal display
#define HEIGHT                      240     // Define the height of the Wio terminal display

// ==========================* METHODS *=============================
// For sensor and time handling

extern bool isTimeToUpdate();               // Checks if it is time to update LCD screen and print data
extern int mapToPercentage(int lightValue); // Maps light value from 0-1023 to percentage (0-100)
extern char* toString(int integer);         //

#endif                                      // UTILS_H