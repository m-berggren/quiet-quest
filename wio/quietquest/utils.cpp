#include "utils.h"     // Include header file

/**
 * Aimed to reduce side effects delay() function has that it stops most activity on the terminal.
 * Ideas adapted from below site. Millis() returns millisecons passed since wio terminal ran the program
 * https://forum.arduino.cc/t/using-millis-for-timing-a-beginners-guide/483573
*/
bool isTimeToUpdate() {
    static unsigned long startMillis = millis();          // Initialize once and remember across calls
    unsigned long currentMillis = millis();             // Reduce calls to millis()
    if (currentMillis - startMillis >= INTERVAL) {      // Checks whether period has passed
        startMillis = currentMillis;                    // 
        return true;
    }
    return false;
}

/**
 * Map from low-max value of the light sensor to a readable 0-100 percentage value,
 * as the raw signal data is not a generalized format.
 * @param lightValue - signal coming from light sensor
 * @return int - mapped value
*/
int mapToPercentage(int lightValue) {
    return map(lightValue, MIN_READING, MAX_READING, 0, 100);
}

char* toString(int value) {
    static char strValue[4];        // Static for retaining data between calls. Memory is allocated once and the pointer can point to valid memory slot
    sprintf(strValue, "%d", value);
    return strValue;
}