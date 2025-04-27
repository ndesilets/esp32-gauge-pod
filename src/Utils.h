#pragma once
template <typename T>
constexpr T theOneTrueModulo(T value, T modulus) {
  return (value % modulus + modulus) % modulus;
}