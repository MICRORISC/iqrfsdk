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

#ifndef _DPA_LIBRARY_H
#define _DPA_LIBRARY_H

// Uncomment for chipKIT boards
#include <WProgram.h>

#include <stddef.h>
#include <stdint.h>

//#define __SPI_INTERFACE__			// select for comunication via SPI
#define __UART_INTERFACE__			// select for comunication via UART

//#define __DPA_LIB_VER_2_0x			// select for DPA ver.2.00 or ver.2.01
//#define __DPA_LIB_VER_2_1x			// select for DPA ver.2.1x
#define __DPA_LIB_VER_2_2x				// select for DPA ver.2.2x

#define	DPA_MAX_DATA_LENGTH		 64

// library status
#define  DPA_READY				 0x00	 // DPA support library ready
#define  DPA_BUSY				 0x01	 // DPA request processing

//Peripheral numbers 
#define  PNUM_COORDINATOR        0x00 
#define  PNUM_NODE               0x01 
#define  PNUM_OS                 0x02 
#define  PNUM_EEPROM             0x03 
#define  PNUM_EEEPROM            0x04 
#define  PNUM_RAM                0x05 
#define  PNUM_LEDR               0x06 
#define  PNUM_LEDG               0x07 
#define  PNUM_SPI                0x08 
#define  PNUM_IO                 0x09 
#define  PNUM_THERMOMETER        0x0A 
#define  PNUM_PWM                0x0B 
#define  PNUM_UART               0x0C 
#define  PNUM_FRC                0x0D 
#define  PNUM_USER               0x20 
#define  PNUM_ERROR_FLAG         0xFE 

//Response Code 
#define  STATUS_NO_ERROR         		0   // No error 
#define  ERROR_FAIL              		1   // General fail 
#define  ERROR_PCMD              		2   // Incorrect PCmd 
#define  ERROR_PNUM              		3   // Incorrect PNum or PCmd 
#define  ERROR_ADDR              		4   // Incorrect Address 
#define  ERROR_DATA_LEN          		5   // Incorrect Data length 
#define  ERROR_DATA              		6   // Incorrect Data 
#define  ERROR_HWPROFILE         		7   // Incorrect HW Profile type used 
#define  ERROR_NADR              		8   // Incorrect NAdr 
#define  ERROR_IFACE_CUSTOM_HANDLER   	9  // Data from interface consumed by Custom DPA Handler 

#ifndef __DPA_LIB_VER_2_0x
#define  ERROR_MISSING_CUSTOM_DPA_HANDLER 	10  // Custom DPA Handler is missing
#endif
#define  STATUS_CONFIRMATION     			0xFF	// Error code used to mark confirmation 

//DPA Commands 
#define  CMD_COORDINATOR_ADDR_INFO  				0 
#define  CMD_COORDINATOR_DISCOVERED_DEVICES 		1 
#define  CMD_COORDINATOR_BONDED_DEVICES 			2 
#define  CMD_COORDINATOR_CLEAR_ALL_BONDS 			3 
#define  CMD_COORDINATOR_BOND_NODE 					4 
#define  CMD_COORDINATOR_REMOVE_BOND 				5 
#define  CMD_COORDINATOR_REBOND_NODE 				6 
#define  CMD_COORDINATOR_DISCOVERY 					7 
#define  CMD_COORDINATOR_SET_DPAPARAMS 				8 
#define  CMD_COORDINATOR_SET_HOPS 					9 
#define  CMD_COORDINATOR_DISCOVERY_DATA 			10 
#define  CMD_COORDINATOR_BACKUP 					11 
#define  CMD_COORDINATOR_RESTORE 					12
#define  CMD_COORDINATOR_AUTHORIZE_BOND                 13 
#define  CMD_COORDINATOR_READ_REMOTELY_BONDED_MID 	15 
#define  CMD_COORDINATOR_CLEAR_REMOTELY_BONDED_MID 	16 
#define  CMD_COORDINATOR_ENABLE_REMOTE_BONDING 		17 

#define  CMD_NODE_READ 								0 
#define  CMD_NODE_REMOVE_BOND 						1 
#define  CMD_NODE_READ_REMOTELY_BONDED_MID 			2 
#define  CMD_NODE_CLEAR_REMOTELY_BONDED_MID 		3 
#define  CMD_NODE_ENABLE_REMOTE_BONDING 			4 
#define  CMD_NODE_REMOVE_BOND_ADDRESS 				5 
#define  CMD_NODE_BACKUP 							6 
#define  CMD_NODE_RESTORE 							7 

