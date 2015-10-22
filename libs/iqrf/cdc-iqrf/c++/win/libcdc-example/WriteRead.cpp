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
 * DPA Write-Read example
 *
 * @author      Michal Konopa, Rostislav Spinar
 * @version     1.0.0
 * @date        1.4.2015
 */

#include <iostream>
#include <windows.h>
#include <cdc/CDCImpl.h>


// length of message header
const unsigned int HEADER_LENGTH = 4;

// confirmation header
const unsigned char CONFIRMATION_HEADER[] = { 0x01, 0x00, 0x0A, 0x00 };

// response header
const unsigned char RESPONSE_HEADER[] = { 0x01, 0x00, 0x0A, 0x80 };

// prints specified data onto standard output in hex format
void printDataInHex(unsigned char* data, unsigned int length) {
    for ( int i = 0; i < length; i++ ) {
        std::cout << "0x" << std::hex << (int)*data;
        data++;
        if ( i != (length - 1) ) {
            std::cout << " ";
        }
    }
    std::cout << std::dec << "\n";
}

// determines, if the specified message header is the CONFIRMATION
bool isConfirmation(unsigned char* msgHeader) {
    for ( int i = 0; i < HEADER_LENGTH; i++ ) {
        if ( *msgHeader != CONFIRMATION_HEADER[i] ) {
            return false;
        }
        msgHeader++;
    }
    return true;
}

// determines, if the specified message header is the RESPONSE
bool isResponse(unsigned char* msgHeader) {
    for ( int i = 0; i < HEADER_LENGTH; i++ ) {
        if ( *msgHeader != RESPONSE_HEADER[i] ) {
            return false;
        }
        msgHeader++;
    }
    return true;
}

// prints specified response onto standard output
void printResponse(unsigned char* response, unsigned int length) {
    // positions of fields in the response
    const int ERROR_CODE_POS = 6;
    const int ERROR_CODE_OK = 0;
    const int INT_VALUE_POS = 8;
    const int FULL_VALUE_POS = 9;
    const int FULL_VALUE_LENGTH = 2;

    // checking of ErrN field
    int errorCode = *(response+ERROR_CODE_POS);
    if ( errorCode != ERROR_CODE_OK ) {
        std::cout << "Error: " << errorCode << "\n";
        return;
    }

    unsigned char value = *(response + INT_VALUE_POS);

    unsigned char* fullTemperatureValue = new unsigned char[FULL_VALUE_LENGTH];
    memcpy(fullTemperatureValue, response+FULL_VALUE_POS, FULL_VALUE_LENGTH);
    unsigned char fractialPart = *fullTemperatureValue;

    std::cout << "Temperature = " << (int)value << "." << (int)fractialPart << " C" << "\n";
    delete[] fullTemperatureValue;
}

void receiveData(unsigned char* data, unsigned int length) {
    if ( data == NULL || length == 0 ) {
        std::cout << "No data received\n";
        return;
    }

    if ( length < HEADER_LENGTH ) {
        std::cout << "Unknown data: ";
        printDataInHex(data, length);
        return;
    }

    unsigned char* msgHeader = new unsigned char[HEADER_LENGTH];
    memcpy(msgHeader, data, HEADER_LENGTH);

    if ( isConfirmation(msgHeader) ) {
        std::cout << "Confirmation received: ";
        printDataInHex(data, length);
        delete[] msgHeader;
        return;
    }

    if ( isResponse(msgHeader) ) {
        std::cout << "Response received: \n";
        printResponse(data, length);
        delete[] msgHeader;
        return;
    }

	std::cout << "Unknown type of message. Data: ";
	printDataInHex(data, length);
	delete[] msgHeader;
}


int main() {
	CDCImpl* testImp = NULL;
	try {
		testImp = new CDCImpl("COM5");

		bool test = testImp->test();

		if ( test ) {
			std::cout << "Test OK\n";
		} else {
			std::cout << "Test FAILED\n";
			delete testImp;
			return 2;
		}
	} catch ( CDCImplException& e ) {
		std::cout << e.getDescr() << "\n";
		if ( testImp != NULL ) {
			delete testImp;
		}
		return 1;
	}

	// register to receiving asynchronous messages
	testImp->registerAsyncMsgListener(&receiveData);

	// data to send to USB device
	const int REQUEST_LENGTH = 6;
	unsigned char temperatureRequest[REQUEST_LENGTH] = { 0x01, 0x00, 0x0A, 0x00, 0xFF, 0xFF };

	for ( int sendCounter = 0; sendCounter < 10; sendCounter++ ) {
		try {
			// sending read temperature request and checking response of the device
			DSResponse dsResponse = testImp->sendData(temperatureRequest, REQUEST_LENGTH);
			if ( dsResponse != OK ) {
				// bad response processing...
				std::cout << "Response not OK: " << dsResponse << "\n";
			}
		} catch ( CDCSendException& ex ) {
			std::cout << ex.getDescr() << "\n";
			// send exception processing...
		} catch ( CDCReceiveException& ex ) {
			std::cout << ex.getDescr() << "\n";
			// receive exception processing...
		}

		// if reception is stopped, is not further possible to send and
		// to receive any next messages
        if ( testImp->isReceptionStopped() ) {
            delete testImp;
            return 1;
        }

        // sending another message and waiting for the answer
        // we should wait for a while - to give USB device time to get ready
        // to succesfully process next message
        Sleep(1000);
    }

    delete testImp;
    return 0;
}


