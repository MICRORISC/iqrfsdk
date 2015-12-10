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

// For IQRF
#include "iqrf_library.h"
#include "iqrf_pgm.h"

//For MPFS2
#include "mchp/TCPIP.h"

//******************************************************************************
//		 	locally used function prototypes
//******************************************************************************
UINT8 ConvertToNum(UINT8 dataByteHi, UINT8 dataByteLo);
UINT8 ReadFWFileByte(void);
UINT8 ReadHEXFileLine(void);
UINT8 ReadTextLine(void);
UINT8 ReadIQRFFileLine(void);

//******************************************************************************
//		 	public variable declarations
//******************************************************************************

typedef enum {
	TR_INIT_PROG_SM = 0,
	TR_ENTER_PROG_MODE,
	TR_WAIT_PROG_MODE,
	TR_WRITE_EEPROM,
	TR_INIT_FLASH_WRITE,
	TR_WRITE_FLASH,
	TR_WRITE_PLUGIN,
	TR_WAIT_PROG_END,
	TR_PROG_END,
} TR_FW_WRITE_SM;

TR_FW_WRITE_SM  TR_fwWriteSm;

RF_PGM_CFG rfPgmCfg;

UINT8 	TR_writePacket;	
DWORD 	TR_programTick;

#define READ_BLOK_SIZE		512
UINT16	fileReadBufferPtr;
UINT8 	fileReadBuffer[READ_BLOK_SIZE];
UINT8 	FWLineBuffer[40];
UINT8   FWLineBufferCnt;
UINT8   textLineBuffer[64];
UINT32	hiAddress;
DWORD	procesByteCnt;
UINT16	halfOfBlockCnt;
UINT16	writeLicencedFlash;
UINT16	flashBlockToWrite;

#if (TR_APP_STORAGE == SD_CARD)
	FSFILE  *firmware;
#else
	MPFS_HANDLE	firmware;
#endif

const UINT8 endPgmMode[] = {0xDE,0x01,0xFF};

extern UINT8 spiIqBusy;
extern UINT8 spiStat;
extern UINT16 iqrfPacketBufferInPtr, iqrfPacketBufferOutPtr;

/***************************************************************************************************
* Function: void IQRF_SendRFPgmConfig(RF_PGM_CFG *rfPgmCfgStuct)
*
* PreCondition: TR module loaded with RFPGM PlugIn module
*
* Input: rfPgmCfgStruct - pointer to RF programmer configuration structure
*
* Output: none
*
* Side Effects: none
*
* Overview: check the range of config data and send it to TR module RF programmer
*
* Note: none
*
***************************************************************************************************/
void IQRF_SendRFPgmConfig(RF_PGM_CFG *rfPgmCfgStruct)
{
	// check the range of config data
	// if any parameter is out of range, set default value
	if (rfPgmCfgStruct->channelA > 61) rfPgmCfgStruct->channelA = 52;		
	if (rfPgmCfgStruct->channelB > 61 && rfPgmCfgStruct->channelB != 255) rfPgmCfgStruct->channelA = 2;
	if (rfPgmCfgStruct->txPower > 7) rfPgmCfgStruct->txPower = 7;
	if (rfPgmCfgStruct->packetRpt > 10) rfPgmCfgStruct->packetRpt = 2;
			
	IQRF_SendData((UINT8 *)rfPgmCfgStruct, 4, 0);
}

/***************************************************************************************************
* Function: void IQRF_EndRFPgm(void)
*
* PreCondition: TR module loaded with RFPGM PlugIn module
*
* Input: none
*
* Output: none
*
* Side Effects: none
*
* Overview: Send END RFPGM mode command
*
* Note: none
*
***************************************************************************************************/
void IQRF_EndRFPgm(void)
{
	TR_SendSpiPacket(SPI_EEPROM_PGM, (UINT8 *)&endPgmMode[0], sizeof(endPgmMode), 0);  // send end of PGM mode packet
}

