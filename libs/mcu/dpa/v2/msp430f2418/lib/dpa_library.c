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

#include "dpa_library.h"

#ifdef __SPI_INTERFACE__

#define SPI_TRANSFER_NONE		0
#define SPI_TRANSFER_WRITE		1
#define SPI_TRANSFER_READ		2

#define SPI_CHECK  			0x00    // Master checks the SPI status of the TR module
#define SPI_WR_RD 	 		0xF0	// Master reads/writes a packet from/to TR module
#define SPI_CRCM_OK         0x3F    // SPI not ready (full buffer, last CRCM ok)
#define SPI_CRCM_ERR        0x3E    // SPI not ready (full buffer, last CRCM error)

#define SPI_STATUS_POOLING_TIME		5		// SPI status pooling time 5ms

typedef struct{
	UINT8	DLEN;
	UINT8	CMD;
	UINT8	PTYPE;
	UINT8	CRCM;
	UINT8	myCRCS;
	UINT8	CRCS;
	UINT8	spiStat;
	UINT8	direction;
	UINT8	packetLen;
	UINT8	packetCnt;
	UINT8   packetRpt;
	T_DPA_PACKET	*dpaPacketPtr;
} T_DPA_SPI_INTERFACE_CONTROL;

T_DPA_SPI_INTERFACE_CONTROL		dpaSpiIfControl;

UINT8 DPA_SendSpiByte(UINT8 Tx_Byte);
void DPA_SpiInterfaceDriver(void);
UINT8 DPA_GetCRCM(void); 	

#endif 

#ifdef __UART_INTERFACE__

#define UART_TRANSFER_NONE			0
#define UART_TRANSFER_WRITE			1
#define UART_TRANSFER_READ			2

#define HDLC_FRM_FLAG_SEQUENCE		0x7E
#define HDLC_FRM_CONTROL_ESCAPE		0x7D
#define HDLC_FRM_ESCAPE_BIT			0x20

typedef struct{
	UINT8	direction;
	UINT8	packetCnt;
	UINT8	packetLen;
	UINT8	CRC;
	UINT8	wasEscape;
	UINT8	rxBuffInPtr;
	UINT8	rxBuffOutPtr;
	UINT8	rxBuff[16];
	T_DPA_PACKET	*dpaPacketPtr;
} T_DPA_UART_INTERFACE_CONTROL;

T_DPA_UART_INTERFACE_CONTROL		dpaUartIfControl;

void DPA_SendUartByte(UINT8 Tx_Byte);
void DPA_ReceiveUartByte(UINT8 Rx_Byte);
void DPA_UartInterfaceDriver(void);
void DPA_SendDataByte(UINT8 dataByte);
UINT8 DPA_doCRC8(UINT8 inData, UINT8 seed);

#endif

//******************************************************************************
//		 	public variable declarations
//******************************************************************************
T_DPA_CONTROL		dpaControl;
T_DPA_PACKET 		dpaLibDpaAnswer;

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
void DPA_Init(void)
{
	dpaControl.status = DPA_READY;
	dpaControl.dpaAnswerHandler = NULL;
	dpaControl.timeFlag = 0;

	#ifdef __SPI_INTERFACE__
	dpaControl.timeCnt = SPI_STATUS_POOLING_TIME;
	dpaSpiIfControl.spiStat = 0;
	dpaSpiIfControl.direction = SPI_TRANSFER_NONE;
	#endif

	#ifdef __UART_INTERFACE__
	dpaUartIfControl.direction = UART_TRANSFER_NONE;
	dpaUartIfControl.wasEscape = 0;
	dpaUartIfControl.rxBuffInPtr = 0;
	dpaUartIfControl.rxBuffOutPtr = 0;
	#endif	
}

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
void DPA_LibraryDriver(void)
{
	#ifdef __SPI_INTERFACE__

	if (dpaControl.timeFlag){							
		dpaControl.timeFlag = 0;
		if (dpaControl.status == DPA_BUSY || !dpaControl.timeCnt){
			DPA_SpiInterfaceDriver();
			dpaControl.timeCnt = SPI_STATUS_POOLING_TIME + 1;
		}
		dpaControl.timeCnt--;
	}

	#endif

	#ifdef __UART_INTERFACE__
		DPA_UartInterfaceDriver();
	#endif
}

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
void DPA_SendRequest(T_DPA_PACKET *dpaRequest, UINT8 dataSize)
{
	dpaControl.dpaRequestPacketPtr = dpaRequest;
	dpaControl.extraDataSize = dataSize;
}

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
UINT16 DPA_GetEstimatedTimeout(void)
{
	UINT16 estimatedTimeout;

	estimatedTimeout = (UINT16)(dpaLibDpaAnswer.DpaMessage.IFaceConfirmation.Hops + 1) * (UINT16)dpaLibDpaAnswer.DpaMessage.IFaceConfirmation.TimeSlotLength * 10;
	if (dpaLibDpaAnswer.DpaMessage.IFaceConfirmation.TimeSlotLength == 20){
		estimatedTimeout += ((UINT16)(dpaLibDpaAnswer.DpaMessage.IFaceConfirmation.HopsResponse + 1) * 200 + 40);
	}
	else{
		estimatedTimeout += ((UINT16)(dpaLibDpaAnswer.DpaMessage.IFaceConfirmation.HopsResponse + 1) * 50 + 40);
	}
	return(estimatedTimeout);
}

