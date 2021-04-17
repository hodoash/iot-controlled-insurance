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
//checkInput()
//test to see if it sends data from phone back to phone
//if it sends back to phone:
//  write code to check if it starts with the name or ends with 'on' or 'off'
//    if it does, then filtercode()

//filtercode(input)
// set data to var
//take the data that starts with {
//if var is off,  change that data to 0 or null and send to phone
//if var i on, send data to phone


  
//Serial.println("testing space/34");
//Serial.write("test");

//delay(2000); 

}
