#ifndef DISPLAY_H
#define DISPLAY_H

#include <Arduino.h>

// Function prototypes
void initializeDisplay();
void updateDisplay(const String &data);
void clearDisplay();

#endif // DISPLAY_H