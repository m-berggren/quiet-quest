#ifndef TUNE_HPP          // Stops header file from being redefined over and over
#define TUNE_HPP

class Tune {
    public:
        void playTune(int, char[], int[], int); //length, notes, beats, tempo
        void playNote(char, int); //note, duration
        void playTone(int, int); //tone, duration
};

extern Tune longRange;
extern Tune midRange;
extern Tune shortRange;
extern Tune questStart;
extern Tune questStop;
extern Tune taskStop;
extern Tune pomodoroInterval;

#endif