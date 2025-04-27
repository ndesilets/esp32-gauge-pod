#include <Arduino.h>
#include "Display.h"
#include "Sensors.h"
#include "Utils.h"

#define A_BTN_PIN 14
#define B_BTN_PIN 32
#define C_BTN_PIN 15
#define INTERNAL_BTN_PIN 38

enum DisplayMode { COMBINED, OIL_TEMP, OIL_PRESSURE, DISPLAY_OFF };
constexpr int DISPLAY_MODE_COUNT = 4;

DisplayMode displayMode = OIL_TEMP;

int minMeasuredTemp = 0;
int maxMeasuredTemp = 0;

const int tempDetents[3] = {0, 180, 250};
constexpr int TEMP_DENTENTS_COUNT = sizeof(tempDetents) / sizeof(int);
const int psiDetents[4] = {20, 40, 60, 80};
constexpr int PSI_DETENTS_COUNT = sizeof(psiDetents) / sizeof(int);

bool intButtonPressed = false;
bool aButtonPressed = false;
bool bButtonPressed = false;
bool cButtonPressed = false;

#ifdef MOCK_SENSORS
ISensors& sensors = getMockSensors();
#else
ISensors& sensors = getHardwareSensors();
#endif

void setup() {
  Serial.begin(115200);

  pinMode(A_BTN_PIN, INPUT_PULLUP);
  pinMode(B_BTN_PIN, INPUT_PULLUP);
  pinMode(C_BTN_PIN, INPUT_PULLUP);
  pinMode(INTERNAL_BTN_PIN, INPUT_PULLUP);

  // TODO: don't remember if i still need this or not but it works as is so im leaving it lmao
  pinMode(A5, OUTPUT);
  digitalWrite(A5, HIGH);

  initDisplay();

  int oilTemp = sensors.oilTemp();
  minMeasuredTemp = oilTemp;
  maxMeasuredTemp = oilTemp;
}

void loop() {
  // --- read sensors

  int oilTemp = sensors.oilTemp();
  int oilPressure = sensors.oilPressure();

  minMeasuredTemp = min(minMeasuredTemp, oilTemp);
  maxMeasuredTemp = max(maxMeasuredTemp, oilTemp);

  // --- button handling

  if (!digitalRead(A_BTN_PIN)) {
    aButtonPressed = true;
  } else if (aButtonPressed) {
    aButtonPressed = false;
    displayMode = static_cast<DisplayMode>(theOneTrueModulo(displayMode + 1, DISPLAY_MODE_COUNT));
  }

  if (!digitalRead(B_BTN_PIN)) {
    bButtonPressed = true;
  } else if (bButtonPressed) {
    bButtonPressed = false;
    displayMode = DISPLAY_OFF;
  }

  if (!digitalRead(C_BTN_PIN)) {
    cButtonPressed = true;
  } else if (cButtonPressed) {
    cButtonPressed = false;
    minMeasuredTemp = oilTemp;
    maxMeasuredTemp = oilTemp;
  }

  // --- display handling

  switch (displayMode) {
    case COMBINED:
      // renderCombinedDisplay(oilTemp, oilPressure);
      renderCombinedDisplay2(oilTemp, oilPressure, tempDetents, TEMP_DENTENTS_COUNT, psiDetents, PSI_DETENTS_COUNT);
      break;
    case OIL_TEMP:
      renderSingleDisplay("OIL TEMP", oilTemp, -20, 300, 3, tempDetents, true, minMeasuredTemp, maxMeasuredTemp);
      break;
    case OIL_PRESSURE:
      renderSingleDisplay("OIL PSI", oilPressure, 0, 100, 4, psiDetents, false, 0, 0);
      break;
    case DISPLAY_OFF:
      displayOff();
      break;
    default:
      Serial.println("oh my god bruh aww hell nah man what the FUCK man");
  }

  delay(66);  // ~15 Hz refresh
  yield();
}
