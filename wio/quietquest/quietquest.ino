#include <WiFi.h>
#include "rpcWiFi.h"
#include "TFT_eSPI.h"
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include "Ultrasonic.h"
#include <string.h>

#define PIR_MOTION_SENSOR D0
Ultrasonic ultrasonic(0);

TFT_eSPI tft;

//Update these values corresponding to your network
const char *ssid = "lianiphone";      //wifi network name
const char *password = "12345678";    //wifi network password


//MQTT server
const char *mqtt_server = "ee9e926915b64224a7bc895977db4ae9.s2.eu.hivemq.cloud";
const char *mqtt_username = "QuietQuest";
const char *mqtt_password = "Quietquest1";
const int mqtt_port = 8883;

WiFiClientSecure wifiClient;
PubSubClient client(wifiClient);


//Topics
const char *TOPIC = "QuietQuest";
const char *TOPIC_PUB_MOTION = "sensor/motion";
const char *TOPIC_PUB_DISTANCE  = "sensor/distance";
const char *TOPIC_SUB_QUEST = "terminal/quest";



unsigned long lastMsg = 0;
#define MSG_BUFFER_SIZE 50
char msg[MSG_BUFFER_SIZE];

/****** root certificate *********/

static const char *root_ca PROGMEM = R"EOF(
-----BEGIN CERTIFICATE-----
MIIFazCCA1OgAwIBAgIRAIIQz7DSQONZRGPgu2OCiwAwDQYJKoZIhvcNAQELBQAw
TzELMAkGA1UEBhMCVVMxKTAnBgNVBAoTIEludGVybmV0IFNlY3VyaXR5IFJlc2Vh
cmNoIEdyb3VwMRUwEwYDVQQDEwxJU1JHIFJvb3QgWDEwHhcNMTUwNjA0MTEwNDM4
WhcNMzUwNjA0MTEwNDM4WjBPMQswCQYDVQQGEwJVUzEpMCcGA1UEChMgSW50ZXJu
ZXQgU2VjdXJpdHkgUmVzZWFyY2ggR3JvdXAxFTATBgNVBAMTDElTUkcgUm9vdCBY
MTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAK3oJHP0FDfzm54rVygc
h77ct984kIxuPOZXoHj3dcKi/vVqbvYATyjb3miGbESTtrFj/RQSa78f0uoxmyF+
0TM8ukj13Xnfs7j/EvEhmkvBioZxaUpmZmyPfjxwv60pIgbz5MDmgK7iS4+3mX6U
A5/TR5d8mUgjU+g4rk8Kb4Mu0UlXjIB0ttov0DiNewNwIRt18jA8+o+u3dpjq+sW
T8KOEUt+zwvo/7V3LvSye0rgTBIlDHCNAymg4VMk7BPZ7hm/ELNKjD+Jo2FR3qyH
B5T0Y3HsLuJvW5iB4YlcNHlsdu87kGJ55tukmi8mxdAQ4Q7e2RCOFvu396j3x+UC
B5iPNgiV5+I3lg02dZ77DnKxHZu8A/lJBdiB3QW0KtZB6awBdpUKD9jf1b0SHzUv
KBds0pjBqAlkd25HN7rOrFleaJ1/ctaJxQZBKT5ZPt0m9STJEadao0xAH0ahmbWn
OlFuhjuefXKnEgV4We0+UXgVCwOPjdAvBbI+e0ocS3MFEvzG6uBQE3xDk3SzynTn
jh8BCNAw1FtxNrQHusEwMFxIt4I7mKZ9YIqioymCzLq9gwQbooMDQaHWBfEbwrbw
qHyGO0aoSCqI3Haadr8faqU9GY/rOPNk3sgrDQoo//fb4hVC1CLQJ13hef4Y53CI
rU7m2Ys6xt0nUW7/vGT1M0NPAgMBAAGjQjBAMA4GA1UdDwEB/wQEAwIBBjAPBgNV
HRMBAf8EBTADAQH/MB0GA1UdDgQWBBR5tFnme7bl5AFzgAiIyBpY9umbbjANBgkq
hkiG9w0BAQsFAAOCAgEAVR9YqbyyqFDQDLHYGmkgJykIrGF1XIpu+ILlaS/V9lZL
ubhzEFnTIZd+50xx+7LSYK05qAvqFyFWhfFQDlnrzuBZ6brJFe+GnY+EgPbk6ZGQ
3BebYhtF8GaV0nxvwuo77x/Py9auJ/GpsMiu/X1+mvoiBOv/2X/qkSsisRcOj/KK
NFtY2PwByVS5uCbMiogziUwthDyC3+6WVwW6LLv3xLfHTjuCvjHIInNzktHCgKQ5
ORAzI4JMPJ+GslWYHb4phowim57iaztXOoJwTdwJx4nLCgdNbOhdjsnvzqvHu7Ur
TkXWStAmzOVyyghqpZXjFaH3pO3JLF+l+/+sKAIuvtd7u+Nxe5AW0wdeRlN8NwdC
jNPElpzVmbUq4JUagEiuTDkHzsxHpFKVK7q4+63SM1N95R1NbdWhscdCb+ZAJzVc
oyi3B43njTOQ5yOf+1CceWxG1bQVs5ZufpsMljq4Ui0/1lvh+wjChP4kqKOJ2qxq
4RgqsahDYVvTH9w7jXbyLeiNdd8XM2w9U/t7y0Ff/9yi0GE44Za4rF2LN9d11TPA
mRGunUHBcnWEvgJBQl9nJEiU0Zsnvgc/ubhPgXRR4Xq37Z0j4r7g1SgEEzwxA57d
emyPxgcYxn/eR44/KJ4EBs+lVDR3veyJm+kXQ99b21/+jh5Xos1AnX5iItreGCc=
-----END CERTIFICATE-----
)EOF";

