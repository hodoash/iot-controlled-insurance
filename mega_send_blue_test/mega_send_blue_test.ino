#include <SoftwareSerial.h>
 
SoftwareSerial SerialBT(0,1); // bluetooth module connected here 
 
//int Vresistor = A0; 
//int Vdata = 0; 
 
void setup(){ 
  
   //pinMode(Vresistor, INPUT); 
   SerialBT.begin(9600); 
   Serial.begin(9600); 
   
} 
void loop(){ 
if(Serial.available() > 0) {
    char data = Serial.read();
    char str[2];
    str[0] = data;
    str[1] = '\0';
    Serial.print(str);
  }
//Serial.println("testing space/34");
//Serial.write("test");

//delay(2000); 

}
