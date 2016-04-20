/*
  Copyright 2015 MICRORISC s.r.o.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

#include "iqrf_library.h"

#define TR_CTRL_READY		0
#define TR_CTRL_RESET		1
#define TR_CTRL_WAIT		2
#define TR_CTRL_PROG_MODE	3

//******************************************************************************
//		 	locally used function prototypes
//******************************************************************************
UINT8 GetCRCM(void);
UINT8 CheckCRCS(void);
void TR_Module_OFF(void);
void TR_Module_ON(void);
void TR_Info_Task(void);
void TR_Control_Task(void);
void TR_process_id_packet_com(UINT8 pktId, UINT8 pktResult);
void TR_process_id_packet_pgm(void);
void TR_dummy_func_com(void);
void TR_dummy_func_pgm(UINT8 pktId, UINT8 pktResult);

//******************************************************************************
//		 	public variable declarations
//******************************************************************************
UINT8   		IQ_SPI_TxBuf[IQ_PKT_SIZE];
UINT8   		IQ_SPI_RxBuf[IQ_PKT_SIZE];
UINT8   		PTYPE, spiStat, repCnt, tmpCnt, pacLen, trInfoReading;
UINT8   		DLEN, spiIqBusy;
UINT8				iqrfSpiMasterEnable;
UINT8				fastIqrfSpiEnable;
UINT8				txPktId;
UINT8				txPktIdCnt;
UINT8				TR_Control_TaskSM = TR_CTRL_READY;
UINT8				TR_Control_ProgFlag;
DWORD   		iqrfCheckMicros;
DWORD 			iqrfMicros;
DWORD 			iqrfSpiByteBytePause;

TR_INFO_STRUCT		  trInfoStruct;
IQRF_PACKET_BUFFER	iqrfPacketBuffer[PACKET_BUFFER_SIZE];
UINT16		  iqrfPacketBufferInPtr, iqrfPacketBufferOutPtr;

#ifdef CHIPKIT
DSPI0    spi;
#endif

IQRF_RX_CALL_BACK	iqrf_rx_call_back_fn;
IQRF_TX_CALL_BACK	iqrf_tx_call_back_fn;

const UINT8 endPgmMode[] = {0xDE, 0x01, 0xFF};

/***************************************************************************************************
  Function: void IQRF_Init(IQRF_RX_CALL_BACK rx_call_back_fn, IQRF_TX_CALL_BACK tx_call_back_fn);

  PreCondition: TickInit() for systick timer initialization must be called before

  Input: rx_call_back_fn  - Pointer to callback function.
				 Function is called when the driver receives data from the TR module
		     tx_call_back_fn  - Pointer to callback function.
				Function is called when the driver sent data to the TR module
  Output: none

  Side Effects: function performes initialization of trInfoStruct identification data structure

  Overview: function perform a TR-module driver initialization

  Note: none

***************************************************************************************************/
void IQRF_Init(IQRF_RX_CALL_BACK rx_call_back_fn, IQRF_TX_CALL_BACK tx_call_back_fn)
{
  spiIqBusy = 0;
  spiStat = 0;
  iqrfCheckMicros = 0;
  fastIqrfSpiEnable = 0;						  // normal SPI communication
  TR_SetByteToByteTime(1000);					// set byte to byte pause to 1000us

  TR_Module_ON();

#ifdef CHIPKIT
  pinMode(TR_SS_IO, OUTPUT);
  digitalWrite(TR_SS_IO, HIGH);

  spi.begin();
  spi.setSpeed(IQRF_SPI_CLK);
  spi.setPinSelect(TR_SS_IO);
#endif

#ifdef LEONARDO
  pinMode(TR_SS_IO, OUTPUT);
  digitalWrite(TR_SS_IO, HIGH);

  SPI.begin();
#endif

  IQRF_SPIMasterEnable();				// enable SPI master function in driver

  trInfoReading = 2;						// read TR module info
  while (trInfoReading) {				// wait for TR module ID reading
    IQRF_Driver();							// IQRF SPI communication driver
    TR_Info_Task();							// TR module info reading task
  }

  if (IQRF_GetModuleType() == TR_72D || IQRF_GetModuleType() == TR_76D) {	// if TR72D or TR76D is conected
    fastIqrfSpiEnable = 1;											                          // set fast SPI mode
    TR_SetByteToByteTime(150);										                        // set byte to byte pause to 150us
    Serial.println("IQRF_Init - set fast spi");
  }

  iqrf_rx_call_back_fn = rx_call_back_fn;
  iqrf_tx_call_back_fn = tx_call_back_fn;
}

