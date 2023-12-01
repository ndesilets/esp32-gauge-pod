#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>
#include <Arduino.h>
#include <SensorHistory.h>
#include <Wire.h>

#define SH1107_DEFAULT_ADDR 0x3C
#define DISPLAY_WIDTH 128
#define DISPLAY_HEIGHT 64
#define A_BTN_PIN 4
#define B_BTN_PIN 36
#define C_BTN_PIN 39

enum DisplayMode { COMBINED, OIL_TEMP, OIL_PRESSURE };
enum DisplayPower { OFF, ON };

Adafruit_SH1107 display = Adafruit_SH1107(64, 128, &Wire);
DisplayMode displayMode = COMBINED;
DisplayPower displayPower = ON;

// --- sensor buffers

SensorHistory oilTempHistory = SensorHistory();
SensorHistory oilPressureHistory = SensorHistory();
unsigned long lastSensorRead = 0;
unsigned long now = 0;

// temporary until buttons are wired up
unsigned long lastDisplayStateChange = 0;

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
  delay(250); // wait for the OLED to power up
  display.begin(SH1107_DEFAULT_ADDR, true);
  display.display();

  delay(1000); // show splash screen

  display.clearDisplay();
  display.display();

  display.setRotation(1); // get rotated idiot
  display.setTextColor(SH110X_WHITE);
}

// combined

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
  display.setTextSize(2);
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
  renderColumn(DISPLAY_WIDTH / 2, oilHeader, psiHeader, oilPressure);

  display.display();
}

// single

void renderSingleDisplay(const char *header, int sensorReading,
                         SensorHistory *history) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  char sensorValueStr[5] = {'\0'};
  char movingAvgsLine[5 * 3 + 3] = {'\0'};

  display.clearDisplay();

  // header
  display.setTextSize(1);
  display.getTextBounds(header, cx, cy, &x1, &y1, &w, &h);
  display.setCursor((DISPLAY_WIDTH - w) / 2, cy);
  display.print(header);
  cy += h + 12;

  // current sensor value
  display.setTextSize(3);
  std::snprintf(sensorValueStr, 5, "%d", sensorReading);

  display.getTextBounds(sensorValueStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor((DISPLAY_WIDTH - w) / 2, cy);
  display.print(sensorValueStr);
  cy += h + 12;

  // 1m, 5m, 15m moving averages
  display.setTextSize(1);
  std::snprintf(movingAvgsLine, 5 * 3, "%d   %d   %d",
                history->get1mMovingAvg(), history->get5mMovingAvg(),
                history->get15mMovingAvg());

  display.getTextBounds(movingAvgsLine, cx, cy, &x1, &y1, &w, &h);
  display.setCursor((DISPLAY_WIDTH - w) / 2, cy);
  display.print(movingAvgsLine);

  display.display();
}

// --- main

void setup() {
  Serial.begin(115200);

  pinMode(A_BTN_PIN, INPUT_PULLUP);
  pinMode(B_BTN_PIN, INPUT_PULLUP);
  pinMode(C_BTN_PIN, INPUT_PULLUP);

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
  }

  // temporary until buttons are wired up
  if (now - lastDisplayStateChange > 5000) {
    lastDisplayStateChange = now;
    displayMode = static_cast<DisplayMode>((displayMode + 1) % 3);
  }

  if (digitalRead(A_BTN_PIN) == HIGH) {
    Serial.println("A");
  }

  if (digitalRead(B_BTN_PIN) == HIGH) {
    Serial.println("B");
  }

  if (digitalRead(C_BTN_PIN) == HIGH) {
    Serial.println("C");
  }

  if (displayPower == OFF) {
    display.clearDisplay();
  } else {
    switch (displayMode) {
    case COMBINED:
      renderCombinedDisplay(oilTemp, oilPressure);
      break;
    case OIL_TEMP:
      renderSingleDisplay("OIL TEMP", oilTemp, &oilTempHistory);
      break;
    case OIL_PRESSURE:
      renderSingleDisplay("OIL PSI", oilPressure, &oilPressureHistory);
      break;
    default:
      Serial.println("oh my god bruh aww hell nah man what the FUCK man");
    }
  }

  display.display();
  delay(33);
  yield();
}