#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>
#include <Arduino.h>
#include <Wire.h>

#define SH1107_DEFAULT_ADDR 0x3C
#define A_BTN_PIN 14
#define B_BTN_PIN 32
#define C_BTN_PIN 15
#define INTERNAL_BTN_PIN 38

enum DisplayMode { COMBINED, OIL_TEMP, OIL_PRESSURE };
enum DisplayPower { OFF, ON };

Adafruit_SH1107 display = Adafruit_SH1107(64, 128, &Wire);
DisplayMode displayMode = COMBINED;
DisplayPower displayPower = ON;

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

double interpolatePressure(int adcValue) {
  // values are linear for the rife 100psi sensor so just interpolate off the
  // 0-5v reading scaled down to 3.3v
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
  // no fuckery needed here suprisingly
  return interpolatePressure(analogValue);
}

double calcOilTemp(int analogValue) {
  int calculatedResistance = 10000 * (((double)4095 / analogValue) - 1);
  double interpolatedValue = interpolateTemperature(calculatedResistance);
  const double MODIFIER =
      7; // seems to be spot on compared to combustion thermometer

  return interpolatedValue + MODIFIER;
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
  //
  // read sensor values
  //

  int adcReading = analogRead(A0);
  int oilTemp = calcOilTemp(adcReading);
  Serial.printf("temp - adc: %d, val: %d - ", adcReading, oilTemp);

  adcReading = analogRead(A1);
  int oilPressure = calcOilPressure(adcReading);
  Serial.printf("pressure - adc: %d, val: %d\n", adcReading, oilPressure);

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
      // renderCombinedDisplay(oilTemp, oilPressure);
      renderCombinedDisplay2(oilTemp, oilPressure);
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