#include <Arduino_FreeRTOS.h>
#include "HX711.h"
const int DOUT=A1;
const int CLK=A0;
int analogPin = A3;
int volatile val = 0;
float volatile valf=0;
int volatile batt=0;
HX711 balanza;
float volatile p=0;
void bat( void *pvParameters );
void peso( void *pvParameters );
void luces( void *pvParameters );
int frecBat=5000;
int frecPes=1000;


void setup(){
Serial.begin(9600);
balanza.begin(DOUT, CLK);
balanza.set_scale(439430.25); // Establecemos la escala
balanza.tare(20);  //El peso actual es considerado Tara.
pinMode(2,OUTPUT);
pinMode(4,OUTPUT);
pinMode(8,OUTPUT);
pinMode(10,OUTPUT);

xTaskCreate(
    peso
    ,  "peso"        // Nombre descriptivo de la función (MAX 8 caracteres)
    ,  128               // Tamaño necesario en memoria STACK
    ,  NULL              // Parámetro INICIAL a recibir (void *)
    ,  0                 // Prioridad, priridad = 3 (configMAX_PRIORITIES - 1) es la mayor, prioridad = 0 es la menor.
    ,  NULL );           // Variable que apunta al task (opcional)

  xTaskCreate(
    bat
    ,  "bateria"        // Nombre descriptivo de la función (MAX 8 caracteres)
    ,  128               // Tamaño necesario en memoria STACK
    ,  NULL              // Parámetro INICIAL a recibir (void *)
    ,  0                 // Prioridad, priridad = 3 (configMAX_PRIORITIES - 1) es la mayor, prioridad = 0 es la menor.
    ,  NULL );           // Variable que apunta al task (opcional)
  xTaskCreate(
    luces
    ,  "leds"        // Nombre descriptivo de la función (MAX 8 caracteres)
    ,  128               // Tamaño necesario en memoria STACK
    ,  NULL              // Parámetro INICIAL a recibir (void *)
    ,  0                 // Prioridad, priridad = 3 (configMAX_PRIORITIES - 1) es la mayor, prioridad = 0 es la menor.
    ,  NULL );           // Variable que apunta al task (opcional)
  }

  void loop(){
    
    }

  void bat(void *pvParameters){
    while(1){
      vTaskDelay(frecBat / portTICK_PERIOD_MS);
      
      Serial.println(" B,"+String(batt)); 
    }  
  }

  void peso(void *pvParameters){
    while(1){
      vTaskDelay(frecPes / portTICK_PERIOD_MS);
      
      Serial.println("P,"+String(p));
      
    }  
  }

  void luces(void *pvParameters){
    while(1){
      p=balanza.get_units(20),3;
      val = analogRead(analogPin);  // read the input pin
      valf=val*5.0f/1024.0f;
      batt=map(valf, 1.80f, 4.7f, 0, 100);
      if(p>0.268f){
        digitalWrite(8,HIGH);
         digitalWrite(10,LOW);  
      }
      if(p<=0.268f){
        digitalWrite(10,HIGH);
         digitalWrite(8,LOW);  
      }
       if(batt>=35){
        digitalWrite(2,HIGH);
         digitalWrite(4,LOW);  
      }
      if(batt<15){
        digitalWrite(4,HIGH);
         digitalWrite(2,LOW);  
      } 
      vTaskDelay(100 / portTICK_PERIOD_MS);
      
    }  
  }
