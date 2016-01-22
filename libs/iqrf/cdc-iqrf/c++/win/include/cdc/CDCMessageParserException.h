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
 * Message parser exceptions for CDCImpl class.
 *
 * @author		Michal Konopa
 * @file		CDCMessageParserException.h
 * @version		1.0.0
 * @date		17.12.2011
 */


#ifndef __CDCMessageParserException_h_
#define __CDCMessageParserException_h_


#include "CDCImplException.h"


/**
 * Exception, which occurs during running of CDC message parser.
 */
class CDCMessageParserException : public CDCImplException {
private:
	/* Identity string of this exception class. */
	std::string identity;

public:
	/**
	 * Constructs exception object.
	 * @param cause description of exception cause.
	 */
	CDCMessageParserException(const char* cause);

	~CDCMessageParserException() throw();
};

#endif // __CDCReceiveException_h_
