/**
\brief GNODE-specific definition of the "board" bsp module.

\author Thomas Watteyne <watteyne@eecs.berkeley.edu>, February 2012.
\author Rostislav Spinar <rostislav.spinar@microrisc.com>, April 2015.
*/

#include "board.h"

// bsp modules
#include "leds.h"
#include "uart0.h"
#include "uart1.h"
#include "spi.h"
#include "bsp_timer.h"
#include "debugpins.h"

#define ISR_BUTTON 1

//=========================== variables =======================================

//=========================== prototypes ======================================

//=========================== main ============================================

extern int mote_main(void);

int main(void) {
   return mote_main();
}

//=========================== public ==========================================

void board_init() {
   // disable watchdog timer
   WDTCTL  = WDTPW + WDTHOLD;

   BCSCTL2 = SELM_0 | DIVM_0 | DIVS_0;

   // setup clock speed
   if ( CALBC1_8MHZ != 0xFF ) {

     __delay_cycles(5000);

     DCOCTL   = 0x00;
     BCSCTL1  = CALBC1_8MHZ;    // Set DCO to 8MHz
     DCOCTL   = CALDCO_8MHZ;

   }
   else { 			// Start using reasonable values at 8 Mhz

    __delay_cycles(5000);

     DCOCTL   = 0x00;
     BCSCTL1  = 0x8D;
     //BCSCTL1  = 0x0D;
     DCOCTL   = 0x88;
     //DCOCTL   = 0x9A;
   }

   //BCSCTL1 |= XT2OFF | DIVA_0;
   //BCSCTL3 = XT2S_0 | LFXT1S_0 | XCAP_1;

#ifdef ISR_BUTTON
   //p2.6 button
   P2DIR &= ~0x40; 	// Set P2.6 to output direction
   P2IE |= 0x40; 	// P2.6 interrupt enabled
   P2IES |= 0x40; 	// P2.6 Hi/lo edge
   P2IFG &= ~0x40; 	// P2.6 IFG cleared
#endif

   // initialize bsp modules
   debugpins_init();
   leds_init();
   uart0_init();
   uart1_init();
   spi_init();
   bsp_timer_init();

   // enable interrupts
   __bis_SR_register(GIE);
}

void board_sleep() {
   __bis_SR_register(GIE+LPM3_bits);          // sleep, but leave ACLK on
}

void board_reset() {
   WDTCTL = (WDTPW+0x1200) + WDTHOLD; 	      // writing a wrong watchdog password to causes handler to reset
}

//=========================== private =========================================

//=========================== interrupt handlers ==============================

ISR(USCIAB0TX) {
   debugpins_isr_set();
   if ( (IFG2 & UCA0TXIFG) && (IE2 & UCA0TXIE) ){
      if (uart0_tx_isr()==KICK_SCHEDULER) {       // UART0: TX
         __bic_SR_register_on_exit(CPUOFF);
      }
   }
   debugpins_isr_clr();
}

ISR(USCIAB0RX) {
   debugpins_isr_set();
   if ( (IFG2 & UCA0RXIFG) && (IE2 & UCA0RXIE) ){
      if (uart0_rx_isr()==KICK_SCHEDULER) {       // UART0: RX
         __bic_SR_register_on_exit(CPUOFF);
      }
   }
   debugpins_isr_clr();
}

ISR(USCIAB1TX) {
   debugpins_isr_set();
   if ( (UC1IFG & UCA1TXIFG) && (UC1IE & UCA1TXIE) ){
      if (uart1_tx_isr()==KICK_SCHEDULER) {       // UART1: TX
         __bic_SR_register_on_exit(CPUOFF);
      }
   }
   debugpins_isr_clr();
}

ISR(USCIAB1RX) {
   debugpins_isr_set();
   if ( (UC1IFG & UCB1RXIFG) && (UC1IE & UCB1RXIE) ){
   	if(spi_isr()==KICK_SCHEDULER) {
	   __bic_SR_register_on_exit(CPUOFF);
	}
   }
   if ( (UC1IFG & UCA1RXIFG) && (UC1IE & UCA1RXIE) ){
      if (uart1_rx_isr()==KICK_SCHEDULER) {       // UART1: RX
         __bic_SR_register_on_exit(CPUOFF);
      }
   }
   debugpins_isr_clr();
}

ISR(TIMERB0) {
   debugpins_isr_set();
   if (bsp_timer_isr()==KICK_SCHEDULER) {        // TIMERB: 0
      __bic_SR_register_on_exit(CPUOFF);
   }
   debugpins_isr_clr();
}

ISR(PORT2) {
   debugpins_isr_set();
#ifdef ISR_BUTTON
   if ((P2IFG & 0x40)!=0) {                      // button: [P2.6]
      P2IFG &= ~0x40;
      //scheduler_push_task(ID_ISR_BUTTON);
      __bic_SR_register_on_exit(CPUOFF);
   } else {
      while (1); // should never happen
   }
   debugpins_isr_clr();
#else
   while(1); // should never happen
#endif
}

// PORT1_VECTOR

// DACDMA_VECTOR

// TIMERA0_VECTOR

// TIMERA1_VECTOR

// WDT_VECTOR

// COMPARATORA_VECTOR

// TIMERB1_VECTOR

// NMI_VECTOR
