#include "utils.h"     // Include header file

/**
 * Map from low-max value of the light sensor to a readable 0-100 percentage value,
 * as the raw signal data is not a generalized format.
 * @param lightValue - signal coming from light sensor
 * @return int - mapped value
*/
int mapToPercentage(int lightValue) {
    return map(lightValue, MIN_READING, MAX_READING, 0, 100)
}

/**
 * Parse digital value signal to more readable format.
 * @param digitalValue - digital signal, either 0 or 1
 * @return char* - parsed motion signal
*/
char* parseDigitalValue(int digitalValue) {
    if (digitalValue == 0) {
        return "false";
    }
    return "true";
}

/**
 * Aimed to reduce side effects delay() function has that it stops most activity on the terminal.
 * Ideas adapted from below site.
 * https://forum.arduino.cc/t/using-millis-for-timing-a-beginners-guide/483573
*/
bool isTimeToUpdate() {
    static unsigned long startTime = millis();      // Initialize once and remember across calls
    unsigned long currentMillis = millis();         // Reduce calls to millis()
    if (currentMillis - startTime >= INTERVAL) {    // Checks whether period has passed
        startTime = currentMillis;
        return true;
    }
    return false;

}