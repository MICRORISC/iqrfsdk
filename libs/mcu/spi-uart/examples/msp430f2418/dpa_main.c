/*
 * Copyright 2015 MICRORISC s.r.o.
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
 * ======== Standard MSP430 includes ========
 */

#include <stdio.h>

// bsp modules required
#include "board.h"
#include "uart0.h"
//#include "uart1.h"
#include "spi.h"
#include "bsp_timer.h"
#include "leds.h"

// dpa framework
#include "dpa_library.h"

//=========================== defines =========================================

#define BSP_TIMER_PERIOD     	32 		// 32@32kHz = 1ms
#define USER_TIMER_PERIOD	1000		// 1000@1ms = 1s

static const uint8_t requestToSend[]       	= "Req: ";
static const uint8_t confirmationToSend[]  	= "Con: ";
static const uint8_t responseToSend[]       	= "Res: ";
static const uint8_t endingToSend[] 		= "\r\n\n";

#define CONFIRMATION			1
#define RESPONSE			2

#define NO_WAITING			0
#define CONFIRMATION_WAITING		1
#define RESPONSE_WAITING		2

#define COORDINATOR			0
#define NODE1				1
#define NODE2				2
#define NODE3				3
#define NODE4				4
#define NODE5				5

//=========================== variables =======================================

typedef enum {
	GLED_PULSE,
	RLED_PULSE
}DPA_RQ;

typedef struct {
   // uart
   volatile uint8_t uart0Done;
   uint8_t uart0_lastTxByteIndex;
   //volatile uint8_t uart1Done;
   // spi
   uint8_t txbuf[1];
   uint8_t rxbuf[1];
   // dpa
   volatile	uint16_t dpaTimeoutCnt;
   uint16_t	dpaStep;
   T_DPA_PACKET myDpaRequest;
   char *ptrRequest;
   uint8_t debugRequest;
   T_DPA_PACKET myDpaAnswer;
   char *ptrAnswer;
   //debug
   uint8_t debugAnswer;
   uint8_t debugActive;
   char debugString[128];
   //timer
   volatile uint16_t swTimer;
   volatile uint8_t swTimerAck;
   volatile uint8_t ticks;
   DPA_RQ cmds;
} app_vars_t;

app_vars_t app_vars;

//=========================== prototypes ======================================

void MyDpaAnswerHandler(T_DPA_PACKET *MyDpaAnswer);
void MyDpaLibTimeoutHandler(void);
void MyDpaLibRequests(void);

//dpa requests
void DpaLedG(uint16_t addr, uint8_t cmd);
void DpaLedR(uint16_t addr, uint8_t cmd);

//user timer
void MySwTimerTimeoutHandler(void);

//debug
void MyDebugMessages(void);

//dpa driver timing
void cb_compare(void);

//usb interface 115200
void cb_uart0TxDone(void);
void cb_uart0RxCb(void);

//dpa interface 115200
//void cb_uart1TxDone(void);
//void cb_uart1RxCb(void);

//=========================== main ============================================

int mote_main(void) {

	// clear local variable
	memset(&app_vars,0,sizeof(app_vars_t));
	app_vars.swTimer = USER_TIMER_PERIOD;

	// initialize the board
	board_init();

	// setup UART0
	uart0_setCallbacks(cb_uart0TxDone,cb_uart0RxCb);
	uart0_enableInterrupts();

	// setup UART1
	//uart1_setCallbacks(cb_uart1TxDone,cb_uart1RxCb);
	//uart1_enableInterrupts();

	// setup BSP timer
	bsp_timer_set_callback(cb_compare);
	bsp_timer_scheduleIn(BSP_TIMER_PERIOD);

	DPA_Init();					// DPA library initialization
	DPA_SetAnswerHandler(MyDpaAnswerHandler);	// set DPA response handler

	for (;;) {

		// dpa msgs
		if(app_vars.swTimerAck == TRUE) {
/*
			// to test basic spi communication
			leds_red_toggle();

			app_vars.txbuf[0] = 0x00;

			spi_txrx(
         			app_vars.txbuf,
         			sizeof(app_vars.txbuf),
         			SPI_BUFFER,
         			app_vars.rxbuf,
         			sizeof(app_vars.rxbuf),
         			SPI_FIRST,
         			SPI_LAST
      			);

			if(app_vars.rxbuf[0] == 0x80)
				leds_green_toggle();
*/

			MyDpaLibRequests();				// function for sending DPA requests - SPI/UART1
			app_vars.swTimerAck = FALSE;
		}

		// debug msgs
		if(!app_vars.debugActive) {				// only if there no debug processing already on
			if(app_vars.debugRequest == TRUE || \
					(app_vars.debugAnswer == CONFIRMATION) || (app_vars.debugAnswer == RESPONSE))  {
				MyDebugMessages();			// function for printing DEBUG messages - UART0
			}
		}

		DPA_LibraryDriver();					// DPA library handler
	}
}

