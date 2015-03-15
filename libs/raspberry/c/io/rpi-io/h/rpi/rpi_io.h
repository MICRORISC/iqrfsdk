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
 * Current implementation works only with ports, whose numbers are constants
 * of @c rpi_io_Port enum.
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
 * Available ports.
 */
typedef enum rpi_io_Port {
    RPIIO_PORT_GPIO2 = 2,
    RPIIO_PORT_GPIO3 = 3,
    RPIIO_PORT_GPIO4 = 4,
    RPIIO_PORT_CE1 = 7,
    RPIIO_PORT_CE0 = 8,
    RPIIO_PORT_GPIO14 = 14,
    RPIIO_PORT_GPIO15 = 15,
    RPIIO_PORT_GPIO17 = 17,
    RPIIO_PORT_GPIO18 = 18,
    RPIIO_PORT_LED = 22,
    RPIIO_PORT_RST = 23,
    RPIIO_PORT_GPIO24 = 24,
    RPIIO_PORT_GPIO25 = 25,
    RPIIO_PORT_GPIO27 = 27
} rpi_io_Port;

/**
 * Logical levels of a port.
 */
typedef enum rpi_io_PortLevel {
    RPIIO_PORTLEVEL_LOW = 0, /**< low */
    RPIIO_PORTLEVEL_HIGH = 1 /**< high */
} rpi_io_PortLevel;

/**
 * Signal direction of a port.
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
 * Configures and sets specified port to specified direction.
 * If @c port or @c direction has invalid value, @c BASE_TYPES_OPER_ERROR is returned.
 * @param @c port number of port to configure
 * @param @c direction direction to use
 * @return @c BASE_TYPES_OPER_OK if setting has been successful
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation.
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_set(uint8_t port, rpi_io_Direction direction);

/**
 * Writes specified value to specified port.
 * If @c port or @c value has invalid value, @c BASE_TYPES_OPER_ERROR is returned.
 * @param @c port number of port to write into
 * @param @c value value write into specified port
 * @return @c BASE_TYPES_OPER_OK if writing has been successful
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation.
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_write(uint8_t port, rpi_io_PortLevel value);

/**
 * Reads a value from specified port.
 * If @c port has invalid value, @c BASE_TYPES_OPER_ERROR is returned.
 * @param @c port number of port to read from
 * @return value read from specified port, a constants from @c rpi_io_PortLevel enum
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_read(uint8_t port);

/**
 * Resets TR module.
 * @return @c BASE_TYPES_OPER_OK if operation has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_io_resetTR(void);

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
