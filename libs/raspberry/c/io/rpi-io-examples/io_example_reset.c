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

int main() {
    int8_t operResult = 0;

    printf("IQRF module reset example \n");

    // enable access to IOs
    operResult = rpi_io_init();
    if (operResult != BASE_TYPES_OPER_OK) {
        printf("Initialization failed: %s", rpi_io_getLastError()->descr);
        return operResult;
    }

    operResult = rpi_io_resetTR();
    if (operResult != BASE_TYPES_OPER_OK) {
        printf("Reseting TR module failed: %s", rpi_io_getLastError()->descr);
        rpi_io_destroy();
        return operResult;
    }

    printf("IQRF module reset done. \n");
    rpi_io_destroy();
    return 0;
}
