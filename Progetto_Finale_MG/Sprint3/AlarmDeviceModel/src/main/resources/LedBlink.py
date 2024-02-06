import RPi.GPIO as GPIO
import sys
import time

GPIO.setmode(GPIO.BCM)
GPIO.setup(25,GPIO.OUT)
 
try:
	while True:
		GPIO.output(25,GPIO.HIGH)
		time.sleep(0.5)
		GPIO.output(25,GPIO.LOW)
		time.sleep(0.5)
except:
	print("LedDevice | An exception occurred")