#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>
#include <Arduino.h>
#include <SPI.h>
#include <Wire.h>
#include <type_traits>

#define SH1107_DEFAULT_ADDRESS 0x3C
#define A_BTN_PIN 4
#define B_BTN_PIN 36
#define C_BTN_PIN 39
#define BUFFER_CAPACITY 28800 // ~15 minutes at ~33ms intervals - 115kB ram

enum DisplayMode { COMBINED, OIL_TEMP, OIL_PRESSURE };
enum DisplayPower { OFF, ON };

Adafruit_SH1107 display = Adafruit_SH1107(64, 128, &Wire);
DisplayMode displayMode = COMBINED;
DisplayPower displayPower = ON;

// --- sensor data functions

double getOilPressure() {
  static uint8_t x = 0;
  x += 1;
  return sin(x) * 90 + 122;
}

double getOilTemp() {
  static uint8_t x = 0;
  x += 1;
  return sin(x) * 50 + 50;
}

// --- display functions

void initDisplay() {
  delay(250); // wait for the OLED to power up
  display.begin(SH1107_DEFAULT_ADDRESS, true);
  display.display();

  delay(1000);

  display.clearDisplay();
  display.display();

  display.setRotation(1); // get rotated idiot
  display.setTextSize(1);
  display.setTextColor(SH110X_WHITE);
  display.setCursor(0, 0);

  display.display();
}

void renderCombinedDisplay(int oilTemp, int oilPressure) {
  int16_t cx, cy, x1, y1;
  uint16_t w, h;
  const char *oilHeader = "OIL";
  const char *tempHeader = "TEMP";
  const char *psiHeader = "PSI";
  char readingStr[5] = {'\0'};

  display.clearDisplay();
  display.setCursor(0, 0);

  display.drawFastVLine(64, 0, 64, SH110X_WHITE);

  // oil temp

  cy = 0;

  display.setTextSize(1);

  display.getTextBounds(oilHeader, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(0 + ((64 - w) / 2), cy);
  display.print(oilHeader);
  cy += h + 2;

  display.getTextBounds(tempHeader, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(0 + ((64 - w) / 2), cy);
  display.print(tempHeader);
  cy += h + 16;

  display.setTextSize(2);
  std::snprintf(readingStr, 5, "%d", oilTemp);

  display.getTextBounds(readingStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(0 + ((64 - w) / 2), cy);
  display.print(readingStr);

  memset(readingStr, '\0', 5);

  // oil pressure

  cy = 0;

  display.setTextSize(1);

  display.getTextBounds(oilHeader, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(64 + ((64 - w) / 2), 0);
  display.print(oilHeader);
  cy += h + 2;

  display.getTextBounds(psiHeader, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(64 + ((64 - w) / 2), h + 2);
  display.print(psiHeader);
  cy += h + 16;

  display.setTextSize(2);
  std::snprintf(readingStr, 5, "%d", oilPressure);

  display.getTextBounds(readingStr, cx, cy, &x1, &y1, &w, &h);
  display.setCursor(64 + ((64 - w) / 2), cy);
  display.print(readingStr);

  memset(readingStr, '\0', 5);

  display.display();
}

void renderOilTempDisplay() {
  display.clearDisplay();

  display.setCursor(0, 0);
  display.println("Oil Temp");

  display.display();
}

void renderOilPressureDisplay() {
  display.clearDisplay();

  display.setCursor(0, 0);
  display.println("Oil Pressure");

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
  // oilTempReadings.add(getOilTemp());
  // oilPressureReadings.add(getOilPressure());

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
      // renderCombinedDisplay(oilTempReadings.getCurrent(),
      //                       oilPressureReadings.getCurrent());
      renderCombinedDisplay(-69, 420);
      break;
    case OIL_TEMP:
      renderOilTempDisplay();
      break;
    case OIL_PRESSURE:
      renderOilPressureDisplay();
      break;
    }
  }

  display.display();
  delay(33);
  yield();
}