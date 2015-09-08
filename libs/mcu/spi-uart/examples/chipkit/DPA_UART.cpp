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

#include "DPA_UART.h"
#include "BOARD.h"

typedef struct 
{
	uart2_tx_cbt txCb;
	uart2_rx_cbt rxCb;
} uart2_vars_t;

uart2_vars_t uart2_vars;

DPA_UART::DPA_UART()
{
	_uart2_vector = _UART2_VECTOR;
	_uart2_tx_irq = _UART2_TX_IRQ;
	_uart2_rx_irq = _UART2_RX_IRQ;
	_ipl = 0x03; // Priority 0-7
	_spl = 0x02; // Subpriority 0-3

	u2mode = ((p32_regset *)&U2MODE);
	u2sta = ((p32_regset *)&U2STA); // Status register
	ifs1 = ((p32_regset *)&IFS1);	// Interrupt flag register
	iec1 = ((p32_regset *)&IEC1);	// Interrupt enable register
	u2brg = ((volatile uint16_t *)&U2BRG);
	u2txbuf = ((volatile uint8_t *)&U2TXREG);
	u2rxbuf = ((volatile uint8_t *)&U2RXREG);
}

void DPA_UART::init(uint32_t baude_rate)
{
	uint32_t f_pb = getPeripheralClock();
	*u2brg = (f_pb / (16 * baude_rate)) - 1;	// BRGH = 0 -> (16 * baude_rate)
												// BRGH = 1 -> (4 * baude_rate)

	// reset local variables
	memset(&uart2_vars, 0, sizeof(uart2_vars_t));

	u2sta->set = (1 << _U2STA_UTXEN); 			// TX = 40 
	u2sta->set = (1 << _U2STA_URXEN); 			// RX = 39
	u2mode->set = (1 << _U2MODE_ON);
}

void DPA_UART::setCallbacks(uart2_tx_cbt txCb, uart2_rx_cbt rxCb)
{
	uart2_vars.txCb = txCb;
	uart2_vars.rxCb = rxCb;
}

void DPA_UART::enableInterrupts()
{
	setIntVector(_uart2_vector, &Uart2EventHandler);
	setIntPriority(_uart2_vector, _ipl, _spl);

	clearIntFlag(_uart2_tx_irq);
	clearIntFlag(_uart2_rx_irq);
	// Enable interrupt RX & TX //
	iec1->set = (1 << _IEC1_TXIE); 
	iec1->set = (1 << _IEC1_RXIE);
}

void DPA_UART::disableInterrupts()
{
	// Enable interrupt RX & TX //
	iec1->clr = (1 << _IEC1_TXIE);
	iec1->clr = (1 << _IEC1_RXIE);
	clearIntVector(_uart2_vector);
}


void DPA_UART::writeByte(uint8_t byteToWrite)
{
	*u2txbuf = byteToWrite;
}

uint8_t DPA_UART::readByte()
{
	return *u2rxbuf;
}


//=========================== interrupt handlers ==============================

kick_scheduler_t uart2_tx_isr(void)
{
	clearIntFlag(_UART2_TX_IRQ); // TODO: do not clear, but disable when done
	uart2_vars.txCb();
	return DO_NOT_KICK_SCHEDULER;
}

kick_scheduler_t uart2_rx_isr(void)
{
	clearIntFlag(_UART2_RX_IRQ); // TODO: do not clear, but disable when done
	uart2_vars.rxCb();
	return DO_NOT_KICK_SCHEDULER;
}
