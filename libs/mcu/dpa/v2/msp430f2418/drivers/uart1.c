/**
\brief GNODE-specific definition of the "uart" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
*author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/

#include "board.h"
#include "uart1.h"

//=========================== defines =========================================

//=========================== variables =======================================

typedef struct {
   uart1_tx_cbt txCb;
   uart1_rx_cbt rxCb;
} uart1_vars_t;

uart1_vars_t uart1_vars;

//=========================== prototypes ======================================

//=========================== public ==========================================

void uart1_init() {
   // reset local variables
   memset(&uart1_vars,0,sizeof(uart1_vars_t));

   //initialize UART openserial_vars.mode
   P3SEL    |=  0xC0;                             // P3.6,7 = USCI_A1 TXD/RXD

   //UCA1CTL1 |=  UCSSEL_2;                         // CLK = SMCL
   //UCA1BR0   =  0x41;                             // 9600 baud if SMCLK@8MHz
   //UCA1BR1   =  0x03;
   //UCA1MCTL  =  UCBRS_2+UCBRF_0;                  // Modulation UCBRSx = 2

   UCA1CTL1 |= UCSSEL_1;                          // CLK = ACLK
   UCA1BR0 = 0x03;                                // 32kHz/9600 = 3.41
   UCA1BR1 = 0x00;
   UCA1MCTL = UCBRS1 + UCBRS0;                    // Modulation UCBRSx = 3

   UCA1CTL1 &= ~UCSWRST;                          // Initialize USCI state machine
   UC1IFG   &= ~(UCA1TXIFG | UCA1RXIFG);          // clear possible pending interrupts
   UC1IE    |=  (UCA1RXIE  | UCA1TXIE);           // Enable USCI_A1 TX & RX interrupt
}

void uart1_setCallbacks(uart1_tx_cbt txCb, uart1_rx_cbt rxCb) {
   uart1_vars.txCb = txCb;
   uart1_vars.rxCb = rxCb;
}

void    uart1_enableInterrupts(){
  UC1IE    |=  (UCA1RXIE  | UCA1TXIE);
}

void    uart1_disableInterrupts(){
  UC1IE &= ~(UCA1RXIE | UCA1TXIE);
}

void    uart1_clearRxInterrupts(){
  UC1IFG   &= ~(UCA1RXIFG);
}

void    uart1_clearTxInterrupts(){
  UC1IFG   &= ~(UCA1TXIFG);
}

void    uart1_writeByte(uint8_t byteToWrite){
  UCA1TXBUF = byteToWrite;
}

uint8_t uart1_readByte(){
  return UCA1RXBUF;
}

//=========================== interrupt handlers ==============================

kick_scheduler_t uart1_tx_isr() {
   uart1_clearTxInterrupts(); // TODO: do not clear, but disable when done
   uart1_vars.txCb();
   return DO_NOT_KICK_SCHEDULER;
}

kick_scheduler_t uart1_rx_isr() {
   uart1_clearRxInterrupts(); // TODO: do not clear, but disable when done
   uart1_vars.rxCb();
   return DO_NOT_KICK_SCHEDULER;
}
