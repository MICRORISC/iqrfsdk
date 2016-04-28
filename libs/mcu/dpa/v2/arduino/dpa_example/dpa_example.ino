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

//for ARDUINO BOARDS
#include <SPI.h>
#include <MsTimer2.h>

//for CHIPKIT BOARDS
//#include <DSPI.h>

//dpa library
#include "dpa_library.h"

//=========================== defines =========================================
//#define UNO32
#define LEONARDO

#define USER_TIMER_PERIOD		1000		// 1000@1ms = 1s
#define TRUE                1
#define FALSE               0

#define CONFIRMATION		    1
#define RESPONSE			      2

#define NO_WAITING			      0
#define CONFIRMATION_WAITING	1
#define RESPONSE_WAITING		  2

#define COORDINATOR			0
#define NODE1				    1
#define NODE2				    2
#define NODE3				    3
#define NODE4				    4
#define NODE5				    5
#define LOCAL           0xFC

#define SS              10
//=========================== variables =======================================
typedef enum 
{
    GLED_PULSE,
	  RLED_PULSE
}DPA_RQ;

typedef struct 
{
	// timer
	volatile uint16_t swTimer;
	volatile uint8_t swTimerAck;
	volatile uint8_t ticks;

	// dpa
	volatile uint16_t dpaTimeoutCnt;
	uint16_t dpaStep;
  uint8_t state;
	T_DPA_PACKET myDpaRequest;
	DPA_RQ cmds;
} app_vars_t;

app_vars_t app_vars;

#ifdef UNO32
/* Unlike many chipKIT/Arduino libraries, the DSPI library doesn't
** automatically instantiate any interface objects. It is necessary
** to declare an instance of one of the interface objects in the
** sketch. This creates an object to talk to SPI port 0. Similarly,
** declaring a variable of type DSPI1, DSPI2, DSPI3, etc. will create
** an object to talk to DSPI port 1, 2, or 3.
*/
DSPI0    spi;
#endif

//=========================== prototypes ======================================

//dpa handlers
void MyDpaAnswerHandler(T_DPA_PACKET *MyDpaAnswer);
void MyDpaLibTimeoutHandler(void);
void MyDpaLibRequests(void);

//dpa requests
void DpaLedG(uint16_t addr, uint8_t cmd);
void DpaLedR(uint16_t addr, uint8_t cmd);

//dpa driver timing
#ifdef LEONARDO
void cb_timer1ms(void);
#endif
#ifdef UNO32
uint32_t cb_timer1ms(uint32_t currentTime);
#endif

//user timer
void MySwTimerTimeoutHandler(void);

//serial check and read
void MySerialEvent(void);

//=========================== init peripherals ============================================
void setup()
{
    //up - PC
    Serial.begin(9600);
    
    //down - DCTR 
#ifdef __UART_INTERFACE__
    Serial1.begin(115200);
#endif
#ifdef __SPI_INTERFACE__
#ifdef LEONARDO
    pinMode( SS, OUTPUT );
    digitalWrite( SS, HIGH );

    SPI.begin();
#endif
#ifdef UNO32
    pinMode( SS, OUTPUT );
    digitalWrite( SS, HIGH );

    spi.begin();
    spi.setSpeed(250000);
    spi.setPinSelect(SS);
#endif
#endif
    
    //timer 1ms
#ifdef LEONARDO
    MsTimer2::set(1, cb_timer1ms);
    MsTimer2::start();
#endif
#ifdef UNO32
    attachCoreTimerService(cb_timer1ms);
#endif

    //clear local variable
    memset(&app_vars, 0, sizeof(app_vars_t));
    app_vars.swTimer = USER_TIMER_PERIOD;
    app_vars.state = 1;
    
    //dpa library initialization
    DPA_Init();
    DPA_SetAnswerHandler(MyDpaAnswerHandler);   // set DPA response handler

    //waiting 1s
    delay(1000);

    //done here
    Serial.println("Peripheral and DPA init done");
}

//=========================== main ============================================
void loop()
{   
    //check and read serial data 
#ifdef __UART_INTERFACE__   
    MySerialEvent();
#endif
    
    //sending dpa requests
    if (app_vars.swTimerAck == TRUE) 
    {
      MyDpaLibRequests();				    // function for sending DPA requests - SPI/UART
      app_vars.swTimerAck = FALSE;
    }

    //run dpa driver
    DPA_LibraryDriver();					// DPA library handler
}
//=============================================================================

