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

const int chipSelectPin = 7; // SDO2 = 11, SDI2 = 12, SCK2 = 13

typedef struct 
{
	// information about the current transaction
	uint8_t*        pNextTxByte;
	uint8_t         numTxedBytes;
	uint8_t         txBytesLeft;
	spi_return_t    returnType;
	uint8_t*        pNextRxByte;
	uint8_t         maxRxBytes;
	spi_first_t     isFirst;
	spi_last_t      isLast;
	// state of the module
	uint8_t         busy;
	// help
	uint8_t	   i;
	#ifdef SPI_IN_INTERRUPT_MODE
		// callback when module done
		spi_cbt         callback;
	#endif
} spi_vars_t;

spi_vars_t spi_vars;

DPA_SPI::DPA_SPI()
{
	_spi2_vector = _SPI2_VECTOR;
	_spi2_rx_irq = _SPI2_RX_IRQ;
	_ipl = 0x03; // Priority 0-7
	_spl = 0x01; // Subpriority 0-3

	spi2con = ((p32_regset *)&SPI2CON);		// Control register
	spi2stat = ((p32_regset *)&SPI2STAT);	// Status register
	spi2brg = ((volatile uint8_t *)&SPI2BRG);
	ifs1 = ((p32_regset *)&IFS1);	// Interrupt flag register
	iec1 = ((p32_regset *)&IEC1);	// Interrupt enable register
}


void DPA_SPI::init(uint32_t f_sck)
{
	pinMode(chipSelectPin, OUTPUT);
	memset(&spi_vars, 0, sizeof(spi_vars_t));
	uint8_t		tmp;
	
	spi2con->reg = 0; 		// Disable and reset the SPI controller
	spi2stat->reg = 0;		// Clear flag
	SPI2BUF = 0; 			// Clear the receive buffer

	uint32_t f_pb = getPeripheralClock();
	*spi2brg = (f_pb / (2 * f_sck)) - 1; // Set SCK
	
	spi2con->set = (1 << _SPI2CON_MSTEN);		// Mode Master
	spi2con->set = (1 << _SPI2CON_CKE);			
	spi2con->set = (1 << _SPI2CON_ON); 			// Enable SPI1

	//pinMode(chipSelectPin, OUTPUT);
	
	#ifdef SPI_IN_INTERRUPT_MODE
											
	setIntVector(_spi2_vector, &Spi2RxEventHandler);	// we only enable the SPI RX interrupt
	setIntPriority(_spi2_vector, _ipl, _spl);	// since TX and RX happen concurrently,
	iec1->set = (1 << _IEC1_RXIE);				// i.e. an RX completion necessarily
												// implies a TX completion.
	#endif
	
	/*
	uint32_t *reg;
	reg = ((uint32_t *)&SPI2BRG);
	Serial.print("/nSPI2BRG: ");
	Serial.print(*reg);
	*/
}

uint8_t DPA_SPI::transfer(uint8_t _data)
{
	digitalWrite(chipSelectPin, LOW);
	delayMicroseconds(15);
	while ((spi2stat->reg & (1 << _SPI2STAT_SPITBE)) == 0 || (spi2stat->reg & (1 << _SPI2STAT_SPIBUSY))); // while transmit buffer not empty and busy
	SPI2BUF = _data;
	while ((spi2stat->reg & (1 << _SPI2STAT_SPIRBF)) == 0 || (spi2stat->reg & (1 << _SPI2STAT_SPIBUSY))); // while receive buffer not full and busy
	delayMicroseconds(15);
	digitalWrite(chipSelectPin, HIGH);

	spi2stat->clr = (1 << _SPI2STAT_SPITBE);
	spi2stat->clr = (1 << _SPI2STAT_SPIRBF);
	spi2stat->clr = (1 << _SPI2STAT_SPIBUSY);
	return SPI2BUF;
}

