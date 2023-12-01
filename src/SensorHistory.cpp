#include "SensorHistory.h"

SensorHistory::SensorHistory() {
  index = 0;
  capacity = BUFFER_CAPACITY_15m;
  size = 0;

  sum_1m = 0;
  sum_5m = 0;
  sum_15m = 0;
}

void SensorHistory::add(int value) {
  buffer[index % capacity] = value;
  index++;

  if (size < capacity) {
    size++;
  }

  sum_1m += value;
  if (size >= 60) {
    sum_1m -= buffer[(index - 60) % capacity];
  }

  sum_5m += value;
  if (size >= 300) {
    sum_5m -= buffer[(index - 300) % capacity];
  }

  sum_15m += value;
  if (size >= 900) {
    sum_15m -= buffer[(index - 900) % capacity];
  }
}

int SensorHistory::getCurrent() { return buffer[index - 1 % capacity]; }

int SensorHistory::get1mMovingAvg() { return sum_1m / 60; }

int SensorHistory::get5mMovingAvg() { return sum_5m / 300; }

int SensorHistory::get15mMovingAvg() { return sum_15m / 900; }