#define  CMD_OS_READ 				0 
#define  CMD_OS_RESET 				1 
#define  CMD_OS_READ_CFG 			2 
#define  CMD_OS_RFPGM 				3 
#define  CMD_OS_SLEEP 				4 
#define  CMD_OS_BATCH 				5 
#define  CMD_OS_SET_USEC 			6 
#define  CMD_OS_SET_MID 			7 
#ifdef __DPA_LIB_VER_2_2x
#define  CMD_OS_RESTART 			8 
#define  CMD_OS_WRITE_CFG 			15 
#endif

#define  CMD_RAM_READ 				0 
#define  CMD_RAM_WRITE 				1 

#define  CMD_EEPROM_READ 			CMD_RAM_READ 
#define  CMD_EEPROM_WRITE 			CMD_RAM_WRITE 

#define  CMD_EEEPROM_READ 			CMD_RAM_READ 
#define  CMD_EEEPROM_WRITE 			CMD_RAM_WRITE 

#define  CMD_LED_SET_OFF 			0 
#define  CMD_LED_SET_ON 			1 
#define  CMD_LED_GET 				2 
#define  CMD_LED_PULSE 				3 

#define  CMD_SPI_WRITE_READ 		0 

#define  CMD_IO_DIRECTION  			0 
#define  CMD_IO_SET 				1 
#define  CMD_IO_GET 				2 

#define  CMD_THERMOMETER_READ 		0 

#define  CMD_PWM_SET 				0 

#define  CMD_UART_OPEN 				0 
#define  CMD_UART_CLOSE 			1 
#define  CMD_UART_WRITE_READ 		2 

#define  CMD_FRC_SEND 				0 
#define  CMD_FRC_EXTRARESULT 		1 
#ifdef __DPA_LIB_VER_2_2x
#define  CMD_FRC_SEND_SELECTIVE 	2 
#define  CMD_FRC_SET_PARAMS 		3
#endif

#define  CMD_GET_PER_INFO  			0x3F

// Peripheral Types 
#define  PERIPHERAL_TYPE_DUMMY 			0x00
#define  PERIPHERAL_TYPE_COORDINATOR 	0x01
#define  PERIPHERAL_TYPE_NODE 			0x02
#define  PERIPHERAL_TYPE_OS 			0x03
#define  PERIPHERAL_TYPE_EEPROM 		0x04
#define  PERIPHERAL_TYPE_BLOCK_EEPROM 	0x05
#define  PERIPHERAL_TYPE_RAM 			0x06
#define  PERIPHERAL_TYPE_LED 			0x07
#define  PERIPHERAL_TYPE_SPI 			0x08
#define  PERIPHERAL_TYPE_IO 			0x09
#define  PERIPHERAL_TYPE_UART 			0x0A
#define  PERIPHERAL_TYPE_THERMOMETER 	0x0B
#define  PERIPHERAL_TYPE_ADC 			0x0C
#define  PERIPHERAL_TYPE_PWM 			0x0D
#define  PERIPHERAL_TYPE_FRC 			0x0E
#define  PERIPHERAL_TYPE_USER_AREA 		0x80

// Custom DPA Handler Events 
#ifdef __DPA_LIB_VER_2_2x
#define  DpaEvent_DpaRequest      			0 
#define  DpaEvent_Interrupt      			1	 
#define  DpaEvent_Idle        				2 
#define  DpaEvent_Init        				3 
#define  DpaEvent_Notification    			4 
#define  DpaEvent_AfterRouting    			5 
#define  DpaEvent_BeforeSleep      			6 
#define  DpaEvent_AfterSleep      			7 
#define  DpaEvent_Reset      				8 
#define  DpaEvent_DisableInterrupts 	  	9	 
#define  DpaEvent_FrcValue      			10 
#define  DpaEvent_ReceiveDpaResponse    	11 
#define  DpaEvent_IFaceReceive    			12 
#define  DpaEvent_ReceiveDpaRequest   		13 
#define  DpaEvent_BeforeSendingDpaResponse 	14 
#define  DpaEvent_PeerToPeer      			15 
#define  DpaEvent_AuthorizePreBonding  		16 
#define  DpaEvent_UserDpaValue    			17 
#define  DpaEvent_FrcResponseTime    		18 
#endif

