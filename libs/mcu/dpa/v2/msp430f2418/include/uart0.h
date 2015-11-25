#ifndef __UART0_H
#define __UART0_H

/**
\addtogroup BSP
\{
\addtogroup uart
\{

\brief Cross-platform declaration "uart" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
*author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/

#include <stdint.h>
#include "board.h"
 
//=========================== define ==========================================

//=========================== typedef =========================================

typedef enum {
   UART0_EVENT_THRES,
   UART0_EVENT_OVERFLOW,
} uart0_event_t;

typedef void (*uart0_tx_cbt)(void);
typedef void (*uart0_rx_cbt)(void);

//=========================== variables =======================================

//=========================== prototypes ======================================

void    uart0_init(void);
void    uart0_setCallbacks(uart0_tx_cbt txCb, uart0_rx_cbt rxCb);
void    uart0_enableInterrupts(void);
void    uart0_disableInterrupts(void);
void    uart0_clearRxInterrupts(void);
void    uart0_clearTxInterrupts(void);
void    uart0_writeByte(uint8_t byteToWrite);
uint8_t uart0_readByte(void);

// interrupt handlers
kick_scheduler_t uart0_tx_isr(void);
kick_scheduler_t uart0_rx_isr(void);

/**
\}
\}
*/

#endif
