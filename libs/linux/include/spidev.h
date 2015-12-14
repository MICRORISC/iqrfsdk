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
 * RPi_spi_iqrf library serves as an programming interface for communication
 * between Linux system and TR modules using SPI and IO. IO functionality is build
 * up on 'Rpi_io' static library.
 *
 * @file		rpi_spi_iqrf.h
 * @version		1.0
 * @date		26.8.2014
 */

#ifndef __RPI_SPI_IQRF_H
#define __RPI_SPI_IQRF_H

/**
 * Error constants.
 */
typedef enum rpi_spi_iqrf_Errors {
    RPISPIIQRF_ERROR_BAD_STATUS = -10, /**< bad value of SPI Status was returned from SPI device */
    RPISPIIQRF_ERROR_CRCS = -11 /**< CRCS mismatch */
} rpi_spi_iqrf_Errors;

/**
 * Values according to the table in IQRF SPI User's guide (chapter SPI status).
 * Status, which is different from Data Ready SPI status.
 */
typedef enum rpi_spi_iqrf_SPIStatus_DataNotReady {
    RPISPIIQRF_SPI_DISABLED = 0x0,
    RPISPIIQRF_SPI_SUSPENDED = 0x07,
    RPISPIIQRF_SPI_BUFF_PROTECT = 0x3F,
    RPISPIIQRF_SPI_CRCM_ERR = 0x3E,
    RPISPIIQRF_SPI_READY_COMM = 0x80,
    RPISPIIQRF_SPI_READY_PROG = 0x81,
    RPISPIIQRF_SPI_READY_DEBUG = 0x82,
    RPISPIIQRF_SPI_SLOW_MODE = 0x83,
    RPISPIIQRF_SPI_HW_ERROR = 0xFF
} rpi_spi_iqrf_SPIStatus_DataNotReady;

/**
 * Current SPI status.
 */
typedef struct rpi_spi_iqrf_SPIStatus {
    int isDataReady; /**< determines if dataReady field is valid. */

    union {
        rpi_spi_iqrf_SPIStatus_DataNotReady dataNotReadyStatus;
        int dataReady; /**< SPI data ready */
    };
} rpi_spi_iqrf_SPIStatus;

/**
 * Other common constants.
 */
typedef enum rpi_spi_iqrf_CommonConstants {
    RPISPIIQRF_MAX_DATA_LENGTH = 128 /**< maximal length of data to be write or read to or from SPI slave */
} rpi_spi_iqrf_CommonConstants;


/* Default SPI device. */
#define RPISPIIQRF_DEFAULT_SPI_DEVICE "/dev/spidev0.0"


/**
 * Initializes SPI device to use.
 * @param dev SPI device to communicate with
 * @return @c BASE_TYPES_OPER_OK if initialization has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during initialization
 *         or if library is already initialized
 */
extern int rpi_spi_iqrf_init(const char* dev);

/**
 * Initializes default SPI device to use.
 * SPI device to communicate with will be the @c RPISPIIQRF_DEFAULT_SPI_DEVICE
 * @return @c BASE_TYPES_OPER_OK if initialization has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during initialization
 *         or if library is already initialized
 */
extern int rpi_spi_iqrf_initDefault(void);

/**
 * Returns SPI status of TR module.
 * @param spiStatus returned SPI status, cannot be @c NULL
 * @return @c BASE_TYPES_OPER_OK if operation has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation.
 *         This includes the situation when @c spiStatus has had @c NULL value.
 * @return @c RPISPIIQRF_ERROR_BAD_STATUS if returned status value is incorrect
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_spi_iqrf_getSPIStatus(rpi_spi_iqrf_SPIStatus* spiStatus);

/**
 * Writes specified data into the module.
 * @param dataToWrite data to write, cannot be @c NULL
 * @param dataLen length (in bytes) of data to write, valid value must be in interval of
 *        (0, RPISPIIQRF_MAX_DATA_LENGTH>
 * @return @c BASE_TYPES_OPER_OK if operation has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_spi_iqrf_write(void* dataToWrite, unsigned int dataLen);

/**
 * Read specified number of bytes from the module and stores them into specified buffer.
 * @param readBuffer buffer to store data from module, cannot be @c NULL
 * @param dataLen length (in bytes) of data to read, valid value must be in interval of
 *        (0, RPISPIIQRF_MAX_DATA_LENGTH>
 * @return @c BASE_TYPES_OPER_OK if operation has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation
 * @return @c RPISPIIQRF_ERROR_CRCS if CRSC of returned data doesn't match
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_spi_iqrf_read(void* readBuffer, unsigned int dataLen);

/**
 * Terminates the library and frees up used resources.
 * @return @c BASE_TYPES_OPER_OK if operation has performed successfully
 * @return @c BASE_TYPES_OPER_ERROR if an error has occurred during operation
 * @return @c BASE_TYPES_LIB_NOT_INITIALIZED if the library has not been initialized
 */
extern int rpi_spi_iqrf_destroy(void);

/**
 * Returns information about last error or @c NULL, if no error has yet occurred.
 * @return information about last error
 * @return @c NULL if no error has yet occurred
 */
//extern errors_OperError* rpi_spi_iqrf_getLastError(void);

#endif // __RPI_SPI_IQRF_H