/***************************************************************************************************
  Function: void IQRF_Driver(void);

  PreCondition: IQRF_Init(rx_call_back_fn) must be called before

  Input: none

  Output: none

  Side Effects: none

  Overview: periodically called IQRF_Driver

  Note: none

***************************************************************************************************/
void IQRF_Driver(void)
{
  if (iqrfSpiMasterEnable) {											      // SPI Master enabled

    iqrfMicros = micros();

    if (spiIqBusy != IQRF_SPI_MASTER_FREE)							// is anything to send in IQ_SPI_TxBuf?
    {
        if ((iqrfMicros - iqrfCheckMicros) > iqrfSpiByteBytePause) {	// send 1 byte every defined time interval via SPI
        iqrfCheckMicros = iqrfMicros;							                    // reset counter

        IQ_SPI_RxBuf[tmpCnt] = IQRF_SPI_Byte(IQ_SPI_TxBuf[tmpCnt]);	// send/receive 1 byte via SPI
        tmpCnt++;													// counts number of send/receive bytes, it must be zeroing on packet preparing
        // pacLen contains length of whole packet it must be set on packet preparing
        if (tmpCnt == pacLen || tmpCnt == IQ_PKT_SIZE)				// sent everything? + buffer overflow protection
        {
          //digitalWrite(TR_SS_IO, HIGH);							// CS - deactive

          if ((IQ_SPI_RxBuf[DLEN + 3] == SPI_CRCM_OK) && CheckCRCS()) {	// CRC ok
            if (spiIqBusy == IQRF_SPI_MASTER_WRITE)	iqrf_tx_call_back_fn(txPktId, IQRF_TX_PKT_OK);
            if (spiIqBusy == IQRF_SPI_MASTER_READ) iqrf_rx_call_back_fn();

            spiIqBusy = IQRF_SPI_MASTER_FREE;
          }
          else {									  // CRC error
            if (--repCnt) {					// rep_cnt - must be set on packet preparing
              tmpCnt = 0;						// another attempt to send data
            }
            else {
              if (spiIqBusy == IQRF_SPI_MASTER_WRITE)	iqrf_tx_call_back_fn(txPktId, IQRF_TX_PKT_ERR);
              spiIqBusy = IQRF_SPI_MASTER_FREE;
            }
          }
        }
      }
    }
    else
    { // no data to send => SPI status will be updated
      if ((iqrfMicros - iqrfCheckMicros) > (MICRO_SECOND / 100)) 	// every 10ms
      {
        iqrfCheckMicros = iqrfMicros;				// reset counter

        spiStat = IQRF_SPI_Byte(SPI_CHECK);	// get SPI status of TR module
        //digitalWrite(TR_SS_IO, HIGH);			// CS - deactive

        if ((spiStat & 0xC0) == 0x40)    		// if the status is dataready
        { // prepare packet to read it
          memset(IQ_SPI_TxBuf, 0, sizeof(IQ_SPI_TxBuf));
          if (spiStat == 0x40) DLEN = 64;   // state 0x40 means 64B
          else DLEN = spiStat & 0x3F; 			// clear bit 7,6 - rest is length (1 az 63B)
          PTYPE = DLEN;
          IQ_SPI_TxBuf[0] = SPI_WR_RD;
          IQ_SPI_TxBuf[1] = PTYPE;
          IQ_SPI_TxBuf[DLEN + 2] = GetCRCM();		// CRCM

          pacLen = DLEN + 4;						// length of whole packet + (CMD, PTYPE, CRCM, 0)
          tmpCnt = 0;								    // counter of sent bytes
          repCnt = 1;								    // number of attempts to send data

          spiIqBusy = IQRF_SPI_MASTER_READ;		// reading from buffer COM of TR module
          spiStat = SPI_DATA_TRANSFER;			  // current SPI status must be updated
        }

        if (!spiIqBusy) {							// if TR module ready and no data in module pending
          if (iqrfPacketBufferInPtr != iqrfPacketBufferOutPtr) {	// check if packet to send ready

            memset(IQ_SPI_TxBuf, 0, sizeof(IQ_SPI_TxBuf));
            DLEN = iqrfPacketBuffer[iqrfPacketBufferOutPtr].dataLength;
            PTYPE = (DLEN | 0x80); 							// PBYTE set bit7 - write to buffer COM of TR module
            IQ_SPI_TxBuf[0] = iqrfPacketBuffer[iqrfPacketBufferOutPtr].spiCmd;
            if (IQ_SPI_TxBuf[0] == SPI_MODULE_INFO && DLEN == 16) PTYPE = 0x10;
            IQ_SPI_TxBuf[1] = PTYPE;

            memcpy (&IQ_SPI_TxBuf[2], iqrfPacketBuffer[iqrfPacketBufferOutPtr].pDataBuffer, DLEN);
            IQ_SPI_TxBuf[DLEN + 2] = GetCRCM();				// CRCM
            pacLen = DLEN + 4;								        // length of whole packet + (CMD, PTYPE, CRCM, 0)

            txPktId = iqrfPacketBuffer[iqrfPacketBufferOutPtr].pktId; // set actual TX packet ID

            tmpCnt = 0;										// counter of sent bytes
            repCnt = 3;										// number of attempts to send data
            spiIqBusy = IQRF_SPI_MASTER_WRITE;				// writing to buffer COM of TR module

            if (iqrfPacketBuffer[iqrfPacketBufferOutPtr].unallocationFlag) {
              free(iqrfPacketBuffer[iqrfPacketBufferOutPtr].pDataBuffer);		// unallocate temporary TX data buffer
            }

            if (++iqrfPacketBufferOutPtr >= PACKET_BUFFER_SIZE) iqrfPacketBufferOutPtr = 0;

            spiStat = SPI_DATA_TRANSFER;					// current SPI status must be updated
          }
        }
      }
    }
  }
  else TR_Control_Task();												// SPI master is disabled
}

