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

/*****************************************************************************
 * Microrisc DPA support library demo - skeleton
 * This program shows how to use the DPA support library.
 *****************************************************************************/
 
#include "dpa_library.h"

#define NO_WAITING					0
#define CONFIRMATION_WAITING		1
#define RESPONSE_WAITING			2

void MyDpaAnswerHandler(T_DPA_PACKET *MyDpaAnswer);
void MyDpaLibTimeoutHandler(void);
void MyDpaLibRequests(void);

typedef enum{
	RLED_ON,
	RLED_OFF,
	RLED_PULESE,
	GLED_ON,
	GLED_OFF,
	GLED_PULSE
}DPA_RQ

DPA_RQ	dpaRq;
UINT16	dpaStep;
UINT16  dpaTimeoutCnt;
UINT16	dpaNAdr;
UINT16	dpaHwProfile;
T_DPA_PACKET	myDpaRequest;

/**
 * Main function
 *
 * @param none
 * @return error code
 *
 **/
int main(void)
{

/* some user initialization */

/* 1ms timer initialization */

/* UART or SPI initialization */

	DPA_Init();
	DPA_SetAnswerHandler(MyDpaAnswerHandler);

/* 1ms timer start */

	while(1)
    {

		/* some user code which defines DPA requests */

		MyDpaLibRequests();
		DPA_LibraryDiver();

		/* some user code */
	}
}
//=================================================================================================================


/**
 * DPA requests sending
 *
 * @param 	none
 * @return 	none
 *
 **/
void MyDpaLibRequests(void)
{
	// if DPA library is busy, or dpa operation in progress, then return
	if (DPA_GetStatus() != DPA_READY || dpaStep != NO_WAITING) return;

	myDpaRequest.NAdr = dpaNAdr;								// set destination address
	myDpaRequest.HwProfile = dpaHwProfile;						// set dpa HW profile

	switch(dpaRq){												// send specific DPA request

		case RLED_ON:{
			myDpaRequest.PNum = PNUM_LEDR;
			myDpaRequest.PCmd = CMD_LED_SET_ON;
			DPA_SendRequest(&myDpaRequest, 0);
		}
		break;

		case RLED_OFF:{
			myDpaRequest.PNum = PNUM_LEDR;
			myDpaRequest.PCmd = CMD_LED_SET_OFF;
			DPA_SendRequest(&myDpaRequest, 0);
		}
		break;	

		case RLED_PULESE:{
			myDpaRequest.PNum = PNUM_LEDR;
			myDpaRequest.PCmd = CMD_LED_PULSE;
			DPA_SendRequest(&myDpaRequest, 0);
		}
		break;	

		case GLED_ON:{
			myDpaRequest.PNum = PNUM_LEDG;
			myDpaRequest.PCmd = CMD_LED_SET_ON;
			DPA_SendRequest(&myDpaRequest, 0);
		}
		break;

		case GLED_OFF:{
			myDpaRequest.PNum = PNUM_LEDG;
			myDpaRequest.PCmd = CMD_LED_SET_OFF;
			DPA_SendRequest(&myDpaRequest, 0);
		}
		break;	

		case GLED_PULSE:{
			myDpaRequest.PNum = PNUM_LEDG;
			myDpaRequest.PCmd = CMD_LED_PULSE;
			DPA_SendRequest(&myDpaRequest, 0);
		}
		break;	
	}

	if (myDpaRequest.NAdr == 0) dpaStep = RESPONSE_WAITING;				// dpa answer would be response
	else dpaStep = CONFIRMATION_WAITING;								// dpa answer would be confirmation

	dpaTimeoutCnt = 1000;												// set timeout 1s
}
//==================================================================================================================

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

	/* some user code for timeout handling */
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

		/* some user code for correct response handling */
	}
}
//================================================================================================================

/*********************************************************************
* Function: 1ms Timer3 ISR
* PreCondition: none
* Input: none
* Output: none
* Side Effects: none
* Overview: Timer3 ISR generates 1ms flags and timming DPA tomeouts
********************************************************************/    
void __ISR(_TIMER_3_VECTOR, ipl4) _T3Interrupt(void)
{
	DPA_SetTimmingFlag();												// set 1ms flag

	if (dpaTimeoutCnt){
		if ((--dpaTimeoutCnt) == 0) MyDpaLibTimeoutHandler();			// if timeout expired, call timeout handler
	}
}
//==================================================================================================================

/**
 * user functions for sending/receiving byte over SPI or UART 
 *
 * @param 	pointer to DPA answer packet
 * @return 	none
 *
 **/
#ifdef __SPI_INTERFACE__
UINT8 DPA_SendSpiByte(UINT8 Tx_Byte)
{
	/* check if SPI is ready */

	/* send Tx_Byte over SPI */

	/* wait until Tx_Byte is sent */

	/* read byte from SPI Rx buffer */

	/* return received byte */
}
#endif

#ifdef __UART_INTERFACE__
void DPA_SendUartByte(UINT8 Tx_Byte);
{
	/* check if UART transmiter is ready */

	/* send Tx_Byte over UART */
}
    
void __ISR(_UART_Rx_VECTOR, ipl3) _UART_Rx_Interrupt(void)
{
	Rx_Byte = UART_RX_BUFF;						// read received byte from UART Rx buffer
	DPA_ReceiveUartByte(Rx_Byte);				// send received byte to DPA library
}
#endif