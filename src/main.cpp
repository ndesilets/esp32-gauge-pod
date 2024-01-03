#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>
#include <Arduino.h>
#include <SensorHistory.h>
#include <Wire.h>

#define SH1107_DEFAULT_ADDR 0x3C
#define DISPLAY_WIDTH 128
#define DISPLAY_HEIGHT 64
#define A_BTN_PIN 14
#define B_BTN_PIN 32
#define C_BTN_PIN 15
#define INTERNAL_BTN_PIN 38

enum DisplayMode { COMBINED, OIL_TEMP, OIL_PRESSURE };
enum DisplayPower { OFF, ON };

Adafruit_SH1107 display = Adafruit_SH1107(64, 128, &Wire);
DisplayMode displayMode = COMBINED;
DisplayPower displayPower = ON;

const int tempDetents[] = {0, 180, 250};
const int psiDetents[] = {20, 40, 60, 80};

bool intButtonPressed = false;
bool aButtonPressed = false;
bool bButtonPressed = false;
bool cButtonPressed = false;

// --- jank

// positive modulo
inline int the_one_true_modulo(int i, int n) {
    return (i % n + n) % n;
}

// --- sensor buffers

SensorHistory oilTempHistory = SensorHistory();
SensorHistory oilPressureHistory = SensorHistory();
unsigned long lastSensorRead = 0;
unsigned long now = 0;

// --- sensor data functions

double getOilPressure() {
  static double x = 0;
  x += 0.01;
  return (sin(x) * 50) + 50;
}

double getOilTemp() {
  static double x = 0;
  x += 0.01;
  return sin(x) * 160 + 140;
}

// --- display functions

void initDisplay() {
  delay(500); // wait for the OLED to power up
  display.begin(SH1107_DEFAULT_ADDR, true);
  display.display();

  delay(1000); // show splash screen

  display.clearDisplay();
  display.display();

  display.setRotation(1); // get rotated idiot
  display.setTextColor(SH110X_WHITE);
}

// combined gauge view

void renderColumn(int offset, const char *header1, const char *header2,
                  int sensorValue) {
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

void renderCombinedDisplay(int oilTemp, int oilPressure) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  const char *oilHeader = "OIL";
  const char *tempHeader = "TEMP";
  const char *psiHeader = "PSI";

  display.clearDisplay();
  display.setCursor(0, 0);

  display.drawFastVLine(DISPLAY_WIDTH / 2, 0, DISPLAY_HEIGHT, SH110X_WHITE);
  renderColumn(0, oilHeader, tempHeader, oilTemp);
  renderColumn(DISPLAY_WIDTH / 2 + 2, oilHeader, psiHeader, oilPressure);

  display.display();
}

// single gauge view

void renderSingleDisplay(const char *header, int sensorReading,
                         SensorHistory *history, int minV, int maxV,
                         int numDetents, const int *detents) {
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

  // moving averages
  display.setTextSize(1);

  std::snprintf(sensorValueStr, 5, "%d", history->get1mMovingAvg());
  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  cy = y1 - 4;
  display.setCursor(DISPLAY_WIDTH / 2 + 36, cy);
  display.print(sensorValueStr);

  std::snprintf(sensorValueStr, 5, "%d", history->get5mMovingAvg());
  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  cy += h + 2;
  display.setCursor(DISPLAY_WIDTH / 2 + 36, cy);
  display.print(sensorValueStr);

  std::snprintf(sensorValueStr, 5, "%d", history->get15mMovingAvg());
  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  cy += h + 2;
  display.setCursor(DISPLAY_WIDTH / 2 + 36, cy);
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

// --- main

void setup() {
  Serial.begin(115200);

  pinMode(A_BTN_PIN, INPUT_PULLUP);
  pinMode(B_BTN_PIN, INPUT_PULLUP);
  pinMode(C_BTN_PIN, INPUT_PULLUP);
  pinMode(INTERNAL_BTN_PIN, INPUT_PULLUP);

  Serial.println("LMAO!");

  initDisplay();
}


void loop() {
  int oilTemp = getOilTemp();
  int oilPressure = getOilPressure();

  now = millis();
  if (now - lastSensorRead > 1000) {
    lastSensorRead = now;
    oilTempHistory.add(oilTemp);
    oilPressureHistory.add(oilPressure);

    // int sensorValue = analogRead(A0);
    // Serial.printf("A0: %d\n", sensorValue);
  }

  //
  // button handling
  //

  if (!digitalRead(INTERNAL_BTN_PIN)) {
    intButtonPressed = true;
  } else if (intButtonPressed) {
    intButtonPressed = false;
    displayMode = static_cast<DisplayMode>((displayMode + 1) % 3);
  }

  if (!digitalRead(A_BTN_PIN)) {
    aButtonPressed = true;
  } else if (aButtonPressed) {
    aButtonPressed = false;
    displayMode = COMBINED;
  }

  if (!digitalRead(B_BTN_PIN)) {
    bButtonPressed = true;
  } else if (bButtonPressed) {
    bButtonPressed = false;
    displayMode = static_cast<DisplayMode>(the_one_true_modulo(displayMode - 1, 3));
  }

  if (!digitalRead(C_BTN_PIN)) {
    cButtonPressed = true;
  } else if (cButtonPressed) {
    cButtonPressed = false;
    displayMode = static_cast<DisplayMode>((displayMode + 1) % 3);
  }

  //
  // display rendering
  //

  if (displayPower == OFF) {
    display.clearDisplay();
  } else {
    switch (displayMode) {
    case COMBINED:
      renderCombinedDisplay(oilTemp, oilPressure);
      break;
    case OIL_TEMP:
      renderSingleDisplay("OIL TEMP", oilTemp, &oilTempHistory, -20, 300, 3,
                          tempDetents);
      break;
    case OIL_PRESSURE:
      renderSingleDisplay("OIL PSI", oilPressure, &oilPressureHistory, 0, 100,
                          4, psiDetents);
      break;
    default:
      Serial.println("oh my god bruh aww hell nah man what the FUCK man");
    }
  }

  display.display();
  delay(33);
  yield();
}