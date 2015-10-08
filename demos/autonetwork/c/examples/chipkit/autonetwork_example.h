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

#ifndef _AUTONETWORK_EXAMPLE_H
#define _AUTONETWORK_EXAMPLE_H


void MySerialEvent(void);

//--------------
// UART commands
//--------------
#define CMD_START_AN            0x01
#define CMD_STOP_AN             0x02
#define CMD_REMOVE_BONDS        0x03

#endif
