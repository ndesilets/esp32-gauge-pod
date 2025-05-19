#include <Adafruit_ADS1X15.h>
#include <ISensors.h>
#include <RIFELiquidTempSensor.h>
#include <Wire.h>

Adafruit_ADS1115 ads1115;

// --- helpers

static double interpolatePressure(double voltage) {
  // rife 100psi voltage/pressure scales linearly from 0.5-4.5v (0-100PSI), so dont need the LUT
  if (voltage < 0.5) {
    return 0.0;
  } else if (voltage > 4.5) {
    return 100.0;
  }

  return (voltage - 0.5) * (100.0 / 4.0);
}

// --- concrete class

class AnalogSensors : public ISensors {
 public:
  AnalogSensors() {
    ads1115.setGain(GAIN_TWOTHIRDS);          // 6.144v FSR
    ads1115.setDataRate(RATE_ADS1115_32SPS);  // screen only refreshes around 15hz
  }

  int oilTemp() override {
    int16_t adc = ads1115.readADC_SingleEnded(2);
    double voltage = adc * (VFSR / 32767.0);
    constexpr double offset = 3;  // TODO: figure out actual offset, but this seems close?

    int unsmoothed = (int)(tempSensor.readTemperature(voltage) + offset);
    smoothedTemp += ((unsmoothed << 8) - smoothedTemp) >> ALPHA_SHIFT;
    int final = smoothedTemp >> 8;  // shift back to 12 bit

    return final;
  }

  int oilPressure() override {
    int adc = ads1115.readADC_SingleEnded(3);
    double voltage = adc * (VFSR / 32767.0);
    // TODO: could filter noise here but any excessive latency would be bad for fast drops

    return (int)(interpolatePressure(voltage) + 0.5);  // round to nearest int
  }

 private:
  static constexpr double RB = 3000.0;   // 3k ohm bias resistor
  static constexpr double VDD = 5.0;     // 5v supply
  static constexpr double VFSR = 6.144;  // 6.144v FSR
  static constexpr int ALPHA_SHIFT = 1;
  int smoothedTemp = 0;
  RIFELiquidTempSensor tempSensor = RIFELiquidTempSensor(VDD, RB);
};

// --- factory

namespace {
AnalogSensors hwInstance;
}

ISensors& getAnalogSensors() {
  ads1115.begin();
  return hwInstance;
}