/***************************************************************************************************
  Function: IQRF_TR_Reset(void);

  PreCondition: IQRF_Init(rx_call_back_fn) must be called before

  Input: none

  Output: none

  Side Effects: none

  Overview: function perform a TR-module reset

  Note: none

***************************************************************************************************/
void IQRF_TR_Reset(void)
{
  if (iqrfSpiMasterEnable) {					// SPI Master enabled
    TR_Module_OFF();						// TR module OFF
    delay(100);								// RESET pause
    TR_Module_ON();							// TR module ON
    delay(1);
  }
  else {
    TR_Control_TaskSM = TR_CTRL_RESET;		// TR module RESET process in SPI Master disable mode
    spiStat = SPI_BUSY;
  }
}

/***************************************************************************************************
  Function: IQRF_TR_EnterProgMode(void);

  PreCondition: IQRF_Init(rx_call_back_fn) must be called before

  Input: none

  Output: none

  Side Effects: none

  Overview: function switch TR-module to programming mode

  Note: none

***************************************************************************************************/
void IQRF_TR_EnterProgMode(void)
{
  DWORD enterP_millis;

  if (iqrfSpiMasterEnable) {								// SPI Master enabled

#ifdef CHIPKIT
    spi.end();										          // SPI EE-TR OFF
#endif

#ifdef LEONARDO
    SPI.end();                              			// SPI EE-TR OFF
#endif

    IQRF_TR_Reset();

    pinMode(TR_SS_IO, OUTPUT);
    digitalWrite( TR_SS_IO, LOW );					// TR CS - must be low

    pinMode(TR_SDO_IO, OUTPUT);							// TR SDO - output
    pinMode(TR_SDI_IO, INPUT);							// TR SDI - input

    enterP_millis = millis();

    do {
      // copy SDI to SDO for approx. 500ms => TR into prog. mode
      digitalWrite( TR_SDO_IO, digitalRead(TR_SDI_IO));
    }
    while ((millis() - enterP_millis) < (MILLI_SECOND / 2));

#ifdef CHIPKIT
    digitalWrite( TR_SS_IO, HIGH );						// TR CS - HIGH

    spi.begin();
    spi.setSpeed( IQRF_SPI_CLK );
    spi.setPinSelect( TR_SS_IO );
#endif

#ifdef LEONARDO
  digitalWrite(TR_SS_IO, HIGH);

  SPI.begin();
#endif

  }
  else {
    TR_Control_TaskSM = TR_CTRL_RESET;
    TR_Control_ProgFlag = 1;
    spiStat = SPI_BUSY;
  }
}