/**
 * DPA requests sending
 *
 * @param 	none
 * @return 	none
 *
 **/
void MyDpaLibRequests(void) {
	// if DPA library is busy, or dpa operation in progress, then return
	if (DPA_GetStatus() != DPA_READY || app_vars.dpaStep != NO_WAITING) {
		leds_red_toggle();
		return;
	}

	// 100ms before sending another request
	app_vars.ticks = 100;
	while(app_vars.ticks)
		;

	switch(app_vars.cmds) {

		case GLED_PULSE:
			DpaLedG(COORDINATOR, CMD_LED_PULSE);
			app_vars.debugRequest = TRUE;
		break;

		case RLED_PULSE:
			DpaLedR(NODE1, CMD_LED_PULSE);
			//DpaLedR(COORDINATOR, CMD_LED_PULSE);
			app_vars.debugRequest = TRUE;
		break;

		default:
		break;
	}

	leds_green_toggle();

	if (app_vars.myDpaRequest.NAdr == 0)
		app_vars.dpaStep = RESPONSE_WAITING;			// dpa answer would be response
	else
		app_vars.dpaStep = CONFIRMATION_WAITING;		// dpa answer would be confirmation
}
//=============================================================================

/**
 * DPA answer handler
 *
 * @param 	pointer to DPA answer packet
 * @return 	none
 *
 **/
void MyDpaAnswerHandler(T_DPA_PACKET *dpaAnswerPkt) {

	if (app_vars.dpaStep == CONFIRMATION_WAITING
			&& dpaAnswerPkt->Extension.Confirmation.StatusConfirmation
					== STATUS_CONFIRMATION) {
		app_vars.dpaStep = RESPONSE_WAITING;
		app_vars.dpaTimeoutCnt = DPA_GetEstimatedTimeout();

		memcpy(&app_vars.myDpaAnswer, dpaAnswerPkt, sizeof(app_vars.myDpaAnswer));
		app_vars.debugAnswer = CONFIRMATION;

		return;
	}

	if (app_vars.dpaStep == RESPONSE_WAITING
			&& dpaAnswerPkt->Extension.Response.ResponseCode == STATUS_NO_ERROR) {
		app_vars.dpaStep = NO_WAITING;
		app_vars.dpaTimeoutCnt = 0;

		// ANY USER CODE FOR DPA RESPONSE PROCESSING
		memcpy(&app_vars.myDpaAnswer, dpaAnswerPkt, sizeof(app_vars.myDpaAnswer));
		app_vars.debugAnswer = RESPONSE;

		app_vars.cmds++;
		app_vars.cmds %= 2;

		return;
	}

	// ANY USER CODE FOR ASYNCHRONOUS DPA MESSAGES PROCESSING
}
//=============================================================================

/**
 * DPA response timeout handler
 *
 * @param 	none
 * @return 	none
 *
 **/
void MyDpaLibTimeoutHandler(void) {
	app_vars.dpaStep = NO_WAITING;

	// ANY USER CODE FOR OPERATION TIMEOUT HANDLING
	app_vars.cmds++;
	app_vars.cmds %= 2;
}
//=============================================================================

/**
 * DPA request to drive ledG peripheral
 *
 * @param 	addr
 * @param 	cmd
 * @return
 *
 **/
void DpaLedG(uint16_t addr, uint8_t cmd) {
	app_vars.myDpaRequest.NAdr = addr;
	app_vars.myDpaRequest.PNum = PNUM_LEDG;
	app_vars.myDpaRequest.PCmd = cmd;
	app_vars.myDpaRequest.HwProfile = HW_PROFILE_DO_NOT_CHECK;

	// initial timeout 1s for either confirmation or response
	app_vars.dpaTimeoutCnt = 1000;

	DPA_SendRequest(&app_vars.myDpaRequest, 0);
}

/**
 * DPA request to drive ledR peripheral
 *
 * @param 	addr
 * @param 	cmd
 * @return
 *
 **/
