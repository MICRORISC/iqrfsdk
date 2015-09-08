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

#ifndef _DPA_TIMER_H
#define _DPA_TIMER_H

#include <WProgram.h>
#include <p32_defs.h>
#include <sys/attribs.h>

#include "BOARD.h"

#define _TMR2_SET_PRESCALER					4
#define _PRESCALE_VALUE						1 // 1:2 prascale
#define _T2CON_ON							15
#define _IFS0_T2IF							8
#define _IEC0_T2IE							8

#define _TIMER2_VECTOR						0x08
#define PORT_TIMER_WIDTH                    uint16_t

typedef void(*bsp_timer_cbt)(void);


class DPA_TIMER 
{
	public:
		int _timer2_vector;
		int _ipl;
		int _spl;
		volatile p32_regset*		iec0;
		volatile p32_regset*		ifs0;
		volatile p32_regset*		t2con;
		volatile uint32_t*			tmr2;
		volatile uint32_t*			pr2;

		DPA_TIMER();
		void				init(void);
		void				setCallback(bsp_timer_cbt cb);
		void				reset(void);
		void				attachInterrupt();
		PORT_TIMER_WIDTH	getCurrentValue(void);
};

kick_scheduler_t   timer_isr(void);

#endif