/***************************************************************************************************
  Function: UINT8 IQRF_SendData(UINT8 *pDataBuffer, UINT8 dataLength, UINT8 unallocationFlag);

  PreCondition: IQRF_Init(rx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly

  Input: pDataBuffer		- pointer to a buffer that contains data that I want to send to TR module
		 dataLength			- number of bytes to send
		 unallocationFlag	- if the pDataBuffer is dynamically allocated using malloc function.
							  If you wish to unallocate buffer after data is sent, set the unallocationFlag
							  to 1, otherwise to 0

  Output: TX packet ID		- number from 1 to 255

  Side Effects: none

  Overview: function sends data from buffer to TR module

  Note: none

***************************************************************************************************/
UINT8 IQRF_SendData(UINT8 *pDataBuffer, UINT8 dataLength, UINT8 unallocationFlag) {
  return (TR_SendSpiPacket(SPI_WR_RD, pDataBuffer, dataLength, unallocationFlag));
}

/***************************************************************************************************
  Function: void IQRF_GetRxData(UINT8 *userDataBuffer, UINT8 rxDataSize);

  PreCondition: IQRF_Init(rx_call_back_fn) must be called before and IQRF_Driver() must be called periodicaly

  Input:  userDataBuffer		- pointer to my buffer, to which I want to load data received from the TR module
		      rxDataSize			  - number of bytes I want to read

  Output: none

  Side Effects: none

  Overview: function is usually called inside the callback function, whitch is called when the driver
			receives data from TR module

  Note: none

***************************************************************************************************/
void IQRF_GetRxData(UINT8 *userDataBuffer, UINT8 rxDataSize) {
  memcpy(userDataBuffer, &IQ_SPI_RxBuf[2], rxDataSize);
}

/**
   Send and receive single byte over SPI

   @param Tx_Byte character to be send via SPI
   @return byte received via SPI

 **/
UINT8 IQRF_SPI_Byte(UINT8 Tx_Byte)
{
  UINT8 Rx_Byte = 0;

#ifdef CHIPKIT
  spi.setSelect(LOW);
  delayMicroseconds( 10 );

  spi.transfer(1, Tx_Byte, &Rx_Byte);

  delayMicroseconds( 10 );
  spi.setSelect(HIGH);
#endif

#ifdef LEONARDO
  digitalWrite( TR_SS_IO, LOW );
  delayMicroseconds( 10 );

  SPI.beginTransaction(SPISettings(IQRF_SPI_CLK, MSBFIRST, SPI_MODE0));
  Rx_Byte = SPI.transfer( Tx_Byte );
  SPI.endTransaction();

  delayMicroseconds( 10 );
  digitalWrite( TR_SS_IO, HIGH );
#endif

  return Rx_Byte;
}
//***************************************************************************

/**
   Read Module Info from TR module, uses SPI master implementation

   @param none
   @return none

 **/
