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

#include "stdint.h"
#include "stdio.h"
#include "string.h"
// bsp modules required
#include "board.h"
#include "uart0.h"
#include "uart1.h"
#include "bsp_timer.h"
#include "leds.h"
// dpa framework
#include "dpa_library.h"

//=========================== defines =========================================

#define BSP_TIMER_PERIOD     32 	// 32@32kHz = 1ms
#define USER_TIMER_PERIOD	 1000	// 1000@1ms = 1s
static const uint8_t stringToSend[]       = "Hello, World!\r\n";

#define NO_WAITING					0
#define CONFIRMATION_WAITING		1
#define RESPONSE_WAITING			2

#define COORDINATOR					0
#define NODE1						1
#define NODE2						2
#define NODE3						3
#define NODE4						4
#define NODE5						5

//=========================== variables =======================================

typedef enum {
	GLED_PULSE,
	RLED_PULSE
}DPA_RQ;

typedef struct {
   volatile uint8_t uart0Done;
   volatile uint8_t uart1Done;
   uint8_t uart1_lastTxByteIndex;
   volatile	uint16_t dpaTimeoutCnt;
   uint16_t	dpaStep;
   T_DPA_PACKET myDpaRequest;
   volatile uint16_t swTimer;
   volatile uint8_t swTimerAck;
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
//dpa interface 115200
void cb_uart0TxDone(void);
void cb_uart0RxCb(void);
//usb interface 115200
void cb_uart1TxDone(void);
void cb_uart1RxCb(void);

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
	uart1_setCallbacks(cb_uart1TxDone,cb_uart1RxCb);
	uart1_enableInterrupts();

	// setup BSP timer
	bsp_timer_set_callback(cb_compare);
	bsp_timer_scheduleIn(BSP_TIMER_PERIOD);

	DPA_Init();						  			// DPA library initialization
	DPA_SetAnswerHandler(MyDpaAnswerHandler);	// set DPA response handler

	for (;;) {

		// every 1s
		if(app_vars.swTimerAck == TRUE) {
			MyDpaLibRequests();					// function for sending DPA requests - UART0
			MyDebugMessages();					// function for printing DEBUG messages - UART1
			app_vars.swTimerAck = FALSE;
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

	switch(app_vars.cmds) {

		case GLED_PULSE:
			DpaLedG(COORDINATOR, CMD_LED_PULSE);
		break;

		case RLED_PULSE:
			DpaLedR(NODE1, CMD_LED_PULSE);
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
		return;
	}

	if (app_vars.dpaStep == RESPONSE_WAITING
			&& dpaAnswerPkt->Extension.Response.ResponseCode == STATUS_NO_ERROR) {
		app_vars.dpaStep = NO_WAITING;
		app_vars.dpaTimeoutCnt = 0;

		// ANY USER CODE FOR DPA RESPONSE PROCESSING
		app_vars.cmds++;
		app_vars.cmds %= 2;

		//TODO: add some delay before sending another request to the network

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

	//TODO: add some delay before sending another request to the network
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
		app_vars.uart1Done = 0;
		app_vars.uart1_lastTxByteIndex = 0;
		uart1_writeByte(stringToSend[app_vars.uart1_lastTxByteIndex]);

		while(!app_vars.uart1Done)
			;
}
//=============================================================================

/**
 * Send DPA byte over UART
 *
 * @param 	Tx_Byte to send
 * @return 	none
 *
 **/
void DPA_SendUartByte(UINT8 Tx_Byte) {
	app_vars.uart0Done = 0;

	//send byte
	uart0_writeByte(Tx_Byte);

	while (!app_vars.uart0Done)
		;
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
			leds_blue_toggle();
			MyDpaLibTimeoutHandler();										// if timeout expired, call timeout handler
		}
	}

	if (app_vars.swTimer) {													// sw timer, call timeout handler
		if ((--app_vars.swTimer) == 0) {
			MySwTimerTimeoutHandler();
			app_vars.swTimer = USER_TIMER_PERIOD;
		}
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
	app_vars.uart0Done = 1;
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
   uint8_t byte;

   // read received byte
   byte = uart0_readByte();

   // load that byte to dpa library
   DPA_ReceiveUartByte(byte);
}
//=============================================================================

/**
 * UART1 Tx callback
 *
 * @param 	none
 * @return 	none
 *
 **/
void cb_uart1TxDone(void) {
	app_vars.uart1_lastTxByteIndex++;
	if(app_vars.uart1_lastTxByteIndex<sizeof(stringToSend)) {
		uart1_writeByte(stringToSend[app_vars.uart1_lastTxByteIndex]);
	} else {
		app_vars.uart1Done = 1;
	}
}
//=============================================================================

/**
 * UART1 Rx callback
 *
 * @param 	none
 * @return 	none
 *
 **/
void cb_uart1RxCb(void) {
}
//=============================================================================
