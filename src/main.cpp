#include <Arduino.h>
#include <Wire.h>

// --- sensor data functions

double getOilPressure() {
  static double x = 0;
  x += 0.01;
  return (sin(x) * 50) + 50;
}

double getOilTemp() {
  static double x = 0;
  x += 0.01;
  return sin(x) * 160 + 140;
}

// --- main

void setup() {
  Serial.begin(115200);

  Serial.println("LMAO!");
}


void loop() {
  int oilTemp = getOilTemp();
  int oilPressure = getOilPressure();

  delay(33);
}