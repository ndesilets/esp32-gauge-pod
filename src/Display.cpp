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

static void drawHorizontalGauge(int x, int y, int numDetents, const int* detents, int minV, int maxV,
                                int sensorReading) {
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

void initDisplay() {
  delay(500);  // let OLED power-up
  display.begin(SH1107_ADDR, true);
  display.display();
  delay(1000);  // splash

  display.clearDisplay();
  display.setRotation(1);
  display.setTextColor(SH110X_WHITE);
}

void displayOff() {
  display.clearDisplay();
  display.display();
}

void renderCombinedDisplay(int oilTemp, int oilPressure) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  const char* oilHeader = "OIL";
  const char* tempHeader = "TEMP";
  const char* psiHeader = "PSI";

  display.clearDisplay();
  display.setCursor(0, 0);

  display.drawFastVLine(DISPLAY_WIDTH / 2, 0, DISPLAY_HEIGHT, SH110X_WHITE);
  renderColumn(0, oilHeader, tempHeader, oilTemp);
  renderColumn(DISPLAY_WIDTH / 2 + 2, oilHeader, psiHeader, oilPressure);

  display.display();
}

void renderCombinedDisplay2(int oilTemp, int oilPressure, const int* tempDetents, int numTempDetents,
                            const int* psiDetents, int numPsiDetents) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  char sensorValueStr[5] = {'\0'};
  const char* tempHeader = "OIL TEMP";
  const char* psiHeader = "OIL PRESSURE";

  display.clearDisplay();
  display.setCursor(0, 0);

  // set oil temp header
  display.setTextSize(1);
  display.getTextBounds(tempHeader, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(0, cy);
  display.print(tempHeader);

  // set oil temp value
  std::snprintf(sensorValueStr, 5, "%d", oilTemp);
  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(DISPLAY_WIDTH - w, cy);
  display.print(sensorValueStr);

  // set oil temp gauge
  drawHorizontalGauge(0, cy + 26, numTempDetents, tempDetents, -20, 300, oilTemp);

  cy += 38;

  // set oil pressure header
  display.setTextSize(1);
  display.getTextBounds(psiHeader, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(0, cy);
  display.print(psiHeader);

  // set oil pressure value
  memset(sensorValueStr, '\0', 5);
  std::snprintf(sensorValueStr, 5, "%d", oilPressure);
  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(DISPLAY_WIDTH - w, cy);
  display.print(sensorValueStr);

  // set oil pressure gauge
  drawHorizontalGauge(0, cy + 26, numPsiDetents, psiDetents, 0, 100, oilPressure);

  display.display();
}

void renderSingleDisplay(const char* header, int sensorReading, int minV, int maxV, int numDetents, const int* detents,
                         bool showMinMax, int minMeasured, int maxMeasured) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  char sensorValueStr[5] = {'\0'};
  const int movingAvgsLineLength = 5 * 3 + 6;  // 3 vals + whitespace
  char movingAvgsLine[movingAvgsLineLength] = {'\0'};

  display.clearDisplay();

  // header
  display.setTextSize(1);
  display.getTextBounds(header, cx, cy, &x1, &y1, &w, &h);
  display.setCursor((DISPLAY_WIDTH - w) / 2, cy);
  display.print(header);
  cy += h + 10;

  // current sensor value
  display.setTextSize(3);
  std::snprintf(sensorValueStr, 5, "%d", sensorReading);

  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor((DISPLAY_WIDTH - w) / 2, cy);
  display.print(sensorValueStr);

  // min/max values
  if (showMinMax) {
    display.setTextSize(1);

    // min
    memset(&sensorValueStr, '\0', 5);
    std::snprintf(sensorValueStr, 5, "%d", minMeasured);
    display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
    display.setCursor(0, cy + h);
    display.print(sensorValueStr);

    // max
    memset(&sensorValueStr, '\0', 5);
    std::snprintf(sensorValueStr, 5, "%d", maxMeasured);
    display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
    display.setCursor(DISPLAY_WIDTH - w, cy + h);
    display.print(sensorValueStr);
  }

  drawHorizontalGauge(0, DISPLAY_HEIGHT, numDetents, detents, minV, maxV, sensorReading);

  display.display();
}