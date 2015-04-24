/**
\brief TelosB-specific definition of the "debugpins" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
\author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/
#include <stdint.h>               // needed for uin8_t, uint16_t
#include "msp430f1611.h"
#include "debugpins.h"

//=========================== defines =========================================

//=========================== variables =======================================

//=========================== prototypes ======================================

//=========================== public ==========================================

void debugpins_init() {
   P6DIR |=  0x01;      // isr   [P6.0]
}

// P6.0
void debugpins_isr_toggle() {
   P6OUT ^=  0x01;
}
void debugpins_isr_clr() {
   P6OUT &= ~0x01;
}
void debugpins_isr_set() {
   P6OUT |=  0x01;
}

//=========================== private =========================================
