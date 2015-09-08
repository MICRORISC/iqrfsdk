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

#include "DPA_TIMER.h"
#include "BOARD.h"

typedef struct 
{
	bsp_timer_cbt    cb;
} bsp_timer_vars_t;

bsp_timer_vars_t bsp_timer_vars;

//=========================== prototypes ======================================

//=========================== public ==========================================

DPA_TIMER::DPA_TIMER()
{
	_timer2_vector = _TIMER2_VECTOR;
	_ipl = 0x03; // Priority
	_spl = 0x01; // Subpriority

	tmr2 = ((volatile uint32_t *)&TMR2); // Timer register
	pr2 = ((volatile uint32_t *)&PR2);	// Period register
	t2con = ((p32_regset *)&T2CON);		// Timer2 control register
	ifs0 = ((p32_regset *)&IFS0);	// Interrupt flag register
	iec0 = ((p32_regset *)&IEC0);	// Interrupt flag register
}

/**
\brief Initialize this module.
This functions starts the timer, i.e. the counter increments, but doesn't set
any compare registers, so no interrupt will fire.
*/
void DPA_TIMER::init()
{
	// clear local variables
	memset(&bsp_timer_vars, 0, sizeof(bsp_timer_vars_t));
	
	*pr2 = 0x9C40; // Period 40000 ticks to 1ms
	*tmr2 = 0;
	t2con->set = (_PRESCALE_VALUE << _TMR2_SET_PRESCALER); // Clock Timer 2 = 40MHz
}

void DPA_TIMER::attachInterrupt()
{
	setIntVector(_timer2_vector, &Timer2EventHandler);
	setIntPriority(_timer2_vector, _ipl, _spl);
	ifs0->clr = (1 << _IFS0_T2IF);
	t2con->set = (1 << _T2CON_ON); // Start timer
	iec0->set = (1 << _IEC0_T2IE);
}

/**
\brief Register a callback.
\param cb The function to be called when a compare event happens.
*/
void DPA_TIMER::setCallback(bsp_timer_cbt cb)
{
	bsp_timer_vars.cb = cb;
}

/**
\brief Reset the timer.
This function does not stop the timer, it rather resets the value of the
counter, and cancels a possible pending compare event.
*/
void DPA_TIMER::reset()
{
	*tmr2 = 0;
}

/**
\brief Return the current value of the timer's counter.
\returns The current value of the timer's counter.
*/
PORT_TIMER_WIDTH DPA_TIMER::getCurrentValue()
{
	return *tmr2;
}

//=========================== interrupt handlers ==============================

kick_scheduler_t timer_isr() 
{
	// call the callback
	bsp_timer_vars.cb();
	clearIntFlag(_TIMER2_VECTOR);
	return DO_NOT_KICK_SCHEDULER;
}