void TR_Info_Task(void)
{
  static UINT8 dataToModule[16];
  static UINT8 attempts;
  static DWORD timeoutMilli;
  static UINT8 idfMode;

  static enum {
    TR_INFO_INIT_TASK = 0,
    TR_INFO_ENTER_PROG_MODE,
    TR_INFO_SEND_REQUEST,
    TR_INFO_WAIT_INFO,
    TR_INFO_DONE
  } TR_Info_TaskSM = TR_INFO_INIT_TASK;

  switch (TR_Info_TaskSM)
  {
    case TR_INFO_INIT_TASK:
      attempts = 1;										// try enter to programming mode
      idfMode = 0;										// try to read idf in com mode
      iqrf_rx_call_back_fn = TR_dummy_func_com;			    // set call back function to process id data
      iqrf_tx_call_back_fn = TR_process_id_packet_com;	// set call back function after data were sent
      trInfoStruct.mcuType = MCU_UNKNOWN;
      memset(&dataToModule[0], 0, 16);
      timeoutMilli = millis();
      TR_Info_TaskSM = TR_INFO_ENTER_PROG_MODE /* TR_INFO_SEND_REQUEST */ ;		// next state - will read info in PGM mode or /* in COM mode */
      break;

    case TR_INFO_ENTER_PROG_MODE:
      IQRF_TR_EnterProgMode();									// enter prog. mode
      idfMode = 1;												      // try to read idf in pgm mode
      iqrf_rx_call_back_fn = TR_process_id_packet_pgm;	// set call back function to process id data
      iqrf_tx_call_back_fn = TR_dummy_func_pgm;					// set call back function after data were sent
      timeoutMilli = millis();
      TR_Info_TaskSM = TR_INFO_SEND_REQUEST;
      break;

    case TR_INFO_SEND_REQUEST:
      if (spiStat == COMMUNICATION_MODE && spiIqBusy == 0) 		// only if the IQRF_Driver() is not busy and TR mudule is in communication mode
      { // packet preparing
        TR_SendSpiPacket(SPI_MODULE_INFO, &dataToModule[0], 16, 0);
        timeoutMilli = millis();				      // initialize timeout timer
        TR_Info_TaskSM = TR_INFO_WAIT_INFO;		// next state
      }
      else {
        if (spiStat == PROGRAMMING_MODE && spiIqBusy == 0) 		// only if the IQRF_Driver() is not busy and TR mudule is in programming mode
        { // packet preparing
          TR_SendSpiPacket(SPI_MODULE_INFO, &dataToModule[0], 1, 0);
          timeoutMilli = millis();				// initialize timeout timer
          TR_Info_TaskSM = TR_INFO_WAIT_INFO;		// next state
        }
        else {
          if (millis() - timeoutMilli >= MILLI_SECOND / 2) {
            if (attempts) {						// in a case, try it twice to enter programming mode
              attempts--;
              TR_Info_TaskSM = TR_INFO_ENTER_PROG_MODE;
            }
            else TR_Info_TaskSM = TR_INFO_DONE; // TR module probably does not work
          }
        }
      }
      break;

    case TR_INFO_WAIT_INFO:								    // wait for info data from TR module
      if ((trInfoReading == 1) || (millis() - timeoutMilli >= MILLI_SECOND / 2)) {
        if (idfMode == 1) {
          TR_SendSpiPacket(SPI_EEPROM_PGM, (UINT8 *)&endPgmMode[0], 3, 0);        // send end of PGM mode packet
        }
        TR_Info_TaskSM = TR_INFO_DONE;				// next state
      }
      break;

    case TR_INFO_DONE:									      // the task is finished
      if (iqrfPacketBufferInPtr == iqrfPacketBufferOutPtr && spiIqBusy == 0) {		// if no packet is pending to send to TR module
        trInfoReading = 0;
      }
      break;
  }
}
//***************************************************************************

/**
   Make TR module reset or switch to prog mode when SPI master is disabled

   @param none
   @return none

 **/
