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

#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <cdc/C_CdcInterface.h>


// length of message header
const unsigned int HEADER_LENGTH = 4;

// confirmation header
const unsigned char CONFIRMATION_HEADER[] = { 0x01, 0x00, 0x0A, 0x00 };

// response header
const unsigned char RESPONSE_HEADER[] = { 0x01, 0x00, 0x0A, 0x80 };


// prints specified data onto standard output in hex format 
void printDataInHex(unsigned char* data, unsigned int length) {
    int i = 0;
    
    for ( i = 0; i < length; i++ ) {
        printf("0x%.2x", (int)*data);
        data++;
        if ( i != (length - 1) ) {
            printf(" ");
        }
    }
    printf("\n");
}

// determines, if the specified message header is the CONFIRMATION
int isConfirmation(unsigned char* msgHeader) {
    int i = 0;
    
    for ( i = 0; i < HEADER_LENGTH; i++ ) {
        if ( *msgHeader != CONFIRMATION_HEADER[i] ) {
            return 0;
        }
        msgHeader++;
    }
    return 1;
}

// determines, if the specified message header is the RESPONSE
int isResponse(unsigned char* msgHeader) {
    int i = 0;
    
    for ( i = 0; i < HEADER_LENGTH; i++ ) {
        if ( *msgHeader != RESPONSE_HEADER[i] ) {
            return 0;
        }
        msgHeader++;
    }
    return 1;
}

// prints specified response onto standard output
void printResponse(unsigned char* response, unsigned int length) {
    // positions of fields in the response
    const int ERROR_CODE_POS = 6;
    const int ERROR_CODE_OK = 0;
    const int INT_VALUE_POS = 8;
    const int FULL_VALUE_POS = 9;
    const int FULL_VALUE_LENGTH = 2;
    
    unsigned char value;
    unsigned char* fullTemperatureValue;
    unsigned char fractialPart;
    
    // checking of ErrN field
    int errorCode = *(response+ERROR_CODE_POS); 
    if ( errorCode != ERROR_CODE_OK ) {
        printf("Error: %u\n", errorCode);
        return;
    }
    
    value = *(response + INT_VALUE_POS);        
    fullTemperatureValue = malloc(FULL_VALUE_LENGTH);
    memcpy(fullTemperatureValue, response+FULL_VALUE_POS, FULL_VALUE_LENGTH);
    fractialPart = *fullTemperatureValue;

    printf("Temperature = %i.%u C\n", (int)value, (int)fractialPart);
    free(fullTemperatureValue); 
}

void receiveData(unsigned char* data, unsigned int length) {
    unsigned char* msgHeader;
    
    if ( data == NULL || length == 0 ) {
        printf("No data received\n");
        return;
    }

    if ( length < HEADER_LENGTH ) {
        printf("Unknown data: ");
        printDataInHex(data, length);
        return;
    }
    
    msgHeader = malloc(HEADER_LENGTH);
    memcpy(msgHeader, data, HEADER_LENGTH);
    
    if ( isConfirmation(msgHeader) ) {
        printf("Confirmation received: ");
        printDataInHex(data, length);
        free(msgHeader);
        return;
    }
    
    if ( isResponse(msgHeader) ) {
        printf("Response received: \n");
        printResponse(data, length);
        free(msgHeader);
        return;
    }
    
    printf("Unknown type of message. Data: ");
    printDataInHex(data, length);
    free(msgHeader);
}


int main() {
    int initRes = 0;
    int testRes = 0;
    int asynclRes = 0;
    int sendRes = 0;
    C_DSResponse dsResponse;
    int sendCounter = 0;
    Error initError;
    
    // DPA temperature request
    unsigned char temperatureRequest[] = { 0x01, 0x00, 0x0A, 0x00, 0xFF, 0xFF };

    
    // library initializing - communication via /dev/ttyACM0
    initRes = init("/dev/ttyACM0");
    if ( initRes == OPER_ERROR ) {
        if ( getLastError(&initError) == OPER_OK ) {
            printf("%s\n", initError.descr);
        } else {
            printf("Error occurred during initialization\n");
        }
        return 1;
    }

    // communication testing
    testRes = test();
    switch ( testRes ) {
        case OPER_ERROR:
            printf("Error occurred during test operation\n");
            destroy();
            return 1;
        case 1:
            printf("Test OK\n");
            break;
        default:
            printf("Test FAILED\n");
            destroy();
            return 2;
    }

    // register to receiving asynchronous messages
    asynclRes = registerAsyncMsgListener(&receiveData);
    if ( asynclRes != OPER_OK ) {
        printf("Error while installing asynchronous listener.\n");
        destroy();
        return 1;
    }

    printf("Sending and receiving...\n");

    for ( sendCounter = 0; sendCounter < 10; sendCounter++ ) {
        sendRes = sendData(temperatureRequest, sizeof(temperatureRequest), &dsResponse);
        if ( sendRes != OPER_OK ) {
            Error sendError;
            getLastError(&sendError);
            if ( sendError.descr != NULL ) {
                printf("Error occurred during data send: %s\n", sendError.descr);
            }
            // error processing...
        } else {
            if ( dsResponse != DS_OK ) {
                // bad response processing...
                printf("Response not OK: %u\n", dsResponse);
            }
        }

        // if reception is stopped, is not further possible to send and
        // to receive any next messages
        if ( isReceptionStopped() ) {
            destroy();
            return 1;
        }

        // sending another message and waiting for the answer
        
        // please consult DPA guide (chapter 2.6.2) for correct timing of the 
        // next request to be sent to the network
        
        // e.g. if having only one node in STD network = 2*30ms (forward request 
        // routing) + 2*50ms (backward response routing) + safety time (time 
        // needed for the processing on the node)
        
        // ! do not send next request just right after receiving response since
        // this may cause RF collisions due to the fact that some of the nodes
        // close to coordinator are still routing the response, wait calculated 
        // time and then sent next request to the network !
        
        if ( sleep(1) != 0 ) {
            printf("Waiting for giving USB time to get ready interrupted.");
            destroy();
            return 1;
        }
    }

    destroy();
    return 0;
}


