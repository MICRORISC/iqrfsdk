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

#ifndef _DPA_SPI_H
#define _DPA_SPI_H

#include <WProgram.h>
#include <stdio.h>
#include <sys/attribs.h>
#include <p32_defs.h>

#include "BOARD.h"
//=========================== define ==========================================

//#define SPI_IN_INTERRUPT_MODE

#define _SPI2CON_ON			15 
#define	_SPI2CON_CKE		8
#define _SPI2CON_CKP		6
#define _SPI2CON_MSTEN		5

#define	_SPI2STAT_SPIROV		6
#define	_SPI2STAT_SPITBE		3
#define _SPI2STAT_SPIRBF		0
#define _SPI2STAT_SPIBUSY		11

//Interrupt//
#define _IEC1_EIE		5 	// SPI2 Error Interrupt Enable bit
#define _IEC1_TXIE		6 	// SPI2 Transmit Buffer Empty Interrupt Enable bit
#define _IEC1_RXIE		7 	// SPI2 Receive Buffer Full Interrupt Enable bit

#define _IFS1_EIF		5 	// SPI2 Error Interrupt Flag bit
#define _IFS1_TXIF		6 	// SPI2 Transmit Buffer Empty Interrupt Flag bit
#define _IFS1_RXIF		7 	// SPI2 Receive Buffer Full Interrupt Flag bit

#define _SPI2_VECTOR	31  // Interrupt vector
#define _SPI2_RX_IRQ	39

//=========================== typedef =========================================

typedef enum 
{
	SPI_FIRSTBYTE = 0,
	SPI_BUFFER = 1,
	SPI_LASTBYTE = 2,
} spi_return_t;

typedef enum
{
	SPI_NOTFIRST = 0,
	SPI_FIRST = 1,
} spi_first_t;

typedef enum 
{
	SPI_NOTLAST = 0,
	SPI_LAST = 1,
} spi_last_t;

typedef void(*spi_cbt)(void);


class DPA_SPI
{
public:
	int _ipl;
	int _spl;
	int _spi2_vector;
	int _spi2_rx_irq;
	volatile p32_regset*		iec1;
	volatile p32_regset*		ifs1;
	volatile p32_regset*		spi2con;
	volatile p32_regset*		spi2stat;
	volatile uint8_t*			spi2brg;

	DPA_SPI();
	void init(uint32_t f_sck);
	uint8_t transfer(uint8_t data);
	void    txrx(uint8_t*     bufTx,
		uint8_t      lenbufTx,
		spi_return_t returnType,
		uint8_t*     bufRx,
		uint8_t      maxLenBufRx,
		spi_first_t  isFirst,
		spi_last_t   isLast);

	#ifdef SPI_IN_INTERRUPT_MODE
		void    setCb(spi_cbt cb);
	#endif
};

//=========================== interrupt handlers ======================================
kick_scheduler_t spi_rx_isr(void);

#endif
