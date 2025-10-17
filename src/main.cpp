#include <Arduino.h>
#include "Display.h"
#include "Sensors.h"
#include "Utils.h"

#define A_BTN_PIN 12
#define B_BTN_PIN 14
#define C_BTN_PIN 27
// #define INTERNAL_BTN_PIN 38

#define MIN_TEMP_VAL -20
#define MAX_TEMP_VAL 300
#define MIN_PSI_VAL 0
#define MAX_PSI_VAL 100

#define OIL_TEMP_WARN 250     // if above 250F
#define OIL_PRESSURE_WARN 15  // if below 15psi

// display mode
enum DisplayMode { COMBINED, OIL_TEMP, OIL_PRESSURE, DISPLAY_OFF };
constexpr int DISPLAY_MODE_COUNT = 4;
DisplayMode displayMode = COMBINED;

// gauge detents
const int tempDetents[3] = {0, 180, 250};
constexpr int TEMP_DENTENTS_COUNT = sizeof(tempDetents) / sizeof(int);
const int psiDetents[4] = {20, 40, 60, 80};
constexpr int PSI_DETENTS_COUNT = sizeof(psiDetents) / sizeof(int);

// gauge configs
const BarGaugeConfig oilTempConfig = {"OIL TEMP", tempDetents, TEMP_DENTENTS_COUNT, MIN_TEMP_VAL, MAX_TEMP_VAL, true};
const BarGaugeConfig oilPressureConfig = {"OIL PSI", psiDetents, PSI_DETENTS_COUNT, MIN_PSI_VAL, MAX_PSI_VAL, true};

// button state
bool intButtonPressed = false;
bool aButtonPressed = false;
bool bButtonPressed = false;
bool cButtonPressed = false;

// monitoring state
int minMeasuredTemp = 0;
int maxMeasuredTemp = 0;
int minMeasuredPressure = 0;
int maxMeasuredPressure = 0;
bool engineHasStarted = false;
bool uhOhStinky = false;

ISensors* sensors = nullptr;

void setup() {
  Serial.begin(115200);

  Serial.println("Starting up...");

#ifdef MOCK_SENSORS
  sensors = &getMockSensors();
#else
  sensors = &getAnalogSensors();
#endif

#ifdef LONG_AS_FUCK_I2C
  // this can help w/ ethernet cables longer than 1'
  Wire.begin(SDA, SCL, 50000);
#endif

  pinMode(A_BTN_PIN, INPUT_PULLUP);
  pinMode(B_BTN_PIN, INPUT_PULLUP);
  // pinMode(C_BTN_PIN, INPUT_PULLUP);
  // pinMode(INTERNAL_BTN_PIN, INPUT_PULLUP);

  Serial.println("Initializing display...");
  bool displayOK = initDisplay();
  Serial.printf("\tDisplay: %s\n", displayOK ? "OK" : "absolutely cooked brother");

  Serial.println("Initializing oil temp.");

  int oilTemp = sensors->oilTemp();
  minMeasuredTemp = oilTemp;
  maxMeasuredTemp = oilTemp;

  Serial.println("Start up complete.");
}

void loop() {
  // --- read sensors

  int oilTemp = sensors->oilTemp();
  int oilPressure = sensors->oilPressure();

  minMeasuredTemp = min(minMeasuredTemp, oilTemp);
  maxMeasuredTemp = max(maxMeasuredTemp, oilTemp);
  minMeasuredPressure = min(minMeasuredPressure, oilPressure);
  maxMeasuredPressure = max(maxMeasuredPressure, oilPressure);

  SensorState oilTempState = {oilTemp, minMeasuredTemp, maxMeasuredTemp};
  SensorState oilPressureState = {oilPressure, minMeasuredPressure, maxMeasuredPressure};

  // --- monitoring logic

  // good enough until we can use canbus one day
  if (!engineHasStarted && oilPressure >= OIL_PRESSURE_WARN) {
    engineHasStarted = true;
  }

  if (engineHasStarted) {
    uhOhStinky = (oilTempState.current >= OIL_TEMP_WARN) || (oilPressureState.current < OIL_PRESSURE_WARN);
    if (uhOhStinky)
      displayMode = COMBINED;  // force combined to show all metrics
  }

  // --- button handling

  // switch display mode
  if (!digitalRead(A_BTN_PIN)) {
    aButtonPressed = true;
  } else if (aButtonPressed) {
    aButtonPressed = false;
    displayMode = static_cast<DisplayMode>(theOneTrueModulo(displayMode + 1, DISPLAY_MODE_COUNT));
  }

  // reset min/maxes
  if (!digitalRead(B_BTN_PIN)) {
    bButtonPressed = true;
  } else if (bButtonPressed) {
    bButtonPressed = false;
    minMeasuredTemp = oilTemp;
    maxMeasuredTemp = oilTemp;
    minMeasuredPressure = oilPressure;
    maxMeasuredPressure = oilPressure;
  }

  // C button disabled for my jank i2c fix (short C button to GND w/ 4.7k resistor)
  // this mostly fixes i2c over a 3' ethernet cable at the expense of one less button and maybe a small bit of dignity

  // if (!digitalRead(B_BTN_PIN)) {
  //   bButtonPressed = true;
  // } else if (bButtonPressed) {
  //   bButtonPressed = false;
  //   displayMode = DISPLAY_OFF;
  // }

  // if (!digitalRead(C_BTN_PIN)) {
  //   cButtonPressed = true;
  // } else if (cButtonPressed) {
  //   cButtonPressed = false;
  //   minMeasuredTemp = oilTemp;
  //   maxMeasuredTemp = oilTemp;
  // }

  // --- display handling

  if (uhOhStinky && (millis() / 333 % 2 == 0)) {
    flashDisplay();
  } else {
    switch (displayMode) {
      case COMBINED:
        renderCombinedDisplay2(oilTempState, oilTempConfig, oilPressureState, oilPressureConfig);
        break;
      case OIL_TEMP:
        renderSingleDisplay(oilTempState, oilTempConfig);
        break;
      case OIL_PRESSURE:
        renderSingleDisplay(oilPressureState, oilPressureConfig);
        break;
      case DISPLAY_OFF:
        displayOff();
        break;
      default:
        Serial.println("oh my god bruh aww hell nah man what the FUCK man");
    }
  }

  delay(66);  // ~15 Hz refresh
  yield();
}
