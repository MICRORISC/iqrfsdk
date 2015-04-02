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

int main(void) {
    int cycle = 0;
    int value = 0;
    int operResult = 0;

    struct timespec sleepValue = {0, 0};
    sleepValue.tv_nsec = INTERVAL_MS;

    printf("LED usage example \n");

    // enable access to IOs
    operResult = rpi_io_init();
    if (operResult != BASE_TYPES_OPER_OK) {
        printErrorAndExit("Initialization failed: %s", rpi_io_getLastError(), operResult);
    }

    // set pin output
    operResult = rpi_io_set(RPIIO_PIN_LED, RPIIO_DIR_OUTPUT);
    if (operResult != BASE_TYPES_OPER_OK) {
        printErrorAndExit("Setting LED port failed", rpi_io_getLastError(), operResult);
    }

    do {
        value ^= 1;
        operResult = rpi_io_write(RPIIO_PIN_LED, value);
        if (operResult != BASE_TYPES_OPER_OK) {
            printf("Cycle: %d", cycle);
            printErrorAndExit("Writing to LED port failed", rpi_io_getLastError(), operResult);
        }
        nanosleep(&sleepValue, NULL);
    } while (cycle++ <= 10);

    // finish the library
    rpi_io_destroy();

    return 0;
}
