#include <Arduino.h>
#include <avr/wdt.h>
#include <math.h>

char deviceEvent[30]="";
const int TEMPPIN = 0;
const int LEDPIN = 13;
const int BUZZPIN = 3;
int B=3975; //B value of the thermistor

void setup() {
  pinMode(BUZZPIN,OUTPUT);
  pinMode(LEDPIN,OUTPUT);
  Serial.begin(9600);
}

void loop() {
  if (Serial.available()) {
    //int value = Serial.parseInt();
    int value = Serial.read();
    //Serial.println(value);
    if(value == 1) {
      buzz();
      //Serial.println("Buzz Once");
    } else if(value == 2) {
      blinkLED();
      //Serial.println("Buzz 2 times");
    } else if(value == 3) {
      //Serial.println("Blink LED Once");
      blinkLED();
      //Serial.println("Call SoftReboot");
      reboot();
    } else {
      Serial.println(value);
    }
 } 
  
  strcpy(deviceEvent, "");
  char val[10];
  strcat(deviceEvent,"status temp:");
  dtostrf(getTemp(),1,2, val);
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

void buzz(){
    digitalWrite(BUZZPIN, HIGH);
    delay(100);
    digitalWrite(BUZZPIN, LOW);
    delay(100);
}

void blinkLED(void){
  digitalWrite(LEDPIN, HIGH);
  delay(1000);              
  digitalWrite(LEDPIN, LOW);
  delay(1000); 
}

void reboot(void) {
  buzz();
  wdt_enable(WDTO_15MS); // turn on the WatchDog and wait.
  for(;;) { 
    // do nothing and wait for the eventual...
  } 
}
