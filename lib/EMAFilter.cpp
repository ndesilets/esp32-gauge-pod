#pragma once
template <typename T>
class EMAFilter {
 public:
  explicit EMAFilter(float alpha) : alpha(alpha), isFirst(true) {}

  T update(T value) {
    if (isFirst) {
      isFirst = false;
      updated = value;
    }

    updated += alpha * (value - updated);

    return updated;
  }

 private:
  float alpha;
  bool isFirst;
  T updated{};
};