#ifdef __SPI_INTERFACE__
/***************************************************************************************************
* Function: void DPA_SpiInterfaceDriver(void)
*
* PreCondition: SPI module must be initialized and user written function DPA_SendSpiByte must exist
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Overview: function implements IQRF packet communication over SPI with TR module
*
* Note: none
*
***************************************************************************************************/
void DPA_SpiInterfaceDriver(void)
{
	UINT8 last_spistat;
	UINT8 tempData = 0;
    

	if (dpaSpiIfControl.direction != SPI_TRANSFER_NONE){			// is anything to send / receive

		if (dpaSpiIfControl.direction == SPI_TRANSFER_WRITE){
			switch(dpaSpiIfControl.packetCnt){
				case 0: DPA_SendSpiByte(dpaSpiIfControl.CMD); dpaSpiIfControl.myCRCS = 0x5F; break; 							// send SPI CMD
				case 1: DPA_SendSpiByte(dpaSpiIfControl.PTYPE); dpaSpiIfControl.myCRCS ^= dpaSpiIfControl.PTYPE; break; 		// send PTYPE
				case 2: dpaSpiIfControl.myCRCS ^= DPA_SendSpiByte((dpaSpiIfControl.dpaPacketPtr->NADR) & 0x00FF); break;		// send LOW(NADR)
				case 3: dpaSpiIfControl.myCRCS ^= DPA_SendSpiByte((dpaSpiIfControl.dpaPacketPtr->NADR) >> 8); break;			// send HIGH(NADR)
				case 4: dpaSpiIfControl.myCRCS ^= DPA_SendSpiByte(dpaSpiIfControl.dpaPacketPtr->PNUM); break;					// send PNUM
				case 5: dpaSpiIfControl.myCRCS ^= DPA_SendSpiByte(dpaSpiIfControl.dpaPacketPtr->PCMD); break;					// send PCMD
				case 6: dpaSpiIfControl.myCRCS ^= DPA_SendSpiByte((dpaSpiIfControl.dpaPacketPtr->HWPID) & 0x00FF); break;		// send LOW(HWPID)
				case 7: dpaSpiIfControl.myCRCS ^= DPA_SendSpiByte((dpaSpiIfControl.dpaPacketPtr->HWPID) >> 8); break;			// send HIGH(HWPID)
				default:{
					if (dpaSpiIfControl.packetCnt == dpaSpiIfControl.packetLen-2){												// send CRCM / receive CRCS
						dpaSpiIfControl.CRCS = DPA_SendSpiByte(dpaSpiIfControl.CRCM);
					}
					else{
						if (dpaSpiIfControl.packetCnt == dpaSpiIfControl.packetLen-1){											// receive SPI_STATUS
							dpaSpiIfControl.spiStat = DPA_SendSpiByte(0);
						}
						else{																									// send additional data
							dpaSpiIfControl.myCRCS ^= DPA_SendSpiByte(dpaSpiIfControl.dpaPacketPtr->DpaMessage.Request.PData[dpaSpiIfControl.packetCnt-8]);
						}
					}
				}
			}
		}
		else{	
			if (dpaSpiIfControl.packetCnt>=2 && dpaSpiIfControl.packetCnt<dpaSpiIfControl.packetLen-2){
				tempData = DPA_SendSpiByte(0);
				dpaSpiIfControl.myCRCS ^= tempData;
			}

			switch(dpaSpiIfControl.packetCnt){
	
				case 0: DPA_SendSpiByte(dpaSpiIfControl.CMD); dpaSpiIfControl.myCRCS = 0x5F; break; 							// send SPI CMD
				case 1: DPA_SendSpiByte(dpaSpiIfControl.PTYPE); dpaSpiIfControl.myCRCS ^= dpaSpiIfControl.PTYPE; break; 		// send PTYPE
				case 2: dpaSpiIfControl.dpaPacketPtr->NADR = tempData; break;													// receive LOW(NADR)
				case 3: dpaSpiIfControl.dpaPacketPtr->NADR |= ((UINT16)tempData << 8); break;									// receive HIGH(NADR)
				case 4: dpaSpiIfControl.dpaPacketPtr->PNUM = tempData; break;													// receive PNUM
				case 5: dpaSpiIfControl.dpaPacketPtr->PCMD = tempData; break;													// receive PCMD
				case 6: dpaSpiIfControl.dpaPacketPtr->HWPID = tempData; break;													// receive LOW(HWPID)
				case 7: dpaSpiIfControl.dpaPacketPtr->HWPID |= ((UINT16)tempData << 8); break;									// receive HIGH(HWPID)				
				default:{
					if (dpaSpiIfControl.packetCnt == dpaSpiIfControl.packetLen-2){												// send CRCM / receive CRCS
						dpaSpiIfControl.CRCS = DPA_SendSpiByte(dpaSpiIfControl.CRCM);
					}
					else{
						if (dpaSpiIfControl.packetCnt == dpaSpiIfControl.packetLen-1){											// receive SPI_STATUS
							dpaSpiIfControl.spiStat = DPA_SendSpiByte(0);
						}
						else{																									
							if (dpaSpiIfControl.packetCnt == 8) dpaSpiIfControl.dpaPacketPtr->ResponseCode = tempData;			// receive ResponseCode
							else{
								if (dpaSpiIfControl.packetCnt == 9) dpaSpiIfControl.dpaPacketPtr->DpaValue = tempData;			// receive DpaValue
								else dpaSpiIfControl.dpaPacketPtr->DpaMessage.Response.PData[dpaSpiIfControl.packetCnt-10] = tempData; // receive additional data
							}
						}
					}
				}	
			}
		}
		dpaSpiIfControl.packetCnt++;											// counts number of send/receive bytes
																	
		if (dpaSpiIfControl.packetCnt == dpaSpiIfControl.packetLen){			// sent everything ? 				
			if ((dpaSpiIfControl.spiStat == SPI_CRCM_OK) && (dpaSpiIfControl.CRCS == dpaSpiIfControl.myCRCS)){	// CRC ok ?				
				if (dpaSpiIfControl.direction == SPI_TRANSFER_READ && dpaControl.dpaAnswerHandler != NULL){
					dpaControl.dpaAnswerHandler(&dpaLibDpaAnswer);				// call user response handler
				}
				dpaControl.status = DPA_READY;									// library is ready for next packet
				dpaSpiIfControl.direction = SPI_TRANSFER_NONE;					// stop data transfer
			}
			else{																// CRC error
				if (--dpaSpiIfControl.packetRpt){								// pktRepeats - must be set on packet preparing
					dpaSpiIfControl.packetCnt = 0;								// another attempt to send data
				}
				else{
					dpaControl.status = DPA_READY;								// library is ready for next packet
					dpaSpiIfControl.direction = SPI_TRANSFER_NONE;				// stop data transfer
				}
			}

			dpaSpiIfControl.spiStat = 0;										// current SPI status must be updated
		}
	}
	else{																		// no data to send => SPI status will be updated

		last_spistat = DPA_SendSpiByte(SPI_CHECK);								// get SPI status of TR module

 		if (dpaSpiIfControl.spiStat != last_spistat){							// the status must be 2x the same			
    		dpaSpiIfControl.spiStat = last_spistat;
  			return;
		}
 	     	
 	    if ((dpaSpiIfControl.spiStat & 0xC0) == 0x40){    						// if the status is dataready, prepare packet to read it
		    if (dpaSpiIfControl.spiStat == 0x40) dpaSpiIfControl.DLEN = 64;     // stav 0x40 znamena nabidku 64B          
		    else dpaSpiIfControl.DLEN = dpaSpiIfControl.spiStat & 0x3F; 		// clear bit 7,6 - rest is length (1 az 63B)
			dpaSpiIfControl.dpaPacketPtr = &dpaLibDpaAnswer;					// set pointer to DPA receive structure
           	dpaSpiIfControl.CMD = SPI_WR_RD;									// read / write data 
           	dpaSpiIfControl.PTYPE = dpaSpiIfControl.DLEN;
           	dpaSpiIfControl.CRCM = 0x5F ^ dpaSpiIfControl.CMD ^ dpaSpiIfControl.PTYPE;		// CRCM
           	
           	dpaSpiIfControl.packetLen = dpaSpiIfControl.DLEN + 4;				// length of whole packet + (CMD, PTYPE, CRCM, 0) 
			dpaSpiIfControl.packetCnt = 0;										// counter of sent bytes
			dpaSpiIfControl.packetRpt = 1;										// number of attempts to send data
				
			dpaSpiIfControl.direction = SPI_TRANSFER_READ;						// reading from buffer COM of TR module
			dpaSpiIfControl.spiStat = 0;										// current SPI status must be updated
			dpaControl.status = DPA_BUSY;										// library si busy
			return;
		}

		if (dpaControl.dpaRequestPacketPtr != NULL){							// check if packet to send is ready
			dpaSpiIfControl.dpaPacketPtr = dpaControl.dpaRequestPacketPtr;		// set pointer to DpaRequest packet 
			dpaControl.dpaRequestPacketPtr = NULL;

			dpaSpiIfControl.DLEN = dpaControl.extraDataSize + 6;				// NADR + PNUM + PCMD + HWPID + Data 			
           	dpaSpiIfControl.CMD = SPI_WR_RD;
           	dpaSpiIfControl.PTYPE = (dpaSpiIfControl.DLEN | 0x80);				// PBYTE set bit7 - write to buffer COM of TR module 
			dpaSpiIfControl.CRCM = DPA_GetCRCM();								// CRCM

           	dpaSpiIfControl.packetLen = dpaSpiIfControl.DLEN + 4;				// length of whole packet + (CMD, PTYPE, CRCM, 0) 
			dpaSpiIfControl.packetCnt = 0;										// counter of sent bytes
			dpaSpiIfControl.packetRpt = 3;										// number of attempts to send data
				
			dpaSpiIfControl.direction = SPI_TRANSFER_WRITE;						// reading from buffer COM of TR module
			dpaSpiIfControl.spiStat = 0;										// current SPI status must be updated
			dpaControl.status = DPA_BUSY;										// library si busy
		}
    }
}