void callback(char *topic, byte *payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");

  char *txt = (char *) malloc(length + 1);
  memcpy(txt, payload, length);
  txt[length] = '\0';

  if (strcmp(topic, TOPIC_SUB_QUEST) == 0){
    clear(TFT_RED);
  }
  else {
    clear(TFT_PURPLE);
  }
  show(txt);
  free(txt);

  Serial.println();
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    //Create a random client ID
    String clientID = "ESP8266Client-";
    clientID += String(random(0xffff), HEX);
    //try to connect
    if (client.connect(clientID.c_str(), mqtt_username, mqtt_password)) {
      Serial.println("connected");
      client.publish(TOPIC, "{\"message\": \"Wio Terminal is connected\"}");
      Serial.println("Published connection message successfully!");

      Serial.print("Subscribed to: ");
      Serial.println(TOPIC_SUB_QUEST);

      client.subscribe(TOPIC_SUB_QUEST);


    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void clear(uint32_t color) {
  tft.fillScreen(color);
  tft.setTextColor(TFT_BLACK);
}

void show(char *text) {
  tft.setTextSize(2);
  tft.drawString(text, 30, 30);
}

void setup() {

  // initiate tft screen
  tft.begin();
  tft.setRotation(3);

  pinMode(PIR_MOTION_SENSOR, INPUT);
  Serial.begin(9600);

  //Serial.begin(115200);
  Serial.print("Attempting to connect to SSID:");
  Serial.println(ssid);
  WiFi.mode(WIFI_STA);
  delay(1000);
  WiFi.begin(ssid, password);
  clear(TFT_BLUE);

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print("Connecting to WiFi...");
    show("Not connected to WiFi");
    WiFi.begin(ssid, password);
    delay(1000);
  }
  clear(TFT_PURPLE);
  randomSeed(micros());
  Serial.print("Connected to ");
  Serial.println(ssid);
  show("Connected to WiFi");
  delay(500);


  wifiClient.setCACert(root_ca);

  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);


}

void loop() {
  if (!client.connected()) {
    reconnect();
  } else {
  client.loop();

  long RangeInCentimeters = ultrasonic.MeasureInCentimeters();
  char msgBuffer[50];
  sprintf(msgBuffer, "%ld", RangeInCentimeters);
  client.publish(TOPIC_PUB_DISTANCE, msgBuffer);



  Serial.printf("The distance to obstacles in front is: %ld cm\n", RangeInCentimeters);

  if (digitalRead(PIR_MOTION_SENSOR)) {

    client.publish(TOPIC_PUB_MOTION,"Hi people are coming");
    Serial.println("Hi people are coming");
  } else {

    client.publish(TOPIC_PUB_MOTION,"Sensor is watching");
    Serial.println("PIR Motion Sensor: Seonsor is watching");
  }


  delay(1000);
 }
}
