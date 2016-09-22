#include <Arduino.h>
#include <avr/wdt.h>
#include <math.h>

char deviceEvent[30]="";
const int TEMPPIN = 0;
const int MOSPIN = 2;
const int BUZZPIN = 3;
int B=3975; //B value of the thermistor

void setup() {
  pinMode(BUZZPIN,OUTPUT);
  Serial.begin(9600);
}

void loop() {
  if (Serial.available()) {
    int value = Serial.parseInt();
    if(value == 101) {
      buzz(1);
    } else if(value == 105) {
      buzz(5);
    } else if(value == 110) {
      buzz(10);
    } else if(value == 115) {
      buzz(3);
      reboot();
    } else {
      buzz(1);
    }
 } 
  
  strcpy(deviceEvent, "");
  char val[10];
  strcat(deviceEvent,"status temp:");
  dtostrf(getTemp(),1,2, val);
  strcat(deviceEvent,val);
  strcat(deviceEvent,",mois:");
  int sensorValue = analogRead(MOSPIN);
  itoa(sensorValue/10, val, 10);
  strcat(deviceEvent,val);
  
  Serial.println(deviceEvent);
  delay(1000);
}

double getTemp(void) {
  double temperature;
  int a = analogRead(TEMPPIN);
  float resistance=(float)(1023-a)*10000/a; //get the resistance of the sensor;
  temperature=1/(log(resistance/10000)/B+1/298.15)-273.15;//convert to temperature via datasheet&nbsp;;
  return temperature;
}

void buzz(int n){
  for (int i = 0; i < n; i++) {
    digitalWrite(BUZZPIN, HIGH);
    delay(100);
    digitalWrite(BUZZPIN, LOW);
    delay(100);
  }
}

void reboot(void) {
  wdt_enable(WDTO_15MS); // turn on the WatchDog and wait.
  for(;;) { 
    // do nothing and wait for the eventual...
  } 
}