/**
 * Calculate CRC before master's send
 *
 * @param none
 * @return crc_val 
 *
 **/
UINT8 DPA_GetCRCM(void) 	// see IQRF SPI user manual
{
 	unsigned char i, crc_val, dataSize;

	crc_val = 0x5F;													// initialize CRC
	crc_val ^= dpaSpiIfControl.CMD;									// add SPI CMD
	crc_val ^= dpaSpiIfControl.PTYPE;								// add PTYPE
	crc_val ^= dpaSpiIfControl.dpaPacketPtr->NADR & 0x00FF;			// add LOW(NADR)
	crc_val ^= dpaSpiIfControl.dpaPacketPtr->NADR >> 8;				// add HIGH(NADR)
	crc_val ^= dpaSpiIfControl.dpaPacketPtr->PNUM;					// add PNUM
	crc_val ^= dpaSpiIfControl.dpaPacketPtr->PCMD;					// add PCMD
	crc_val ^= dpaSpiIfControl.dpaPacketPtr->HWPID & 0x00FF;		// add LOW(HWPID)
	crc_val ^= dpaSpiIfControl.dpaPacketPtr->HWPID >> 8;			// add HIGH(HWPID)
	
	dataSize = dpaSpiIfControl.DLEN - 6;							// number of extra data bytes (except NADR, PNUM, PCMD, HWPID)
	for (i=0; i<dataSize;i++){
		crc_val ^= dpaSpiIfControl.dpaPacketPtr->DpaMessage.Request.PData[i];
	}

	return crc_val;
}
//*************************************************************************** 
#endif


