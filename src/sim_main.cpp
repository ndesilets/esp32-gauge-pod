#include <chrono>
#include <iostream>
#include <thread>
#include "EMAFilter.h"
#include "MockSensors.h"

int main() {
  MockSensors sensors;         // your existing mock
  EMAFilter<int> tempF(0.2f);  // tweak α live
  EMAFilter<int> presPsi(0.1f);

  std::cout << "i,raw_temp,filt_temp,raw_pressure,filt_pressure\n";

  for (int i = 0; i < 10_000; ++i) {  // ~100 s at 10 ms/iter
    int rawT = sensors.oilTemp();
    int rawP = sensors.oilPressure();
    int filtT = tempF.update(rawT);
    int filtP = presPsi.update(rawP);

    // CSV-ish output you can pipe to Python, Excel, etc.
    std::cout << i << ',' << rawT << ',' << filtT << ',' << rawP << ',' << filtP << '\n';

    std::this_thread::sleep_for(std::chrono::milliseconds(66));
  }
}