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

/*****************************************************************************
 *
 * DPA support library ver.0.91
 *
 *****************************************************************************/

#ifndef _DPA_LIBRARY_H
#define _DPA_LIBRARY_H

#define __SPI_INTERFACE__			// select for comunication via SPI
//#define __UART_INTERFACE__			// select for comunication via UART

//uint8(16)_t and NULL defines
#include <stdint.h>
#include <stddef.h>

//DPA defines
#include "DPA.h"

typedef uint8_t  UINT8;				// Define DPA.h types
typedef uint16_t UINT16;

// library status
#define  DPA_READY	0x00	 	// DPA support library ready
#define  DPA_BUSY		0x01	 	// DPA request processing

typedef struct{
	  UINT16  	NADR;
  	UINT8   	PNUM;
  	UINT8   	PCMD;
  	UINT16  	HWPID;
   	UINT8 		ResponseCode;
 	  UINT8 		DpaValue;
	  TDpaMessage	DpaMessage;
} T_DPA_PACKET;

typedef void (*T_DPA_ANSWER_HANDLER)(T_DPA_PACKET *DpaAnswer);			// DPA response callback function type

typedef struct{
	UINT8	status;
	UINT8	timeFlag;
	UINT8	timeCnt;
	UINT8	extraDataSize;
	T_DPA_ANSWER_HANDLER	dpaAnswerHandler;
	T_DPA_PACKET	*dpaRequestPacketPtr;
}T_DPA_CONTROL;

extern T_DPA_CONTROL	dpaControl;

/***************************************************************************************************
* Function: void DPA_Init(void)
*
* PreCondition: none 						
*
* Input: none 
*
* Output: none
*
* Side Effects: none
*
* Overview: function initialize DPA support library
*
* Note: none
*
***************************************************************************************************/
void DPA_Init(void);

/***************************************************************************************************
* Function: void DPA_LibraryDriver(void)
*
* PreCondition: DPA_Init() for library initialization must be called before 						
*
* Input: none 
*
* Output: none
*
* Side Effects: none
*
* Overview: function provides background communication with TR module
*
* Note: none
*
***************************************************************************************************/
void DPA_LibraryDriver(void);

/***************************************************************************************************
* Function: void DPA_SendRequest(T_DPA_PACKET *dpaRequest, UINT8 dataSize)
*
* PreCondition: DpaInit() for library initialization must be called before 						
*
* Input: dpaRequest	- pointer to DPA request packet
*        dataSize  	- number of additional data bytes in DPA request packet
*
* Output: none 
*
* Side Effects: none
*
* Overview: sends DPA request packet to desired destination address
*
* Note: none
*
***************************************************************************************************/
void DPA_SendRequest(T_DPA_PACKET *dpaRequest, UINT8 dataSize);

/***************************************************************************************************
* Function: UINT16 DPA_GetEstimatedTimeout(void)
*
* PreCondition: DpaInit() for library initialization must be called before 						
*
* Input: none
*
* Output: estimated timeout for response packet in ms (computed from confirmation packet data) 
*
* Side Effects: none
*
* Overview: returns estimated timeout for response packet in miliseconds
*
* Note: none
*
***************************************************************************************************/
UINT16 DPA_GetEstimatedTimeout(void);

/***************************************************************************************************
* Function: void DPA_ReceiveUartByte(UINT8 Rx_Byte)
*
* PreCondition: DPA_Init() for library initialization must be called before 						
*
* Input: Rx_Byte - received byte from UART modul
*
* Output: none
*
* Side Effects: none
*
* Overview: function transfers received byte from UART to DPA support library
*
* Note: none
*
***************************************************************************************************/
void DPA_ReceiveUartByte(UINT8 Rx_Byte);

/***************************************************************************************************
* Macro: DPA_GetStatus()
*
* PreCondition: none
*
* Input: none
*
* Output: DPA support library actual status
*
* Side Effects: none
*
* Overview: none
*
* Note: none
*
***************************************************************************************************/
#define DPA_GetStatus()			dpaControl.status

/***************************************************************************************************
* Macro: DPA_SetTimmingFlag()
*
* PreCondition: none
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Overview: setting time flag for DPA_LibraryDiver function
*
* Note: function must be called periodicaly every 1 ms
*
***************************************************************************************************/
#define DPA_SetTimmingFlag()  dpaControl.timeFlag = 1;

/***************************************************************************************************
* Macro: DPA_SetAnswerHandler(T_DPA_ANSWER_HANDLER newHandler)
*
* PreCondition: DPA_Init() for library initialization must be called before 						
*
* Input: pointer to user DPA response handler
*
* Output: none
*
* Side Effects: none
*
* Overview: macro sets pointer to user DPA response handler
*
* Note: none
*
***************************************************************************************************/
#define DPA_SetAnswerHandler(A1)	dpaControl.dpaAnswerHandler = A1

#endif

