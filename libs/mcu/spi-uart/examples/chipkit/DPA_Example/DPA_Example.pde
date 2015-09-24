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

#include <stdio.h>

#include <DPA_SPI.h>
#include <DPA_UART.h>
#include <DPA_TIMER.h>
#include <DPA_LIBRARY.h>

//=========================== defines =========================================
#define USER_TIMER_PERIOD		1000		// 1000@1ms = 1s
#define TRUE 1
#define FALSE 0

DPA_SPI 	DPASpi;
DPA_UART 	DPAUart;
DPA_TIMER 	DPATimer;

uint8_t state = 1;

#define CONFIRMATION			1
#define RESPONSE			2

#define NO_WAITING			0
#define CONFIRMATION_WAITING	        1
#define RESPONSE_WAITING		2

#define COORDINATOR			0x00
#define NODE1				0x01
#define NODE2				0x02
#define NODE3				0x03
#define NODE4				0x04
#define NODE5				0x05
#define LOCAL               0xFC

//=========================== variables =======================================
typedef enum 
{
	GLED_PULSE,
	RLED_PULSE
}DPA_RQ;

typedef struct 
{
	// spi
	uint8_t txbuf[1];
	uint8_t rxbuf[1];

	// uart
	volatile uint8_t uartDone;

	// timer
	volatile uint16_t swTimer;
	volatile uint8_t swTimerAck;
	volatile uint8_t ticks;

	// dpa
	volatile uint16_t dpaTimeoutCnt;
	uint16_t dpaStep;
	T_DPA_PACKET myDpaRequest;
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

//dpa driver timing
void cb_compare(void);

//dpa interface
void cb_uartTxDone(void);
void cb_uartRxCb(void);

//=========================== init peripherals ============================================
void setup()
{
	Serial.begin(9600);

	DPASpi.init(250000); 	// select for comunication via SPI
	//DPAUart.init(115200); // select for comunication via UART

	DPATimer.init();

	delay(2000);
}

//=========================== main ============================================
void loop()
{
	// clear local variable
	memset(&app_vars, 0, sizeof(app_vars_t));
	app_vars.swTimer = USER_TIMER_PERIOD;

	// setup DPA UART 
	//DPAUart.setCallbacks(cb_uartTxDone, cb_uartRxCb);
	//DPAUart.enableInterrupts();

	// setup timer
	DPATimer.setCallback(cb_compare);
        DPATimer.attachInterrupt();

	// DPA library initialization
	DPA_Init();
	DPA_SetAnswerHandler(MyDpaAnswerHandler);	                // set DPA response handler
        
	Serial.print("Peripheral and DPA init done");

	for (;;) {
		// dpa msgs
		if (app_vars.swTimerAck == TRUE) 
		{
			MyDpaLibRequests();				// function for sending DPA requests - SPI/UART
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
void MyDpaLibRequests(void) 
{
	// if DPA library is busy, or dpa operation in progress, then return
	if (DPA_GetStatus() != DPA_READY || app_vars.dpaStep != NO_WAITING) {
		return;
	}

	// 100ms before sending another request
	app_vars.ticks = 100;
	while (app_vars.ticks);

	switch (state) {

        case 1:
            DpaLedR(COORDINATOR, CMD_LED_PULSE);
            state = 2;
		break;

		case 2:
			DpaLedG(COORDINATOR, CMD_LED_PULSE); 
        	state = 1;
		break;

	default:
		break;
	}


	if (app_vars.myDpaRequest.NAdr == 0x00 || app_vars.myDpaRequest.NAdr == 0xFC)
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
void MyDpaAnswerHandler(T_DPA_PACKET *dpaAnswerPkt) 
{
	if (app_vars.dpaStep == CONFIRMATION_WAITING
		&& dpaAnswerPkt->Extension.Confirmation.StatusConfirmation
		== STATUS_CONFIRMATION) 
	{
		app_vars.dpaStep = RESPONSE_WAITING;
		app_vars.dpaTimeoutCnt = DPA_GetEstimatedTimeout();
		return;
	}

	if (app_vars.dpaStep == RESPONSE_WAITING
		&& dpaAnswerPkt->Extension.Response.ResponseCode == STATUS_NO_ERROR) 
	{
		app_vars.dpaStep = NO_WAITING;
		app_vars.dpaTimeoutCnt = 0;

		// ANY USER CODE FOR DPA RESPONSE PROCESSING
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
void MyDpaLibTimeoutHandler(void) 
{
	app_vars.dpaStep = NO_WAITING;

	// ANY USER CODE FOR OPERATION TIMEOUT HANDLING
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
void DpaLedG(uint16_t addr, uint8_t cmd) 
{
	app_vars.myDpaRequest.NAdr = addr;
	app_vars.myDpaRequest.PNum = PNUM_LEDG;
	app_vars.myDpaRequest.PCmd = cmd;
	app_vars.myDpaRequest.HwProfile = HWPID_DoNotCheck; // replace HW_PROFILE_DO_NOT_CHECK

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
void DpaLedR(uint16_t addr, uint8_t cmd)
{
	app_vars.myDpaRequest.NAdr = addr;
	app_vars.myDpaRequest.PNum = PNUM_LEDR;
	app_vars.myDpaRequest.PCmd = cmd;
	app_vars.myDpaRequest.HwProfile = HWPID_DoNotCheck; // replace HW_PROFILE_DO_NOT_CHECK

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
void MySwTimerTimeoutHandler(void) 
{
	app_vars.swTimerAck = TRUE;
}
//=============================================================================

/**
* Send DPA byte over UART
*
* @param 	Tx_Byte to send
* @return 	none
*
**/
void DPA_SendUartByte(uint8_t Tx_Byte) 
{
  app_vars.uartDone = 0;
  DPAUart.writeByte(Tx_Byte);
  while (!app_vars.uartDone);
}

//=============================================================================

/**
* Send DPA byte over SPI
*
* @param       Tx_Byte to send
* @return      Received Rx_Byte
*
**/
uint8_t DPA_SendSpiByte(uint8_t Tx_Byte) 
{
	app_vars.txbuf[0] = Tx_Byte;

	DPASpi.txrx(
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
void cb_compare(void) 
{

	// dpa timing
	DPA_SetTimmingFlag();	// set 1ms flag

	if (app_vars.dpaTimeoutCnt) 
	{
		if ((--app_vars.dpaTimeoutCnt) == 0) 
		{
			MyDpaLibTimeoutHandler();  // if timeout expired, call timeout handler
		}
	}

	// sw timer, call timeout handler
	if (app_vars.swTimer) 
	{							
		if ((--app_vars.swTimer) == 0) 
		{
			MySwTimerTimeoutHandler();
			app_vars.swTimer = USER_TIMER_PERIOD;
		}
	}

	// delay timer
	if (app_vars.ticks) 
	{
		--app_vars.ticks;
	}

}
//=============================================================================

/**
* UART Tx callback
*
* @param 	none
* @return 	none
*
**/
void cb_uartTxDone(void) 
{
  app_vars.uartDone = 1;
}
//=============================================================================

/**
* UART Rx callback
*
* @param 	none
* @return 	none
*
**/
void cb_uartRxCb(void) 
{
  uint8_t byte;
  // read received byte
  byte = DPAUart.readByte();
  // load that byte to dpa library
  DPA_ReceiveUartByte(byte);
}
//=============================================================================

