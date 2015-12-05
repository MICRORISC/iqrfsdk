/**
\brief TelosB-specific definition of the "leds" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
\author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/

#include "msp430f1611.h"
#include "leds.h"

//=========================== defines =========================================

//=========================== variables =======================================

//=========================== prototypes ======================================

//=========================== public ==========================================

void    leds_init() {
   P5DIR     |=  0x70;                           // P5DIR = 0bx111xxxx for LEDs
   P5OUT     |=  0x70;                           // P2OUT = 0bx111xxxx, all LEDs off
}

// red = LED1 = P5.4
void    leds_red_on() {
   P5OUT     &= ~0x10;
}
void    leds_red_off() {
   P5OUT     |=  0x10;
}
void    leds_red_toggle() {
   P5OUT     ^=  0x10;
}
uint8_t leds_red_isOn() {
   return (uint8_t)(~P5OUT & 0x10)>>4;
}
void leds_red_blink() {
   uint8_t i;
   volatile uint16_t delay;
   // turn all LEDs off
   P5OUT     |=  0x70;
   
   // blink error LED for ~10s
   for (i=0;i<80;i++) {
      P5OUT     ^=  0x10;
      for (delay=0xffff;delay>0;delay--);
   }
}

// green = LED2 = P5.5
void    leds_green_on() {
   P5OUT     &= ~0x20;
}
void    leds_green_off() {
   P5OUT     |=  0x20;
}
void    leds_green_toggle() {
   P5OUT     ^=  0x20;
}
uint8_t leds_green_isOn() {
   return (uint8_t)(~P5OUT & 0x20)>>5;
}

// blue = LED3 = P5.6
void    leds_blue_on() {
   P5OUT     &= ~0x40;
}
void    leds_blue_off() {
   P5OUT     |=  0x40;
}
void    leds_blue_toggle() {
   P5OUT     ^=  0x40;
}
uint8_t leds_blue_isOn() {
   return (uint8_t)(~P5OUT & 0x40)>>6;
}

//all
void    leds_all_on() {
   P5OUT     &= ~0x70;
}
void    leds_all_off() {
   P5OUT     |=  0x70;
}
void    leds_all_toggle() {
   P5OUT     ^=  0x70;
}

void    leds_circular_shift() {
   uint8_t leds_on;
   // get LED state
   leds_on  = (~P5OUT & 0x70) >> 4;
   // modify LED state
   if (leds_on==0) {                             // if no LEDs on, switch on one
      leds_on = 0x01;
   } else {
      leds_on <<= 1;                             // shift by one position
      if ((leds_on & 0x08)!=0) {
         leds_on &= ~0x08;
         leds_on |=  0x01;                       // handle overflow
      }
   }
   // apply updated LED state
   leds_on <<= 4;                                // send back to position 4
   P5OUT |=  (~leds_on & 0x70);                  // switch on the leds marked '1' in leds_on
   P5OUT &= ~( leds_on & 0x70);                  // switch off the leds marked '0' in leds_on
}

void    leds_increment() {
   uint8_t leds_on;
   // get LED state
   leds_on  = (~P5OUT & 0x70) >> 4;
   // modify LED state
   if (leds_on==0) {                             // if no LEDs on, switch on one
      leds_on = 0x01;
   } else {
      leds_on += 1;
   }
   // apply updated LED state
   leds_on <<= 4;                                // send back to position 4
   P5OUT |=  (~leds_on & 0x70);                  // switch on the leds marked '1' in leds_on
   P5OUT &= ~( leds_on & 0x70);                  // switch off the leds marked '0' in leds_on
}

//=========================== private =========================================