/**
* Serial checking and reading
*
* @param     none
* @return   none
*
**/
void MySerialEvent(void)
{
    uint8_t byte;

    while(Serial1.available())
    {
        // read received byte
        byte = Serial1.read();
    
        // load that byte to dpa library
        DPA_ReceiveUartByte(byte);
    }
} 
//=============================================================================

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
    while (app_vars.ticks)
        ;

    switch (app_vars.state) {
        case 1:
            DpaLedR(COORDINATOR, CMD_LED_PULSE);
            Serial.println("LEDR pulse");
            app_vars.state = 2;
	    break;

        case 2:
	        DpaLedG(COORDINATOR, CMD_LED_PULSE); 
            Serial.println("LEDG pulse");
            app_vars.state = 1;
	    break;

        default:
        break;
    }

    if (app_vars.myDpaRequest.NADR == 0x00 || app_vars.myDpaRequest.NADR == 0xFC)
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
        && dpaAnswerPkt->ResponseCode
        == STATUS_CONFIRMATION) 
    {
        app_vars.dpaStep = RESPONSE_WAITING;
        app_vars.dpaTimeoutCnt = DPA_GetEstimatedTimeout();
        return;
    }

    if (app_vars.dpaStep == RESPONSE_WAITING
        && dpaAnswerPkt->ResponseCode == STATUS_NO_ERROR) 
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
* DPA deselect module
*
* @param   none
* @return   none
*
**/
void DPA_DeselectTRmodule(void)
{
    pinMode( SS, OUTPUT );
    digitalWrite( SS, HIGH );
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
    app_vars.myDpaRequest.NADR = addr;
    app_vars.myDpaRequest.PNUM = PNUM_LEDG;
    app_vars.myDpaRequest.PCMD = cmd;
    app_vars.myDpaRequest.HWPID = HWPID_DoNotCheck; // replace HW_PROFILE_DO_NOT_CHECK

    // initial timeout 1s for either confirmation or response
    app_vars.dpaTimeoutCnt = 1000;

    DPA_SendRequest(&app_vars.myDpaRequest, 0);
}
//=============================================================================

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
    app_vars.myDpaRequest.NADR = addr;
    app_vars.myDpaRequest.PNUM = PNUM_LEDR;
    app_vars.myDpaRequest.PCMD = cmd;
    app_vars.myDpaRequest.HWPID = HWPID_DoNotCheck; // replace HW_PROFILE_DO_NOT_CHECK

    // initial timeout 1s for either confirmation or response
    app_vars.dpaTimeoutCnt = 1000;

    DPA_SendRequest(&app_vars.myDpaRequest, 0);
}
//=============================================================================

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
* 1ms timer callback
*
* @param 	none or currentTime
* @return 	none or nextTime
*
**/
#ifdef LEONARDO
void cb_timer1ms(void) 
#endif
#ifdef UNO32
uint32_t cb_timer1ms(uint32_t currentTime)
#endif
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
#ifdef UNO32
    return (currentTime + CORE_TICK_RATE);
#endif
}
//=============================================================================

/**
* Send DPA byte over UART
*
* @param     Tx_Byte to send
* @return   none
*
**/
void DPA_SendUartByte(uint8_t Tx_Byte) 
{
    Serial1.write(Tx_Byte);
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
    uint8_t Rx_Byte;    

#ifdef LEONARDO
    digitalWrite( SS, LOW );
    delayMicroseconds( 15 );

    SPI.beginTransaction(SPISettings(250000, MSBFIRST, SPI_MODE0));
    Rx_Byte = SPI.transfer( Tx_Byte );
    SPI.endTransaction();
  
    delayMicroseconds( 15 );
    digitalWrite( SS, HIGH );
#endif
#ifdef UNO32
    spi.setSelect(LOW);
    delayMicroseconds( 15 );

    spi.transfer(1, Tx_Byte, &Rx_Byte);

    delayMicroseconds( 15 );
    spi.setSelect(HIGH);
#endif
  
    return Rx_Byte;
}
//=============================================================================

