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
#include "iqrf_pgm.h"

//For MPFS2
#include "mchp/TCPIP.h"

// 5000@1ms = 5s 
#define USER_TIMER_PERIOD 5000
#define FILE_NUMBER 1

///////////////////////////////////////////////////////////////////////////////
//                            LOCAL PROTOTYPES
///////////////////////////////////////////////////////////////////////////////

void MyIqrfRxHandler(void);
void MyIqrfTxHandler(UINT8 txPktId, UINT8 txPktResult);
void AppTimerHandler(void);

UINT8 TrProgrammer(IQRF_FW_HEADER *TrFWptr);

#ifdef CHIPKIT
uint32_t cb_timer1ms(uint32_t currentTime);
#endif

#ifdef LEONARDO
void cb_timer1ms(void);
#endif

///////////////////////////////////////////////////////////////////////////////
//                             GLOBAL VARIABLES 
///////////////////////////////////////////////////////////////////////////////

//App data
typedef struct 
{
	// TR - APP
	uint8_t myIqrfRxBuf[IQ_PKT_SIZE];
	uint8_t *myIqrfTxBuf;
	uint8_t testPktId;

	// TR - APP programmer
	IQRF_FW_HEADER	*TrModuleData;
	uint8_t		storageInitialized;
	uint8_t  	programmerActive;

	volatile uint16_t appTimer;
	volatile uint8_t appTimerAck;
} app_vars_t;

app_vars_t app_vars;

//Const data
const uint8_t testBuffer[64] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64};

#ifdef CHIPKIT
const uint8_t ledPin = 43;
#endif

#ifdef LEONARDO
const uint8_t ledPin = 13;
#endif

/*****************************************************************************/
/*****************************************************************************/

//=========================== init peripherals ================================
void setup(void)
{
	//clear variables
    memset(&app_vars, 0, sizeof(app_vars_t));

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

	#if (TR_APP_STORAGE == SD_CARD)
		if (FSInit()) app_vars.storageInitialized = TRUE;
	#else
		MPFSInit();
		app_vars.storageInitialized = TRUE;
	#endif

	if (storageInitialized == TRUE) {														// if filesystem is initialized
		if (IQRF_GetModuleType()==TR_72D || IQRF_GetModuleType()==TR_76D) {
			switch(FILE_NUMBER) {
				case 1: app_vars.TrModuleData = IQRF_FwPreprocess("tr_app1_7.hex"); break;			// 1. application file
				case 2: app_vars.TrModuleData = IQRF_FwPreprocess("tr_app2_7.hex"); break;			// 2. application file
				case 3: app_vars.TrModuleData = IQRF_PlugInPreprocess("tr_app3_7.iqrf"); break;		// 3. application file
				case 4:	app_vars.TrModuleData = IQRF_CnfgPreprocess("tr_cfg.trcnfg"); break;		// configuration file
			}
		}
	}
	else app_vars.TrModuleData=NULL;

	if (app_vars.TrModuleData != NULL) {				// if application or config is corect	
		app_vars.programmerActive = TRUE;				// run programmer
	}

#ifdef CHIPKIT
    //timing 1ms callback
    attachCoreTimerService(cb_timer1ms);
#endif

#ifdef LEONARDO
    //MsTimer2::set(1, cb_timer1ms);
    //MsTimer2::start();
#endif

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

	if (app_vars.programmerActive == TRUE){
		switch (IQRF_FwWrite(app_vars.TrModuleData, PGM_SPI)){				// call programming function	
			case IQRF_PGM_SUCCESS:{
				Serial.println("Upload successful");
				app_vars.programmerActive = FALSE;							// programmer END
			}
			break;

			case IQRF_PGM_ERROR:{
				Serial.println("Upload failed"); 
				app_vars.programmerActive = FALSE;							// programmer END
			}
			break;

			default:;
		}
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
#ifdef LEONARDO
void cb_timer1ms(void) 
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
