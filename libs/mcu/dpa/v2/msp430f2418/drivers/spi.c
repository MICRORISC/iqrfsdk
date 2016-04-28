/**
\brief gnode-specific definition of the "spi" bsp module.

\author Xavier Vilajosana <xvilajosana@eecs.berkeley.edu>, May 2013.
\author Rostislav Spinar <rostislav.spinar@microrisc.com>, June 2015.
*/

#include "spi.h"

//=========================== variables =======================================

typedef struct {
   // information about the current transaction
   uint8_t*        pNextTxByte;
   uint8_t         numTxedBytes;
   uint8_t         txBytesLeft;
   spi_return_t    returnType;
   uint8_t*        pNextRxByte;
   uint8_t         maxRxBytes;
   spi_first_t     isFirst;
   spi_last_t      isLast;
   // state of the module
   uint8_t         busy;
   // help
   uint8_t	   i;
#ifdef SPI_IN_INTERRUPT_MODE
   // callback when module done
   spi_cbt         callback;
#endif
} spi_vars_t;

spi_vars_t spi_vars;

//=========================== prototypes ======================================

//=========================== public ==========================================

void spi_init() {
   // clear variables
   memset(&spi_vars,0,sizeof(spi_vars_t));
   
   // hold USART state machine in reset mode during configuration
   UCB1CTL1   =  UCSWRST;                        // [b0] SWRST=1: Enabled. USART logic held in reset state
   
   // configure SPI-related pins
   MOSI_SEL  |=  MOSI_PIN;                       // MOSI mode
   MOSI_DIR  |=  MOSI_PIN;                       // MOSI output
   MISO_SEL  |=  MISO_PIN;                       // MISO mode
   MISO_DIR  &= ~MISO_PIN;                       // MISO output
   SCK_SEL   |=  SCK_PIN;                        // SCK  mode
   SCK_DIR   |=  SCK_PIN;                        // SCK  output 
   CS_OUT    |=  CS_PIN;                         // CS   hold high
   CS_DIR    |=  CS_PIN;                         // CS   output
   
   
   /* 
    * Control Register 0
    * 
    * UCCKPH -- Data is changed on the first UCLK edge and captured on the following edge
    * ~UCCKPL -- Inactive state is low
    * UCMSB -- MSB first
    * ~UC7BIT -- 8-bit
    * UCMST -- Master mode
    * UCMODE_0 -- 3-Pin SPI
    * UCSYNC -- Synchronous Mode
    * 
    * Note: ~<BIT> indicates that <BIT> has value zero
    */
   UCB1CTL0 = UCCKPH | UCMSB | UCMST | UCMODE_0 | UCSYNC;


   /* 
    * Control Register 1
    * 
    * UCSSEL_2 -- SMCLK
    * UCSWRST -- Enabled. USCI logic held in reset state
    */
   UCB1CTL1 = UCSSEL_2 | UCSWRST;

   UCB1BR0    =  0x20;                            // UCLK/32 ~ 250k
   UCB1BR1    =  0x00;                            // 0
   
   // clear USART state machine from reset, starting operation
   UCB1CTL1  &= ~UCSWRST;
   
   // enable interrupts via the IEx SFRs
#ifdef SPI_IN_INTERRUPT_MODE
   UC1IE     |=  UCB1RXIE;                       // we only enable the SPI RX interrupt
                                                 // since TX and RX happen concurrently,
                                                 // i.e. an RX completion necessarily
                                                 // implies a TX completion.
#endif
}

#ifdef SPI_IN_INTERRUPT_MODE
void spi_setCallback(spi_cbt cb) {
   spi_vars.callback = cb;
}
#endif

