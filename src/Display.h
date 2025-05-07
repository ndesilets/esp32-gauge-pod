#pragma once
#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>
#include <Wire.h>

bool initDisplay();
void renderCombinedDisplay(int oilTemp, int oilPressure);
void renderCombinedDisplay2(int oilTemp, int oilPressure, const int* tempDetents, int numTempDetents,
                            const int* psiDetents, int numPsiDetents);
void renderSingleDisplay(const char* header, int sensorReading, int minV, int maxV, int numDetents, const int* detents,
                         bool showMinMax, int minMeasured, int maxMeasured);
void flashDisplay();
void displayOff();