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
#include <sysfs_gpio.h>
#include <machines_def.h>

#define NANO_SECOND_MULTIPLIER  1000000  // 1 millisecond = 1,000,000 Nanoseconds
const long INTERVAL_MS = 500 * NANO_SECOND_MULTIPLIER;

int main(void) {
    int cycle = 0;
    int value = 0;
    int ret;

    struct timespec sleepValue = {0, INTERVAL_MS};

    printf("LED usage example \n");

    // enable access to IOs
    ret = setup_gpio(LED_GPIO, GPIO_DIRECTION_OUT, value);
    if (ret)
	    return;

    do {
        value ^= 1;
	// toggle value
	gpio_set_value(LED_GPIO, value);
        nanosleep(&sleepValue, NULL);
    } while (cycle++ <= 10);

    cleanup_gpio(LED_GPIO);
    return 0;
}
