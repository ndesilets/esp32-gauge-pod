#include "Display.h"
#include "Sensors.h"

#define DISPLAY_WIDTH 128
#define DISPLAY_HEIGHT 64
#define SH1107_ADDR 0x3C

static Adafruit_SH1107 display(64, 128, &Wire);

// --- helpers

static void renderColumn(int offset, const char* header1, const char* header2, int sensorValue) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  char sensorValueStr[5] = {'\0'};

  display.setTextSize(1);

  // first header
  display.getTextBounds(header1, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(offset + ((DISPLAY_WIDTH / 2 - w) / 2), cy);
  display.print(header1);
  cy += h + 2;

  // second header
  display.getTextBounds(header2, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(offset + ((DISPLAY_WIDTH / 2 - w) / 2), cy);
  display.print(header2);
  cy += h + 16;

  // sensor value
  display.setTextSize(3);
  std::snprintf(sensorValueStr, 5, "%d", sensorValue);

  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(offset + ((DISPLAY_WIDTH / 2 - w) / 2), cy);
  display.print(sensorValueStr);
}

static void drawBarGauge(int x, int y, int numDetents, const int* detents, int minV, int maxV, int sensorReading) {
  // bounding box
  display.drawRect(x, y - 11, DISPLAY_WIDTH, 10, SH110X_WHITE);

  // detents
  for (int i = 0; i < numDetents; i++) {
    int detentX = map(detents[i], minV, maxV, 0, DISPLAY_WIDTH - 8);
    display.drawFastVLine(x + 4 + detentX, y - 15, 4, SH110X_WHITE);
  }

  // gauge fill
  int width = map(sensorReading, minV, maxV, 0, DISPLAY_WIDTH - 4);
  display.fillRect(x + 2, y - 9, width, 6, SH110X_WHITE);
}

// --- public

bool initDisplay() {
  delay(500);  // let OLED power-up
  if (!display.begin(SH1107_ADDR, true)) {
    return false;
  }
  display.display();
  delay(1000);  // splash

  display.clearDisplay();
  display.setRotation(1);  // get rotated idiot
  display.setTextColor(SH110X_WHITE);

  return true;
}

void displayOff() {
  display.clearDisplay();
  display.display();
}

// void renderCombinedDisplay(int oilTemp, int oilPressure) {
//   int16_t cx, cy, x1, y1;
//   uint16_t w, h;
//   const char* oilHeader = "OIL";
//   const char* tempHeader = "TEMP";
//   const char* psiHeader = "PSI";

//   display.clearDisplay();
//   display.setCursor(0, 0);

//   display.drawFastVLine(DISPLAY_WIDTH / 2, 0, DISPLAY_HEIGHT, SH110X_WHITE);
//   renderColumn(0, oilHeader, tempHeader, oilTemp);
//   renderColumn(DISPLAY_WIDTH / 2 + 2, oilHeader, psiHeader, oilPressure);

//   display.display();
// }

void renderCombinedDisplay2(SensorState oilTemp, BarGaugeConfig oilTempConfig, SensorState oilPressure,
                            BarGaugeConfig oilPressureConfig) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  char sensorValueStr[5] = {'\0'};

  display.clearDisplay();
  display.setCursor(0, 0);

  // set oil temp header
  display.setTextSize(1);
  display.getTextBounds(oilTempConfig.header, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(0, cy);
  display.print(oilTempConfig.header);

  // set oil temp value
  std::snprintf(sensorValueStr, 5, "%d", oilTemp);
  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(DISPLAY_WIDTH - w, cy);
  display.print(sensorValueStr);

  // set oil temp gauge
  drawBarGauge(0, cy + 26, oilTempConfig.detentsCount, oilTempConfig.detents, oilTempConfig.minVal,
               oilTempConfig.maxVal, oilTemp.current);

  cy += 38;

  // set oil pressure header
  display.setTextSize(1);
  display.getTextBounds(oilPressureConfig.header, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(0, cy);
  display.print(oilPressureConfig.header);

  // set oil pressure value
  memset(sensorValueStr, '\0', 5);
  std::snprintf(sensorValueStr, 5, "%d", oilPressure);
  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(DISPLAY_WIDTH - w, cy);
  display.print(sensorValueStr);

  // set oil pressure gauge
  drawBarGauge(0, cy + 26, oilPressureConfig.detentsCount, oilPressureConfig.detents, oilPressureConfig.minVal,
               oilPressureConfig.maxVal, oilPressure.current);

  display.display();
}

void renderSingleDisplay(SensorState sensor, BarGaugeConfig config) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  char sensorValueStr[5] = {'\0'};

  display.clearDisplay();

  // header
  display.setTextSize(1);
  display.getTextBounds(config.header, cx, cy, &x1, &y1, &w, &h);
  display.setCursor((DISPLAY_WIDTH - w) / 2, cy);
  display.print(config.header);
  cy += h + 10;

  // current sensor value
  display.setTextSize(3);
  std::snprintf(sensorValueStr, 5, "%d", sensor.current);

  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor((DISPLAY_WIDTH - w) / 2, cy);
  display.print(sensorValueStr);

  // min/max values
  if (config.showSessionMinMax) {
    display.setTextSize(1);

    // min
    memset(&sensorValueStr, '\0', 5);
    std::snprintf(sensorValueStr, 5, "%d", sensor.min);
    display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
    display.setCursor(0, cy + h);
    display.print(sensorValueStr);

    // max
    memset(&sensorValueStr, '\0', 5);
    std::snprintf(sensorValueStr, 5, "%d", sensor.max);
    display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
    display.setCursor(DISPLAY_WIDTH - w, cy + h);
    display.print(sensorValueStr);
  }

  drawBarGauge(0, DISPLAY_HEIGHT, config.detentsCount, config.detents, config.minVal, config.maxVal, sensor.current);

  display.display();
}

void flashDisplay() {
  display.fillScreen(SH110X_WHITE);
  display.display();
}