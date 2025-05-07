# esp32-gauge-pod

ESP32 project to replace traditional gauge pods for oil temp/pressure with a single MCU and display.

## Parts List

- ~~Adafruit Feather ESP32 V2~~
- A questionable ESP32 clone that mostly works
- Adafruit ADS1115 ADC (see Things Learned)
- Some random 2 way level shifter
- LM7805 5v regulator
- Adafruit FeatherWing 128x64 OLED Display
- RIFE 1/8" NPT Liquid Temperature Direct Wire Sensor 52-1226
- RIFE 1/8" NPT 100PSI Pressure Sensor Transducer 52-100PSI

## Features

This has a three mode display that shows:
- Combined oil temperature and pressure
- Oil temperature with resettable min/max values and horizontal bar gauge
- Oil pressure with horizontal bar gauge 

Can be powered by USB or 12V from fuse box.

![eee](https://i.imgur.com/RDA9ZbF.jpeg)

## Things Learned
The ESP32 built-in ADC is less than stellar in that it's not fully linear, especially at the extreme ends. My temp readings were accurate around ~50-150F but past about 200F the readings were way off. This was measured when I had the real Adafruit ESP32 and not the clone, which I'm guessing the clone is the same or worse. So use an external ADC if you need a truly linear ADC. One thing to note is that while the ADS115 is a 16-bit ADC you really only get 15 bits of resolution since you get a signed int back. I assumed it was 16 bit unsigned for positive voltages only but that is not the case. On top of that you have to sacrifice some resolution when setting the gain on it because it cannot just read between 0-5v. The closest gain settings are 0-4.1v and 0-6.1v so you need to pick 0-6.1v and accept some loss of resolution knowing you can't read past 5v anyway. Anyway, none of this really matters because super high precision is not needed for this and it's still better than the ESP32's ADC. Just stuff I learned after buying it instead of reading about what I was actually buying beforehand (datasheets can be scary ya know?).

I2C does not like longer runs of ethernet cables. 1' is fine, 3' works but is not exactly happy unless you do some jank things and I needed a 3' cable. If I did this again I would add a LTC4311 but I did not realize any of this until after I soldered a bunch of stuff together and tried it. I am too lazy to do it all over again proper since my protoboard is full, so I settled with a workaround.

*\*disclaimer: i am by no means any sort of EE, in fact i barely know what i'm doing*

## Long Term Goals
The Android project is a leftover from when I had a grander idea of using a CAN transceiver to read data off the ECU (like what the COBB Accessport does) and combine it with my own sensors then ship it to the Android app over BT. From there the Android app would display it and have its own alerting system built in. This is so that ideally you never have to look at your gauges at all and instead they will yell at you when somethings wrong. That way you never have to take your eyes off the road and you can focus on not flying off a cliff.

The problems with this (for me) are three things:
1. Maybe I want to fly off the cliff?
1. I need to find all the CAN message IDs I want and what they mean. I would imagine things like RPM, coolant temp, and other common OBDII parameters are probably published somewhere. But the bigger problem is...
1. I want this to also work with the COBB flex fuel kit I have for my Subaru. Somehow the COBB Accessport is able to read the ethanol content and fuel pressure sensor data over the OBDII port but I have no idea if this is over CAN or some other protocol since their stuff is all proprietary. I tried asking them but it's all a secret.

I could probably find all these things with enough time, but that is going to take a lot of time. Maybe One Day™. 