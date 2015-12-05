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

#ifndef _IQRFLIBRARY_H
#define _IQRFLIBRARY_H

//Target
//#define CHIPKIT
#define LEONARDO

#ifdef CHIPKIT
#include <WProgram.h>
#include <DSPI.h>
#endif

#ifdef LEONARDO
#include <Arduino.h>
#include <SPI.h>
#include <MsTimer2.h>
#endif

//uint8(16, 32)_t and NULL defines
#include <stddef.h>
#include <stdint.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define IQ_PKT_SIZE						68			// size of SPI TX and RX buffer
#define PACKET_BUFFER_SIZE		32			// size of SPI TX packet buffer
#define IQRF_SPI_CLK					250000		// SPI clk 250kHz

// MCU type of TR module
#define MCU_UNKNOWN						0
#define PIC16LF819  					1			  // TR-xxx-11A not supported
#define PIC16LF88   					2			  // TR-xxx-21A
#define PIC16F886   					3  			// TR-31B, TR-52B, TR-53B
#define PIC16LF1938   				4  			// TR-52D, TR-54D

// TR module types
#define TR_52D							0
#define TR_58D_RJ						1
#define TR_72D							2
#define TR_53D							3
#define TR_54D							8
#define TR_55D							9
#define TR_56D							10
#define TR_76D							11

// FCC cerificate
#define FCC_NOT_CERTIFIED			0
#define FCC_CERTIFIED					1

//******************************************************************************
//		 	SPI status of TR module (see IQRF SPI user manual)
//******************************************************************************
#define NO_MODULE			      0xFF	// SPI not working (HW error)
#define SPI_BUSY			      0xFE 	// SPI busy in Master disabled mode
#define SPI_DATA_TRANSFER   0xFD  // SPI data transfer in progress
#define SPI_DISABLED		    0x00	// SPI not working (disabled)
#define SPI_CRCM_OK			    0x3F	// SPI not ready (full buffer, last CRCM ok)
#define SPI_CRCM_ERR		    0x3E	// SPI not ready (full buffer, last CRCM error)
#define COMMUNICATION_MODE	0x80	// SPI ready (communication mode)
#define PROGRAMMING_MODE	  0x81	// SPI ready (programming mode)
#define DEBUG_MODE			    0x82	// SPI ready (debugging mode)
#define SPI_SLOW_MODE    	  0x83  // SPI not working in background
#define SPI_USER_STOP	      0x07  // state after stopSPI();

//******************************************************************************
//		 	SPI commands for TR module (see IQRF SPI user manual)
//******************************************************************************
#define SPI_CHECK  			  0x00  // Master checks the SPI status of the TR module
#define SPI_WR_RD 	 		  0xF0	// Master reads/writes a packet from/to TR module
#define SPI_RAM_READ  		0xF1	// Master reads data from ram in debug mode
#define SPI_EEPROM_READ		0xF2	// Master reads data from eeprom in debug mode
#define SPI_EEPROM_PGM		0xF3	// Master writes data to eeprom in programming mode
#define SPI_MODULE_INFO		0xF5	// Master reads Module Info from TR module
#define SPI_FLASH_PGM		  0xF6	// Master writes data to flash in programming mode
#define SPI_PLUGIN_PGM		0xF9	// Master writes plugin data to flash in programming mode

// IQRF TX packet result
#define IQRF_TX_PKT_OK		1		// packet sent OK
#define IQRF_TX_PKT_ERR		2		// packet sent with ERROR

// IQRF SPI master status
#define IQRF_SPI_MASTER_FREE	0
#define IQRF_SPI_MASTER_WRITE	1
#define IQRF_SPI_MASTER_READ	2

// Pins
#define TR_RESET_IO  		9
#define TR_SS_IO 			  10
#define TR_SDO_IO 			11
#define TR_SDI_IO 			12

// Timing
#define MICRO_SECOND		1000000
#define MILLI_SECOND		1000

#define FALSE  0
#define TRUE 1

typedef uint8_t  UINT8;
typedef uint16_t UINT16;
typedef uint32_t UINT32;
typedef unsigned long DWORD;

typedef void (*IQRF_RX_CALL_BACK)(void);									            // SPI RX data callback function type
typedef void (*IQRF_TX_CALL_BACK)(UINT8 pktId, UINT8 pktResult);			// SPI TX data callback function type

typedef struct{										// TR module info structure
	UINT16	osVersion;
	UINT16	osBuild;
	UINT32	moduleId;
	UINT16	mcuType;
	UINT16  moduleType;
	UINT16  fcc;
	UINT8	moduleInfoRawData[8];
} TR_INFO_STRUCT;

typedef struct{										// item of SPI TX packet buffer
	UINT8 pktId;
	UINT8 spiCmd;
	UINT8 *pDataBuffer;
	UINT8 dataLength;
	UINT8 unallocationFlag;
} IQRF_PACKET_BUFFER;

