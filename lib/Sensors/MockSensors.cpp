#include <Arduino.h>
#include <ISensors.h>
#include <math.h>

/*

// 12-bit raw reading → smoothed value (Q0.8 fixed-point)
constexpr uint8_t ALPHA_SHIFT = 4;     // 1/16 ≈ 0.0625  (bigger shift = more smoothing)
uint32_t smooth = 0;                   // Q0.8 format (<<8)

uint16_t readThermistor() {
  uint16_t raw = adc1_get_raw(ADC1_CHANNEL_6);   // GPIO34 e.g.
  smooth += ((raw << 8) - smooth) >> ALPHA_SHIFT;
  return smooth >> 8;                  // back to 0-4095
}

*/

class MockSensors : public ISensors {
 public:
  MockSensors() { randomSeed(esp_random()); }
  int oilTemp() override {
    const double base = sin(step()) * 160 + 140;  // ~ -20-300F
    const double noise = uniformNoise(TEMP_NOISE);
    const int unsmoothed = int(base + noise);

    Serial.printf("unsmoothed: %d\t", unsmoothed);

    smoothedTemp += ((unsmoothed << 8) - smoothedTemp) >> ALPHA_SHIFT;

    const int final = smoothedTemp >> 8;  // shift back to 12 bit

    Serial.printf("smoothed: %d\n", final);

    return final;
  }
  int oilPressure() override {
    const double base = (sin(step()) * 50) + 50;
    const double noise = uniformNoise(PSI_NOISE);

    return static_cast<int>(base + noise);
  }

 private:
  static constexpr double TEMP_NOISE = 0.50;
  static constexpr double PSI_NOISE = 1.5;
  static constexpr int ALPHA_SHIFT = 1;
  int smoothedTemp = 0;
  int smoothedPressure = 0;

  double step() {
    static double x = 0.0;
    x += 0.00075;  // speed of the wave
    // x += 0.00001;  // speed of the wave
    return x;
  }

  // generate noise within range [-amplitude, amplitude]
  static double uniformNoise(double amplitude) {
    return (static_cast<double>(random(-1000, 1001)) / 1000.0) * amplitude;
  }
};

// --- factory

namespace {
MockSensors mockInstance;
}

ISensors& getMockSensors() {
  return mockInstance;
}