void DpaLedR(uint16_t addr, uint8_t cmd) {
	app_vars.myDpaRequest.NAdr = addr;
	app_vars.myDpaRequest.PNum = PNUM_LEDR;
	app_vars.myDpaRequest.PCmd = cmd;
	app_vars.myDpaRequest.HwProfile = HW_PROFILE_DO_NOT_CHECK;

	// initial timeout 1s for either confirmation or response
	app_vars.dpaTimeoutCnt = 1000;

	DPA_SendRequest(&app_vars.myDpaRequest, 0);
}

/**
 * User timer timeout handler
 *
 * @param 	none
 * @return 	none
 *
 **/
void MySwTimerTimeoutHandler(void) {
	app_vars.swTimerAck = TRUE;
}
//=============================================================================

/**
 * Printing messages over UART(USB)
 *
 * @param 	none
 * @return 	none
 *
 **/
void MyDebugMessages(void) {
		uint8_t i = 0;

		//number of bytes from extension block
		uint8_t len = 10;

		app_vars.uart0Done = 0;
		app_vars.uart0_lastTxByteIndex = 0;
		memset(&app_vars.debugString,0,sizeof(app_vars.debugString));

		if (app_vars.debugRequest) {
			// message type
			memcpy(app_vars.debugString, requestToSend, sizeof(requestToSend));

			// message data
			sprintf(app_vars.debugString + sizeof(requestToSend), "%x    %x    %x    %x    ", app_vars.myDpaRequest.NAdr \
																				    		, app_vars.myDpaRequest.PNum \
																				    		, app_vars.myDpaRequest.PCmd \
																				    		, app_vars.myDpaRequest.HwProfile);

			app_vars.ptrRequest = (char*)&app_vars.myDpaRequest.Extension.Plain.Data;
			//first 5 elemets from extension
			for(i=0; i<len; i++) {
				sprintf(app_vars.debugString + sizeof(requestToSend) + 4*5 + (i*5), "%x    ", *app_vars.ptrRequest);
				app_vars.ptrRequest++;
			}

			// message ending
			memcpy(app_vars.debugString + sizeof(requestToSend) + 4*5 + len*5, endingToSend, sizeof(endingToSend));

			app_vars.ptrRequest = (char*)&app_vars.debugString;
			uart0_writeByte(*app_vars.ptrRequest);
		}
		else if(app_vars.debugAnswer == CONFIRMATION) {
			// message type
			memcpy(app_vars.debugString, confirmationToSend, sizeof(confirmationToSend));

			// message header
			sprintf(app_vars.debugString + sizeof(confirmationToSend), "%x    %x    %x    %x    ", app_vars.myDpaAnswer.NAdr \
																					 	 	 	 , app_vars.myDpaAnswer.PNum \
																					 	 	 	 , app_vars.myDpaAnswer.PCmd \
																					 	 	 	 , app_vars.myDpaAnswer.HwProfile);

			// message data
			sprintf(app_vars.debugString + sizeof(confirmationToSend) + 4*5, "%x    %x    %x    %x    %x    ", app_vars.myDpaAnswer.Extension.Confirmation.StatusConfirmation \
																					   	   	       	    	 , app_vars.myDpaAnswer.Extension.Confirmation.DpaValue \
																					   	   	       	    	 , app_vars.myDpaAnswer.Extension.Confirmation.Hops \
																					   	   	       	    	 , app_vars.myDpaAnswer.Extension.Confirmation.TimeSlotLength \
																					   	   	       	    	 , app_vars.myDpaAnswer.Extension.Confirmation.HopsResponse);

			// message ending
			memcpy(app_vars.debugString + sizeof(confirmationToSend) + 4*5 + 5*5, endingToSend, sizeof(endingToSend));
		}
		else if (app_vars.debugAnswer == RESPONSE) {
			// message type
			memcpy(app_vars.debugString, responseToSend, sizeof(responseToSend));

			// message header
			sprintf(app_vars.debugString + sizeof(responseToSend), "%x    %x    %x    %x    ", app_vars.myDpaAnswer.NAdr \
																						 	 , app_vars.myDpaAnswer.PNum \
																						 	 , app_vars.myDpaAnswer.PCmd \
																						 	 , app_vars.myDpaAnswer.HwProfile);

			// message data
			sprintf(app_vars.debugString + sizeof(responseToSend) + 4*5, "%x    %x    ", app_vars.myDpaAnswer.Extension.Response.ResponseCode \
																	           	   	   , app_vars.myDpaAnswer.Extension.Response.DpaValue );

			app_vars.ptrAnswer = (char*)&app_vars.myDpaAnswer.Extension.Response.Data;
			//first 5 elemets from extension
			for(i=0; i<len; i++) {
				sprintf(app_vars.debugString + sizeof(responseToSend) + 6*5 + (i*5), "%x    ", *app_vars.ptrAnswer);
				app_vars.ptrAnswer++;
			}

			// message ending
			memcpy(app_vars.debugString + sizeof(responseToSend) + 6*5 + len*5, endingToSend, sizeof(endingToSend));
		}

		app_vars.ptrAnswer = (char*)&app_vars.debugString;
		uart0_writeByte(*app_vars.ptrAnswer);

		app_vars.debugActive = TRUE;

		/*
		while(!app_vars.uart0Done)
			;
		*/
}
//=============================================================================

