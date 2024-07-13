#include <Arduino.h>
#include <Wire.h>
#include <cstdint>
#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

// --- sensor data functions

// primary values

float getOilPressure() {
  static float x = 0;
  x += 0.01;
  return (sin(x) * 50) + 50; // 0-100
}

float getOilTemp() {
  static float x = 0;
  x += 0.01;
  return sin(x) * 160 + 140; // -20-300
}

float getCoolantTemp() {
  static float x = 0;
  x += 0.01;
  return sin(x) * 160 + 140; // -20-300
}

float getBoostPsi() {
  static float x = 0;
  x += 0.01;
  return sin(x) * 25 + 5; // -20-30
}

float getDAM() {
  static float x = 0;
  x += 0.01;
  return sin(x) * 0.5 + 0.5; // 0-1
}

float getFineKnock() {
  return 0.0;
}

float getFeedbackKnock() {
  return 0.0;
}

float getAFLearn() {
  return 0.0;
}

// contextual values

float getEngineRpm() {
  static float x = 0;
  x += 0.01;
  return sin(x) * 3500 + 3500; // 0-7000
}

float getEngineLoad() {
  static float x = 0;
  x += 0.01;
  return sin(x) * 50 + 50; // 0-100
}

float getThrottlePosition() {
  static float x = 0;
  x += 0.01;
  return sin(x) * 50 + 50; // 0-100
}

// --- payload stuff

struct Payload {
  float oilPressure;
  float oilTemp;
  float coolantTemp;
  float boostPsi;
  float dam;
  float fineKnockCorrection;
  float feedbackKnockLearn;
  float afLearn;

  float engineRpm;
  float engineLoad;
  float throttlePosition;
};

uint8_t payloadBuffer[4 * 11] = {0};

BluetoothSerial ESP_BT;

// --- main

void setup() {
  Serial.begin(115200);
  ESP_BT.begin("ESP32 Test");

  Serial.println("LMAO");
}


void loop() {
  Payload payload = {
    getOilPressure(),
    getOilTemp(),
    getCoolantTemp(),
    getBoostPsi(),
    getDAM(),
    getFineKnock(),
    getFeedbackKnock(),
    getAFLearn(),

    getEngineRpm(),
    getEngineLoad(),
    getThrottlePosition()
  };

  // Serial.printf("%f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f\n",
  //   payload.oilPressure,
  //   payload.oilTemp,
  //   payload.coolantTemp,
  //   payload.boostPsi,
  //   payload.dam,
  //   payload.fineKnockCorrection,
  //   payload.feedbackKnockLearn,
  //   payload.afLearn,

  //   payload.engineRpm,
  //   payload.engineLoad,
  //   payload.throttlePosition
  // );

  memcpy(payloadBuffer, &payload, sizeof(payloadBuffer));

  if (ESP_BT.hasClient()) {
    ESP_BT.write(payloadBuffer, sizeof(payloadBuffer));
  }

  for (int i = 0; i < sizeof(payloadBuffer); i++) {
    Serial.print(payloadBuffer[i]);
    Serial.print(" ");
  }
  Serial.println();

  delay(1000);
  // delay(33);
}