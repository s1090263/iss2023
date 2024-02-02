import RPi.GPIO as GPIO
import sys

GPIO.setmode(GPIO.BCM)
GPIO.setup(25,GPIO.OUT)
 
try:
	GPIO.output(25,GPIO.HIGH)
except:
	print("LedDevice | An exception occurred")	