/**
 * Send DPA byte over UART
 *
 * @param 	Tx_Byte to send
 * @return 	none
 *
 **/
/*
void DPA_SendUartByte(UINT8 Tx_Byte) {
	app_vars.uart1Done = 0;

	//send byte
	uart1_writeByte(Tx_Byte);

	while (!app_vars.uart1Done)
		;
}
*/
//=============================================================================

/**
 * Send DPA byte over SPI
 *
 * @param       Tx_Byte to send
 * @return      Received Rx_Byte
 *
 **/
uint8_t DPA_SendSpiByte(UINT8 Tx_Byte) {

	app_vars.txbuf[0] = Tx_Byte;

	spi_txrx(
         	app_vars.txbuf,
         	sizeof(app_vars.txbuf),
         	SPI_BUFFER,
         	app_vars.rxbuf,
         	sizeof(app_vars.rxbuf),
         	SPI_FIRST,
         	SPI_LAST
      	);

	return app_vars.rxbuf[0];
}
//=============================================================================

/**
 * 1ms timer callback
 *
 * @param 	none
 * @return 	none
 *
 **/
void cb_compare(void) {

	// dpa timing
	DPA_SetTimmingFlag();													// set 1ms flag

	if (app_vars.dpaTimeoutCnt) {
		if ((--app_vars.dpaTimeoutCnt) == 0) {
			leds_yellow_toggle();
			MyDpaLibTimeoutHandler();										// if timeout expired, call timeout handler
		}
	}

	if (app_vars.swTimer) {													// sw timer, call timeout handler
		if ((--app_vars.swTimer) == 0) {
			MySwTimerTimeoutHandler();
			app_vars.swTimer = USER_TIMER_PERIOD;
		}
	}

	if(app_vars.ticks) {													// delay timer
		--app_vars.ticks;
	}

	// schedule again
	bsp_timer_scheduleIn(BSP_TIMER_PERIOD);
}
//=============================================================================

/**
 * UART0 Tx callback
 *
 * @param 	none
 * @return 	none
 *
 **/
void cb_uart0TxDone(void) {
	app_vars.uart0_lastTxByteIndex++;

	if(app_vars.debugRequest) {
		app_vars.ptrRequest++;

		if(app_vars.uart0_lastTxByteIndex < sizeof(app_vars.debugString)) {
			uart0_writeByte(*app_vars.ptrRequest);
		} else {
			app_vars.debugRequest = FALSE;
			app_vars.debugActive = FALSE;
			//app_vars.uart0Done = 1;
		}
	}
	else if (app_vars.debugAnswer) {
		app_vars.ptrAnswer++;

		if(app_vars.uart0_lastTxByteIndex < sizeof(app_vars.debugString)) {
			uart0_writeByte(*app_vars.ptrAnswer);
		} else {
			app_vars.debugAnswer = FALSE;
			app_vars.debugActive = FALSE;
			//app_vars.uart0Done = 1;
		}
	}
}
//=============================================================================

/**
 * UART0 Rx callback
 *
 * @param 	none
 * @return 	none
 *
 **/
void cb_uart0RxCb(void) {
}
//=============================================================================

/**
 * UART1 Tx callback
 *
 * @param 	none
 * @return 	none
 *
 **/
/*
void cb_uart1TxDone(void) {
	app_vars.uart1Done = 1;
}
*/
//=============================================================================

/**
 * UART1 Rx callback
 *
 * @param 	none
 * @return 	none
 *
 **/
/*
void cb_uart1RxCb(void) {
   uint8_t byte;

   // read received byte
   byte = uart1_readByte();

   // load that byte to dpa library
   DPA_ReceiveUartByte(byte);
}
*/
//=============================================================================