void DPA_SPI::txrx(uint8_t*     bufTx,
	uint8_t      lenbufTx,
	spi_return_t returnType,
	uint8_t*     bufRx,
	uint8_t      maxLenBufRx,
	spi_first_t  isFirst,
	spi_last_t   isLast)
{
	#ifdef SPI_IN_INTERRUPT_MODE	
	iec1->clr = (1 << _IEC1_RXIE); // Disable RX interrupt 
	#endif

	// register spi frame to send
	spi_vars.pNextTxByte = bufTx;
	spi_vars.numTxedBytes = 0;
	spi_vars.txBytesLeft = lenbufTx;
	spi_vars.returnType = returnType;
	spi_vars.pNextRxByte = bufRx;
	spi_vars.maxRxBytes = maxLenBufRx;
	spi_vars.isFirst = isFirst;
	spi_vars.isLast = isLast;

	spi_vars.busy = 1; // SPI is now busy

	// lower CS signal to have slave listening
	if (spi_vars.isFirst == SPI_FIRST) 
	{
		digitalWrite(chipSelectPin, LOW);

		for (spi_vars.i = 0; spi_vars.i < 10; spi_vars.i++) 
		{
			delayMicroseconds(1);
		}
	}

#ifdef SPI_IN_INTERRUPT_MODE
	// implementation 1. use a callback function when transaction finishes
	
	*spi2buf = *spi_vars.pNextTxByte; // write first byte to TX buffer
	ifs1->clr = (1 << _IFS1_RXIF); // Clear interrupt flag
	iec1->set = (1 << _IEC1_RXIE); // Enable interrupt RX 
#else
	// implementation 2. busy wait for each byte to be sent

	// send all bytes
	while (spi_vars.txBytesLeft>0) 
	{
		// write next byte to TX buffer
		SPI2BUF = *spi_vars.pNextTxByte;
		// busy wait on the interrupt flag
		//while ((UC1IFG & UCB1RXIFG) == 0);
		while ((spi2stat->reg & (1 << _SPI2STAT_SPIRBF)) == 0)
			// clear the interrupt flag
			//UC1IFG &= ~UCB1RXIFG;
			spi2stat->clr = (1 << _SPI2STAT_SPIRBF);
		// save the byte just received in the RX buffer
		switch (spi_vars.returnType) 
		{
			case SPI_FIRSTBYTE:
				if (spi_vars.numTxedBytes == 0) 
				{
					*spi_vars.pNextRxByte = SPI2BUF;
				}
			break;
			case SPI_BUFFER:
				*spi_vars.pNextRxByte = SPI2BUF;
				spi_vars.pNextRxByte++;
			break;
			case SPI_LASTBYTE:
				*spi_vars.pNextRxByte = SPI2BUF;
			break;
		}
		// one byte less to go
		spi_vars.pNextTxByte++;
		spi_vars.numTxedBytes++;
		spi_vars.txBytesLeft--;
	}

	// put CS signal high to signal end of transmission to slave
	if (spi_vars.isLast == SPI_LAST) 
	{
		for (spi_vars.i = 0; spi_vars.i < 10; spi_vars.i++)
		{
			delayMicroseconds(1);
		}

		digitalWrite(chipSelectPin, HIGH);
	}
	spi_vars.busy = 0; // SPI is not busy anymore
#endif
}

kick_scheduler_t spi_rx_isr()
{
	#ifdef SPI_IN_INTERRUPT_MODE
		clearIntFlag(_SPI2_RX_IRQ);
		// save the byte just received in the RX buffer
		switch (spi_vars.returnType) 
		{
			case SPI_FIRSTBYTE:
				if (spi_vars.numTxedBytes == 0) 
				{
					*spi_vars.pNextRxByte = SPI2BUF;
				}
			break;
			case SPI_BUFFER:
				*spi_vars.pNextRxByte = SPI2BUF;
				spi_vars.pNextRxByte++;
			break;
			case SPI_LASTBYTE:
				*spi_vars.pNextRxByte = SPI2BUF;
			break;
		}

	// one byte less to go
	spi_vars.pNextTxByte++;
	spi_vars.numTxedBytes++;
	spi_vars.txBytesLeft--;

	if (spi_vars.txBytesLeft>0) 
	{
		// write next byte to TX buffer
		SPI2BUF = *spi_vars.pNextTxByte;
	}
	else 
	{
		// put CS signal high to signal end of transmission to slave
		if (spi_vars.isLast == SPI_LAST) 
		{
			for (spi_vars.i = 0; spi_vars.i < 5; spi_vars.i++) {
				delayMicroseconds(1);
			}

			digitalWrite(chipSelectPin, HIGH);
		}
		// SPI is not busy anymore
		spi_vars.busy = 0;

		// SPI is done!
		if (spi_vars.callback != NULL) 
		{
			// call the callback
			spi_vars.callback();
			// kick the OS
			return KICK_SCHEDULER;
		}
	}
	#else
		while (1);// this should never happen
	#endif
}