/***************************************************************************************************
* Function: IQRF_FW_HEADER* IQRF_FwPreprocess(char *hexFile)
*
* PreCondition: file system must be initialized first
*
* Input: hexFile - string = TR module firmware file name
*
* Output: poinrer to structure witch contains data for IQRF_FwWrite function
*
* Side Effects: none
*
* Overview: function preprocess TR module firmware file on SD card and prepare data for 
*           IQRF_FwWrite function
*
* Note: none
*
***************************************************************************************************/
IQRF_FW_HEADER* IQRF_FwPreprocess(char *hexFile)
{
	IQRF_FW_HEADER	*firmwareHeader;
	UINT8	*tempPtr;
	UINT16	cnt, result, address, flashBlockIndex;

	firmwareHeader = malloc(sizeof(IQRF_FW_HEADER));										// allocate memory for TR modupe program header
	if (firmwareHeader != NULL){
		if ((firmwareHeader->eepromBuffer = malloc(1024)) == NULL) goto unallocate_header;  // allocate memory for eeprom data
		if ((firmwareHeader->flashBuffer = malloc(sizeof(FLASH_BUFFER))) == NULL) goto unallocate_eeprom;	// allocate memory for flash data

		#if (TR_APP_STORAGE == SD_CARD)
			firmware = FSfopen(hexFile,FS_READ);												// open HEX file with TR module application from SD
			if (firmware == NULL){																// if HEX file not exist
		#else
			firmware = MPFSOpen(hexFile);														// open HEX file with TR module application from FLASH
			if (firmware == MPFS_INVALID_HANDLE){												// if HEX file not exist
		#endif
				unallocate_all: free(firmwareHeader->flashBuffer);								// unallocate memory
				unallocate_eeprom: free(firmwareHeader->eepromBuffer);
				unallocate_header: free(firmwareHeader);
				return(NULL);																	// and return
			}

		memset((UINT8 *)&((firmwareHeader->flashBuffer)->licencedMemMap[0]), 0, IQRF_LICENCED_MEMORY_BLOCKS+IQRF_MAIN_MEMORY_BLOCKS);
		tempPtr = (UINT8 *)&((firmwareHeader->flashBuffer)->licencedFlash);
		for (cnt=0; cnt<(sizeof((firmwareHeader->flashBuffer)->licencedFlash) + sizeof((firmwareHeader->flashBuffer)->mainFlash))/2; cnt++){
			*tempPtr++ = 0xFF;
			*tempPtr++ = 0x3F;
		}
	    fileReadBufferPtr = 0;																// initialize variables for HEX file preprocess
		procesByteCnt = 0;			
		hiAddress = 0;
		firmwareHeader->eepromPacketPtr = firmwareHeader->eepromBuffer;						// initialize TR module program header structure
		firmwareHeader->eepromPacketsNo = 0;
		firmwareHeader->flashPacketsNo = 0;
		firmwareHeader->packetCnt = 0;
	
		while ((result = ReadHEXFileLine()) == 0){											// read one line of input hex file
			if (FWLineBuffer[3] == 0){														// if line contains data
	
				address = (hiAddress + (FWLineBuffer[1] << 8) + FWLineBuffer[2]) / 2;		// compute destination data address
				cnt = 0;
	
				// if address is in range of allowed flash addresses of TR module
				if (address >= IQRF_MAIN_MEM_MIN_ADR && address <= IQRF_MAIN_MEM_MAX_ADR){
					tempPtr = (UINT8 *)&((firmwareHeader->flashBuffer)->mainFlash[0]);
					if ((address + FWLineBuffer[0]/2) > IQRF_MAIN_MEM_MAX_ADR) cnt = (IQRF_MAIN_MEM_MAX_ADR - address) * 2;
					else cnt = FWLineBuffer[0];
					flashBlockIndex = address - IQRF_MAIN_MEM_MIN_ADR;
					tempPtr += flashBlockIndex*2;
					flashBlockIndex /= 32;
					if ((firmwareHeader->flashBuffer)->mainMemMap[flashBlockIndex] == 0){
						(firmwareHeader->flashBuffer)->mainMemMap[flashBlockIndex] = 1;
						firmwareHeader->flashPacketsNo += 2;
					}
				}
				else{
					if (address >= IQRF_LICENCED_MEM_MIN_ADR && address <= IQRF_LICENCED_MEM_MAX_ADR){
						tempPtr = (UINT8 *)&((firmwareHeader->flashBuffer)->licencedFlash[0]);
						if ((address + FWLineBuffer[0]/2) > IQRF_LICENCED_MEM_MAX_ADR) cnt = (IQRF_LICENCED_MEM_MAX_ADR - address) * 2;
						else cnt = FWLineBuffer[0];
						flashBlockIndex = address - IQRF_LICENCED_MEM_MIN_ADR;
						tempPtr += flashBlockIndex*2;
						flashBlockIndex /= 32;
						if ((firmwareHeader->flashBuffer)->licencedMemMap[flashBlockIndex] == 0){
							(firmwareHeader->flashBuffer)->licencedMemMap[flashBlockIndex] = 1;
							firmwareHeader->flashPacketsNo += 2;
						}
					}
					else{
						// if address is in range of allowed eeprom addresses of TR module
						if (address>=PIC16LF1938_EEPROM_MIN && address<=PIC16LF1938_EEPROM_MAX) {			
	
							*firmwareHeader->eepromPacketPtr++ = (address & 0x00ff);			// write to eeprom packet LSB address
							*firmwareHeader->eepromPacketPtr++ = (FWLineBuffer[0]/2);			// write to eeprom packet number of data bytes in line
							// write to eeprom packet all valid data in line (every second byte is dummy and lost)
							for (cnt=0; cnt<FWLineBuffer[0]/2; cnt++){					
								*firmwareHeader->eepromPacketPtr++ = FWLineBuffer[2*cnt+4];
							}
							firmwareHeader->eepromPacketsNo++;									// increment flash packets counter
							cnt = 0;															// no copy to flash buffer
						} 
					}
				}		
	
				if (cnt) memcpy(tempPtr, (UINT8 *)&FWLineBuffer[4], cnt);						// copy flash data to flash buffer
			}
			else{
				if (FWLineBuffer[3] == 4){														// if line contains HiWord of address
					hiAddress = ((UINT32)FWLineBuffer[4] << 24) + ((UINT32)FWLineBuffer[5] << 16);
				}
			}
		}

		#if (TR_APP_STORAGE == SD_CARD)
			FSfclose(firmware);																	// close TR module application file
		#else
			MPFSClose(firmware);																// close TR module application file
		#endif

		if ((firmwareHeader->flashBuffer)->licencedMemMap[IQRF_LICENCED_MEMORY_BLOCKS-2] == 1){	// never write to last two blocks in licenced flash
			(firmwareHeader->flashBuffer)->licencedMemMap[IQRF_LICENCED_MEMORY_BLOCKS-2] = 0;
			firmwareHeader->flashPacketsNo -= 2;
		}

		if ((firmwareHeader->flashBuffer)->licencedMemMap[IQRF_LICENCED_MEMORY_BLOCKS-1] == 1){	// never write to last two blocks in licenced flash
			(firmwareHeader->flashBuffer)->licencedMemMap[IQRF_LICENCED_MEMORY_BLOCKS-1] = 0;
			firmwareHeader->flashPacketsNo -= 2;
		}

		// if HEX file contains no valid data for TR module or HEX file CRC error, unallocate memory and return
		if ((firmwareHeader->eepromPacketsNo == 0 && firmwareHeader->flashPacketsNo == 0) || result == 2) goto unallocate_all;
		else{
			TR_fwWriteSm = TR_INIT_PROG_SM;										// initialize programming state machine
			firmwareHeader->eepromPacketPtr = firmwareHeader->eepromBuffer;		// initialize pointers to flash and eeprom buffers
			firmwareHeader->pgmFunction = FW_PGM;								// application programming
			return(firmwareHeader);												// return pointer to TR module program header
		}
	}
	else return(NULL);

}

/***************************************************************************************************
* Function: IQRF_FW_HEADER* IQRF_PlugInPreprocess(char *iqrfFile)
*
* PreCondition: file system must be initialized first
*
* Input: iqrfFile - string = TR module plugin file name
*
* Output: poinrer to structure witch contains data for IQRF_FwWrite function
*
* Side Effects: none
*
* Overview: function preprocess TR module plugin file on SD card and prepare data for 
*           IQRF_FwWrite function
*
* Note: none
*
***************************************************************************************************/
IQRF_FW_HEADER* IQRF_PlugInPreprocess(char *iqrfFile)
{
	IQRF_FW_HEADER	*firmwareHeader;
	UINT8	result;
	UINT8	*packetStartPtr;

	firmwareHeader = malloc(sizeof(IQRF_FW_HEADER));										// allocate memory for TR modupe program header
	if (firmwareHeader != NULL){
		if ((firmwareHeader->eepromBuffer = malloc(1024)) == NULL) goto unallocate_header;  // allocate memory for eeprom data
		if ((firmwareHeader->pluginBuffer = malloc(16384)) == NULL) goto unallocate_eeprom;	// allocate memory for flash data

		#if (TR_APP_STORAGE == SD_CARD)
			firmware = FSfopen(iqrfFile,FS_READ);											// open IQRF file with TR module plugin from SD
			if (firmware == NULL){															// if IQRF file not exist
		#else
			firmware = MPFSOpen(iqrfFile);													// open IQRF file with TR module plugin from FLASH
			if (firmware == MPFS_INVALID_HANDLE){											// if IQRF file not exist
		#endif
				unallocate_all: free(firmwareHeader->pluginBuffer);							// unallocate memory
				unallocate_eeprom: free(firmwareHeader->eepromBuffer);
				unallocate_header: free(firmwareHeader);
				return(NULL);																// and return
			}

	    fileReadBufferPtr = 0;																// initialize variables for HEX file preprocess
		procesByteCnt = 0;			
		firmwareHeader->eepromPacketPtr = firmwareHeader->eepromBuffer;						// initialize TR module program header structure
		firmwareHeader->flashPacketPtr = firmwareHeader->pluginBuffer;
		firmwareHeader->eepromPacketsNo = 0;
		firmwareHeader->flashPacketsNo = 0;
		firmwareHeader->packetCnt = 0;

		while ((result = ReadIQRFFileLine()) == 0){					// read one line of input IQRF file
			packetStartPtr = firmwareHeader->flashPacketPtr++;  	// pointer to start of flash packet in flash data buffer
			*packetStartPtr = FWLineBufferCnt;						// number of bytes in packet 
 			memcpy(firmwareHeader->flashPacketPtr, (UINT8 *)&FWLineBuffer[0], FWLineBufferCnt);	// TR module IQRF data 
			firmwareHeader->flashPacketPtr += FWLineBufferCnt;		// move pointer at the end of packet
			firmwareHeader->flashPacketsNo++;						// increment flash packets counter
		}
	
		#if (TR_APP_STORAGE == SD_CARD)
			FSfclose(firmware);																	// close TR module plugin file
		#else
			MPFSClose(firmware);																// close TR module plugin file
		#endif

		// if IQRF file contains no valid data for TR module or IQRF file CRC error, unallocate memory and return
		if ((firmwareHeader->flashPacketsNo == 0) || result == 2) goto unallocate_all;
		else{
			TR_fwWriteSm = TR_INIT_PROG_SM;										// initialize programming state machine
			firmwareHeader->flashPacketPtr = firmwareHeader->pluginBuffer;
			firmwareHeader->pgmFunction = PLUGIN_PGM;							// application programming
			return(firmwareHeader);												// return pointer to TR module program header
		}
	}
	else return(NULL);
}
/*-----------------------------------------------------------------------------------------------------------------------*/

/***************************************************************************************************
* Function: IQRF_FW_HEADER* IQRF_CnfgPreprocess(char *cnfgFile)
*
* PreCondition: file system must be initialized first
*
* Input: cnfgFile - string = TR module config file name
*
* Output: poinrer to structure witch contains data for IQRF_FwWrite function
*
* Side Effects: none
*
* Overview: function preprocess TR module config file on SD card and prepare data for 
*           IQRF_FwWrite function
*
* Note: none
*
***************************************************************************************************/
IQRF_FW_HEADER* IQRF_CnfgPreprocess(char *cnfgFile)
{
	IQRF_FW_HEADER	*firmwareHeader;
	UINT8	*tempPtr;
	UINT8	tempData[33];
	UINT16	cnt;

	#if (TR_APP_STORAGE == SD_CARD)
		FSFILE  *config;
	#else
		MPFS_HANDLE	config;
	#endif

	firmwareHeader = malloc(sizeof(IQRF_FW_HEADER));										// allocate memory for TR modupe program header
	if (firmwareHeader != NULL)
	{
		if ((firmwareHeader->eepromBuffer = malloc(1024)) == NULL) goto unallocate_header;  // allocate memory for eeprom data
		if ((firmwareHeader->flashBuffer = malloc(sizeof(FLASH_BUFFER))) == NULL) goto unallocate_eeprom; // allocate memory for flash data

		#if (TR_APP_STORAGE == SD_CARD)
			config = FSfopen(cnfgFile,FS_READ);													// open config file from SD
			if (config == NULL){																// if config file not exist
		#else
			config = MPFSOpen(cnfgFile);														// open config file from FLASH
			if (config == MPFS_INVALID_HANDLE ){												// if config file not exist
		#endif
				unallocate_all: free(firmwareHeader->flashBuffer);								// unallocate memory
				unallocate_eeprom: free(firmwareHeader->eepromBuffer);
				unallocate_header: free(firmwareHeader);
	 			return(NULL);																	// and return
			}

		memset((UINT8 *)&((firmwareHeader->flashBuffer)->licencedMemMap[0]), 0, IQRF_LICENCED_MEMORY_BLOCKS+IQRF_MAIN_MEMORY_BLOCKS);

		tempPtr = (UINT8 *)&((firmwareHeader->flashBuffer)->licencedFlash[IQRF_CFG_MEMORY_BLOCK]);	// set ptr to config FLASH memory

		#if (TR_APP_STORAGE == SD_CARD)
			FSfread(tempData,1,33,config);     											    // read 33 bytes from config file from SD
		#else
			MPFSGetArray(config, tempData, 33);												// read 33 bytes from config file from FLASH
		#endif

		for (cnt=0; cnt<32; cnt++){															// read configuration from file
			*tempPtr++ = tempData[cnt];
			*tempPtr++ = 0x34;																// mandatory byte
		}
		(firmwareHeader->flashBuffer)->licencedMemMap[IQRF_CFG_MEMORY_BLOCK] = 1;			// set active block in memory map

		#if (TR_APP_STORAGE == SD_CARD)
			FSfclose(config);																// close TR module config
		#else
			MPFSClose(config);																// close TR module config
		#endif

		firmwareHeader->eepromPacketPtr = firmwareHeader->eepromBuffer;						// initialize TR module program header structure
		firmwareHeader->eepromPacketsNo = 0;												// 0 eeprom packet with config data
		firmwareHeader->flashPacketsNo = 2;													// 2 flash packets with config
		firmwareHeader->packetCnt = 0;
		firmwareHeader->pgmFunction = FW_PGM;												// application programming
		TR_fwWriteSm = TR_INIT_PROG_SM;														// initialize programming state machine
		return(firmwareHeader);																// return pointer to TR module program header

	}
	else return(NULL);
}

/***************************************************************************************************
* Function: UINT16 IQRF_FwWrite(IQRF_FW_HEADER *iqrfFwHeader, UINT16 pgmWay)
*
* PreCondition: IQRF_Init(rx_call_back_fn) must be called before, IQRF_Driver() must be called periodicaly
*               and IQRF_FwPreprocess("FW file name") must be called before
*
* Input: iqrfFwHeader		- pointer to a structure that contains information necessary for programming
*							  the application to the TR module
*
*		 pgmWay				- programming way (PGM_SPI or PGM_RF)
*
* Output: TR module programing state in % or IQRF_PGM_SUCCESS or IQRF_PGM_ERROR return code
*
* Side Effects: none
*
* Overview: function writes user application from SD card to TR module
*
* Note: if iqrfFwHeader pointer poins to TR module firmware structure, IQRF_FwWrite function must be
*       called periodicaly until returns IQRF_PGM_SUCCESS or IQRF_PGM_ERROR return code
*
***************************************************************************************************/
UINT16 IQRF_FwWrite(IQRF_FW_HEADER *iqrfFwHeader, UINT16 pgmWay)
{
	static UINT8 attempts;
	UINT8	tempDLEN;
	UINT16	writeAddress;

 	switch (TR_fwWriteSm)
 	{
	 	case TR_INIT_PROG_SM:	 						// initialize programming state machine
			attempts = 0;
			if (pgmWay == PGM_SPI) TR_fwWriteSm = TR_ENTER_PROG_MODE;
			else{
				TR_writePacket = 1;						// set flag to send packet to TR module
				if (iqrfFwHeader->pgmFunction == FW_PGM) TR_fwWriteSm = TR_WRITE_EEPROM;	// naxt state
				else TR_fwWriteSm = TR_WRITE_PLUGIN;    // naxt state
			}
	 		break;

	 	case TR_ENTER_PROG_MODE:
	 		IQRF_TR_EnterProgMode();					// enter prog. mode	
			TR_programTick = TickGet();					// sample system time
			TR_fwWriteSm = TR_WAIT_PROG_MODE;
	 		break;
	 		
	 	case TR_WAIT_PROG_MODE:		 				    // wait for TR module programming mode
	 		if (spiStat==PROGRAMMING_MODE && spiIqBusy==0){	// only if the IQRF_SPI_Task() is not busy and TR mudule is in prog mode
				TR_writePacket = 1;						// set flag to send packet to TR module
				if (iqrfFwHeader->pgmFunction == FW_PGM) TR_fwWriteSm = TR_WRITE_EEPROM;	// naxt state
				else TR_fwWriteSm = TR_WRITE_PLUGIN;    // naxt state
				TR_SetByteToByteTime(100);				// set byte to byte pause to 100us
	 		}
			else{
				if (TickGet() - TR_programTick > TICK_SECOND/2){	// wait for 0.5s to enter to programming mode
					if(attempts < 1) {								// in a case, try it twice to enter programming mode
						attempts++;
						TR_fwWriteSm = TR_ENTER_PROG_MODE;
					}
					else TR_fwWriteSm = TR_PROG_END;		
				}
			}			
	 		break;
	 			 	
	 	case TR_WRITE_EEPROM:							 			// write eeprom data to TR module
			if (iqrfPacketBufferInPtr==iqrfPacketBufferOutPtr){		// if no packet is pending to send to TR module
				if (!TR_writePacket && spiIqBusy==0){				// wait until packet is send
					if (++iqrfFwHeader->packetCnt >= iqrfFwHeader->eepromPacketsNo){	    // all eeprom packet was sent
						TR_fwWriteSm = TR_INIT_FLASH_WRITE;			// goto flash programing
					}
					TR_writePacket = 1;
				}
				else{
					if (spiStat==PROGRAMMING_MODE){					// wait until TR module returns PROGRAMMING_MODE
						if (iqrfFwHeader->eepromPacketsNo){			// if exists packets to send to TR module
							tempDLEN = *(iqrfFwHeader->eepromPacketPtr + 1) + 2;	// nuber of bytes to send
							TR_SendSpiPacket(SPI_EEPROM_PGM, iqrfFwHeader->eepromPacketPtr, tempDLEN, 0);  // send eeprom PGM packet
							iqrfFwHeader->eepromPacketPtr += tempDLEN;	// move pointer to next eeprom packet
							TR_writePacket = 0;						// wait until packet is send
						}
						else TR_fwWriteSm = TR_INIT_FLASH_WRITE;	// no data to eeprom programming, goto flash programing
						
					}
				}
			}
			break;

	 	case TR_INIT_FLASH_WRITE:									// initialize flash write variables
			iqrfFwHeader->flashPacketPtr = NULL;
			halfOfBlockCnt = 2;
			writeLicencedFlash = 1;
			flashBlockToWrite = 0;
			TR_fwWriteSm = TR_WRITE_FLASH;							// goto flash programing
			break;
		
	 	case TR_WRITE_FLASH:										// write flash data to TR module
			if (iqrfPacketBufferInPtr==iqrfPacketBufferOutPtr){		// if no packet is pending to send to TR module
				if (!TR_writePacket && spiIqBusy==0){				// wait until packet is send
					if (++iqrfFwHeader->packetCnt >= (iqrfFwHeader->eepromPacketsNo + iqrfFwHeader->flashPacketsNo)){	    // all flash packet was sent
						TR_fwWriteSm = TR_WAIT_PROG_END;			// goto end programming mode
					}
					TR_writePacket = 1;
				}
				else{
					if (spiStat==PROGRAMMING_MODE){					// wait until TR module returns PROGRAMMING_MODE
						if (iqrfFwHeader->flashPacketsNo){			// if exists packets to send to TR module

							if (writeLicencedFlash){																		// write data to licenced memory
								if ((iqrfFwHeader->flashBuffer)->licencedMemMap[flashBlockToWrite] == 1){					// exist data to write
									iqrfFwHeader->flashPacketPtr = (UINT8 *)&((iqrfFwHeader->flashBuffer)->licencedFlash[flashBlockToWrite]);	// set pointer to start of actual flash block
									writeAddress = (0x20 * flashBlockToWrite) + IQRF_LICENCED_MEM_MIN_ADR;					// compute start address of actual flash block
									if (halfOfBlockCnt == 1){ 																// write second half of block
										iqrfFwHeader->flashPacketPtr += 32;													// move data pointer to half of flash block
										writeAddress += 0x10;																// compute start address of second half of actual block
									}
									halfOfBlockCnt--;																		// decrement block counter
								}
								else halfOfBlockCnt = 0;																	// no data to send in actual block

								if (halfOfBlockCnt == 0){																	// move to next block
									halfOfBlockCnt = 2;																		// set new block counter
									if (++flashBlockToWrite >= IQRF_LICENCED_MEMORY_BLOCKS){								// all data of licenced memory sent
										flashBlockToWrite = 0;																// clear block counter
										writeLicencedFlash = 0;																// write data to main memory
									}
								}
							}
							else{																							// write data to main memory
								if ((iqrfFwHeader->flashBuffer)->mainMemMap[flashBlockToWrite] == 1){			 			// exist data to write
									iqrfFwHeader->flashPacketPtr = (UINT8 *)&((iqrfFwHeader->flashBuffer)->mainFlash[flashBlockToWrite]);	// set pointer to start of actual flash block
									writeAddress = (0x20 * flashBlockToWrite) + IQRF_MAIN_MEM_MIN_ADR;						// compute start address of actual flash block
									if (halfOfBlockCnt == 1){ 																// write second half of block
										iqrfFwHeader->flashPacketPtr += 32;													// move data pointer to half of flash block
										writeAddress += 0x10;																// compute start address of second half of actual block
									}
									halfOfBlockCnt--;																		// decrement block counter
								}
								else halfOfBlockCnt = 0;																	// no data to send in actual block

								if (halfOfBlockCnt == 0){																	// move to next block
									halfOfBlockCnt = 2;																		// set new block counter
									flashBlockToWrite++;																	// move to next block
								}
							}

							if (iqrfFwHeader->flashPacketPtr != NULL){							  // if exist data to send
								FWLineBuffer[0] = writeAddress & 0x00FF;						  // write FLASH address to TX buffer	
								FWLineBuffer[1] = writeAddress >> 8;
								memcpy(&FWLineBuffer[2], iqrfFwHeader->flashPacketPtr, 32);	  	  // copy FLASH data to TX buffer	
								TR_SendSpiPacket(SPI_FLASH_PGM, &FWLineBuffer[0], 2 + 32, 0);     // send FLASH PGM packet
								TR_writePacket = 0;												  // wait until packet is send
								iqrfFwHeader->flashPacketPtr = NULL;                              // clear pointer to data
							}
						}
						else TR_fwWriteSm = TR_WAIT_PROG_END;		// goto end programming mode
					}
				}
			}
			break;

	 	case TR_WRITE_PLUGIN:										// write plugin to TR module
			if (iqrfPacketBufferInPtr==iqrfPacketBufferOutPtr){		// if no packet is pending to send to TR module
				if (!TR_writePacket && spiIqBusy==0){				// wait until packet is send
					if (++iqrfFwHeader->packetCnt >= iqrfFwHeader->flashPacketsNo){	    // all plugin packet was sent
						TR_fwWriteSm = TR_WAIT_PROG_END;			// goto end programming mode
					}
					TR_writePacket = 1;
				}
				else{
					if (spiStat==PROGRAMMING_MODE){					// wait until TR module returns PROGRAMMING_MODE
						if (iqrfFwHeader->flashPacketsNo){			// if exists packets to send to TR module
							tempDLEN = *iqrfFwHeader->flashPacketPtr;	// nuber of bytes to send
							iqrfFwHeader->flashPacketPtr++;			// move pointer to plugin data
							TR_SendSpiPacket(SPI_PLUGIN_PGM, iqrfFwHeader->flashPacketPtr, tempDLEN, 0);  // send plugin PGM packet
							iqrfFwHeader->flashPacketPtr += tempDLEN; // move pointer to next flash packet
							TR_writePacket = 0;						// wait until packet is send
						}
						else TR_fwWriteSm = TR_WAIT_PROG_END;		// goto end programming mode
					}
				}
			}
			break;

	 	case TR_WAIT_PROG_END:										// wait until last packet is written to TR module
			if (spiStat==PROGRAMMING_MODE){
				TR_SendSpiPacket(SPI_EEPROM_PGM, (UINT8 *)&endPgmMode[0], sizeof(endPgmMode), 0);  // send end of PGM mode packet
				TR_fwWriteSm = TR_PROG_END;							// goto end programming mode
			}
			break;
		
	 	case TR_PROG_END:
			if (iqrfPacketBufferInPtr==iqrfPacketBufferOutPtr && spiIqBusy==0){		// if no packet is pending to send to TR module
				if (iqrfFwHeader->packetCnt >= (iqrfFwHeader->eepromPacketsNo + iqrfFwHeader->flashPacketsNo)) tempDLEN = 1; // programming success
				else tempDLEN = 0;										// programming error
	
				if (iqrfFwHeader->pgmFunction == FW_PGM) free(iqrfFwHeader->flashBuffer); // unallocate flash data buffer
				else free(iqrfFwHeader->pluginBuffer);					// unallocate flash data buffer
				free(iqrfFwHeader->eepromBuffer);						// unallocete eeprom data buffer
				free(iqrfFwHeader);										// unallocate TR module application header structure
	
				if (fastIqrfSpiEnable) TR_SetByteToByteTime(150);		// set byte to byte pause to 150us - fast SPI
				else TR_SetByteToByteTime(1000);						// set byte to byte pause to 1000us - normal SPI

				if (tempDLEN) return(IQRF_PGM_SUCCESS);					// return with corresponding return code
				else return(IQRF_PGM_ERROR);
			}
			break;
	}

	// return TR module programing state in %
	return((iqrfFwHeader->packetCnt * 100) / (iqrfFwHeader->eepromPacketsNo + iqrfFwHeader->flashPacketsNo));
}
/*-----------------------------------------------------------------------------------------------------------------------*/


/**
 * Convert two ascii char to number
 * 
 * @param - High and Low nibble in ascii
 * @return - number
 *
 **/
UINT8 ConvertToNum(UINT8 dataByteHi, UINT8 dataByteLo)
{
	UINT8 result=0;

    /* convert High nibble */
	if (dataByteHi >= '0' && dataByteHi <= '9') result = (dataByteHi-'0') << 4;
    else if (dataByteHi >= 'a' && dataByteHi <= 'f') result = (dataByteHi-87) << 4;
    /* convert Low nibble */
	if (dataByteLo >= '0' && dataByteLo <= '9') result |= (dataByteLo-'0');
    else if (dataByteLo >= 'a' && dataByteLo <= 'f') result |= (dataByteLo-87);

	return(result);
}
/*-----------------------------------------------------------------------------------------------------------------------*/

/**
 * Read byte from firmware file
 *
 * @param - none
 * @return - byte from firmware file or 0 = end of file
 *
 **/
UINT8 ReadFWFileByte(void)
{
	UINT8  dataByte;

	#if (TR_APP_STORAGE == SD_CARD)
		if (fileReadBufferPtr == 0){										/* readind pointer points to the cache begin */
			FSfread(fileReadBuffer,1,READ_BLOK_SIZE,firmware);     			/* read blok of data to the cache */
	 	}   
	
		dataByte = fileReadBuffer[fileReadBufferPtr++];						/* read byte from cache */
	    if (fileReadBufferPtr >= READ_BLOK_SIZE) fileReadBufferPtr = 0;		/* if readind pointer points to the cache end, set it to begin */
		if (++procesByteCnt > firmware->size) return(0);					/* check end of file */
	    return(dataByte);
	#else
		if (fileReadBufferPtr == 0){										/* readind pointer points to the cache begin */
			MPFSGetArray(firmware, fileReadBuffer, READ_BLOK_SIZE);			/* read blok of data to the cache */
	 	}   
	
		dataByte = fileReadBuffer[fileReadBufferPtr++];						/* read byte from cache */
	    if (fileReadBufferPtr >= READ_BLOK_SIZE) fileReadBufferPtr = 0;		/* if readind pointer points to the cache end, set it to begin */
		if (++procesByteCnt > MPFSGetSize(firmware)) return(0);				/* check end of file */
	    return(dataByte);

	#endif
}
/*-----------------------------------------------------------------------------------------------------------------------*/


/**
 * Read one text line from file
 *
 * @param - none
 * @return - number of chars in buffer
 *
 **/
UINT8 ReadTextLine(void)
{
	UINT8 dataCnt = 0;
    UINT8 znak; 

	while (((znak = ReadFWFileByte()) != 0) && (znak != 0x0D)){
		textLineBuffer[dataCnt++] = znak;
	}			
	if ((znak == 0x0D) || (znak == 0 && dataCnt)){
		ReadFWFileByte();
		return(dataCnt);
	}
	return(0);
}
/*-----------------------------------------------------------------------------------------------------------------------*/


/**
 * Read and process line from firmware file
 *
 * @param - none
 * @return - return code
 * 			 0 - HEX file line processed and ready in buffer
 *			 1 - end if file
 *			 2 - CRC error in HEX file line
 *
 **/
UINT8 ReadHEXFileLine(void)
{
    UINT8 znak; 
	UINT8 dataByteHi,dataByteLo;
	UINT8 dataByte;
	UINT8 FWLineBufferCnt=0;
	UINT8 FWLineBufferCrc=0;

	while (((znak = ReadFWFileByte()) != 0) && (znak != ':'));			// find start of line or end of file
    if (znak == 0) return(1);											// end of file

	for (;;){															// read data to end of line and convert if to numbers
		dataByteHi = tolower(ReadFWFileByte());							// read High nibble
        if (dataByteHi==10 || dataByteHi==13){							// check end of line
			if (FWLineBufferCrc != 0) return(2);						// check line CRC
			return(0);													// stop reading
		}
		dataByteLo = tolower(ReadFWFileByte());							// read Low nibble

		dataByte = ConvertToNum(dataByteHi,dataByteLo);					// convert two ascii to number
		FWLineBufferCrc += dataByte;									// add to crc
		FWLineBuffer[FWLineBufferCnt++] = dataByte;						// store to line buffer
    }
}
/*-----------------------------------------------------------------------------------------------------------------------*/



/**
 * Read and process line from plugin file
 *
 * @param - none
 * @return - return code
 * 			 0 - iqrf file line processed and ready in buffer
 *			 1 - end if file
 *			 2 - input file format error
 **/
UINT8 ReadIQRFFileLine(void)
{
	UINT8 cnt;
	UINT8 charCount;

	FWLineBufferCnt = 0;

	repeat_read:
	if ((charCount=ReadTextLine()) == 0) return(1);					   // end of file
	if (textLineBuffer[0]=='#') goto repeat_read;					   // line without data to program
	if (charCount & 0x01) return(2);								   // wrong file format

	for (cnt=0; cnt<charCount; cnt+=2){								   // read data to end of line and convert if to numbers
		FWLineBuffer[FWLineBufferCnt++] = ConvertToNum(textLineBuffer[cnt],textLineBuffer[cnt+1]);
	}

	return(0);
}
/*-----------------------------------------------------------------------------------------------------------------------*/

