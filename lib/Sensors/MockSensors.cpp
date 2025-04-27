#include <Arduino.h>
#include <ISensors.h>
#include <math.h>

class MockSensors : public ISensors {
 public:
  MockSensors() { randomSeed(esp_random()); }
  int oilTemp() override {
    const double base = sin(step()) * 160 + 140;  // ~ -20-300F
    const double noise = uniformNoise(TEMP_NOISE);

    return static_cast<int>(base + noise);
  }
  int oilPressure() override {
    const double base = (sin(step()) * 50) + 50;
    const double noise = uniformNoise(PSI_NOISE);

    return static_cast<int>(base + noise);
  }

 private:
  static constexpr double TEMP_NOISE = 1.0;
  static constexpr double PSI_NOISE = 1.5;

  double step() {
    static double x = 0.0;
    x += 0.001;  // speed of the wave
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