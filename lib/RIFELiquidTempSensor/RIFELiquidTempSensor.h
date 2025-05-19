#pragma once
#include <gtest/gtest_prod.h>

class RIFELiquidTempSensor {
 public:
  RIFELiquidTempSensor(double VDD, double bias) : VDD(VDD), bias(bias) {}
  int readTemperature(double Vout);

 private:
  double VDD;
  double bias;
  inline static const int rifeTempSensorRef[] = {
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
      226      //  290Fb
  };
  static constexpr int tempSensorRefLen = sizeof(rifeTempSensorRef) / sizeof(int);

  double interpolateTemperature(double res);
  double calculateResistance(double Vout);

  FRIEND_TEST(RIFELiquidTempSensorPrivate, interpolateTemperature);
  FRIEND_TEST(RIFELiquidTempSensorPrivate, calculateResistance);
};