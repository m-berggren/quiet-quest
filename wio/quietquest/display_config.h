#ifndef DISPLAY_CONFIG_H
#define DISPLAY_CONFIG_H

#include <TFT_eSPI.h>
#include "utils.h"

// ==========================* CONSTANTS *===========================

#define QUIET_GREEN         0x97d0
#define NORMAL_FONT_SIZE    2
#define HEADER_FONT_SIZE    4
#define WIFI_Y              50
#define MQTT_Y              80
#define MOTION_Y            110
#define LIGHT_Y             140
#define DISTANCE_Y          170
#define BOX_Y               200
#define VALUE_INDENTATION   250
#define NORMAL_INDENTATION  10

// ==========================* VARIABLES *===========================

extern TFT_eSPI tft;

// ==========================* METHODS *=============================

extern void displaySetup();
extern void drawOnScreen(String wifi, String mqtt, String motion, int light, long distance, String box);
void initializeBuffer();
void drawStaticElements();
void drawLabel(const char* label, int yPosition);
void drawValue(const char* value, int yPosition);

#endif