extern UINT8   DLEN, spiIqBusy;
extern UINT8   spiStat;
extern UINT8   iqrfSpiMasterEnable;
extern UINT8   fastIqrfSpiEnable;
extern TR_INFO_STRUCT	trInfoStruct;

#ifdef CHIPKIT
/* Unlike many chipKIT/Arduino libraries, the DSPI library doesn't
** automatically instantiate any interface objects. It is necessary
** to declare an instance of one of the interface objects in the
** sketch. This creates an object to talk to SPI port 0. Similarly,
** declaring a variable of type DSPI1, DSPI2, DSPI3, etc. will create
** an object to talk to DSPI port 1, 2, or 3.
*/
extern DSPI0    spi;
#endif

/***************************************************************************************************
* Function: void IQRF_Init(IQRF_RX_CALL_BACK rx_call_back_fn, IQRF_TX_CALL_BACK tx_call_back_fn);
*
* PreCondition: TickInit() for systick timer initialization must be called before     						
*
* Input: rx_call_back_fn  - Pointer to callback function. 
*				 Function is called when the driver receives data from the TR module
*		     tx_call_back_fn  - Pointer to callback function. 
*				 Function is called when the driver sent data to the TR module
* Output: none
*
* Side Effects: function performes initialization of trInfoStruct identification data structure
*
* Overview: function perform a TR-module driver initialization
*
* Note: none
*
***************************************************************************************************/
void IQRF_Init(IQRF_RX_CALL_BACK rx_call_back_fn, IQRF_TX_CALL_BACK tx_call_back_fn);

/***************************************************************************************************
* Function: void IQRF_Driver(void);
*
* PreCondition: IQRF_Init(rx_call_back_fn) must be called before 
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Overview: periodically called IQRF_Driver
*
* Note: none
*
***************************************************************************************************/
void IQRF_Driver(void);

/***************************************************************************************************
* Function: IQRF_TR_Reset(void);
*
* PreCondition: IQRF_Init(rx_call_back_fn) must be called before 
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Overview: function perform a TR-module reset
*
* Note: none
*
***************************************************************************************************/
void IQRF_TR_Reset(void);

/***************************************************************************************************
* Function: IQRF_TR_EnterProgMode(void);
*
* PreCondition: IQRF_Init(rx_call_back_fn) must be called before 
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Overview: function switch TR-module to programming mode
*
* Note: none
*
***************************************************************************************************/
void IQRF_TR_EnterProgMode(void);

/***************************************************************************************************
* Function: UINT8 IQRF_SendData(UINT8 *pDataBuffer, UINT8 dataLength, UINT8 unallocationFlag);
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly
*
* Input: pDataBuffer		  - pointer to a buffer that contains data that I want to send to TR module
*		     dataLength			  - number of bytes to send 
*		     unallocationFlag	- if the pDataBuffer is dynamically allocated using malloc function. 
*							  If you whish to unallocate buffer after data is sent, set the unallocationFlag
*							  to 1, otherwise to 0
*
* Output: TX packet ID		- number from 1 to 255
*
* Side Effects: none
*
* Overview: function sends data from buffer to TR module
*
* Note: none
*
***************************************************************************************************/
UINT8 IQRF_SendData(UINT8 *pDataBuffer, UINT8 dataLength, UINT8 unallocationFlag);

/***************************************************************************************************
* Function: void IQRF_GetRxData(UINT8 *userDataBuffer, UINT8 rxDataSize);
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly
*
* Input: userDataBuffer		- pointer to my buffer, to which I want to load data received from the TR module
*		     rxDataSize			  - number of bytes I want to read
*
* Output: none
*
* Side Effects: none
*
* Overview: function is usually called inside the callback function, whitch is called when the driver
*			      receives data from TR module
*
* Note: none
*
***************************************************************************************************/
void IQRF_GetRxData(UINT8 *userDataBuffer, UINT8 rxDataSize);

/***************************************************************************************************
* Macro: IQRF_GetRxDataSize();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly
*
* Input: none
*
* Output: returns number of bytes recieved from TR module
*
* Side Effects: none
*
* Note: none
*
*****************************************************************************************************/
#define IQRF_GetRxDataSize()	DLEN

/***************************************************************************************************
* Macro: IQRF_GetOsVersion();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before
*
* Input: none
*
* Output: returns version of OS used inside of TR module
*
* Side Effects: none
*
* Overview: macro returns UINT16 data word. HB - major version, LB - minor version
*
* Note: none
*
*****************************************************************************************************/
#define IQRF_GetOsVersion()		trInfoStruct.osVersion

/***************************************************************************************************
* Macro: IQRF_GetOsBuild();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before
*
* Input: none
*
* Output: returns build of OS used inside of TR module
*
* Side Effects: none
*
* Overview: macro returns UINT16 data word
*
* Note: none
*
*****************************************************************************************************/
#define IQRF_GetOsBuild()		trInfoStruct.osBuild

