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
 * RPi_io library serves as a base programming interface for accessing
 * IO functionality on Raspberry platform.
 * Current implementation works only with pins, whose numbers are constants
 * of @c rpi_io_Pin enum.
 *
 * @file		rpi_io.h
 * @version		1.0
 * @date		26.8.2014
 */

#ifndef __RPI_IO_H_
#define __RPI_IO_H_

#include <linux/types.h>
#include <commons/base_types.h>
#include <commons/errors.h>

/**
 * Available pins.
 */
typedef enum rpi_io_Pin {
    RPIIO_PIN_GPIO2 = 2,
    RPIIO_PIN_GPIO3 = 3,
    RPIIO_PIN_GPIO4 = 4,
    RPIIO_PIN_GPIO5 = 5,
    RPIIO_PIN_GPIO6 = 6,
    RPIIO_PIN_BUTTON = 7,
    RPIIO_PIN_CE0 = 8,
    RPIIO_PIN_MISO = 9,
    RPIIO_PIN_MOSI = 10,
    RPIIO_PIN_SCLK = 11,
    RPIIO_PIN_GPIO12 = 12,
    RPIIO_PIN_GPIO13 = 13,
    RPIIO_PIN_TXD = 14,
    RPIIO_PIN_RXD = 15,
    RPIIO_PIN_GPIO16 = 16,
    RPIIO_PIN_GPIO17 = 17,
    RPIIO_PIN_GPIO18 = 18,
    RPIIO_PIN_GPIO19 = 19,
    RPIIO_PIN_GPIO20 = 20,
    RPIIO_PIN_GPIO21 = 21,
    RPIIO_PIN_LED = 22,
    RPIIO_PIN_RESET = 23,
    RPIIO_PIN_IO1 = 24,
    RPIIO_PIN_IO2 = 25,
    RPIIO_PIN_GPIO26 = 26,
    RPIIO_PIN_GPIO27 = 27
} rpi_io_Pin;

/**
 * Logical levels of a pin.
 */
typedef enum rpi_io_PinLevel {
    RPIIO_PINLEVEL_LOW = 0, /**< low */
    RPIIO_PINLEVEL_HIGH = 1 /**< high */
} rpi_io_PinLevel;

/**
 * Signal direction of a pin.
 */
typedef enum rpi_io_Direction {
    RPIIO_DIR_INPUT = 0, /**< input */
    RPIIO_DIR_OUTPUT = 1 /**< output */
} rpi_io_Direction;


/**
 * Initializes IO.
 * @return @c BASE_TYPES_OPER_OK if initialization has been successful
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred
 *         or if library is already initialized
 */
extern int rpi_io_init();

/**
 * Configures and sets specified pin to specified direction.
 * If @c pin or @c direction has invalid value, @c BASE_TYPES_OPER_ERROR is returned.
 * @param @c pin number of pin to configure
 * @param @c direction direction to use
 * @return @c BASE_TYPES_OPER_OK if setting has been successful
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation.
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_set(uint8_t pin, rpi_io_Direction direction);

/**
 * Writes specified value to specified pin.
 * If @c pins or @c value has invalid value, @c BASE_TYPES_OPER_ERROR is returned.
 * @param @c pins number of pins to write into
 * @param @c value value write into specified pins
 * @return @c BASE_TYPES_OPER_OK if writing has been successful
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation.
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_write(uint8_t pins, rpi_io_PinLevel value);

/**
 * Reads a value from specified pins.
 * If @c pins has invalid value, @c BASE_TYPES_OPER_ERROR is returned.
 * @param @c pins number of pins to read from
 * @return value read from specified pins, a constants from @c rpi_io_PinLevel enum
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_read(uint8_t pins);

/**
 * Resets TR module.
 * @return @c BASE_TYPES_OPER_OK if operation has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_resetTr(void);

/**
 * Terminates the library and frees up used resources.
 * After this method return the results of subsequent usages of the library are undefined.
 * @return @c BASE_TYPES_OPER_OK if operation has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if a error has occurred during destroying
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_destroy(void);

/**
 * Returns information about last error or @c NULL if no error has yet occurred.
 * @return information about last error
 * @return @c NULL if no error has yet occurred
 */
extern errors_OperError* rpi_io_getLastError(void);

#endif // __RPI_IO_H_
