#pragma once
#include <Arduino.h>

extern const int tempDetents[3];  // -20 → 300 °F reference marks
extern const int psiDetents[4];   // 20, 40, 60, 80 PSI reference marks

int calcOilTemp(int analogValue);      // returns °F
int calcOilPressure(int analogValue);  // returns PSI