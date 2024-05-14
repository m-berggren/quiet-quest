#include "display_config.h"

TFT_eSPI tft = TFT_eSPI();                              // Create instance for the display

// ==========================* SETUP SCREEN *========================

/**
 * Method that sets up the initial display upside down and draw static text.
 */
void displaySetup() {
    tft.begin();
    tft.setRotation(1);                                 // Screen turns upside-down, 3 is standard rotation
    drawStaticElements();
}

// ==========================* DRAW ON SCREEN *======================

/**
 * Method that draw all values on screen. Used when quest is running and sensors are all on.
 *
 * @param wifi string with yes/no.
 * @param mqtt string with yes/no.
 * @param motion string with yes/no.
 * @param light string value 0-100.
 * @param distance value 0-525.
 * @param box string with yes/no.
 */
void drawOnScreen(String wifi, String mqtt, String motion, int light, long distance, String box) {
    tft.fillRect(250, 50, 320, 240, TFT_BLACK);         // Clear previous values

    drawValue(wifi.c_str(), WIFI_Y);
    drawValue(mqtt.c_str(), MQTT_Y);
    drawValue(motion.c_str(), MOTION_Y);
    drawValue(String(light).c_str(), LIGHT_Y);
    drawValue(String(distance).c_str(), DISTANCE_Y);
    drawValue(box.c_str(), BOX_Y);
}

/**
 * Secondary method when only wifi and mqtt should be updated. Used when quest is not active.
 * @param wifi string as yes/no.
 * @param mqtt string as yes/no.
 */
void drawOnScreen(String wifi, String mqtt) {
    tft.fillRect(250, 50, 320, 240, TFT_BLACK);         // Clear previous values

    drawValue(wifi.c_str(), WIFI_Y);
    drawValue(mqtt.c_str(), MQTT_Y);
}

/**
 * Draws static elements. Used in {@link #displaySetup}.
 */
void drawStaticElements() {
    tft.fillScreen(TFT_BLACK);  // Clear the screen
    tft.setTextSize(HEADER_FONT_SIZE);
    tft.setTextColor(QUIET_GREEN);

    tft.drawString("Quiet Quest", 50 , 10);
    tft.drawLine(0, 40, 320, 40, TFT_WHITE); // Horizontal line under title
    drawLabel("    Wifi connected:", WIFI_Y);
    drawLabel("    MQTT connected:", MQTT_Y);
    drawLabel("   Motion detected: ", MOTION_Y);
    drawLabel("    Light measured: ", LIGHT_Y);
    drawLabel("Distance to object: ", DISTANCE_Y);
    drawLabel("     Is box closed: ", BOX_Y);
}

// ==========================* HELPER METHODS *======================

/**
 * Draws standard label value with some minor styling.
 * @param label the char* to draw.
 * @param yPosition int value determines location on the Y-axis.
 */
void drawLabel(const char* label, int yPosition) {
    tft.setTextColor(QUIET_GREEN);
    tft.setTextSize(NORMAL_FONT_SIZE);
    tft.drawString(label, NORMAL_INDENTATION, yPosition); // Draws string at specific position
}

/**
 * Draws value on the screen in certain position.
 * @param value char* to draw.
 * @param yPosition int value determine location on the Y-axis.
 */
void drawValue(const char* value, int yPosition) {
    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.setTextSize(NORMAL_FONT_SIZE);
    tft.drawString(value, VALUE_INDENTATION, yPosition); // Draws string at specific position
}