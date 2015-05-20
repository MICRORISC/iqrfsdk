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

/**
 * Common types for CDCImpl class.
 *
 * @author		Michal Konopa
 * @file		CDCTypes.h
 * @version		1.0.0
 * @date		12.9.2012
 */

#ifndef __CDCTypes_h_
#define __CDCTypes_h_

#include <string>

/** String, which consists of unsigned chars. */
typedef std::basic_string<unsigned char> ustring;

/** Message types. */
enum MessageType {
	MSG_ERROR, MSG_TEST, MSG_RES_USB, MSG_RES_TR, MSG_USB_INFO,
	MSG_TR_INFO, MSG_USB_CONN, MSG_SPI_STAT, MSG_DATA_SEND, MSG_SWITCH,
	MSG_ASYNC
};

#endif //__CDCTypes_h_
