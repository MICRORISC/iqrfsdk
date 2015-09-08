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

#include "DPA_SPI.h"
#include "DPA_UART.h"
#include "DPA_TIMER.h"
#include "BOARD.h"

#define U2TXIE	0x400
#define U2RXIE	0x200
#define U2TXIF	0x400
#define U2RXIF	0x200

#define SPI2RXIE 0x80
#define SPI2RXIF 0x80


void Spi2RxEventHandler(void)
{
	if ((IEC1 & SPI2RXIE) && (IFS1 & SPI2RXIF)) // SPI RX interrupt
	{
		if (spi_rx_isr() == KICK_SCHEDULER)
		{
			// Any user code for kick OS
		}
	}
	
}

void Uart2EventHandler(void)
{
	if ((IEC1 & U2TXIE) && (IFS1 & U2TXIF)) // UART2 TX interrupt
	{
		if (uart2_tx_isr() == KICK_SCHEDULER)
		{
			// Any user code for kick OS
		}
	}

	if ((IEC1 & U2RXIE) && (IFS1 & U2RXIF)) // UART2 RX interrupt
	{
		if (uart2_rx_isr() == KICK_SCHEDULER)
		{
			// Any user code for kick OS
		}
	}
	
}

void Timer2EventHandler(void)
{
		if (timer_isr() == KICK_SCHEDULER)
		{
			// Any user code for kick OS
		}
}
