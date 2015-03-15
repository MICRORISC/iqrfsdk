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
#include <time.h>
#include <linux/types.h>
#include <rpi/rpi_io.h>

#define NANO_SECOND_MULTIPLIER  1000000  // 1 millisecond = 1,000,000 Nanoseconds
const long INTERVAL_MS = 500 * NANO_SECOND_MULTIPLIER;

/*
 * Prints specified user message and specified error description, cleans up the
 * Rpi_io library, and exits the program with specified return value
 */
void printErrorAndExit(
        const char* userMessage, errors_OperError* error, int retValue
        ) {
    printf("%s: %s", userMessage, error->descr);
    rpi_io_destroy();
    exit(retValue);
}

int main() {
    int8_t operResult = 0;
    struct timespec sleepValue = {0, 0};
    sleepValue.tv_nsec = INTERVAL_MS;

    printf("IO read usage example\n");

    // enable access to IOs
    operResult = rpi_io_init();
    if (operResult != BASE_TYPES_OPER_OK) {
        printf("Initialization failed: %s", rpi_io_getLastError()->descr);
        return operResult;
    }

    // disable PWR for TR
    operResult = rpi_io_set(RPIIO_PORT_RST, RPIIO_DIR_OUTPUT);
    if (operResult != BASE_TYPES_OPER_OK) {
        printErrorAndExit("Setting RST port failed", rpi_io_getLastError(), operResult);
    }

    operResult = rpi_io_write(RPIIO_PORT_RST, RPIIO_PORTLEVEL_HIGH);
    if (operResult != BASE_TYPES_OPER_OK) {
        printErrorAndExit("Writing to RST port failed", rpi_io_getLastError(), operResult);
    }

    operResult = rpi_io_read(RPIIO_PORT_RST);
    if (operResult < 0) {
        printErrorAndExit("Reading from RST port failed", rpi_io_getLastError(), operResult);
    }

    // if read value is HIGH
    if (operResult == RPIIO_PORTLEVEL_HIGH) {
        operResult = rpi_io_set(RPIIO_PORT_LED, RPIIO_DIR_OUTPUT);
        if (operResult != BASE_TYPES_OPER_OK) {
            printErrorAndExit("Setting LED port failed", rpi_io_getLastError(), operResult);
        }

        operResult = rpi_io_write(RPIIO_PORT_LED, RPIIO_PORTLEVEL_HIGH);
        if (operResult != BASE_TYPES_OPER_OK) {
            printErrorAndExit("Writing to LED port failed", rpi_io_getLastError(), operResult);
        }
    }

    nanosleep(&sleepValue, NULL);

    // enable PWR for TR
    operResult = rpi_io_set(RPIIO_PORT_RST, RPIIO_DIR_OUTPUT);
    if (operResult != BASE_TYPES_OPER_OK) {
        printErrorAndExit("Setting LED port failed", rpi_io_getLastError(), operResult);
    }

    operResult = rpi_io_write(RPIIO_PORT_RST, RPIIO_PORTLEVEL_LOW);
    if (operResult != BASE_TYPES_OPER_OK) {
        printErrorAndExit("Writing to RST port failed", rpi_io_getLastError(), operResult);
    }

    operResult = rpi_io_read(RPIIO_PORT_RST);
    if (operResult < 0) {
        printErrorAndExit("Reading from RST port failed", rpi_io_getLastError(), operResult);
    }

    if (operResult == RPIIO_PORTLEVEL_LOW) {
        operResult = rpi_io_write(RPIIO_PORT_LED, RPIIO_PORTLEVEL_LOW);
        if (operResult != BASE_TYPES_OPER_OK) {
            printErrorAndExit("Writing to LED port failed", rpi_io_getLastError(), operResult);
        }
    }

    rpi_io_destroy();
    return 0;
}
