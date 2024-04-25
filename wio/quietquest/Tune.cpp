#include "delay.h"
#include "wiring_constants.h"
#include "wiring_digital.h"
#include "Tune.h"

Tune longRange;
Tune midRange;
Tune shortRange;

void Tune::playTune(int length, char notes[], int beats[], int tempo){
    for(int i = 0; i < length; i++) {
        if(notes[i] == ' ') {
            delay(beats[i] * tempo);
        } else {
            playNote(notes[i], beats[i] * tempo);
        }
        delay(tempo / 2);    /* delay between notes */
    }
}

void Tune::playNote(char note, int duration){
    char names[] = { 'c', 'd', 'e', 'f', 'g', 'a', 'b', 'C', 'X' };
    int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014, 956, 550 };

    // play the tone corresponding to the note name
    for (int i = 0; i < 9; i++) {
        if (names[i] == note) {
            playTone(tones[i], duration);
        }
    }
}

void Tune::playTone(int tone, int duration){
    for (long i = 0; i < duration * 1000L; i += tone * 2) {
        digitalWrite(WIO_BUZZER, HIGH);
        delayMicroseconds(tone);
        digitalWrite(WIO_BUZZER, LOW);
        delayMicroseconds(tone);
    }
}