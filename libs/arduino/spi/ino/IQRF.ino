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

/*
Example for SPI communication ARDUINO <> IQRF module
Arduino = SPI master
IQRF    = SPI slave

Circuit:
IQRF attached to pins 10 - 13:

      arduino          IQRF
CS:   pin 10 (OUT)    _SS  C5  (IN)
MOSI: pin 11 (OUT)    SDI  C7  (IN)
MISO: pin 12 (IN)     SDO  C8  (OUT)
SCK:  pin 13 (OUT)    SCK  C6  (IN)

VCC                   VIN  C3
GND                   GND  C4

*/

// IQRF communicates using SPI, so include the library:
#include <SPI.h>

// Size of IQRF_SPI buffers
const byte IQRF_SPI_BUFFER_SIZE = 64;

// IQRF SPI task message
const byte IQRF_SPI_NO_MESSAGE = 0;
const byte IQRF_SPI_WRITE_OK   = 1;
const byte IQRF_SPI_WRITE_ERR  = 2;
const byte IQRF_SPI_DATA_READY = 3;

// IQRF SPI task status
const byte IQRF_NOT_BUSY     = 0;
const byte IQRF_BUSY_WRITING = 1;
const byte IQRF_BUSY_READING = 2;

//******************************************************************************
//          TR module SPI status
//******************************************************************************
const byte TR_STAT_HW_ERR       = 0xFF;    // SPI not active
const byte TR_STAT_SPI_DISABLED = 0x00;    // SPI not active
const byte TR_STAT_CRCM_OK      = 0x3F;    // SPI not ready (full buffer, last CRCM ok)
const byte TR_STAT_CRCM_ERR     = 0x3E;    // SPI not ready (full buffer, last CRCM error)
const byte TR_STAT_COM_MODE     = 0x80;    // SPI ready (communication mode)
const byte TR_STAT_PROG_MODE    = 0x81;    // SPI ready (programming mode)
const byte TR_STAT_DEBUG_MODE   = 0x82;    // SPI ready (debugging mode)
const byte TR_STAT_SLOW_MODE    = 0x83;    // SPI not working on background - Slow Mode
const byte TR_STAT_USER_STOP    = 0x07;    // User Stop - after stopSPI();

//******************************************************************************
//          TR module SPI commands
//******************************************************************************
const byte TR_CMD_SPI_CHECK = 0x00;    // to get status of TR module
const byte TR_CMD_WR_RD     = 0xF0;    // write/read bufferCOM in TR module

byte IQRF_SPI_TxBuf[IQRF_SPI_BUFFER_SIZE];
byte IQRF_SPI_RxBuf[IQRF_SPI_BUFFER_SIZE];
byte IQRF_SPI_Busy, iq_DLEN, iq_PTYPE, iq_spistat, IQRF_SPI_Task_Message;
byte tmp_cnt, iq_pac_len, Check_Status_Timer;

// pins used for the connection with the module
// the other you need are controlled by the SPI library):
const int chipSelectPin = 8;

void setup()
{
  Serial.begin( 9600 );

  // start the SPI library:
  SPI.begin();
  IQRF_SPI_Init();
  Serial.println( "IQRF SPI demo ver. 1.1" );
}

void loop()
{
  IQRF_SPI_Task();
	
  delayMicroseconds( 400 );        // delay between bytes
  int readData = Serial.read();    // read data from Arduino terminal
  if ( readData > 0 )
  {
    IQRF_SPI_TxBuf[iq_DLEN + 2] = readData;    // save it to IQRF_SPI_TxBuf
    iq_DLEN++;                                 // increment length
    if ( readData == '\n' )                    // end of message
    {
      iq_DLEN--; iq_DLEN--;
      if ( ( iq_spistat == TR_STAT_COM_MODE || iq_spistat == TR_STAT_SLOW_MODE ) && IQRF_SPI_Busy == IQRF_NOT_BUSY )
      {                                           // TR module is in communication mode and the SPI is not busy
        iq_PTYPE = ( iq_DLEN | 0x80 );         // PTYPE set bit7 - write to bufferCOM of TR module
        IQRF_SPI_TxBuf[0] = TR_CMD_WR_RD;
        IQRF_SPI_TxBuf[1] = iq_PTYPE;
        Start_IQRF_SPI();                   // activates IQRF_SPI_Task to send packet
      }
    }
  }
	
  if ( IQRF_SPI_Task_Message )                  // any message from IQRF_SPI_Task?
  {
    switch ( IQRF_SPI_Task_Message )
    {
      case IQRF_SPI_WRITE_OK:             // last sending was successful
        // insert your code
        iq_DLEN = 0;
        break;

      case IQRF_SPI_WRITE_ERR:            // last sending was not successful
        // insert your code
        iq_DLEN = 0;
        break;

      case IQRF_SPI_DATA_READY:           // received packet is in IQRF_SPI_RxBuf
          for ( byte i = 0; i < iq_DLEN; i++ )
          Serial.write( IQRF_SPI_RxBuf[i + 2] );
          Serial.print( "\n" );
          break;
      }
      IQRF_SPI_Task_Message = 0;
    }
}

