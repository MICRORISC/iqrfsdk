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

#ifndef __BOARD_H
#define __BOARD_H

#include <WProgram.h>
#include <sys/attribs.h>

typedef enum 
{
	DO_NOT_KICK_SCHEDULER,
	KICK_SCHEDULER,
} kick_scheduler_t;

void __attribute__((interrupt)) Spi2RxEventHandler(void);
void __attribute__((interrupt)) Uart2EventHandler(void);
void __attribute__((interrupt)) Timer2EventHandler(void);

#endif
