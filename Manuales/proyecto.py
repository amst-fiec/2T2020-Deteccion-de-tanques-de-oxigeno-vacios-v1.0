import serial
import pyrebase
from time import sleep

config = {     
  "apiKey": "AIzaSyB0d312jCisRGQPas0ytfGNTm3rh48_7gc",
  "authDomain": "tanquesoxigeno-d8464.firebaseapp.com",
  "databaseURL": "https://tanquesoxigeno-d8464-default-rtdb.firebaseio.com/",
  "storageBucket": "tanquesoxigeno-d8464.appspot.com"
}
ser = serial.Serial('/dev/ttyACM0',9600)
firebase = pyrebase.initialize_app(config)
# initialisatiing pyrebase
firebase = pyrebase.initialize_app(config)

# initialisatiing Database
db = firebase.database()


try:
	while(True):
		if(ser.in_waiting>0):
			try:
				cadena = ser.readline().strip()
				lista=cadena.split(",")
				if lista[0]=="P":
                                	numero=abs(float(lista[1]))
					print "El peso es: "+ str(numero)
					if(numero>=0):
                                		db.child("alfombra1").child("Tanque").update({"peso": numero})
				if lista[0]=="B":
					bat=int(lista[1])
					if (bat>=0):
						print "Bateria en: "+str(bat)+"%"
						db.child("alfombra1").update({"bateria":bat})
			except(ValueError):
				print "formato no valido"
except(KeyboardInterrupt,SystemExit):
	print 'Cerrando...'
	ser.close()
