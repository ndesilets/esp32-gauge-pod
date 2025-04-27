# esp32-gauge-pod

ESP32 project to replace traditional gauge pods with a single MCU and display for a smaller visual footprint.

## Parts List

- Adafruit Feather ESP32 V2
- Adafruit FeatherWing 128x64 OLED Display
- RIFE 1/8" NPT Liquid Temperature Direct Wire Sensor 52-1226
- RIFE 1/8" NPT 100PSI Pressure Sensor Transducer 52-100PSI

You could definitely use cheaper sensors and easily add them to this project.

## Features

This has a three mode display that shows:
- Combined oil temperature and pressure
- Oil temperature with moving averages and horizontal bar gauge
- Oil pressure with moving averages and horizontal bar gauge 
- Alerts for high oil temperature and low oil pressure

## things learned
FIXME: ESP32 built-in ADC is less than stellar