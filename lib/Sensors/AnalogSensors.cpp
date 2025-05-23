#include <Adafruit_ADS1X15.h>
#include <ISensors.h>
#include <Wire.h>

Adafruit_ADS1115 ads1115;

// --- lookup tables

static const int rifeTempSensorRef[] = {
    189726,  // -20F
    132092,  // -10F
    93425,   //   0F
    67059,   //  10F
    48804,   //  20F
    35983,   //  30F
    26855,   //  40F
    20274,   //  50F
    15473,   //  60F
    11929,   //  70F
    9287,    //  80F
    7295,    //  90F
    5781,    //  100F
    4618,    //  110F
    3718,    //  120F
    3016,    //  130F
    2463,    //  140F
    2025,    //  150F
    1675,    //  160F
    1395,    //  170F
    1167,    //  180F
    983,     //  190F
    832,     //  200F
    707,     //  210F
    604,     //  220F
    519,     //  230F
    447,     //  240F
    387,     //  250F
    336,     //  260F
    294,     //  270F
    257,     //  280F
    226      //  290F
};
static constexpr int tempSensorRefLen = sizeof(rifeTempSensorRef) / sizeof(int);

// --- helpers

static double interpolateTemperature(double res) {
  // find closest resistance values
  // TODO: could do binary search but only 30 elements so who cares
  int hiResIdx = 0;
  int loResIdx = 0;
  for (int i = 0; i < tempSensorRefLen; i++) {
    if (res > rifeTempSensorRef[i]) {
      if (i == 0) {
        return -20.0f;
      } else if (i == tempSensorRefLen - 1) {
        return 290.0f;
      }

      hiResIdx = i;
      loResIdx = i - 1;
      break;
    }
  }

  // convert LUT indexes to known temps
  int hiTemp = -20.0 + (hiResIdx * 10);
  int loTemp = hiTemp - 10;

  int hiRes = rifeTempSensorRef[hiResIdx];
  int loRes = rifeTempSensorRef[loResIdx];

  double interpolated = hiTemp + ((double)(res - hiRes) / (double)(loRes - hiRes)) * (loTemp - hiTemp);

  return interpolated;
}

static double interpolatePressure(double voltage) {
  // rife 100psi voltage/pressure scales linearly from 0.5-4.5v (0-100PSI), so dont need the LUT
  if (voltage < 0.5) {
    return 0.0;
  } else if (voltage > 4.5) {
    return 100.0;
  }

  return (voltage - 0.5) * (100.0 / 4.0);
}

double calculateResistance(double Vout, double VDD, double bias) {
  // R1 = thermistor, R2 = bias resistor
  // R1 = ((VDD * R2) / Vout) - R2
  return ((VDD * bias) / Vout) - bias;
}

// --- concrete class

class AnalogSensors : public ISensors {
 public:
  int oilTemp() override {
    int16_t adc = ads1115.readADC_SingleEnded(2);
    double voltage = adc * (V_FSR / 32767.0);
    double resistance = calculateResistance(voltage, V_SUP, RB);
    // constexpr double offset = 2;  // 259F measured vs 261F expected - but via self-heating it will eventually make up for this (~15m)
    constexpr double offset = 0;

    int unsmoothed = (int)(interpolateTemperature(resistance) + offset);
    smoothedTemp += ((unsmoothed << 8) - smoothedTemp) >> ALPHA_SHIFT;
    int final = smoothedTemp >> 8;  // shift back to 12 bit

    return final;
  }

  int oilPressure() override {
    int adc = ads1115.readADC_SingleEnded(3);
    double voltage = adc * (V_FSR / 32767.0);

    return (int)(interpolatePressure(voltage) + 0.5);  // round to nearest int
  }

 private:
  static constexpr double RB = 3000.0;    // 3k ohm bias resistor
  static constexpr double V_SUP = 4.96;   // 5v supply
  static constexpr double V_FSR = 6.144;  // 6.144v FSR
  static constexpr int ALPHA_SHIFT = 1;
  int smoothedTemp = 0;
};

// --- factory

namespace {
AnalogSensors hwInstance;
}

ISensors& getAnalogSensors() {
  ads1115.begin();
  return hwInstance;
}
