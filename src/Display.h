#pragma once
#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>
#include <State.h>
#include <Wire.h>

struct BarGaugeConfig {
  const char* header;
  const int* detents;
  const int detentsCount;
  const int minVal;
  const int maxVal;
  const bool showSessionMinMax;
};

bool initDisplay();
void renderCombinedDisplay(int oilTemp, int oilPressure);
void renderCombinedDisplay2(SensorState oilTemp, BarGaugeConfig oilTempConfig, SensorState oilPressure,
                            BarGaugeConfig oilPressureConfig);
void renderSingleDisplay(SensorState sensor, BarGaugeConfig config);
void flashDisplay();
void displayOff();