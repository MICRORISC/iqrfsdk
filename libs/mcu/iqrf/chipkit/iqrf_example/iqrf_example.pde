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

//For IQRF
#include "iqrf_library.h"

// 1000@1ms = 1s 
#define USER_TIMER_PERIOD 1000

///////////////////////////////////////////////////////////////////////////////
//                            LOCAL PROTOTYPES
///////////////////////////////////////////////////////////////////////////////

void MyIqrfRxHandler(void);
void MyIqrfTxHandler(UINT8 txPktId, UINT8 txPktResult);
void AppTimerHandler(void);

#ifdef CHIPKIT
uint32_t cb_timer1ms(uint32_t currentTime);
#endif

///////////////////////////////////////////////////////////////////////////////
//                             GLOBAL VARIABLES 
///////////////////////////////////////////////////////////////////////////////

//App data
typedef struct 
{
	uint8_t myIqrfRxBuf[IQ_PKT_SIZE];
	uint8_t *myIqrfTxBuf;
	uint8_t testPktId;
	volatile uint16_t appTimer;
	volatile uint8_t appTimerAck;
} app_vars_t;

app_vars_t app_vars;

//Const data
const uint8_t testBuffer[64] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64};
const uint8_t ledPin = 43;

/*****************************************************************************/
/*****************************************************************************/

//=========================== init peripherals ================================
void setup(void)
{
	//user led
	pinMode(ledPin, OUTPUT);
	
    //up - PC
    Serial.begin(9600);

    //down - IQRF
	IQRF_Init(MyIqrfRxHandler, MyIqrfTxHandler);

	//info - TR
	switch( IQRF_GetModuleType() ) {
		case TR_52D: Serial.println("Module type: TR-52D"); break;
		case TR_72D: Serial.println("Module type: TR-72D"); break;
		default : Serial.println("Module type: UNKNOWN"); break;			
	}

#ifdef CHIPKIT
    //timing 1ms callback
    attachCoreTimerService(cb_timer1ms);
#endif

    //clear variables
    memset(&app_vars, 0, sizeof(app_vars_t));
    app_vars.appTimer = USER_TIMER_PERIOD;

    //done here
    Serial.println("Peripherals and IQRF init done");
}
/*---------------------------------------------------------------------------*/

//================================ main =======================================
void loop(void)
{
	// TR module SPI comunication driver
	IQRF_Driver();

	// Test send data every 1s
	if(app_vars.appTimerAck) {
		app_vars.myIqrfTxBuf = (uint8_t *)malloc(sizeof(testBuffer));								// allocate memory for Tx packet
		if (app_vars.myIqrfTxBuf != NULL){
			memcpy(app_vars.myIqrfTxBuf, (uint8_t *)&testBuffer, sizeof(testBuffer));  				// copy data from test to IQRF TX packet
			app_vars.testPktId = IQRF_SendData(app_vars.myIqrfTxBuf, sizeof(testBuffer), 1);		// send data and unallocate data buffer
		}
		app_vars.appTimerAck = FALSE;
	}
}
/*---------------------------------------------------------------------------*/

/**
* 1ms timer callback
*
* @param 	none or currentTime
* @return 	none or nextTime
*
**/
#ifdef CHIPKIT
uint32_t cb_timer1ms(uint32_t currentTime)
#endif
{
	// app timer, call handler
    if (app_vars.appTimer) 
    {							
        if ((--app_vars.appTimer) == 0) 
        {
            AppTimerHandler();
            app_vars.appTimer = USER_TIMER_PERIOD;
        }
    }
    
#ifdef CHIPKIT
    return (currentTime + CORE_TICK_RATE);
#endif
}
/*---------------------------------------------------------------------------*/

/**
* User timer handler
*
* @param 	none
* @return 	none
*
**/
void AppTimerHandler(void) 
{
    app_vars.appTimerAck = TRUE;
}
/*---------------------------------------------------------------------------*/

/**
* IQRF RX callback
*
* @param 	none
* @return 	none
*
**/
void MyIqrfRxHandler(void)
{
	// read and print received data
	IQRF_GetRxData(app_vars.myIqrfRxBuf, IQRF_GetRxDataSize());
						
	Serial.print("IQRF receive done: ");
	Serial.write(app_vars.myIqrfRxBuf, IQRF_GetRxDataSize());
	Serial.println();
}
/*---------------------------------------------------------------------------*/

/**
* IQRF TX callback
*
* @param 	
*		- txPktId 		Paket ID
*		- txPktResult	Paket writing result
* @return 	none
*
**/
void MyIqrfTxHandler(UINT8 txPktId, UINT8 txPktResult)
{
	Serial.println("IQRF send done");
}
/*---------------------------------------------------------------------------*/

