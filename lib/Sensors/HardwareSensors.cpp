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

static const int rife100PsiSensorRef[] = {
    409,   // 0.0 PSI,
    514,   // 3.2 PSI,
    620,   // 6.5 PSI,
    725,   // 9.7 PSI,
    832,   // 12.9 PSI,
    938,   // 16.1 PSI,
    1043,  // 19.4 PSI,
    1149,  // 22.6 PSI,
    1254,  // 25.8 PSI,
    1360,  // 29.0 PSI,
    1465,  // 32.3 PSI,
    1572,  // 35.5 PSI,
    1677,  // 38.7 PSI,
    1783,  // 41.9 PSI,
    1888,  // 45.2 PSI,
    1994,  // 48.4 PSI,
    2099,  // 51.6 PSI,
    2205,  // 54.8 PSI,
    2311,  // 58.1 PSI,
    2417,  // 61.3 PSI,
    2522,  // 64.5 PSI,
    2628,  // 67.7 PSI,
    2733,  // 71.0 PSI,
    2839,  // 74.2 PSI,
    2944,  // 77.4 PSI,
    3050,  // 80.6 PSI,
    3156,  // 83.9 PSI,
    3262,  // 87.1 PSI,
    3367,  // 90.3 PSI,
    3473,  // 93.5 PSI,
    3578,  // 96.8 PSI,
    3685,  // 100.0 PSI
};

// --- helpers

static double interpolateTemperature(int res) {
  // find closest resistance values
  int maxRIdx = 0;
  int minRIdx = 0;
  for (int i = 0; i < tempSensorRefLen; i++) {
    if (res > rifeTempSensorRef[i]) {
      if (i == 0) {
        return -20.0f;
      } else if (i == tempSensorRefLen - 1) {
        return 290.0f;
      }

      maxRIdx = i - 1;
      minRIdx = i;
      break;
    }
  }

  int maxRes = rifeTempSensorRef[maxRIdx];
  int minRes = rifeTempSensorRef[minRIdx];

  int hiTemp = -20.0 + (maxRIdx * 10);
  int loTemp = hiTemp + 10;

  // R1 = cold, R2 = hot
  double interpolated = hiTemp + ((double)(res - maxRes) / (double)(minRes - maxRes)) * (loTemp - hiTemp);

  return interpolated;
}

static double interpolatePressure(double voltage) {
  // rife 100psi voltage/pressure scales linearly, so probably dont need the LUT
  return (voltage - 0.5) * (100.0 / 4.0);
}

// --- concrete class

class HardwareSensors : public ISensors {
 public:
  int oilTemp() override {
    int16_t adc = ads1115.readADC_SingleEnded(2);
    // can only set range to +/-6.144v or +/-4.096v, so use 6.144v
    // 15 bit effective resolution since its signed
    double Rtemp = RB * adc * V_FSR / (32767.0 * V_SUP - adc * V_FSR);
    constexpr double CAL = 0;  // TODO: figure out new offset

    int unsmoothed = (int)(interpolateTemperature(Rtemp) + CAL);
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
  static constexpr double RB = 3000.0;    // 3k ohm
  static constexpr double V_SUP = 5.0;    // 5v supply
  static constexpr double V_FSR = 6.144;  // 6.144v FSR
  static constexpr int ALPHA_SHIFT = 1;
  int smoothedTemp = 0;
};

// --- factory

namespace {
HardwareSensors hwInstance;
}

ISensors& getHardwareSensors() {
  ads1115.begin();
  return hwInstance;
}
