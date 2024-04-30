#ifndef DISPLAY_CONFIG_H
#define DISPLAY_CONFIG_H

#include <TFT_eSPI.h>
#include "utils.h"

extern TFT_eSPI tft;
//extern uint16_t displayBuffer[];

extern void displaySetup();
extern void drawOnScreen(String wifi, String mqtt, String motion, int light, long distance, String box);
void initializeBuffer();
void drawStaticElements();
void drawLabel(const char* label, int yPosition);
void drawValue(const char* value, int yPosition);

#endif