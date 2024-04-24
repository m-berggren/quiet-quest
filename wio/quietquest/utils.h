#ifndef UTILS_H     //
#define UTILS_H

#define BAUD_RATE 9600      // Common data rate in bits per second
#define MAX_READING 1023    // Max reading for light sensor
#define MIN_READING 0       // Min reading value
#define WIDTH 320           // Define the width of the Wio terminal display
#define HEIGHT 240          // Define the height of the Wio terminal display
#define INTERVAL 1000       // Stndard interval in ms

// Sensor handling
extern int mapToPercentage(int lightValue);
extern char* parseDigitalValue(int digitalValue);
extern bool isTimeToUpdate();

#endif