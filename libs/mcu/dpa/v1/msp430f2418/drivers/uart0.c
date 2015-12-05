/**
\brief GNODE-specific definition of the "uart0" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
*author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/

#include "board.h"
#include "uart0.h"

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
   // reset local variables
   memset(&uart0_vars,0,sizeof(uart0_vars_t));

   //initialize UART openserial_vars.mode
   P3SEL    |=  0x30;                             // P3.4,5 = USCI_A0 TXD/RXD
   UCA0CTL1 |=  UCSSEL_2;                         // CLK = SMCL
   UCA0BR0   =  0x45;                             // 115200 baud if SMCLK@8MHz
   UCA0BR1   =  0x00;
   UCA0MCTL  =  UCBRS_7;                          // Modulation UCBRSx = 7
   UCA0CTL1 &= ~UCSWRST;                          // Initialize USCI state machine
   IFG2   &= ~(UCA0TXIFG | UCA0RXIFG);            // clear possible pending interrupts
   IE2    |=  (UCA0RXIE  | UCA0TXIE);             // Enable USCI_A1 TX & RX interrupt
}

void uart0_setCallbacks(uart0_tx_cbt txCb, uart0_rx_cbt rxCb) {
   uart0_vars.txCb = txCb;
   uart0_vars.rxCb = rxCb;
}

void    uart0_enableInterrupts(){
  IE2    |=  (UCA0RXIE  | UCA0TXIE);
}

void    uart0_disableInterrupts(){
  IE2    &= ~(UCA0RXIE | UCA0TXIE);
}

void    uart0_clearRxInterrupts(){
  IFG2   &= ~(UCA0RXIFG);
}

void    uart0_clearTxInterrupts(){
  IFG2   &= ~(UCA0TXIFG);
}

void    uart0_writeByte(uint8_t byteToWrite){
  UCA0TXBUF = byteToWrite;
}

uint8_t uart0_readByte(){
  return UCA0RXBUF;
}

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