#ifdef __UART_INTERFACE__
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
void DPA_ReceiveUartByte(UINT8 Rx_Byte)
{
	dpaUartIfControl.rxBuff[dpaUartIfControl.rxBuffInPtr++] = Rx_Byte;			// write received byte to Rx FiFo
	dpaUartIfControl.rxBuffInPtr &= 0x0F;										// this makes 16 bytes circle buffer
}

/***************************************************************************************************
* Function: void DPA_UartInterfaceDriver(void)
*
* PreCondition: UART module must be initialized and user written function DPA_SendUartByte must exist
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Overview: function implements IQRF packet communication over UART with TR module
*
* Note: none
*
***************************************************************************************************/

void DPA_UartInterfaceDriver(void)
{
	UINT8 tempData = 0; 

	if (dpaUartIfControl.direction != UART_TRANSFER_NONE){										// is anything to send / receive

		if (dpaUartIfControl.direction == UART_TRANSFER_READ){
			if (dpaUartIfControl.rxBuffInPtr == dpaUartIfControl.rxBuffOutPtr) return;			// no data in Rx buffer
			tempData = dpaUartIfControl.rxBuff[dpaUartIfControl.rxBuffOutPtr++];				// read byte from Rx buffer
			dpaUartIfControl.rxBuffOutPtr &= 0x0F;												// this makes 16 bytes circle buffer

			if (tempData == HDLC_FRM_FLAG_SEQUENCE || dpaUartIfControl.packetCnt == dpaUartIfControl.packetLen){	// end of packet or DPA structure is full
				if (dpaUartIfControl.CRC == 0 && dpaControl.dpaAnswerHandler != NULL){
					dpaControl.dpaAnswerHandler(&dpaLibDpaAnswer);								// call user response handler
				}
				dpaControl.status = DPA_READY;													// library is ready for next packet
				dpaUartIfControl.direction = UART_TRANSFER_NONE;								// stop data transfer
				return;
			}

			if (tempData == HDLC_FRM_CONTROL_ESCAPE){											// discard received ESCAPE character
				dpaUartIfControl.wasEscape = 1;
				return;
			}

			if (dpaUartIfControl.wasEscape){													// previous character was ESCAPE
				dpaUartIfControl.wasEscape = 0;
				tempData ^= HDLC_FRM_ESCAPE_BIT;		
			}

			dpaUartIfControl.CRC = DPA_doCRC8(tempData, dpaUartIfControl.CRC);					// add Rx byte to CRC

			switch(dpaUartIfControl.packetCnt){	
				case 0: dpaUartIfControl.dpaPacketPtr->NADR = tempData; break;   				// receive LOW(NADR)
				case 1: dpaUartIfControl.dpaPacketPtr->NADR |= ((UINT16)tempData << 8); break;	// receive HIGH(NADR)	
				case 2: dpaUartIfControl.dpaPacketPtr->PNUM = tempData; break;					// receive PNUM
				case 3: dpaUartIfControl.dpaPacketPtr->PCMD = tempData;	break;					// receive PCMD
				case 4: dpaUartIfControl.dpaPacketPtr->HWPID = tempData; break;					// receive LOW(HWPID)
				case 5: dpaUartIfControl.dpaPacketPtr->HWPID |= ((UINT16)tempData << 8); break;	// receive HIGH(HWPID)
				case 6: dpaUartIfControl.dpaPacketPtr->ResponseCode = tempData; break;			// receive ResponseCode
				case 7: dpaUartIfControl.dpaPacketPtr->DpaValue = tempData; break;				// receive DpaValue
				default: dpaUartIfControl.dpaPacketPtr->DpaMessage.Response.PData[dpaUartIfControl.packetCnt-8] = tempData;	// receive additional data
			}
			dpaUartIfControl.packetCnt++;															// counts number of send/receive byte
		}
		else{	
			switch(dpaUartIfControl.packetCnt){
				case 0: DPA_SendDataByte((dpaUartIfControl.dpaPacketPtr->NADR) & 0x00FF); break; 	// send LOW(NADR)
				case 1: DPA_SendDataByte((dpaUartIfControl.dpaPacketPtr->NADR) >> 8); break;		// send HIGH(NADR)
				case 2: DPA_SendDataByte(dpaUartIfControl.dpaPacketPtr->PNUM); break;				// send PNUM
				case 3: DPA_SendDataByte(dpaUartIfControl.dpaPacketPtr->PCMD); break;				// send PCMD
				case 4: DPA_SendDataByte((dpaUartIfControl.dpaPacketPtr->HWPID) & 0x00FF); break; 	// send LOW(HWPID)
				case 5: DPA_SendDataByte((dpaUartIfControl.dpaPacketPtr->HWPID) >> 8); break;		// send HIGH(HWPID)
				default: DPA_SendDataByte(dpaUartIfControl.dpaPacketPtr->DpaMessage.Request.PData[dpaUartIfControl.packetCnt-6]); // send additional data
			}	
			dpaUartIfControl.packetCnt++;															// counts number of send/receive bytes																		
			if (dpaUartIfControl.packetCnt == dpaUartIfControl.packetLen){						// sent everything ? 				
				DPA_SendDataByte(dpaUartIfControl.CRC);											// send CRC
				DPA_SendUartByte(HDLC_FRM_FLAG_SEQUENCE);										// send stop of packet character
				dpaControl.status = DPA_READY;													// library is ready for next packet
				dpaUartIfControl.direction = UART_TRANSFER_NONE;								// stop data transfer
			}
		}
	}
	else{														// no data to send / receive => check for new data
		if (dpaUartIfControl.rxBuffInPtr != dpaUartIfControl.rxBuffOutPtr){					// data in Rx buffer
			tempData = dpaUartIfControl.rxBuff[dpaUartIfControl.rxBuffOutPtr++];			// read byte from Rx buffer
			dpaUartIfControl.rxBuffOutPtr &= 0x0F;											// this makes 16 bytes circle buffer

			if (tempData  == HDLC_FRM_FLAG_SEQUENCE){										// start of packet

				dpaUartIfControl.dpaPacketPtr = &dpaLibDpaAnswer;							// set pointer to DPA receive structure

	           	dpaUartIfControl.CRC = 0xFF;												// initialize CRC

	           	dpaUartIfControl.packetLen = sizeof(T_DPA_PACKET);							// maximal size of received data
				dpaUartIfControl.packetCnt = 0;												// counter of received bytes
				dpaUartIfControl.wasEscape = 0;												// clear Escape flag
					
				dpaUartIfControl.direction = UART_TRANSFER_READ;							// reading from TR module
				dpaControl.status = DPA_BUSY;												// library si busy
			}
			return;
		}

		if (dpaControl.dpaRequestPacketPtr != NULL){										// check if packet to send is ready
			dpaUartIfControl.dpaPacketPtr = dpaControl.dpaRequestPacketPtr;					// set pointer to DpaRequest packet 
			dpaControl.dpaRequestPacketPtr = NULL;

			dpaUartIfControl.packetLen = dpaControl.extraDataSize + 6;						// NADR + PNUM + PCMD + HWPID + Data 			

           	dpaUartIfControl.CRC = 0xFF;													// initialize CRC

			dpaUartIfControl.packetCnt = 0;													// counter of sent bytes				
			dpaUartIfControl.direction = UART_TRANSFER_WRITE;								// write data to TR module
			dpaControl.status = DPA_BUSY;													// library si busy

			DPA_SendUartByte(HDLC_FRM_FLAG_SEQUENCE);										// send start of packet character
		}
    }
}

