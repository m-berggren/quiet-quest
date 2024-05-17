# QUIET QUEST

The Quiet Quest system is a productivity tool consisting of two main components: a box device fitted with a microcontroller and sensors, and a desktop application for task management. The box device is used to store the user’s mobile phone while they perform “quests” that they have specified in the application. On approaching or opening the box during an ongoing quest, it will alert the user with audio and light signals as a reminder that they are on a Quiet Quest. 

This is the perfect project to try out for anyone who wants to learn about using microcontrollers, sensors and connectivity between hardware components and a software application. The project also makes use of a number of tools and concepts worth getting familiar with.

The GUI is created with **JavaFX** and **Scenebuilder**. For anyone already somewhat familiar with object-oriented programming using Java, this is a nice addition to your tool kit. 

**Connectivity** between the hardware components and the software application is achieved by using **HiveMQ** as a broker, implementing the **publish-subscribe** architectural pattern. 

To be able to save user data we have set up a database with **PostgreSQL**, using **Docker** for the setup.

To ensure a stable and consistent build, we use **Gradle** for automated build and have incorporated a **CI Pipeline**. 

# Getting Started
## Installation
- [Arduino IDE](https://www.arduino.cc/en/software)
- Your IDE of choice for Java projects (we use [intelliJ](https://www.jetbrains.com/idea/) and [VSCode](https://code.visualstudio.com/))
- JavaFX SDK | [How to install and setup for intelliJ](https://www.youtube.com/watch?v=Ope4icw6bVk) (Bro Code, YouTube)
- SceneBuilder | [How to install SceneBuilder](https://www.youtube.com/watch?v=-Obxf6NjnbQ&t=239s) (Bro Code, YouTube)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
    - Open Docker Desktop
    - In the terminal: cd into _app/docker/_
    - Enter 'docker-compose up'
- [Gradle](https://gradle.org/install/) (do this _after_ installing and setting up Docker)
    - After installing Gradle, find _quietquest/app/quietquest/build.gradle_ in your project files in the IDE
    - Right-click ‘build.gradle’ and choose ‘link gradle project’
    - Run Quiet Quest from the terminal:
        - cd into _quietquest/app/quietquest/_
        - Enter ‘./gradlew run’ (MacOS)  or ‘gradlew run’ (Windows)


## Libraries

### External download and installation:
- [Ultrasonic Ranger Sensor](https://github.com/Seeed-Studio/Seeed_Arduino_UltrasonicRanger) - v1.0.3 by Seed Studio
- [ChainableLED](https://github.com/pjpmarques/ChainableLED) - v1.3 by pjpmarques
- [Seeed Arduino LCD](https://github.com/Seeed-Studio/Seeed_Arduino_LCD) - v2.2.6 by Seeed Studio
    - note: fork from TFT_eSPI
- [Basic Color RGB Control](https://github.com/1ux/LED_RGB_Control)

### Internal installation through Ardunio IDE:
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

## Wio Terminal
Follow the pinout diagrams below when connecting the sensors to the mictrocontroller. 

![Wio Terminal Pinout Diagrams](https://git.chalmers.se/courses/dit113/2024/group-12/quiet-quest/-/raw/main/docs/wio_terminal_pinout.png?ref_type=heads)

1. I2C port:
    - [Grove - I2C Hub](https://wiki.seeedstudio.com/Grove-I2C_Hub/)
    - [Grove - Mini PIR Motion Sensor](https://www.seeedstudio.com/Grove-mini-PIR-motion-sensor-p-2930.html)
    - [Grove - Ultrasonic Ranger](https://wiki.seeedstudio.com/Grove-Ultrasonic_Ranger/)
2. Digital port (D0):
    - [Grove - Light Sensor v1.2](https://wiki.seeedstudio.com/Grove-Light_Sensor/)
3. & 4:
    - [Grove - Chainable RGB Led v2.0](https://wiki.seeedstudio.com/Grove-Chainable_RGB_LED/):
        -  Yellow cable into pin 16, D2
        - White cable into pin 18, D3
        - Black cable into pin 6, GND
        - Red cable into pin 4, 5V


# System Design
![EER Model](docs/db_entity_relations_diagram.png)
_EER model of database_

![System Design](https://git.chalmers.se/courses/dit113/2024/group-12/quiet-quest/-/wikis/uploads/891eb6c9b146f735768f63d7cc882bcc/System_Architecture-Quiet_Quest_1.0.drawio.png)
_System Architecture_

# How To Use
## Wio Terminal Usage
To power on the Wio Terminal, first ensure that it is conneced to a power source via a USB-C cable, then flick the power switch found on the left side of the terminal. Once powered on, the Wio Terminal screen will light up and show text about WiFi connectivity, MQTT connectivity, and sensor information. There is no actual information displayed until both WiFi and MQTT broker connection have been established. Once both WiFi and MQTT connection are established, all sensor input information will appear on the LCD screen of the Wio Terminal. Real-time MQTT connection and sensor information is displayed on the Quest page in the QuietQuest application as well.

## Sensor Ranges
While all connectivity is established and a quest is running, the following ranges are used to determine phone-collection attempts. The Wio Terminal alerts the user via various sounds and LED light outputs if any of the sensor ranges are entered.

### Grove Ultrasonic Ranger
The sensor is located at the front of the box, therefore is measures objects' distance that are placed in front of the box. The user is said to be too close to the box if an object is detected within 15 cm of the sensor. The user is considered to be slightly close if there is an object detected within 50 cm of the sensor. If the measured distance is more than 50 cm, the user is considered to be far enough from the box.

### Grove Light Sensor v1.2
The box is considered to be open once the light value inside the box exceeds 15 cm.

## Establish Wifi Connection
Connection to WiFi is necessary for using the Wio Terminal. To establish WiFi connectivity, the correct WiFi credentials (SSID and password) must be updated in the credentials.cpp file.

## Establish MQTT Connectivity
MQTT connection is crucial for bidirectionally transmitting data between the Wio Terminal and the QuietQuest application. To establish connectivity, the Wio Terminal must be powered on and the QuietQuest application must be running. On the Quest page inside the application, once the START button is clicked, the Wio Terminal automatically connects to the MQTT broker. Once the COMPLETE button is clicked (indicating quest completion), the Wio Terminal automatically disconnects from the MQTT broker.

## Restarting and Powering Off the Wio Terminal
To restart the Wio Terminal, flick the power switch quickly towards the right. To power off the Wio Terminal, fully flick the power switch towards the left. Both restarting and powering off disconnects from WiFi and the MQTT broker.

# Team
| ![Julia](https://git.chalmers.se/courses/dit113/2024/group-12/quiet-quest/-/wikis/uploads/d1d7dc1a2d40aab2a0f404f12d61a51c/julia-colored.png) | ![Lian](https://git.chalmers.se/courses/dit113/2024/group-12/quiet-quest/-/wikis/uploads/7a9d076e4b6ae470d153f80fe05b2e78/lian-colored.png) | ![Marcus](https://git.chalmers.se/courses/dit113/2024/group-12/quiet-quest/-/wikis/uploads/c1480a771c01b8b9eb96ec278b2069dc/marcus-colored.png) | ![Tanya](https://git.chalmers.se/courses/dit113/2024/group-12/quiet-quest/-/wikis/uploads/d704828e4d7e3731a14fdad1eab3705c/tanya-colored.png) | ![Emma](https://git.chalmers.se/courses/dit113/2024/group-12/quiet-quest/-/wikis/uploads/34c3469451982ba86e6c2c0ff698caec/emma-colored_.png) |
| ------ | ------ | ------ | ------ | ------ |
| Julia McCall | Lian Shi | Marcus Berggren | Tanya Nordh | Emma Camén |
| Made significant contributions to the backend, UI development and gamification elements. | Made significant contributions to backend logic related to CI pipeline, MQTT broker and database. | Made significant contributions to code structure, especially relating to Arduino. | Made significant contributions to backend, UI development and audio. | Made significant contributions to the background music and visual artwork. |



