#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>
#include <Arduino.h>
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
inline int the_one_true_modulo(int i, int n) { return (i % n + n) % n; }

// --- eee

const int rifeTempSensorRef[] = {
    189726, // -20F
    132092, // -10F
    93425,  //   0F
    67059,  //  10F
    48804,  //  20F
    35983,  //  30F
    26855,  //  40F
    20274,  //  50F
    15473,  //  60F
    11929,  //  70F
    9287,   //  80F
    7295,   //  90F
    5781,   //  100F
    4618,   //  110F
    3718,   //  120F
    3016,   //  130F
    2463,   //  140F
    2025,   //  150F
    1675,   //  160F
    1395,   //  170F
    1167,   //  180F
    983,    //  190F
    832,    //  200F
    707,    //  210F
    604,    //  220F
    519,    //  230F
    447,    //  240F
    387,    //  250F
    336,    //  260F
    294,    //  270F
    257,    //  280F
    226     //  290F
};
const int tempSensorRefLen = sizeof(rifeTempSensorRef) / sizeof(int);

double interpolateTemperature(int resistance) {
  // find closest resistance values
  int maxResistanceIndex = 0;
  int minResistanceIndex = 0;
  for (int i = 0; i < tempSensorRefLen; i++) {
    if (resistance > rifeTempSensorRef[i]) {
      if (i == 0) {
        return -20.0f;
      } else if (i == tempSensorRefLen - 1) {
        return 290.0f;
      }

      maxResistanceIndex = i - 1;
      minResistanceIndex = i;
      break;
    }
  }

  int maxResistance = rifeTempSensorRef[maxResistanceIndex];
  int minResistance = rifeTempSensorRef[minResistanceIndex];

  int maxResistanceTemp = -20.0 + (maxResistanceIndex * 10);
  int minResistanceTemp = maxResistanceTemp + 10;

  // R1 = cold, R2 = hot
  double interpolated =
      maxResistanceTemp + ((double)(resistance - maxResistance) /
                           (double)(minResistance - maxResistance)) *
                              (minResistanceTemp - maxResistanceTemp);

  return interpolated;
}

const int rife100PsiSensorRef[] = {
    409,  // 0.0 PSI,
    514,  // 3.2 PSI,
    620,  // 6.5 PSI,
    725,  // 9.7 PSI,
    832,  // 12.9 PSI,
    938,  // 16.1 PSI,
    1043, // 19.4 PSI,
    1149, // 22.6 PSI,
    1254, // 25.8 PSI,
    1360, // 29.0 PSI,
    1465, // 32.3 PSI,
    1572, // 35.5 PSI,
    1677, // 38.7 PSI,
    1783, // 41.9 PSI,
    1888, // 45.2 PSI,
    1994, // 48.4 PSI,
    2099, // 51.6 PSI,
    2205, // 54.8 PSI,
    2311, // 58.1 PSI,
    2417, // 61.3 PSI,
    2522, // 64.5 PSI,
    2628, // 67.7 PSI,
    2733, // 71.0 PSI,
    2839, // 74.2 PSI,
    2944, // 77.4 PSI,
    3050, // 80.6 PSI,
    3156, // 83.9 PSI,
    3262, // 87.1 PSI,
    3367, // 90.3 PSI,
    3473, // 93.5 PSI,
    3578, // 96.8 PSI,
    3685, // 100.0 PSI
};

double interpolatePressure(int adcValue) {
  if (adcValue < 409) {
    return 0;
  } else if (adcValue > 3685) {
    return 100;
  }

  double psi = ((double)(adcValue - 409) / (3685 - 409)) * 100;

  return psi;
}

// --- sensor buffers

unsigned long lastSensorRead = 0;
unsigned long now = 0;

// --- sensor data functions

double getOilPressureMocked() {
  static double x = 0;
  x += 0.01;
  return (sin(x) * 50) + 50;
}

double getOilTempMocked() {
  static double x = 0;
  x += 0.01;
  return sin(x) * 160 + 140;
}

double calcOilPressure(int analogValue) {
  const double MODIFIER =
      1.5; // seems to be perfect compared against maddox power bleeder
  double interpolatedValue = interpolatePressure(analogValue - 80) / MODIFIER;

  return interpolatedValue;
}

double calcOilTemp(int analogValue) {
  int calculatedResistance = 10000 * (((double)4095 / analogValue) - 1);
  double interpolatedValue = interpolateTemperature(calculatedResistance);
  const double MODIFIER = 7;

  return interpolatedValue + MODIFIER; // seems to be spot on compared to
                                       // combustion thermometer
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

void renderSingleDisplay(const char *header, int sensorReading, int minV,
                         int maxV, int numDetents, const int *detents) {
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

// --- main

void setup() {
  Serial.begin(115200);

  pinMode(A_BTN_PIN, INPUT_PULLUP);
  pinMode(B_BTN_PIN, INPUT_PULLUP);
  pinMode(C_BTN_PIN, INPUT_PULLUP);
  pinMode(INTERNAL_BTN_PIN, INPUT_PULLUP);

  // power supply (temporary)
  pinMode(A5, OUTPUT);
  digitalWrite(A5, HIGH);

  Serial.println("LMAO!");

  initDisplay();
}

void loop() {
  int adcReading = analogRead(A0);
  int oilTemp = calcOilTemp(adcReading);
  Serial.printf("temp - adc: %d, val: %d - ", adcReading, oilTemp);

  adcReading = analogRead(A1);
  int oilPressure = calcOilPressure(adcReading);
  Serial.printf("pressure - adc: %d, val: %d\n", adcReading, oilPressure);

  // now = millis();
  // if (now - lastSensorRead > 1000) {
  //   lastSensorRead = now;

  //   // A0 is the temp sensor signal pin
  //   // A5 is the vref pin
  //   int analogValue = analogRead(A0);
  //   int calculatedResistance = 10000 * (((double)4095 / analogValue) - 1);
  //   double interpolatedValue = interpolateTemperature(calculatedResistance);

  //   Serial.printf("A0: %d\tCalculated: %d\tInterpolated: %.2f\n",
  //   analogValue, calculatedResistance, interpolatedValue);
  // }

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
    displayMode =
        static_cast<DisplayMode>(the_one_true_modulo(displayMode - 1, 3));
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
      renderSingleDisplay("OIL TEMP", oilTemp, -20, 300, 3, tempDetents);
      break;
    case OIL_PRESSURE:
      renderSingleDisplay("OIL PSI", oilPressure, 0, 100, 4, psiDetents);
      break;
    default:
      Serial.println("oh my god bruh aww hell nah man what the FUCK man");
    }
  }

  display.display();
  delay(66);
  // delay(1000);
  yield();
}