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

#include <stdint.h>
#include <stdio.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <errno.h>
#include <time.h>
#include <rpi/rpi_io.h>

/*
 * Indicates, whether this library is initialized.
 * 0 - not initialized
 * 1 - initialized
 */
static int libIsInitialized = 0;


// Access from ARM Running Linux
#define BCM2708_PERI_BASE        0x20000000
#define GPIO_BASE                (BCM2708_PERI_BASE + 0x200000) /* GPIO controller */

#define PAGE_SIZE (4*1024)
#define BLOCK_SIZE (4*1024)

#define NANO_SECOND_MULTIPLIER  1000000  // 1 millisecond = 1,000,000 Nanoseconds
static const long INTERVAL_MS = 500 * NANO_SECOND_MULTIPLIER;


static void *gpio_map;

// I/O access
static volatile unsigned *gpio;

// GPIO setup macros. Always use INP_GPIO(x) before using OUT_GPIO(x) or SET_GPIO_ALT(x,y)
#define INP_GPIO(g) *(gpio+((g)/10)) &= ~(7<<(((g)%10)*3))
#define OUT_GPIO(g) *(gpio+((g)/10)) |=  (1<<(((g)%10)*3))
#define SET_GPIO_ALT(g,a) *(gpio+(((g)/10))) |= (((a)<=3?(a)+4:(a)==4?3:2)<<(((g)%10)*3))

#define GPIO_SET *(gpio+7)  // sets   bits which are 1 ignores bits which are 0
#define GPIO_CLR *(gpio+10) // clears bits which are 1 ignores bits which are 0

#define GPIO_READ(g)  *(gpio + 13) &= (1<<(g))


/* Last error. */
static errors_OperError* lastError = NULL;


// checks specified port if it is valid

static int checkPort(uint8_t port) {
    switch (port) {
        case RPIIO_PORT_GPIO2:
        case RPIIO_PORT_GPIO3:
        case RPIIO_PORT_GPIO4:
        case RPIIO_PORT_CE1:
        case RPIIO_PORT_CE0:
        case RPIIO_PORT_GPIO14:
        case RPIIO_PORT_GPIO15:
        case RPIIO_PORT_GPIO17:
        case RPIIO_PORT_GPIO18:
        case RPIIO_PORT_LED:
        case RPIIO_PORT_RST:
        case RPIIO_PORT_GPIO24:
        case RPIIO_PORT_GPIO25:
        case RPIIO_PORT_GPIO27:
            return BASE_TYPES_OPER_OK;
        default:
            errors_setError(
                    &lastError, "Unknown port", BASE_TYPES_OPER_ERROR, 0, 1
                    );
            return BASE_TYPES_OPER_ERROR;
    }
}

// checks specified direction if it is valid

