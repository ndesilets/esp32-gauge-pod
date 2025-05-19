#include "RIFELiquidTempSensor.h"

// -- private

double RIFELiquidTempSensor::interpolateTemperature(double res) {
  // find closest resistance values
  // TODO: could do binary search but only 30 elements so who cares
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

  // convert LUT indexes to known temps
  int hiTemp = -20.0 + (maxRIdx * 10);
  int loTemp = hiTemp + 10;

  double interpolated = hiTemp + ((double)(res - maxRes) / (double)(minRes - maxRes)) * (loTemp - hiTemp);

  return interpolated;
}

double RIFELiquidTempSensor::calculateResistance(double Vout) {
  // R1 = thermistor, R2 = bias resistor
  // R1 = ((VDD * R2) / Vout) - R2
  return ((VDD * bias) / Vout) - bias;
}

// -- public

int RIFELiquidTempSensor::readTemperature(double Vout) {
  double resistance = calculateResistance(Vout);

  return (int)interpolateTemperature(resistance);
}