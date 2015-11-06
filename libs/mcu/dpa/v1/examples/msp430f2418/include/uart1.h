#ifndef __UART1_H
#define __UART1_H

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
   UART1_EVENT_THRES,
   UART1_EVENT_OVERFLOW,
} uart1_event_t;

typedef void (*uart1_tx_cbt)(void);
typedef void (*uart1_rx_cbt)(void);

//=========================== variables =======================================

//=========================== prototypes ======================================

void    uart1_init(void);
void    uart1_setCallbacks(uart1_tx_cbt txCb, uart1_rx_cbt rxCb);
void    uart1_enableInterrupts(void);
void    uart1_disableInterrupts(void);
void    uart1_clearRxInterrupts(void);
void    uart1_clearTxInterrupts(void);
void    uart1_writeByte(uint8_t byteToWrite);
uint8_t uart1_readByte(void);

// interrupt handlers
kick_scheduler_t uart1_tx_isr(void);
kick_scheduler_t uart1_rx_isr(void);

/**
\}
\}
*/

#endif