/*********************************************************************
* Function:        void IQRF_SPI_Init(void)
*
* PreCondition:    None
*
* Input:           None
*
* Output:          None
*
* Side Effects:    None
*
* Overview:        Initializes the SPI module to communicate to
*                  IQRF TR module.
*
* Note:            This function is called only once during lifetime
*                  of the application.
********************************************************************/
void IQRF_SPI_Init( void )
{
  // initialize the  data ready and chip select pins:
  pinMode( chipSelectPin, OUTPUT );
  SPI.setClockDivider( SPI_CLOCK_DIV64 );  // set SPI speed

  Check_Status_Timer = 0;
}

/*********************************************************************
* Function:        byte IQRF_SPI_Byte(byte Tx_Byte)
*
* PreCondition:    IQRF_SPI_Init() is already called.
*
* Input:           Tx_Byte - byte to be sent
*
* Output:          Received byte
*
* Side Effects:    None
*
* Overview:        Sends/receives 1 byte via SPI
*
* Note:            It uses Delay10us() which must be defined
*                  according used MCU clock.
********************************************************************/
byte IQRF_SPI_Byte( byte Tx_Byte )
{
  // take the chip select low to select the device:
  digitalWrite( chipSelectPin, LOW );
  delayMicroseconds( 10 );
  byte Rx_Byte = SPI.transfer( Tx_Byte );
  delayMicroseconds( 10 );
  digitalWrite( chipSelectPin, HIGH );
  return Rx_Byte;
}

/*********************************************************************
* Function:        byte IQRF_SPI_GetCRCM(void)
*
* PreCondition:    Data must be prepared in IQRF_SPI_TxBuf and
*                  iq_DLEN must be valid.
*
* Input:           None
*
* Output:          CRCM result
*
* Side Effects:    None
*
* Overview:        Returns CRCM according IQRF SPI specification.
*
* Note:            None
********************************************************************/
byte IQRF_SPI_GetCRCM( void )
{
  byte crc_val = 0x5F;

  for ( byte i = iq_DLEN + 2; --i != 0xff ; )
     crc_val ^= IQRF_SPI_TxBuf[i];

  return crc_val;
}

/*********************************************************************
* Function:        byte IQRF_SPI_CheckCRCS(void)
*
* PreCondition:    Data must be prepared in IQRF_SPI_RxBuf.
*                  iq_DLEN and iq_PTYPE must be valid.
*
* Input:           None
*
* Output:          CRCS result (0 - error, 1 - OK)
*
* Side Effects:    None
*
* Overview:        Returns CRCS result according IQRF SPI specification.
*
* Note:            None
********************************************************************/
byte IQRF_SPI_CheckCRCS( void )
{
  byte crc_val = 0x5F ^ iq_PTYPE;

  for ( byte i = iq_DLEN + 1; --i != 0xff ; )
    crc_val ^= IQRF_SPI_RxBuf[i + 2];

  return 0 == crc_val;
}

/*********************************************************************
* Function:        void Start_IQRF_SPI(void)
*
* PreCondition:    Data must be prepared in IQRF_SPI_TxBuf.
*                  iq_DLEN and iq_PTYPE must be valid.
*
* Input:           None
*
* Output:          None
*
* Side Effects:    None
*
* Overview:        Prepares SPI packet to be sent and activates
*                  IQRF_SPI_Task.
*
* Note:            None
********************************************************************/
void Start_IQRF_SPI( void )
{
  IQRF_SPI_TxBuf[iq_DLEN + 2] = IQRF_SPI_GetCRCM();

  iq_pac_len = iq_DLEN + 4;                   // length of whole packet (user data + CMD + PTYPE + CRCM + SPI_CHECK)
  tmp_cnt = 0;                                // counter of sent SPI bytes

  if ( iq_PTYPE & 0x80 )                        // PTYPE bit7 test
    IQRF_SPI_Busy = IQRF_BUSY_WRITING;
  else
    IQRF_SPI_Busy = IQRF_BUSY_READING;
}

