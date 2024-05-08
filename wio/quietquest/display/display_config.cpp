#include "display_config.h"

TFT_eSPI tft = TFT_eSPI();                              // Create instance for the display

// ==========================* SETUP SCREEN *========================

/**
 * TODO
 */
void displaySetup() {
    tft.begin();
    tft.setRotation(1);                                 // Screen turns upside-down, 3 is standard rotation
    drawStaticElements();
}

// ==========================* DRAW ON SCREEN *======================

/**
 * TODO
 * @param wifi
 * @param mqtt
 * @param motion
 * @param light
 * @param distance
 * @param box
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
 * TODO
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
 * TODO
 * @param label
 * @param yPosition
 */
void drawLabel(const char* label, int yPosition) {
    
    tft.setTextColor(QUIET_GREEN);
    tft.setTextSize(NORMAL_FONT_SIZE);
    tft.drawString(label, NORMAL_INDENTATION, yPosition); // Draws string at specific position
}

/**
 * TODO
 * @param value
 * @param yPosition
 */
void drawValue(const char* value, int yPosition) {
    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.setTextSize(NORMAL_FONT_SIZE);
    tft.drawString(value, VALUE_INDENTATION, yPosition); // Draws string at specific position
}