// Extended Peripheral Characteristic 
#define  PERIPHERAL_TYPE_EXTENDED_DEFAULT 		0x00
#define  PERIPHERAL_TYPE_EXTENDED_READ 			0x01
#define  PERIPHERAL_TYPE_EXTENDED_WRITE 		0x02 
#define  PERIPHERAL_TYPE_EXTENDED_READ_WRITE  	(PERIPHERAL_TYPE_EXTENDED_READ  | PERIPHERAL_TYPE_EXTENDED_WRITE)

// HW Profiles 
#ifdef __DPA_LIB_VER_2_0x
#define  HW_PROFILE_NONE 			0x0000    	// No HW Profile implemented 
#define  HW_PROFILE_USER_AREA	 	0x0101      // User HW Profile type area 
#define  HW_PROFILE_RESERVED_AREA	0xF000  	// Reserved HW Profile type area 
#define  HW_PROFILE_RESERVED 		0xFFFE      // Reserved HW Profile type 
#define  HW_PROFILE_DO_NOT_CHECK	0xFFFF      // Use this type to override HW Profile check 
#else
#define  HWPID_Default  			0x0000 		// No HW Profile specified
#define  HWPID_DoNotCheck 			0xFFFF 		// Use this type to override HW Profile ID check
#endif

// LED Colors
#define	 LED_COLOR_RED 			0
#define	 LED_COLOR_GREEN  		1
#define	 LED_COLOR_BLUE  		2
#define	 LED_COLOR_YELLOW  		3
#define	 LED_COLOR_WHITE  		4
#define	 LED_COLOR_UNKNOWN  	0xFF

//Baud rates
#define  DpaBaud_1200   		0x00
#define  DpaBaud_2400   		0x01
#define  DpaBaud_4800  			0x02
#define  DpaBaud_9600   		0x03
#define  DpaBaud_19200  		0x04
#define  DpaBaud_38400  		0x05
#define  DpaBaud_57600  		0x06
#define  DpaBaud_115200  		0x07

// User FRC Codes
#define  FRC_USER_BIT_FROM 		0x40
#define  FRC_USER_BIT_TO 		0x7F
#define  FRC_USER_BYTE_FROM 	0xC0
#define  FRC_USER_BYTE_TO    	0xDF 
#define  FRC_USER_2BYTE_FROM    0xF0 
#define  FRC_USER_2BYTE_TO    	0xFF 

// for DPA ver.2.10 and more
#ifndef __DPA_LIB_VER_2_0x
#define	NADR	NAdr
#define PNUM	PNum
#define PCMD	PCmd
#define HWPID	HwProfile
#endif

typedef struct{
	uint16_t  NAdr;
	uint8_t   PNum;
	uint8_t   PCmd;
	uint16_t  HwProfile;
	union{
		struct{
			uint8_t Data[DPA_MAX_DATA_LENGTH];
		}Plain;

		struct{
			uint8_t StatusConfirmation;
			uint8_t DpaValue;
			uint8_t Hops;
			uint8_t TimeSlotLength;
			uint8_t HopsResponse;
		}Confirmation;

		struct{
			uint8_t ResponseCode;
			uint8_t DpaValue;
			uint8_t Data[DPA_MAX_DATA_LENGTH];
		} Response;
	}Extension;
} T_DPA_PACKET;

typedef void(*T_DPA_ANSWER_HANDLER)(T_DPA_PACKET *DpaAnswer);			// DPA response callback function type

typedef struct{
	uint8_t	status;
	uint8_t	timeFlag;
	uint8_t	timeCnt;
	uint8_t	extraDataSize;
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
* Function: void DPA_SendRequest(T_DPA_PACKET *dpaRequest, uint8_t dataSize)
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
void DPA_SendRequest(T_DPA_PACKET *dpaRequest, uint8_t dataSize);

/***************************************************************************************************
* Function: uint16_t DPA_GetEstimatedTimeout(void)
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
uint16_t DPA_GetEstimatedTimeout(void);

/***************************************************************************************************
* Function: void DPA_ReceiveUartByte(uint8_t Rx_Byte)
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
void DPA_ReceiveUartByte(uint8_t Rx_Byte);

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
