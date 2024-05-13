#include "utils.h"     // Include header file

// ==========================* SENSOR METHODS *======================

/**
 * Aimed to reduce side effects delay() function has that it stops most activity on the terminal.
 * Ideas adapted from below site. Millis() returns milliseconds passed since wio terminal ran the program
 * https://forum.arduino.cc/t/using-millis-for-timing-a-beginners-guide/483573
 * @return
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

/**
 * TODO
 * @param value
 * @return
 */
char* toString(int value) {
    // Static for retaining data between calls. Memory is allocated once and the pointer can point to valid memory slot
    static char strValue[4];
    sprintf(strValue, "%d", value);
    return strValue;
}