/***************************************************************************************************
* Macro: IQRF_GetModuleId();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before
*
* Input: none
*
* Output: returns a unique 32 bit identifier of TR module
*
* Side Effects: none
*
* Overview: macro returns unique 32 bit identifier data word
*
* Note: none
*
*****************************************************************************************************/
#define IQRF_GetModuleId()		trInfoStruct.moduleId

/***************************************************************************************************
* Macro: IQRF_GetMcuType();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before
*
* Input: none
*
* Output: returns identifier of MCU used inside TR module
*
* Side Effects: none
*
* Overview: macro returns UINT16 data word
*			0 - unknown type
*			1 - PIC16LF819
*			2 - PIC16LF88
*			3 - PIC16F886
*			4 - PIC16LF1938
*
* Note: none
*
*****************************************************************************************************/
#define IQRF_GetMcuType()		trInfoStruct.mcuType

/***************************************************************************************************
* Macro: IQRF_GetModuleType();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before
*
* Input: none
*
* Output: returns identifier of TR module
*
* Side Effects: none
*
* Overview: macro returns UINT16 data word
*			0 - TR_52D
*			1 - TR_58D_RJ
*			2 - TR_72D
*			8 - TR_54D
*			9 - TR_55D
*		  10 - TR_56D
*
* Note: none
*
*****************************************************************************************************/
#define IQRF_GetModuleType()		trInfoStruct.moduleType

/***************************************************************************************************
* Macro: IQRF_GetModuleInfoRawData(x);
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before
*
* Input: x - position in info raw buffer
*
* Output: returns data byte from info raw buffer
*
* Side Effects: none
*
* Note: none
*
*****************************************************************************************************/
#define IQRF_GetModuleInfoRawData(x)	trInfoStruct.moduleInfoRawData[x]

/***************************************************************************************************
* Macro: IQRF_GetStatus();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly
*
* Input: none
*
* Output: returns comunication status of TR module
*
* Side Effects: none
*
* Overview: macro returns TR module comunication status
*		 	0xFF - NO_MODULE	// SPI not working (HW error)
*     0xFE - SPI_BUSY		// SPI busi in Master disabled mode
*     0xFD - SPI_DATA_TRANSFER  // SPI data transfer in progress
* 		0x00 - SPI_DISABLED	// SPI not working (disabled)
* 		0x3F - SPI_CRCM_OK	// SPI not ready (full buffer, last CRCM ok)
* 		0x3E - SPI_CRCM_ERR	// SPI not ready (full buffer, last CRCM error)
* 		0x80 - COMMUNICATION_MODE	// SPI ready (communication mode)
* 		0x81 - PROGRAMMING_MODE	// SPI ready (programming mode)
* 		0x82 - DEBUG_MODE	// SPI ready (debugging mode)
*     0x83 - SPI_SLOW_MODE// SPI not working in background
* 	  0x07 - SPI_USER_STOP// state after stopSPI();
*
* Note: none
*
*****************************************************************************************************/
#define IQRF_GetStatus()		spiStat

/***************************************************************************************************
* Macro: IQRF_SPIMasterEnable();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Note: Enable SPI Master function in IQRF driver
*
*****************************************************************************************************/
#define IQRF_SPIMasterEnable()		iqrfSpiMasterEnable = 1

/***************************************************************************************************
* Macro: IQRF_SPIMasterDisable();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Note: Enable SPI Master function in IQRF driver
*
*****************************************************************************************************/
#define IQRF_SPIMasterDisable()		{iqrfSpiMasterEnable = 0; spiStat = SPI_DISABLED;}

/***************************************************************************************************
* Macro: IQRF_GetSPIMasterState();
*
* PreCondition: IQRF_Init(rx_call_back_fn, tx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly
*
* Input: none
*
* Output: State of IQRF SPI master 0 = Disabled, 1 = enabled
*
* Side Effects: none
*
* Note: Returns thw state of SPI Master function in IQRF driver
*
*****************************************************************************************************/
#define IQRF_GetSPIMasterState()	iqrfSpiMasterEnable


/**
 * Send and receive single byte over SPI
 *
 * @param Tx_Byte character to be send via SPI
 * @return byte received via SPI
 *
 **/
UINT8 IQRF_SPI_Byte(UINT8 Tx_Byte);

/**
 * Prepare SPI packet to packet buffer
 *
 * @param 
 * 		 - spiCmd			      - command that I want to send to TR module
 *		 - pDataBuffer		  - pointer to a buffer that contains data that I want to send to TR module
 *		 - dataLength		    - number of bytes to send 
 *		 - unallocationFlag	- if the pDataBuffer is dynamically allocated using malloc function. 
 *							  If you wish to unallocate buffer after data is sent, set the unallocationFlag
 *							  to 1, otherwise to 0
 * @return packet ID
 *
 **/
UINT8 TR_SendSpiPacket(UINT8 spiCmd, UINT8 *pDataBuffer, UINT8 dataLength, UINT8 unallocationFlag);

/**
 * Set byte to byte pause is SPI driver
 *
 * @param - byte to byte time in us
 * @return none
 *
 **/
void TR_SetByteToByteTime (UINT16 byteToByteTime);

#endif
