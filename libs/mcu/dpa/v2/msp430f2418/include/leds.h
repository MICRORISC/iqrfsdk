#ifndef __LEDS_H
#define __LEDS_H

/**
\addtogroup BSP
\{
\addtogroup leds
\{

\brief Cross-platform declaration "leds" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
\author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/

#include <stdint.h>
 
//=========================== define ==========================================

#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

//=========================== typedef =========================================

//=========================== variables =======================================

//=========================== prototypes ======================================

void    leds_init(void);

void    leds_red_on(void);
void    leds_red_off(void);
void    leds_red_toggle(void);
uint8_t leds_red_isOn(void);
void    leds_red_blink(void);

void    leds_green_on(void);
void    leds_green_off(void);
void    leds_green_toggle(void);
uint8_t leds_green_isOn(void);

void    leds_yellow_on(void);
void    leds_yellow_off(void);
void    leds_yellow_toggle(void);
uint8_t leds_yellow_isOn(void);

void    leds_all_on(void);
void    leds_all_off(void);
void    leds_all_toggle(void);

void    leds_increment(void);

/**
\}
\}
*/

#endif
