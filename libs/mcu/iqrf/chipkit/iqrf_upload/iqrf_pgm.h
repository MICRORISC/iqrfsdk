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

#ifndef _IQRF_PGM_H
#define _IQRF_PGM_H

#define	IQRF_PGM_SUCCESS	111			// IQRF_FwWrite return code (TR module firmware was written successfully)
#define	IQRF_PGM_ERROR		222			// IQRF_FwWrite return code (TR module firmware was not written)

#define	FW_PGM				1
#define PLUGIN_PGM			2

#define RF_PGM_CHANNEL_MIN		0
#define RF_PGM_CHANNEL_MAX		61
#define RF_PGM_CHANNEL_DIS		255
#define RF_PGM_POWER_MIN		1
#define RF_PGM_POWER_MAX		7
#define RF_PGM_PACKET_RPT_MIN	0
#define RF_PGM_PACKET_RPT_MAX	9

#define IQRF_SIZE_OF_FLASH_BLOCK	64
#define IQRF_LICENCED_MEMORY_BLOCKS	96
#define IQRF_MAIN_MEMORY_BLOCKS		48

#define IQRF_CFG_MEMORY_BLOCK		(IQRF_LICENCED_MEMORY_BLOCKS - 2)

#define IQRF_LICENCED_MEM_MIN_ADR	0x2C00
#define IQRF_LICENCED_MEM_MAX_ADR	0x37FF
#define IQRF_MAIN_MEM_MIN_ADR		0x3A00
#define IQRF_MAIN_MEM_MAX_ADR		0x3FFF
#define PIC16LF1938_EEPROM_MIN		0xf000
#define PIC16LF1938_EEPROM_MAX		0xf0ff

//******************************************************************************
//		 	programming sources
//******************************************************************************
#define SD_CARD					0x01
#define INTERNAL_FLASH			0x02
//#define TR_APP_STORAGE		SD_CARD
#define TR_APP_STORAGE			INTERNAL_FLASH

//******************************************************************************
//		 	programming ways
//******************************************************************************
#define	PGM_SPI				0
#define PGM_RF				1

typedef struct{
	UINT8	flashBlock[IQRF_SIZE_OF_FLASH_BLOCK];
} FLASH_BLOCK;

typedef struct{
	UINT8	licencedMemMap[IQRF_LICENCED_MEMORY_BLOCKS];
	UINT8	mainMemMap[IQRF_MAIN_MEMORY_BLOCKS];
	FLASH_BLOCK	licencedFlash[IQRF_LICENCED_MEMORY_BLOCKS];
	FLASH_BLOCK mainFlash[IQRF_MAIN_MEMORY_BLOCKS];
} FLASH_BUFFER;

typedef struct{
	UINT16	pgmFunction;
	UINT16	eepromPacketsNo;
	UINT16	flashPacketsNo;
	UINT16	packetCnt;
	UINT8	*pluginBuffer;
	UINT8	*eepromBuffer;
	FLASH_BUFFER	*flashBuffer;
	UINT8	*eepromPacketPtr;
	UINT8	*flashPacketPtr;
} IQRF_FW_HEADER;

typedef struct{
	UINT8	channelA;
	UINT8	channelB;
	UINT8	txPower;
	UINT8	packetRpt;
	UINT8	channelBMem;
}RF_PGM_CFG;

extern RF_PGM_CFG rfPgmCfg;

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
void IQRF_SendRFPgmConfig(RF_PGM_CFG *rfPgmCfgStuct);

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
void IQRF_EndRFPgm(void);

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
IQRF_FW_HEADER* IQRF_FwPreprocess(char *hexFile);

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
IQRF_FW_HEADER* IQRF_PlugInPreprocess(char *iqrfFile);

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
IQRF_FW_HEADER* IQRF_CnfgPreprocess(char *cnfgFile);

/***************************************************************************************************
* Function: UINT16 IQRF_FwWrite(IQRF_FW_HEADER *iqrfFwHeader, UINT16 pgmWay)
*
* PreCondition: IQRF_Init(rx_call_back_fn) must be called before, IQRF_Driver() must be called periodicaly
                and IQRF_FwPreprocess("FW file name") must be called before
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
UINT16 IQRF_FwWrite(IQRF_FW_HEADER *iqrfFwHeader, UINT16 pgmWay);

#endif

