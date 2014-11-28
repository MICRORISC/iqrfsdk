/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*  
 * Simple example for MCU MSP430
 */

/*
 * ======== Standard MSP430 includes ========
 */
#include "msp430x22x4.h"
#include "dpa_library.h"

/*
 * ======== Grace related declaration ========
 */
extern void Grace_init(void);

/*
 * ======== DPA implementation ========
 */
#define NO_WAITING					0
#define CONFIRMATION_WAITING		1
#define RESPONSE_WAITING			2

void MyDpaAnswerHandler(T_DPA_PACKET *MyDpaAnswer);
void MyDpaLibTimeoutHandler(void);
void MyDpaLibRequsts(void);

void MyDpaUartTxHandler(void);
void MyDpaUartRxHandler(void);

volatile UINT8	uartTxCompleted;
UINT16	dpaStep;
UINT16  dpaTimeoutCnt;
T_DPA_PACKET	myDpaRequest;


/**
 * DPA response timeout handler
 *
 * @param 	none
 * @return 	none
 *
 **/
void MyDpaLibTimeoutHandler(void)
{
	dpaStep = NO_WAITING;

	// ANY USER CODE FOR OPERATION TIMEOUT HANDLING
}
//====================================================================================================================

/**
 * DPA answer handler
 *
 * @param 	pointer to DPA answer packet
 * @return 	none
 *
 **/
void MyDpaAnswerHandler(T_DPA_PACKET *dpaAnswerPkt){

	if (dpaStep == CONFIRMATION_WAITING && dpaAnswerPkt->Extension.Confirmation.StatusConfirmation == STATUS_CONFIRMATION){
		dpaStep = RESPONSE_WAITING;
		dpaTimeoutCnt = DPA_GetEstimatedTimeout();
		return;
	}

	if (dpaStep == RESPONSE_WAITING && dpaAnswerPkt->Extension.Response.ResponseCode == STATUS_NO_ERROR){
		dpaStep = NO_WAITING;
		dpaTimeoutCnt = 0;

		// ANY USER CODE FOR DPA RESPONSE PROCESSING
		
		return;
	}
	
	// ANY USER CODE FOR ASYNCHRONOUS DPA MESSAGES PROCESSING
}
//================================================================================================================

/**
 * DPA requsets sending
 *
 * @param 	none
 * @return 	none
 *
 **/
void MyDpaLibRequsts(void)
{
	// if DPA library is busy, or dpa operation in progress, then return
	if (DPA_GetStatus() != DPA_READY || dpaStep != NO_WAITING) return;

	// **** USER CODE - start
	//
	// before sending any DPA request user should prepare
	// -- myDpaRequest structure with request data,
	// -- set dpaTimeoutCnt with operation timeou in ms
	// -- and then call DPA_SendRequest(&myDpaRequest ... function to send DPA request to TR module
	//	
	// **** USER CODE - end

	if (myDpaRequest.NAdr == 0) dpaStep = RESPONSE_WAITING;				// dpa answer would be response
	else dpaStep = CONFIRMATION_WAITING;								// dpa answer would be confirmation
}
//==================================================================================================================

#ifdef __SPI_INTERFACE__
/**
 * Send/receive byte over SPI
 *
 * @param 	Tx_Byte - byte to send
 * @return 	received byte
 *
 **/
UINT8 DPA_SendSpiByte(UINT8 Tx_Byte){

	UINT8 SpiRxData;
	
	P3OUT &= ~BIT0;							// select TR module
	__delay_cycles(1600);					// mandatory wait
    UCB0TXBUF = Tx_Byte;   					// send data
	while (UCB0STAT & UCBUSY);
	SpiRxData = UCB0RXBUF; 					// read data	
	__delay_cycles(1600);					// wait
	P3OUT |= BIT0;							// deselect TR module
	
	return(SpiRxData);
}
#endif

#ifdef __UART_INTERFACE__
/**
 * Send/receive byte over SPI
 *
 * @param 	Tx_Byte - byte to send
 * @return 	received byte
 *
 **/
void DPA_SendUartByte(UINT8 Tx_Byte){
	uartTxCompleted = 0;
	UCA0TXBUF = Tx_Byte;
	while(!uartTxCompleted);
}

/**
 * UART Tx ISR handler
 *
 * @param 	none
 * @return 	none
 *
 **/
void MyDpaUartTxHandler(void){
	uartTxCompleted = 1;
}

/**
 * UART Rx ISR handler
 *
 * @param 	none
 * @return 	none
 *
 **/
void MyDpaUartRxHandler(void){

	UINT8 Rx_Byte;

	Rx_Byte = UCA0RXBUF;
	DPA_ReceiveUartByte(Rx_Byte);	
}
#endif

/*
 *  ======== main ========
 */
int main( void )
{
	Grace_init();                     // Activate Grace-generated configuration
 
	DPA_Init();						  // DPA library initialization
	DPA_SetAnswerHandler(MyDpaAnswerHandler);	// set DPA response handler
    
    for (;;){
		MyDpaLibRequsts();			  // function for DPA requests generating
		DPA_LibraryDiver();			  // DPA library handler
    }
}
