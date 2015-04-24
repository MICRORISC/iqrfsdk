/**
\brief TelosB-specific definition of the "uart" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
\author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/

#include "msp430f1611.h"
#include "uart0.h"
#include "board.h"

//=========================== defines =========================================

//=========================== variables =======================================

typedef struct {
   uart0_tx_cbt txCb;
   uart0_rx_cbt rxCb;
} uart0_vars_t;

uart0_vars_t uart0_vars;

//=========================== prototypes ======================================

//=========================== public ==========================================

void uart0_init() {
   P3SEL                    |=  0x30;            // P3.4,5 = UART0TX/RX
   
   UCTL0                     =  SWRST;           // hold UART0 module in reset
   UCTL0                    |=  CHAR;            // 8-bit character
   
   //  9600 baud, clocked from 32kHz ACLK
   UTCTL0                   |=  SSEL0;           // clocking from ACLK
   UBR00                     =  0x03;            // 32768/9600 = 3.41
   UBR10                     =  0x00;            //
   UMCTL0                    =  0x4A;            // modulation

   /*
   // 57600 baud, clocked from 4.8MHz SMCLK
   UTCTL0                   |=  SSEL1;           // clocking from SMCLK
   UBR00                     =  83;              // 4.8MHz/57600 - 83.33
   UBR10                     =  0x00;            //
   UMCTL0                    =  0x4A;            // modulation
   */
   
   /*
   // 115200 baud, clocked from 4.8MHz SMCLK
   UTCTL0                   |=  SSEL1;           // clocking from SMCLK
   UBR00                     =  41;              // 4.8MHz/115200 - 41.66
   UBR10                     =  0x00;            //
   UMCTL0                    =  0x4A;            // modulation
   */
   
   ME1                      |=  UTXE0 + URXE0;   // enable UART0 TX/RX
   UCTL0                    &= ~SWRST;           // clear UART0 reset bit
}

void uart0_setCallbacks(uart0_tx_cbt txCb, uart0_rx_cbt rxCb) {
   uart0_vars.txCb = txCb;
   uart0_vars.rxCb = rxCb;
}

void    uart0_enableInterrupts(){
  IE1 |=  (URXIE0 | UTXIE0);
}

void    uart0_disableInterrupts(){
  IE1 &= ~(URXIE0 | UTXIE0);
}

void    uart0_clearRxInterrupts(){
  IFG1   &= ~URXIFG0;
}

void    uart0_clearTxInterrupts(){
  IFG1   &= ~UTXIFG0;
}

void    uart0_writeByte(uint8_t byteToWrite){
  U0TXBUF = byteToWrite;
}

uint8_t uart0_readByte(){
  return U0RXBUF;
}

//=========================== private =========================================

//=========================== interrupt handlers ==============================

kick_scheduler_t uart0_tx_isr() {
   uart0_clearTxInterrupts(); // TODO: do not clear, but disable when done
   uart0_vars.txCb();
   return DO_NOT_KICK_SCHEDULER;
}

kick_scheduler_t uart0_rx_isr() {
   uart0_clearRxInterrupts(); // TODO: do not clear, but disable when done
   uart0_vars.rxCb();
   return DO_NOT_KICK_SCHEDULER;
}
