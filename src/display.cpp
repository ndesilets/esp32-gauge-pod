#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>
#include <Arduino.h>

#define DISPLAY_WIDTH 128
#define DISPLAY_HEIGHT 64

const int tempDetents[] = {0, 180, 250};
const int psiDetents[] = {20, 40, 60, 80};

// combined gauge view

void renderColumn(Adafruit_SH1107 display, int offset, const char *header1,
                  const char *header2, int sensorValue) {
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

void renderCombinedDisplay(Adafruit_SH1107 display, int oilTemp,
                           int oilPressure) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  const char *oilHeader = "OIL";
  const char *tempHeader = "TEMP";
  const char *psiHeader = "PSI";

  display.clearDisplay();
  display.setCursor(0, 0);

  display.drawFastVLine(DISPLAY_WIDTH / 2, 0, DISPLAY_HEIGHT, SH110X_WHITE);
  renderColumn(display, 0, oilHeader, tempHeader, oilTemp);
  renderColumn(display, DISPLAY_WIDTH / 2 + 2, oilHeader, psiHeader,
               oilPressure);

  display.display();
}

void drawHorizontalGauge(Adafruit_SH1107 display, int x, int y, int numDetents,
                         const int *detents, int minV, int maxV,
                         int sensorReading) {
  // box
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

void renderCombinedDisplay2(Adafruit_SH1107 display, int oilTemp,
                            int oilPressure) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  char sensorValueStr[5] = {'\0'};
  const char *tempHeader = "OIL TEMP";
  const char *psiHeader = "OIL PRESSURE";

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
  drawHorizontalGauge(display, 0, cy + 26, 3, tempDetents, -20, 300, oilTemp);

  cy += 34;

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
  drawHorizontalGauge(display, 0, cy + 26, 4, psiDetents, 0, 100, oilPressure);

  display.display();
}

// single gauge view

void renderSingleDisplay(Adafruit_SH1107 display, const char *header,
                         int sensorReading, int minV, int maxV, int numDetents,
                         const int *detents) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  char sensorValueStr[5] = {'\0'};
  const int movingAvgsLineLength = 5 * 3 + 6; // 3 vals + whitespace
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

  // -- horizontal gauge

  // box
  display.drawRect(0, DISPLAY_HEIGHT - 11, DISPLAY_WIDTH, 10, SH110X_WHITE);

  // detents
  for (int i = 0; i < numDetents; i++) {
    int detentX = map(detents[i], minV, maxV, 0, DISPLAY_WIDTH - 8);
    display.drawFastVLine(4 + detentX, DISPLAY_HEIGHT - 15, 4, SH110X_WHITE);
  }

  // gauge fill
  int width = map(sensorReading, minV, maxV, 0, DISPLAY_WIDTH - 4);
  display.fillRect(2, DISPLAY_HEIGHT - 9, width, 6, SH110X_WHITE);

  display.display();
}