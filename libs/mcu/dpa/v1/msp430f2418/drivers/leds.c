/**
\brief TelosB-specific definition of the "leds" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
\author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/

#include <msp430f2418.h>
#include "leds.h"

//=========================== defines =========================================

//=========================== variables =======================================

//=========================== prototypes ======================================

//=========================== public ==========================================

void    leds_init() {
   P1DIR     |=  0x80;                            // P1DIR = 0b1xxxxxxx for LEDs
   P1OUT     &=  ~0x80;                           // P1OUT = 0b0xxxxxxx, LED off

   P2DIR     |=  0x03;                            // P2DIR = 0bxxxxxx11 for LEDs
   P2OUT     &=  ~0x03;                           // P2OUT = 0bxxxxxx00, all LEDs off
}

// red = LED1 = P2.1
void    leds_red_on() {
   P2OUT     |= 0x02;
}
void    leds_red_off() {
   P2OUT     &=  ~0x02;
}
void    leds_red_toggle() {
   P2OUT     ^=  0x02;
}
uint8_t leds_red_isOn() {
   return (uint8_t)(P2OUT & 0x02)>>1;
}
void leds_red_blink() {
   uint8_t i;
   volatile uint16_t delay;
   // turn all LEDs off
   P1OUT     &=  ~0x80;
   P2OUT     &=  ~0x03;

   // blink error LED for ~10s
   for (i=0;i<80;i++) {
      P2OUT     ^=  0x02;
      for (delay=0xffff;delay>0;delay--);
   }
}

// green = LED2 = P1.7
void    leds_green_on() {
   P1OUT     |= 0x80;
}
void    leds_green_off() {
   P1OUT     &=  ~0x80;
}
void    leds_green_toggle() {
   P1OUT     ^=  0x80;
}
uint8_t leds_green_isOn() {
   return (uint8_t)(P1OUT & 0x80)>>7;
}

// yellow = LED3 = P2.0
void    leds_yellow_on() {
   P2OUT     |= 0x01;
}
void    leds_yellow_off() {
   P2OUT     &= ~0x01;
}
void    leds_yellow_toggle() {
   P2OUT     ^=  0x01;
}
uint8_t leds_yellow_isOn() {
   return (uint8_t)(P2OUT & 0x01);
}

//all
void    leds_all_on() {
   P1OUT     |=  0x80;
   P2OUT     |=  0x03;
}
void    leds_all_off() {
   P1OUT     &=  ~0x80;
   P2OUT     &=  ~0x03;
}
void    leds_all_toggle() {
   P1OUT     ^=  0x80;
   P2OUT     ^=  0x03;
}

void    leds_increment() {
   uint8_t leds_on;

   // get LED state
   leds_on = ((P2OUT & 0x03) << 1) + ((P1OUT & 0x80) >> 7);

   // modify LED state
   if (leds_on==0) {                             // if no LEDs on, switch on one
      leds_on = 0x01;
   } else {
      leds_on += 1;
   }

   // apply updated LED state
   P1OUT |=  ((leds_on << 7) & 0x80);            // switch on the leds marked '1' in leds_on
   P2OUT |=  ((leds_on >> 1) & 0x03);

   P1OUT &=  ~((leds_on << 7) & 0x80);
   P2OUT &=  ~((leds_on >> 1) & 0x03);           // switch off the leds marked '0' in leds_on
}

//=========================== private =========================================
