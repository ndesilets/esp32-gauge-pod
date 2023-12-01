#define BUFFER_CAPACITY_15m 900 // holds ~15 mins of data at 1rps

class SensorHistory {
public:
  SensorHistory();
  void add(int value);
  int getCurrent();
  int get1mMovingAvg();
  int get5mMovingAvg();
  int get15mMovingAvg();

private:
  int buffer[BUFFER_CAPACITY_15m];
  unsigned int index;
  unsigned int capacity;
  unsigned int size;

  int sum_1m;
  int sum_5m;
  int sum_15m;
};