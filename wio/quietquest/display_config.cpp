#include "display_config.h"

TFT_eSPI tft = TFT_eSPI();                      // Create instance for the display
//TFT_eSPI tft;                      // Create instance for the display

//uint16_t displayBuffer[WIDTH * HEIGHT];

void displaySetup() {
    tft.begin();
    tft.setRotation(1);                               // Screen turns upside-down, 3 is standard rotation
    drawStaticElements();
}

void drawOnScreen(String wifi, String mqtt, String motion, int light, long distance, String box) {
    tft.fillRect(250, 50, 320, 240, TFT_BLACK); // Clear previous values

    drawValue(wifi.c_str(), 50);
    drawValue(mqtt.c_str(), 80);
    drawValue(motion.c_str(), 110);
    drawValue(String(light).c_str(), 140);
    drawValue(String(distance).c_str(), 170);
    drawValue(box.c_str(), 200);
}

/**
 * Initializes buffer, sets background and calls on s
*/
void initializeBuffer() {
    /*
    for (size_t i = 0; i < WIDTH * HEIGHT; i++) {
    displayBuffer[i] = TFT_BLACK; // Sets all pixels with default background color
    }
    */
    drawStaticElements();
}

void drawStaticElements() {
    tft.fillScreen(TFT_BLACK);  // Clear the screen
    tft.setTextSize(3);
    tft.setTextColor(0x97d0);

    tft.drawString("Quiet Quest", 50 , 10);
    tft.drawLine(0, 40, 320, 40, TFT_WHITE); // Horizontal line under title
    drawLabel("    Wifi connected:", 50);
    drawLabel("    MQTT connected:", 80);
    drawLabel("   Motion detected: ", 110);
    drawLabel("    Light measured: ", 140);
    drawLabel("Distance to object: ", 170);
    drawLabel("     Is box closed: ", 200);
}

void drawLabel(const char* label, int yPosition) {
    //tft.setTextColor(TFT_WHITE, TFT_BLACK); // Sets text color with the background
    tft.setTextColor(0x97d0);
    tft.setTextSize(2); // Textsize
    tft.drawString(label, 10, yPosition); // Draws string at specific position
}

void drawValue(const char* value, int yPosition) {
    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.setTextSize(2); // Textsize
    tft.drawString(value, 250, yPosition); // Draws string at specific position
}