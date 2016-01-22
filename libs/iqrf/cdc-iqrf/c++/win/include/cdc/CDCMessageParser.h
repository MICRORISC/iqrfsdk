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
 * Incomming messages parser for CDCImpl class.
 *
 * @author		Michal Konopa
 * @file		CDCMessageParser.h
 * @version		1.0.0
 * @date		12.9.2012
 */

#ifndef __CDCMessageParser_h_
#define __CDCMessageParser_h_

#include <cdc/CdcInterface.h>
#include <cdc/CDCTypes.h>


/**
 * Result type of parsing process.
 */
enum ParseResultType {
	PARSE_OK,               /**< parsing OK */
	PARSE_NOT_COMPLETE,     /**< not enough data to recognize message format */
	PARSE_BAD_FORMAT        /**< bad format of message */
};

/**
 * Result of incomming data parsing process.
 */
struct ParseResult {
	MessageType msgType;    		/**< standard message type */
	ParseResultType resultType;		/**< result type of parsing */
	unsigned int lastPosition;		/**< last parsed position */
};

/**
 * Forward declaration of CDCMessageParser implementation class.
 */
class CDCMessageParserPrivate;

/**
 * Parser of messages, which come from COM-port. Parser is based on finite
 * automata theory.
 */
class CDCMessageParser {
private:
	// Pointer to implementation object(d-pointer).
	CDCMessageParserPrivate* implObj;

public:
	/**
	 * Constructs new message parser.
	 */
	CDCMessageParser();

	/**
	 *  Frees up used resources.
	 */
    ~CDCMessageParser();

	/**
	 * Parses specified data and returns result.
	 * @return result of parsing of specified data
	 */
	ParseResult parseData(ustring& data);

	/**
	 * Returns USB device info from specified data.
	 * @return USB device info from specified data.
	 */
	DeviceInfo* getParsedDeviceInfo(ustring& data);

	/**
	 * Returns TR module info from specified data.
	 * @return TR module info from specified data.
	 */
	ModuleInfo* getParsedModuleInfo(ustring& data);

	/**
	 * Returns SPI status from specified data.
	 * @return SPI status from specified data.
	 */
	SPIStatus getParsedSPIStatus(ustring& data);

	/**
	 * Returns data send response from specified data.
	 * @return data send response from specified data.
	 */
	DSResponse getParsedDSResponse(ustring& data);

	/**
	 * Returns data part of last parsed DR message.
	 * @returns data part of last parsed DR message.
	 */
    ustring getParsedDRData(ustring& data);
};

#endif // __CDCMessageParser_h_
