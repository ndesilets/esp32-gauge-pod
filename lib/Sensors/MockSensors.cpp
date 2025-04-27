#include <ISensors.h>
#include <math.h>

class MockSensors : public ISensors {
 public:
  int oilTemp() override { return sin(step()) * 160 + 140; }
  int oilPressure() override { return (sin(step()) * 50) + 50; }

 private:
  double step() {
    static double x = 0.0;
    x += 0.05;  // speed of the wave
    return x;
  }
};

// --- factory

namespace {
MockSensors mockInstance;
}

ISensors& getMockSensors() {
  return mockInstance;
}