/*********************************************************************
* Function:        void IQRF_SPI_Task(void)
*
* PreCondition:    IQRF_SPI_Init(), Start_IQRF_SPI() is already called.
*
* Input:           None
*
* Output:          None
*
* Side Effects:    None
*
* Overview:        If IQRF_SPI_Busy is > 0 one byte from IQRF_SPI_TxBuf
*                  is sent/received. After whole packet is sent the
*                  IQRF_SPI_Task_Message is set.
*
*                  If IQRF_SPI_Busy is 0 the SPI_CHECK is sent and
*                  SPISTAT is received. If there is data in bufferCOM
*                  of TR module it is automatically read. Received data
*                  is in IQRF_SPI_RxBuf.
*
* Note:            This function must be called from main loop every
*                  500us if the TR-21A or TR-3xB is used.
*                  If the TR-52B is used this function can be called
*                  every 200us. Calling of this function makes delay
*                  between SPI bytes.
********************************************************************/

void IQRF_SPI_Task( void )
{
  if ( IQRF_SPI_Busy )                          // any SPI packet to read/write?
  {                                             // yes - read/write 1 SPI byte
    IQRF_SPI_RxBuf[tmp_cnt] = IQRF_SPI_Byte( IQRF_SPI_TxBuf[tmp_cnt] );

    tmp_cnt++;                              // tmp_cnt cleared in Start_IQRF_SPI()

    if ( tmp_cnt == iq_pac_len || tmp_cnt == IQRF_SPI_BUFFER_SIZE )
    {                                       // packet sent or buffer overflow

      if ( ( IQRF_SPI_RxBuf[iq_DLEN + 3] == TR_STAT_CRCM_OK ) && IQRF_SPI_CheckCRCS() )
      {                               // CRC ok
        switch ( IQRF_SPI_Busy )
        {
          case IQRF_BUSY_WRITING:
            IQRF_SPI_Task_Message = IQRF_SPI_WRITE_OK;
            iq_DLEN = 0;
            Clear_IQRF_SPI_TxBuf();
            break;

          case IQRF_BUSY_READING:
            IQRF_SPI_Task_Message = IQRF_SPI_DATA_READY;
            break;
        }
      }
      else
      {                                   // CRC error
        IQRF_SPI_Task_Message = IQRF_SPI_WRITE_ERR;
      }

      IQRF_SPI_Busy = IQRF_NOT_BUSY;
      iq_spistat = 0;                     // to recovery SPI status after packet communication
    }
  }
  else                                        // no SPI packet to read/write
  {
    if ( ++Check_Status_Timer >= 20 )         // 20 * every 500us called from main loop = 10ms
    {                                       // every 10ms - sends SPI_CHECK to get TR module status
      Check_Status_Timer = 0;

      byte last_spistat = IQRF_SPI_Byte( TR_CMD_SPI_CHECK );

      if ( iq_spistat != last_spistat )     // the TR module status must be 2x the same
      {
        iq_spistat = last_spistat;
        return;
      }

      if ( ( iq_spistat & 0xC0 ) == 0x40 )
      {                                   // data ready in bufferCOM of TR module
        Clear_IQRF_SPI_TxBuf();
        iq_DLEN = iq_spistat & 0x3F;      // clear bit 7,6 - rest is length
	if ( iq_spistat == 0x40 ) iq_DLEN = 64;
        iq_PTYPE = iq_DLEN;
        IQRF_SPI_TxBuf[0] = TR_CMD_WR_RD;
        IQRF_SPI_TxBuf[1] = iq_PTYPE;
        IQRF_SPI_TxBuf[iq_DLEN + 2] = IQRF_SPI_GetCRCM();
        Start_IQRF_SPI();               // prepare IQRF_SPI_Task to send packet
      }
    }
  }
}
//---------------------------------------------------------------------------

void Clear_IQRF_SPI_TxBuf( void )
{
  byte i = sizeof( IQRF_SPI_TxBuf );
  do
  {
    IQRF_SPI_TxBuf[i] = 0;
  } while ( --i != 0xff );
}