void TR_Control_Task(void)
{
  static DWORD timeoutMilli;

  switch (TR_Control_TaskSM) {					// TR control state machine

    case TR_CTRL_READY:
      spiStat = SPI_DISABLED;						// set SPI state DISABLED
      TR_Control_ProgFlag = 0;
      break;

    case TR_CTRL_RESET:
      spiStat = SPI_BUSY;								// set SPI state BUSY

#ifdef CHIPKIT
      spi.end();											  // SPI EE-TR OFF
#endif

#ifdef LEONARDO
      SPI.end();                        		// SPI EE-TR OFF
#endif

      // SPI controller OFF
      TR_Module_OFF();									    // TR module OFF
      timeoutMilli = millis(); 							// read actual tick
      TR_Control_TaskSM = TR_CTRL_WAIT;			// goto next state
      break;

    case TR_CTRL_WAIT:
      spiStat = SPI_BUSY;									                  // set SPI state BUSY
      if (millis() - timeoutMilli >= MILLI_SECOND / 3) {		// wait 300 ms
        TR_Module_ON();									                    // TR module ON
        if (TR_Control_ProgFlag) TR_Control_TaskSM = TR_CTRL_PROG_MODE;   	// goto enter programming mode
        else {
#ifdef CHIPKIT
          digitalWrite( TR_SS_IO, HIGH );				// TR CS - HIGH

          spi.begin();								          // SPI controller ON
          spi.setSpeed( IQRF_SPI_CLK );
          spi.setPinSelect( TR_SS_IO );
#endif

#ifdef LEONARDO
  		  digitalWrite(TR_SS_IO, HIGH);

        SPI.begin();
#endif
          TR_Control_TaskSM = TR_CTRL_READY;   	// goto ready state
        }
      }
      break;

    case TR_CTRL_PROG_MODE:

#ifdef CHIPKIT
      spi.end();											          // SPI EE-TR OFF
#endif

#ifdef LEONARDO
      SPI.end();                                // SPI EE-TR OFF
#endif

      IQRF_TR_Reset();

      pinMode(TR_SS_IO, OUTPUT);
      digitalWrite( TR_SS_IO, LOW );						// TR CS - must be low

      pinMode(TR_SDO_IO, OUTPUT);							  // TR SDO - output
      pinMode(TR_SDI_IO, INPUT);							  // TR SDI - input

      timeoutMilli = millis();

      do {
        // copy SDI to SDO for approx. 500ms => TR into prog. mode
        digitalWrite( TR_SDO_IO, digitalRead(TR_SDI_IO));
      }
      while ((millis() - timeoutMilli) < (MILLI_SECOND / 2));

#ifdef CHIPKIT
      digitalWrite( TR_SS_IO, HIGH );						// deselect TR module

      spi.begin();										          // SPI controller ON
      spi.setSpeed( IQRF_SPI_CLK );
      spi.setPinSelect( TR_SS_IO );
#endif

#ifdef LEONARDO
  	  digitalWrite(TR_SS_IO, HIGH);

      SPI.begin();
#endif

      TR_Control_TaskSM = TR_CTRL_READY;   			// goto ready state
      break;
  }
}
//***************************************************************************

/**
   Process identification data packet from TR module

   @param none
   @return none

 **/
void TR_process_id_packet_com(UINT8 pktId, UINT8 pktResult) {
  TR_process_id_packet_pgm();
}
//***************************************************************************

/**
   Process identification data packet from TR module

   @param none
   @return none

 **/
void TR_process_id_packet_pgm(void) {
  memcpy((UINT8 *)&trInfoStruct.moduleInfoRawData, (UINT8 *)&IQ_SPI_RxBuf[2], 8);
  trInfoStruct.moduleId = (UINT32)IQ_SPI_RxBuf[2] << 24 | (UINT32)IQ_SPI_RxBuf[3] << 16 | (UINT32)IQ_SPI_RxBuf[4] << 8 | IQ_SPI_RxBuf[5];
  trInfoStruct.osVersion = (UINT16)(IQ_SPI_RxBuf[6] / 16) << 8 | (IQ_SPI_RxBuf[6] % 16);
  trInfoStruct.mcuType = IQ_SPI_RxBuf[7] & 0x07;
  trInfoStruct.fcc = (IQ_SPI_RxBuf[7] & 0x08) >> 3;
  trInfoStruct.moduleType = IQ_SPI_RxBuf[7] >> 4;
  trInfoStruct.osBuild = (UINT16)IQ_SPI_RxBuf[9] << 8 | IQ_SPI_RxBuf[8];
  trInfoReading--;										// TR info data processed
}
//***************************************************************************