static int checkDirection(rpi_io_Direction direction) {
    if ((direction != RPIIO_DIR_INPUT) && (direction != RPIIO_DIR_OUTPUT)) {
        errors_setError(
                &lastError, "Unknown direction", BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }
    return BASE_TYPES_OPER_OK;
}

// checks specified value if it is valid

static int checkValue(rpi_io_PortLevel value) {
    if ((value != RPIIO_PORTLEVEL_LOW) && (value != RPIIO_PORTLEVEL_HIGH)) {
        errors_setError(
                &lastError, "Unknown port level value", BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }
    return BASE_TYPES_OPER_OK;
}

/*
 * Initializes IO.
 * @return @c BASE_TYPES_OPER_OK if initialization has been successful
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred
 */
int rpi_io_init() {
    int mem_fd = 0;

    if (libIsInitialized == 1) {
        errors_setError(
                &lastError, "Library is already initialized", BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    /* open /dev/mem */
    mem_fd = open("/dev/mem", O_RDWR | O_SYNC);
    if (mem_fd < 0) {
        printf("can't open /dev/mem \n");

        // can't open device (be root)
        errors_setError(
                &lastError, "Could not open /dev/mem ", BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    /* mmap GPIO */
    gpio_map = mmap(
            NULL, //Any address in our space will do
            BLOCK_SIZE, //Map length
            PROT_READ | PROT_WRITE, // Enable reading & writing to mapped memory
            MAP_SHARED, //Shared with other processes
            mem_fd, //File to map
            GPIO_BASE //Offset to GPIO peripheral
            );

    // no need to keep mem_fd open after mmap
    close(mem_fd);

    if (gpio_map == MAP_FAILED) {
        printf("mmap error %d\n", (int) gpio_map); //errno also set!
        errors_setError(&lastError, "Mmap error", errno, 1, 1);
        return BASE_TYPES_OPER_ERROR;
    }

    // always use volatile pointer!
    gpio = (volatile unsigned *) gpio_map;

    libIsInitialized = 1;
    return BASE_TYPES_OPER_OK;
}

/*
 * Configures and sets IO direction.
 * @param @c port port number
 * @param @c direction @c INPUT or @c OUTPUT
 * @return @c BASE_TYPES_OPER_OK if setting has been successful
 * @return @c BASE_TYPES_OPER_ERROR if @c port or @c direction has had invalid values
 */
int rpi_io_set(uint8_t port, rpi_io_Direction direction) {
    int portRes = BASE_TYPES_OPER_ERROR;
    int dirRes = BASE_TYPES_OPER_ERROR;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    portRes = checkPort(port);
    if (portRes < 0) {
        return BASE_TYPES_OPER_ERROR;
    }

    dirRes = checkDirection(direction);
    if (dirRes < 0) {
        return BASE_TYPES_OPER_ERROR;
    }

    if (direction == RPIIO_DIR_INPUT) {
        INP_GPIO(port);
    }

    // must use INP_GPIO before we can use OUT_GPIO
    INP_GPIO(port);
    OUT_GPIO(port);
    return BASE_TYPES_OPER_OK;
}

/*
 * IO interfaces to setup, write and read IOs
 * @param @c port port number
 * @param @c value port value HIGH, LOW
 * @return @c BASE_TYPES_OPER_OK if writing has been successful
 * @return @c BASE_TYPES_OPER_ERROR if @c port or @c value has had invalid values
 */
int rpi_io_write(uint8_t port, rpi_io_PortLevel value) {
    int portRes = BASE_TYPES_OPER_ERROR;
    int valueRes = BASE_TYPES_OPER_ERROR;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    portRes = checkPort(port);
    if (portRes < 0) {
        return BASE_TYPES_OPER_ERROR;
    }

    valueRes = checkValue(value);
    if (valueRes < 0) {
        return BASE_TYPES_OPER_ERROR;
    }

    switch (value) {
        case RPIIO_PORTLEVEL_LOW:
            GPIO_CLR = 1 << port;
            break;

        case RPIIO_PORTLEVEL_HIGH:
            GPIO_SET = 1 << port;
            break;

        default:
            return BASE_TYPES_OPER_ERROR;
    }
    return BASE_TYPES_OPER_OK;
}

/*
 * Reads data from specified port.
 * @param @c port port number
 * @return IO logical level
 * @return @c BASE_TYPES_OPER_ERROR if @c port has had invalid value
 */
int rpi_io_read(uint8_t port) {
    int portRes = BASE_TYPES_OPER_ERROR;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    portRes = checkPort(port);
    if (portRes < 0) {
        return BASE_TYPES_OPER_ERROR;
    }

    if (GPIO_READ(port)) {
        return 1;
    }
    return 0;
}

/*
 * Resets TR module.
 */
int rpi_io_resetTR(void) {
    int ioRes = 0;

    struct timespec sleepValue = {0, 0};
    sleepValue.tv_nsec = INTERVAL_MS;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    // switch off the power
    ioRes = rpi_io_set(RPIIO_PORT_CE0, RPIIO_DIR_INPUT);
    if (ioRes == BASE_TYPES_OPER_ERROR) {
        return BASE_TYPES_OPER_ERROR;
    }

    ioRes = rpi_io_set(RPIIO_PORT_RST, RPIIO_DIR_OUTPUT);
    if (ioRes == BASE_TYPES_OPER_ERROR) {
        return BASE_TYPES_OPER_ERROR;
    }

    ioRes = rpi_io_write(RPIIO_PORT_RST, RPIIO_PORTLEVEL_HIGH);
    if (ioRes == BASE_TYPES_OPER_ERROR) {
        return BASE_TYPES_OPER_ERROR;
    }

    // sleep for 500ms
    nanosleep(&sleepValue, NULL);

    // switch on the power
    ioRes = rpi_io_write(RPIIO_PORT_RST, RPIIO_PORTLEVEL_LOW);
    if (ioRes == BASE_TYPES_OPER_ERROR) {
        return BASE_TYPES_OPER_ERROR;
    }

    return BASE_TYPES_OPER_OK;
}

/*
 * Terminates the library and frees up used resources.
 * After this method return the results of subsequent usages of the library are undefined.
 * @return @c BASE_TYPES_OPER_OK if operation has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if a error has occurred during destroying
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
int rpi_io_destroy(void) {
    int unmapRes = BASE_TYPES_OPER_ERROR;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    // after calling this method, the behaviour of the library will be
    // like if the library was not initialized
    libIsInitialized = 0;

    unmapRes = munmap(gpio_map, BLOCK_SIZE);
    if (unmapRes < 0) {
        errors_setError(&lastError, "Munmap error", errno, 1, 1);
        return BASE_TYPES_OPER_ERROR;
    }

    return BASE_TYPES_OPER_OK;
}

/*
 * Returns information about last error or @c NULL, if no error yet occurred.
 * @return information about last error
 * @return @c NULL if no error yet occurred
 */
errors_OperError* rpi_io_getLastError(void) {
    return errors_getErrorCopy(lastError);
}
