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
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <time.h>
#include <linux/types.h>
#include <rpi/rpi_spi_iqrf.h>

#define NANO_SECOND_MULTIPLIER  1000000  // 1 millisecond = 1,000,000 Nanoseconds
const unsigned long INTERVAL_MS = 10 * NANO_SECOND_MULTIPLIER;


// length of request to send
const unsigned int REQUEST_LENGTH = 6;

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

void processReceivedData(unsigned char* data, unsigned int length) {
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


/*
 * Prints specified user message and specified error description onto standard
 * output, cleans up the Rpi_spi_iqrf library, and exits the program 
 * with specified return value
 */
void printErrorAndExit(
    const char* userMessage, errors_OperError* error, int retValue
) {
    printf("%s: %s", userMessage, error->descr);
    rpi_spi_iqrf_destroy();
    exit(retValue);
}

// try to wait for communication ready state in specified timeout (in ms)
rpi_spi_iqrf_SPIStatus tryToWaitForReadyState(uint32_t timeout) {
    rpi_spi_iqrf_SPIStatus spiStatus = { 0, RPISPIIQRF_SPI_DISABLED };
    int operResult = BASE_TYPES_OPER_ERROR;
    uint32_t elapsedTime = 0;
    struct timespec sleepValue = { 0, INTERVAL_MS };
    uint8_t buffer[64];
    unsigned int dataLen = 0;

    do {
        if ( elapsedTime > timeout ) {
            printf("Timeout of waiting on ready state expired\n");
            return spiStatus;
        }

        // getting slave status
        operResult = rpi_spi_iqrf_getSPIStatus(&spiStatus);
        if ( operResult != BASE_TYPES_OPER_OK ) {
            printf("Failed to get SPI status: %s \n", rpi_spi_iqrf_getLastError()->descr);
        } else {
            printf("Status: %x \n", spiStatus.dataNotReadyStatus);
        }

        nanosleep(&sleepValue, NULL);
        elapsedTime += 10;

        if ( spiStatus.isDataReady == 1 ) {
            // if any old data left to read, dispose
            if ( spiStatus.dataReady == 0x40 ) {
                dataLen = 64;
            } else {
                dataLen = spiStatus.dataReady - 0x40;
            }

            // reading - only to dispose old data if any
            rpi_spi_iqrf_read(buffer, dataLen);
        }
    } while ( spiStatus.dataNotReadyStatus != RPISPIIQRF_SPI_READY_COMM );
    return spiStatus;
}


int main() {
    int operResult = BASE_TYPES_OPER_ERROR;
    unsigned int dataLen = 0;
    unsigned int sendCounter = 0;
    uint8_t buffer[64];
    rpi_spi_iqrf_SPIStatus spiStatus = { 0, RPISPIIQRF_SPI_DISABLED };
    
    // DPA LED RED on
    //uint8_t ledSend[] = { 0x01, 0x00, 0x06, 0x01, 0xFF, 0xFF };

    // DPA LED RED off
    //uint8_t ledSend[] = { 0x01, 0x00, 0x06, 0x00, 0xFF, 0xFF };
    
    // DPA temperature request
    uint8_t temperatureRequest[] = { 0x01, 0x00, 0x0A, 0x00, 0xFF, 0xFF };
    
    

    printf("Rpi_iqrf usage simple writeread SPI example\n");

    // library initialization
    operResult = rpi_spi_iqrf_init( "/dev/spidev0.0" );
    if ( operResult != BASE_TYPES_OPER_OK ) {
    	printf("Initialization failed: %s \n", rpi_spi_iqrf_getLastError()->descr);
        return operResult;
    }

    
    for ( sendCounter = 0; sendCounter < 10; sendCounter++ ) {
        // waiting for SPI READY state
        spiStatus = tryToWaitForReadyState(5000);

        // if SPI not ready in 5000 ms, end
        if ( spiStatus.dataNotReadyStatus != RPISPIIQRF_SPI_READY_COMM ) {
            printf("Waiting for ready state failed.\n");
            rpi_spi_iqrf_destroy();
            return 1;
        }
        
        // sending some data to TR module
        operResult = rpi_spi_iqrf_write(temperatureRequest, sizeof(temperatureRequest));
        if ( operResult != BASE_TYPES_OPER_OK ) {
            printErrorAndExit("Error during data sending", rpi_spi_iqrf_getLastError(), operResult);
        }
        printf("Data successfully sent to SPI device.\n");
        
        // waiting for SPI READY state
        spiStatus = tryToWaitForReadyState(5000);

        // if SPI not ready in 5000 ms, end
        if ( spiStatus.dataNotReadyStatus != RPISPIIQRF_SPI_READY_COMM ) {
            printf("Waiting for ready state failed.\n");
            rpi_spi_iqrf_destroy();
            return 1;
        }
        
        // determining the data length
        if ( spiStatus.dataReady == 0x40 ) {
            dataLen = 64;
        } else {
            dataLen = spiStatus.dataReady - 0x40;
        }
        
        // reading data
        operResult = rpi_spi_iqrf_read(buffer, dataLen);
        if ( operResult != BASE_TYPES_OPER_OK ) {
            printErrorAndExit(
                "Error during data reading", rpi_spi_iqrf_getLastError(), operResult
            );
        }
        printf("Data successfully received from IQRF module\n");
        
        processReceivedData(buffer, dataLen);
        
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
            rpi_spi_iqrf_destroy();
            return 1;
        }
    }

    // to free used resources
    rpi_spi_iqrf_destroy();
    return 0;
}
