#include "Scanner.h"
#include "ServerComm.h"
#include "ProximitySensor.h"
#include "DoorControl.h"
#include <SPI.h>
#include <Ethernet.h>
#include <Servo.h>

#define MIN_DIST 16
#define TRASH_LEVEL_1
#define TRASH_LEVEL_2 20
#define TRASH_LEVEL_3 5

Scanner scanner;
ServerComm serverComm;
ProximitySensor prox_cerc(13, 12);
ProximitySensor prox_trash(11, 10);
DoorControl door;

bool trash_open = false;

void setup() {
  Serial.begin(9600);
  scanner.setup();
  serverComm.setup("192.168.77.92:8080");
  door.close();
  pinModo(38, OUTPUT);
  pinMode(39, INPUT);
  pinMode(44, OUTPUT);
  pinMode(45, INPUT);
  pinMode(50, OUTPUT);
  pinMode(51, INPUT);

}

void loop() {
  if(scanner.refresh()) {
    bool post_result = serverComm.sendId(scanner.get_barcode());
    if(post_result) {
      Serial.println("Sent");
    } else {
      Serial.println("Fail");
    }
  }
  if(prox_cerc.read() <= MIN_DIST && !trash_open) {
      door.open();
  }
  if(prox_cerc.read() > MIN_DIST && trash_open) {
      door.close();
  }
  if(!tras_open) {
      int level = 1;
      int trash = prox_trash.read();
      if(trash < LEVEL_3) {
          level = 3;
      } else if(trash < LEVEL_2) {
          level = 2;
      }

  }
  delay(50);
}
