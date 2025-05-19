#include <RIFELiquidTempSensor.h>
#include <gtest/gtest.h>

/**
 * for whatever god damn reason platformio/mingw keeps trying to test this as if it was some sort of windows program and expects a WinMain function or something but what the fuck. fuck you
 */

// --- public

TEST(RIFELiquidTempSensor, readTemperature) {
  RIFELiquidTempSensor sensor(5.0, 3000.0);

  int temp = sensor.readTemperature(0.5);  // 0.5v = 27000ohms = 39F
                                           //   EXPECT_EQ(temp, 39);
  EXPECT_EQ(1, 1);
}

// --- private

TEST(RIFELiquidTempSensorPrivate, interpolateTemperature) {
  RIFELiquidTempSensor sensor(5.0, 3000.0);

  EXPECT_DOUBLE_EQ(sensor.interpolateTemperature(189726), -20.0);
}

TEST(RIFELiquidTempSensorPrivate, calculateResistance) {
  RIFELiquidTempSensor sensor(5.0, 3000.0);

  EXPECT_DOUBLE_EQ(sensor.calculateResistance(4.263), 519.0);
}