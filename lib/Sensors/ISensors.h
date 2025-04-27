#pragma once
#include <Arduino.h>

struct ISensors {
  virtual int oilTemp() = 0;      // °F
  virtual int oilPressure() = 0;  // PSI
  virtual ~ISensors() = default;
};
