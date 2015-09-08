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

#ifndef _DPA_UART_H
#define _DPA_UART_H

#include <WProgram.h>
#include <sys/attribs.h>
#include <stdint.h>

#include "BOARD.h"

#define _U2MODE_ON			15 
#define _U2STA_UTXEN		10
#define _U2STA_URXEN		12

//Interrupt//
#define _IEC1_EIE			8 	// UART2 Error Interrupt Enable bit
#define _IEC1_TXIE			10 	// UART2 Transmit Buffer Empty Interrupt Enable bit
#define _IEC1_RXIE			9 	// UART2 Receive Buffer Full Interrupt Enable bit

#define _IFS1_EIF			8 	// UART2 Error Interrupt Flag bit
#define _IFS1_TXIF			10 	// UART2 Transmit Buffer Empty Interrupt Flag bit
#define _IFS1_RXIF			9 	// UART2 Receive Buffer Full Interrupt Flag bit

#define _UART2_VECTOR		32  // Interrupt vector 
#define _UART2_TX_IRQ		42
#define _UART2_RX_IRQ		41

typedef enum 
{
	UART2_EVENT_THRES,
	UART2_EVENT_OVERFLOW,
} uart2_event_t;

typedef void(*uart2_tx_cbt)(void);
typedef void(*uart2_rx_cbt)(void);

class DPA_UART
{
public:
	int _uart2_vector;
	int _uart2_tx_irq;
	int _uart2_rx_irq;
	int _ipl;
	int _spl;
	
	volatile p32_regset*		iec1;
	volatile p32_regset*		ifs1;
	volatile p32_regset*		u2mode;
	volatile p32_regset*		u2sta;
	volatile uint16_t*			u2brg;
	volatile uint8_t*			u2txbuf;
	volatile uint8_t*			u2rxbuf;
	
	void    init(uint32_t baude_rate);
	void    setCallbacks(uart2_tx_cbt txCb, uart2_rx_cbt rxCb);
	void    enableInterrupts(void);
	void    disableInterrupts(void);
	void    writeByte(uint8_t byteToWrite);
	uint8_t readByte(void);
	
	DPA_UART();
};

//=========================== interrupt handlers ======================================
kick_scheduler_t uart2_tx_isr(void);
kick_scheduler_t uart2_rx_isr(void);

#endif
