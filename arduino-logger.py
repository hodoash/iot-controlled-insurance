#!/usr/bin/python


# Read a text file and send a signal to arduino via a serial connection

print ("\nPython Script prj201205-01.py loaded")
print ("Starting program")
print ("Importing libraries")
import time, os, re, urllib, json
# import msvcrt
import sys      # needed to end the program
import serial   # use pyserial to send commands to arduino
from datetime import datetime

print ("Loading constants")
DEBUGOUT="prj201205-01-debug.txt"
PORT="/dev/cu.usbserial-14340"#"COM3"     # This port will need to be adjusted to match your installation
TARGET="/Users/leftee/Documents/GitHub/iot controlled insurance/datasetForUse/carP2.json"
arduino = serial.Serial(PORT, 9600, timeout=.1)
#"C:/Node/DropBox/Dropbox/RemoteSignal/ArduinoCommand.txt"

print ("Loading variables")
sendvalue=0
prevsendvalue=0

print ("Creating definitions")

# Catalog - Keyboard Interrupt
print (" Load definition:  Keyboard interrupt")
def kbfunc():
    # Intent:  Used to interrupt continuous loops early
    # Dependencies:  msvcrt (Windows only)
    # Last updated:  2012-01-17
    # Not my work
    # Source:  http://stackoverflow.com/questions/292095/polling-the-keyboard-in-python
    # Author:  K. Brafford
    x = msvcrt.kbhit()
    if x:
        ret = ord(msvcrt.getch())
    else:
        ret = 0
    return ret

# Catalog 0002 - Debug Message
print (" Load definition:  Debug Message")
def debugmsg(message):
    # Writes a status message or variable value to a log file for debugging purposes
    # Dependencies:  None
    # Must be defined in master script:  DEBUGOUT (File name such as "testXX-debug.txt")
    # Updated 2012-01-06
    with open(DEBUGOUT, "a") as db:
        db.write("\n %s" % message)

# Program specific function
print (" Load definition:  Read command from text file")



# def readFromFile(FILE):
#     # Search a text file for the query and return the value
#     txtfile=open(FILE,"r")
#     count = 0
 
#     while True:
#         count += 1
    
#         # Get next line from file
#         line = txtfile.readline()
    
#         # if line is empty
#         # end of file is reached
#         if not line:
#             break
#         print("Line{}: {}".format(count, line.strip()))
        
    
#     file1.close()
    # text=txtfile.read()
    # txtfile.close()
    # index = text.find(QUERY)
    # querylength= len(QUERY)
    # querytarget= text[index+querylength:]
    #print QUERY, "found at index ", index+querylength
    #print "Target value is ", text[index+querylength]
    # return querytarget    

# Program specific function
print (" Load definition:  Send Command to Arduino")
def commandarduino(sendvalue):
    # Sends a 0-6 value to the arduino so that it can energize the correct number of LEDs
    print (" Sending Value: %s" % sendvalue)
    #ser.write(sendvalue)
    arduino.write(sendvalue)
    print("sent")
    time.sleep(2)
    
# Start communication with Arduino
print ("Starting serial port communications")
# ser = serial.Serial(PORT, 9600)
# time.sleep(3)

print ("Core Logic start")
print ("Starting command loop - Press 'Z' to end program")
sendvalue=""
endofline="}"
prevsendvalue="".encode('utf-8')
while 1==1:
     # data = []
     with open(TARGET) as f:
        for line in f:
            commandarduino(line.encode('utf-8'))
            #  #COLLECT THE LINE. //////////////////////////////////////
            #  #ADD TO VARIABLE
            #   #IF YOU FIND }, THEN
            #  #  STRIP NL FROM IT
            #  #  AND ADD NL 
            #  # AND SEND
            #  #ELSE KEEP COLLECTING ///////////////////////////////////
            #  #commandarduino(line.encode('utf-8'))
            # if endofline not in sendvalue:
            #     sendvalue=sendvalue+line
            #     sendvalue=sendvalue.strip("\n\t")
            #     # sendvalue=sendvalue.rstrip()#strip("\n")
            # else:   
            #     #sendvalue=sendvalue+"\n"
            #     #line=line+"advsdfbvabfabb abafbfbadfb afbaefrbarbsrbrstbrtabatbtatbtab tabattagrrtabtafbafvfa bvrfbvaertbvarebvftbatfgbatb atrbatrbatbarbatbtabaabbtb ababatbtabtababtabr tabartbrtabartbartbtarbtrb srtbtabrtbgtatbgab"
            #     #commandarduino(line.encode('utf-8'))
            #     commandarduino(sendvalue.encode('utf-8'))
                
            
#             #data.append(json.loads(line))
#             #print(line)
#             sendvalue=line.encode('utf-8') #json.loads(line)#line.strip()
#         print (" sending data :" )
#         print(sendvalue)
#         if sendvalue!=prevsendvalue:
#             commandarduino(sendvalue)
#             print("sent") ///////////////////////////////
        # sleepiter=0
        # while sleepiter<1:
        #     time.sleep(1)
        #     # X=kbfunc()  # This allows the user to push Z to exit
        #     if X==122:
        #         sys.exit()
        #     sleepiter=sleepiter+1

    # txtfile=open(FILE,"r")
    # count = 0
 
    # while True:
    #     count += 1
    
    #     # Get next line from file
    #     line = txtfile.readline()
    
    #     # if line is empty
    #     # end of file is reached
    #     if not line:
    #         break
        #print("Line{}: {}".format(count, line.strip()))
        
    
    #file1.close()
    #sendvalue=int(readFromFile(TARGET))
    #print " New value is %s" % sendvalue
    #if sendvalue!=prevsendvalue:
     #   commandarduino(sendvalue)
    #prevsendvalue=sendvalue
    #sleepiter=0
    #while sleepiter<1:
     #   time.sleep(1)
      #  X=kbfunc()  # This allows the user to push Z to exit
       # if X==122:
        #    sys.exit()
        #sleepiter=sleepiter+1
    
print ("Ending Program\n")
debugmsg("Normal End of Program")