/**
   function called after TR module identification request were sent

   @param 		x - packet ID
  				    y - operation result
   @return none

 **/
void TR_dummy_func_pgm(UINT8 pktId, UINT8 pktResult)
{
  __asm__("nop\n\t");
}
//***************************************************************************

/**
   function called after TR module identification request were sent

   @param 		x - packet ID
  				    y - operation result
   @return none

 **/
void TR_dummy_func_com(void)
{
  __asm__("nop\n\t");
}
//***************************************************************************

/**
   Prepare SPI packet to packet buffer

   @param
  		 - spiCmd			      - command that I want to send to TR module
 		   - pDataBuffer		  - pointer to a buffer that contains data that I want to send to TR module
 		   - dataLength		    - number of bytes to send
 		   - unallocationFlag	- if the pDataBuffer is dynamically allocated using malloc function.
 							              If you wish to unallocate buffer after data is sent, set the unallocationFlag
 							              to 1, otherwise to 0
   @return packet ID
   
 **/
UINT8 TR_SendSpiPacket(UINT8 spiCmd, UINT8 *pDataBuffer, UINT8 dataLength, UINT8 unallocationFlag) {
  if (dataLength == 0) return (0);
  if (dataLength > IQ_PKT_SIZE - 4) dataLength = IQ_PKT_SIZE - 4;
  if ((++txPktIdCnt) == 0) txPktIdCnt++;
  iqrfPacketBuffer[iqrfPacketBufferInPtr].pktId = txPktIdCnt;
  iqrfPacketBuffer[iqrfPacketBufferInPtr].spiCmd = spiCmd;
  iqrfPacketBuffer[iqrfPacketBufferInPtr].pDataBuffer = pDataBuffer;
  iqrfPacketBuffer[iqrfPacketBufferInPtr].dataLength = dataLength;
  iqrfPacketBuffer[iqrfPacketBufferInPtr].unallocationFlag = unallocationFlag;
  if (++iqrfPacketBufferInPtr >= PACKET_BUFFER_SIZE) iqrfPacketBufferInPtr = 0;
  return (txPktIdCnt);
}
//***************************************************************************

/**
   Calculate CRC before master's send

   @param none
   @return crc_val

 **/
UINT8 GetCRCM(void) 	// see IQRF SPI user manual
{
  unsigned char i, crc_val;

  crc_val = 0x5F;

  for (i = 0; i < (DLEN + 2); i++)
    crc_val ^= IQ_SPI_TxBuf[i];

  return crc_val;
}
//***************************************************************************

/**
   Confirm CRC from SPI slave upon received data

   @param none
   @return error code

 **/
UINT8 CheckCRCS(void) 	// see IQRF SPI user manual
{
  unsigned char i, crc_val;

  crc_val = 0x5F ^ PTYPE;

  for (i = 2; i < (DLEN + 2); i++)
    crc_val ^= IQ_SPI_RxBuf[i];

  if (IQ_SPI_RxBuf[DLEN + 2] == crc_val) return 1;	// CRCS ok

  return 0;                           				// CRCS error
}
//***************************************************************************

/**
   Enter TR module into OFF state

   @param none
   @return none

 **/
void TR_Module_OFF(void)	// TR module into OFF state
{
  pinMode( TR_RESET_IO, OUTPUT );
  digitalWrite( TR_RESET_IO, HIGH );
}
//***************************************************************************

/**
   Enter TR module into ON state

   @param none
   @return none

 **/
void TR_Module_ON(void)		// TR module into ON state
{
  pinMode( TR_RESET_IO, OUTPUT );
  digitalWrite( TR_RESET_IO, LOW );
}
//***************************************************************************

/**
   Set byte to byte pause is SPI driver

   @param - byte to byte time in us
   @return none

 **/
void TR_SetByteToByteTime (UINT16 byteToByteTime)
{
  iqrfSpiByteBytePause = byteToByteTime;
}
//***************************************************************************