void spi_txrx(uint8_t*     bufTx,
              uint8_t      lenbufTx,
              spi_return_t returnType,
              uint8_t*     bufRx,
              uint8_t      maxLenBufRx,
              spi_first_t  isFirst,
              spi_last_t   isLast) {

#ifdef SPI_IN_INTERRUPT_MODE
   // disable interrupts
   __disable_interrupt();
#endif
   
   // register spi frame to send
   spi_vars.pNextTxByte      =  bufTx;
   spi_vars.numTxedBytes     =  0;
   spi_vars.txBytesLeft      =  lenbufTx;
   spi_vars.returnType       =  returnType;
   spi_vars.pNextRxByte      =  bufRx;
   spi_vars.maxRxBytes       =  maxLenBufRx;
   spi_vars.isFirst          =  isFirst;
   spi_vars.isLast           =  isLast;
   
   // SPI is now busy
   spi_vars.busy             =  1;
   
   // lower CS signal to have slave listening
   if (spi_vars.isFirst==SPI_FIRST) {

	CS_OUT                 &= ~CS_PIN;
       	
	// delay 1/8MHz = 125ns * 8 = 1us
 	for(spi_vars.i=0; spi_vars.i < 10; spi_vars.i++) {
       		__delay_cycles(8);
	}
   }
   
#ifdef SPI_IN_INTERRUPT_MODE
   // implementation 1. use a callback function when transaction finishes
   
   // write first byte to TX buffer
   UCB1TXBUF                 = *spi_vars.pNextTxByte;
   
   // re-enable interrupts
   __enable_interrupt();
#else
   // implementation 2. busy wait for each byte to be sent
   
   // send all bytes
   while (spi_vars.txBytesLeft>0) {
      // write next byte to TX buffer
      UCB1TXBUF              = *spi_vars.pNextTxByte;
      // busy wait on the interrupt flag
      while ((UC1IFG & UCB1RXIFG)==0);
      // clear the interrupt flag
      UC1IFG &= ~UCB1RXIFG;
      // save the byte just received in the RX buffer
      switch (spi_vars.returnType) {
         case SPI_FIRSTBYTE:
            if (spi_vars.numTxedBytes==0) {
               *spi_vars.pNextRxByte   = UCB1RXBUF;
            }
            break;
         case SPI_BUFFER:
            *spi_vars.pNextRxByte      = UCB1RXBUF;
            spi_vars.pNextRxByte++;
            break;
         case SPI_LASTBYTE:
            *spi_vars.pNextRxByte      = UCB1RXBUF;
            break;
      }
      // one byte less to go
      spi_vars.pNextTxByte++;
      spi_vars.numTxedBytes++;
      spi_vars.txBytesLeft--;
   }
   
   // put CS signal high to signal end of transmission to slave
   if (spi_vars.isLast==SPI_LAST) {

        // delay 1/8MHz = 125ns * 8 = 1us
        for(spi_vars.i=0; spi_vars.i < 10; spi_vars.i++) {
                __delay_cycles(8);
        }

        CS_OUT                 |=  CS_PIN;
   }
   
   // SPI is not busy anymore
   spi_vars.busy             =  0;
#endif
}

//=========================== private =========================================

//=========================== interrupt handlers ==============================

kick_scheduler_t spi_isr() {
#ifdef SPI_IN_INTERRUPT_MODE
   // save the byte just received in the RX buffer
   switch (spi_vars.returnType) {
      case SPI_FIRSTBYTE:
         if (spi_vars.numTxedBytes==0) {
            *spi_vars.pNextRxByte = UCB1RXBUF;
         }
         break;
      case SPI_BUFFER:
         *spi_vars.pNextRxByte    = UCB1RXBUF;
         spi_vars.pNextRxByte++;
         break;
      case SPI_LASTBYTE:
         *spi_vars.pNextRxByte    = UCB1RXBUF;
         break;
   }
   
   // one byte less to go
   spi_vars.pNextTxByte++;
   spi_vars.numTxedBytes++;
   spi_vars.txBytesLeft--;
   
   if (spi_vars.txBytesLeft>0) {
      // write next byte to TX buffer
      UCB1TXBUF              = *spi_vars.pNextTxByte;
   } else {
      // put CS signal high to signal end of transmission to slave
      if (spi_vars.isLast==SPI_LAST) {

	 // delay 1/8MHz = 125ns * 8 = 1us
         for(spi_vars.i=0; spi_vars.i < 5; spi_vars.i++) {
         	__delay_cycles(8);
         }

         CS_OUT              |=  CS_PIN;
      }
      // SPI is not busy anymore
      spi_vars.busy          =  0;
      
      // SPI is done!
      if (spi_vars.callback!=NULL) {
         // call the callback
         spi_vars.callback();
         // kick the OS
         return KICK_SCHEDULER;
      }
   }
#else
   while(1);// this should never happen
#endif
}