/**
 * send data byte to TR module + make HDLC byte stuffing and comute CRC
 *
 * @param data byte for TR module
 * @return none
 *
 **/
void DPA_SendDataByte(UINT8 dataByte)
{
	if (dataByte == HDLC_FRM_FLAG_SEQUENCE || dataByte == HDLC_FRM_CONTROL_ESCAPE){
		DPA_SendUartByte(HDLC_FRM_CONTROL_ESCAPE);
		DPA_SendUartByte(dataByte ^ HDLC_FRM_ESCAPE_BIT);
	}
	else DPA_SendUartByte(dataByte);
	
	dpaUartIfControl.CRC = DPA_doCRC8(dataByte, dpaUartIfControl.CRC);
}

/**
 *  Compute the CRC8 value of a data set
 *
 * @param 	inData  One byte of data to compute CRC from
 *			seed    The starting value of the CRC
 *
 * @return	The CRC8 of inData with seed as initial value
 *
 **/
UINT8 DPA_doCRC8(UINT8 inData, UINT8 seed)
{
    UINT8 bitsLeft;

    for (bitsLeft = 8; bitsLeft > 0; bitsLeft--){
        if (((seed ^ inData) & 0x01) == 0) seed >>= 1;
        else seed = (seed >>= 1)^0x8C;
        inData >>= 1;
    }
    return seed;    
}

//*************************************************************************** 
#endif
