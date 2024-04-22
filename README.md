# Quiet Quest

## Description
Quiet Quest is a system that supports intentional breaks from the phone. The system is made up of two main parts: a desktop application and a box device. The application lets the user enter and keep track of tasks, set pomodoro style intervals, and provides usage statistics. The box device is where the phone is stored. If a task or timer is active when the box is approached, it will emit audio and visual signals to deter the user from collecting their phone.

## Visuals
Possibly include screenshots and other visuals to demonstrate QuietQuest.

## Installation
List specific installation steps that would be sufficient guidance for a novice to be able to use the project. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.
### Wio Terminal

![Wio Terminal Pinout Diagrams]((uploads/891eb6c9b146f735768f63d7cc882bcc/System_Architecture-Quiet_Quest_1.0.drawio.png))

#### Sensor guidance
1. Digital port, used as D0 by the [Grove - Light Sensor v1.2](https://wiki.seeedstudio.com/Grove-Light_Sensor/)
2. I2C port, used by [Grove - I2C Hub](https://wiki.seeedstudio.com/Grove-I2C_Hub/)
    - [Grove - Mini PIR Motion Sensor](https://www.seeedstudio.com/Grove-mini-PIR-motion-sensor-p-2930.html)
    - [Grove - Ultrasonic Ranger](https://wiki.seeedstudio.com/Grove-Ultrasonic_Ranger/)
3. [Grove - Chainable RGB Led v2.0](https://wiki.seeedstudio.com/Grove-Chainable_RGB_LED/)
    - Black cable into pin 6, GND
    - Red cable into pin 4, 5V
4. [Grove - Chainable RGB Led v2.0](https://wiki.seeedstudio.com/Grove-Chainable_RGB_LED/)
    - Yellow cable into pin 16, D2
    - White cable into pin 18, D3

### Desktop Application

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.
### Wio Terminal
### Desktop Application

## System Design
Include the system design diagram, possibly the class diagram as well.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.
