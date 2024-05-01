# Quiet Quest

## Description
Quiet Quest is a system that supports intentional breaks from the phone. The system is made up of two main parts: a desktop application and a box device. The application lets the user enter and keep track of tasks, set pomodoro style intervals, and provides usage statistics. The box device is where the phone is stored. If a task or timer is active when the box is approached, it will emit audio and visual signals to deter the user from collecting their phone.

## Visuals
Possibly include screenshots and other visuals to demonstrate QuietQuest.

## Installation
List specific installation steps that would be sufficient guidance for a novice to be able to use the project. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

### Wio Terminal
#### Pinout Diagrams
![Wio Terminal Pinout Diagrams](https://git.chalmers.se/courses/dit113/2024/group-12/quiet-quest/-/raw/main/docs/wio_terminal_pinout.png?ref_type=heads)

#### Connecting the sensors
1. I2C port, used by [Grove - I2C Hub](https://wiki.seeedstudio.com/Grove-I2C_Hub/)
    - [Grove - Mini PIR Motion Sensor](https://www.seeedstudio.com/Grove-mini-PIR-motion-sensor-p-2930.html)
    - [Grove - Ultrasonic Ranger](https://wiki.seeedstudio.com/Grove-Ultrasonic_Ranger/)
2. Digital port (D0), used by [Grove - Light Sensor v1.2](https://wiki.seeedstudio.com/Grove-Light_Sensor/)
3. & 4. [Grove - Chainable RGB Led v2.0](https://wiki.seeedstudio.com/Grove-Chainable_RGB_LED/)
    - Yellow cable into pin 16, D2
    - White cable into pin 18, D3
    - Black cable into pin 6, GND
    - Red cable into pin 4, 5V

#### Libraries

##### External download and installation:
- [Ultrasonic Ranger Sensor](https://github.com/Seeed-Studio/Seeed_Arduino_UltrasonicRanger) - v1.0.3 by Seed Studio
- [ChainableLED](https://github.com/pjpmarques/ChainableLED) - v1.3 by pjpmarques
- [Seeed Arduino LCD](https://github.com/Seeed-Studio/Seeed_Arduino_LCD) - v2.2.6 by Seed Studio
    - note: fork from TFT_eSPI

##### Internal installation through Ardunio IDE:
- [Adafruit_ZeroDMA](https://github.com/adafruit/Adafruit_NeoMatrix_ZeroDMA) - v1.04 by Adafruit <br>
    **Dependencies:** installed by IDE
    | Library                      | Version | 
    | ---------------------------- | ------- |
    | Adafruit BusIO               | v1.16.0 |
    | Adafruit DMA neopixel library| v1.3.3  |
    | Adafruit GFX Library         | v1.11.9 |
    | Adafruit NeoPixel            | v1.12.1 |
    | Adafruit Zero DMA Library    | v1.1.3  |

- [Seed Arduino rpcWiFi](https://github.com/Seeed-Studio/Seeed_Arduino_rpcWiFi) - v1.0.7 by Seed Studio <br>
    **Dependencies:** installed by IDE
    | Library                      | Version |
    | ---------------------------- | ------- |
    | Seeed Arduino FS             | v2.1.1  |
    | Seeed Arduino SFUD           | v2.0.2  |
    | Seeed Arduino rpcUnified     | v2.1.4  |
    | Seeed_Arduino_mbedtls        | v3.0.1  |
    
- [PubSubClient](https://github.com/knolleary/pubsubclient) - v2.8 by Nick O'Leary

### Desktop Application

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.
### Wio Terminal
### Desktop Application

## System Design
Include the system design diagram, possibly the class diagram as well.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.
