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

#include "uart.h"
#include "GenericTypeDefs.h"
#include "Compiler.h"

//------------------------------------------
//Initialize UART 57600 bd, 8-bit, no parity
//------------------------------------------
void uartInit(void)
{
    unsigned char i;
    
    //OK, init UART
    PIE3bits.RC2IE = 0;
    PIE3bits.TX2IE = 0;
    RCSTA2bits.SPEN2 = 0;

    //Transmit control register
    TXSTA2 = 0x20; //0b.0010.0000;
 	//         ^           CSRC - Not used in asynchron mode
  //          ^          TX9 - 9-bit transmission
	//           ^         TXEN - Enable Transmitter
	//            ^	       SYNC - 0=Asynchronous mode
	//             ^       SENDB - 0=Send no break character bit
	//              ^      BRGH - 0=Low; 1=High speed
	//               ^     TRMT - Readonly
	//                ^    TX9D - Ninth data bit

    //Config register (BAUDCON replaces BAUDCTL register in newer revisions)
    BAUDCON2 = 0x08; //0b.0000.1000;
  	//           ^         ABDOVF - Autobaud overflow (not used)
  	//            ^        RCIDL - Readonly
  	//             ^       DTRXP - 0=Data not inverted; 1=Data inverted
  	//              ^      CKTXP - 0=Transmitter idle high; 1=Idle low
  	//               ^     BRG16 - 16Bit clock generator
  	//                ^    Unimplemented
  	//                 ^   WUE - Wake up enable
  	//                  ^  ABDEN - Autobaud enable

    //Receive control register
    RCSTA2 = 0x10; //0b.0001.0000;
  	//         ^           SPEN - Enable usart
  	//          ^          RX9 - 9-bit recipition
  	//           ^         SREN - Not used in asynchron mode
  	//            ^        CREN - Enable Receiver
  	//             ^       ADDEN - Not used in asynchron mode
  	//              ^      FERR - Readonly
  	//               ^     OERR - Readonly
  	//                ^    RX9D - Ninth data bit

    //Setup baud rate
    SPBRGH2 = 0;
    SPBRG2 = 51;

    //Enable UART module
    RCSTA2bits.SPEN2 = 1;      //Enable serial port - SPEN2
    if(PIR3bits.RC2IF == 1)
        i = uartGetChar();

    //Enable UART RX interrupt - high priority
    PIE3bits.RC2IE = 1;
    IPR3bits.RC2IP = 1;

    //UART TX interrupt - high priority
    IPR3bits.TX2IP = 1;
}

//------------------
//UART put character
//------------------
void uartPutChar(unsigned char tx)
{
    TXREG2 = tx;
    PIE3bits.TX2IE = 1;
}

//------------------
//UART get character
//------------------
unsigned char uartGetChar(void)
{
    unsigned char rx;

    rx = RCREG2;

    //Framing error ?
    if(RCSTA2bits.OERR2)
    {
        RCSTA2bits.CREN2 = 0;
        RCSTA2bits.CREN2 = 1;
    }
    
    return(rx);
}
