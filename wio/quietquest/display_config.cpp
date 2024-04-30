#include "display_config.h"

TFT_eSPI tft;

uint16_t displayBuffer{WIDTH * HEIGHT};

void displaySetup() {
    tft.begin();
    tft.setRotation(1);                         // Screen turns upside-down, 3 is standard rotation

    // Choose what draw methods to use:
    
}