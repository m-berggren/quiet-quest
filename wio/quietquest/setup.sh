#!/usr/bin/env bash
export ARDUINO_LIBRARY_ENABLE_UNSAFE_INSTALL=true
arduino-cli core update-index --config-file .arduino-cli.yaml
arduino-cli core install Seeeduino:samd --config-file .arduino-cli.yaml
arduino-cli lib install 'Adafruit NeoMatrix ZeroDMA library@1.0.4'
arduino-cli lib install 'Seeed Arduino rpcWiFi@1.0.7'
arduino-cli lib install 'PubSubClient@2.8.0'
arduino-cli lib install --git-url 'https://github.com/Seeed-Studio/Seeed_Arduino_LCD#2.2.6'
arduino-cli lib install --git-url 'https://github.com/pjpmarques/ChainableLED#v1.3'
arduino-cli lib install --git-url 'https://github.com/Seeed-Studio/Seeed_Arduino_UltrasonicRanger#v1.0.3'
arduino-cli lib install --git-url 'https://github.com/1ux/LED_RGB_Control'
arduino-cli lib list
