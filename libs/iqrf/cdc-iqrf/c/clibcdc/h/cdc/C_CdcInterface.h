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
 * This interface form the pure C-language wrapper for CDCImpl class. 
 * The CDCImpl class provides concrete implementation of communication
 * between computer system and MICRORISC USB device. Currently supported
 * platforms are Windows and Linux. 
 * 
 * @author      	Michal Konopa
 * @file		C_CdcInterface.h
 * @version		1.0
 * @date		27.1.2012
 */

#ifndef __InterfaceToC_h_
#define __InterfaceToC_h_

/**
 * Sizes of some inner members of C_ModuleInfo structure.
 */
typedef enum {
    MI_BUILD_SIZE = 2, /**< size of OS build information */
    MI_SN_SIZE = 4 /**< size of serial number */
} ModuleInfoSize;

/**
 * USB device identification. Response information of "I-command"
 * (get USB device info).
 */
typedef struct {
    char* type; /**< device type */
    unsigned int typeLen; /**< length of device type information */
    char* firmwareVersion; /**< firmware version */
    unsigned int fvLen; /**< length of firmware version information */
    char* serialNumber; /**< serial number */
    unsigned int snLen; /**< length of serial number information */
} C_DeviceInfo;

/**
 * Information about TR module identification inside the USB device.
 * IQRF OS User's guide (chapter Identification -> Module Data).
 */
typedef struct {
    unsigned char serialNumber[MI_SN_SIZE]; /**< serial number */
    unsigned char osVersion; /**< OS version */
    unsigned char PICType; /**< PIC type */
    unsigned char osBuild[MI_BUILD_SIZE]; /**< OS build */
} C_ModuleInfo;

/**
 * Values according to the table in IQRF SPI User's guide (chapter SPI status).
 */
typedef enum {
    SPI_DISABLED = 0x0, SPI_SUSPENDED = 0x07, SPI_BUFF_PROTECT = 0x3F,
    SPI_CRCM_ERR = 0x3E, SPI_READY_COMM = 0x80, SPI_READY_PROG = 0x81,
    SPI_READY_DEBUG = 0x82, SPI_SLOW_MODE = 0x83, SPI_HW_ERROR = 0xFF
} C_SPIModes;

/**
 * Current status. Response information of "S-command" (get status).
 */
typedef struct {
    int isDataReady; /**< determines, that DATA_READY is used */

    union {
        C_SPIModes SPI_MODE;
        int DATA_READY; /**< SPI data ready */
    };
} C_SPIStatus;

/**
 * Response information of "DS-command"(send data). Its precise meaning
 * is according to: "CDC Implementation in IQRF USB devices User Guide".
 */
typedef enum {
    DS_OK, DS_ERR, DS_BUSY
} C_DSResponse;

/**
 * Results of operations.
 */
typedef enum {
    OPER_NOINIT = -2, /**< CDC library was not properly initialized */
    OPER_ERROR = -1, /**< during operation occurred error */
    OPER_OK = 0 /**< operation performs correctly */
} OperationResult;

/**
 * Encapsulates information about error, which occurred during operation.
 */
typedef struct {
    char* descr; /**< description of the error */
} Error;


// for using in C-code
#ifdef __cplusplus
extern "C" {
#endif
    /**
     * Read listener, which will be called by asynchronous message reception. <br>
     * The first parameter is a pointer to received message data. <br>
     * The second parameter is a length of received message data. <br>
     * If any error in input asynchronous message is discovered, @c NULL will be
     * returned as the first parameter, and @c 0 as the second one.
     */
    typedef void (*C_AsyncMsgListener)(unsigned char*, unsigned int);

    /**
     * Fills specified output parameter with information about last error.
     * If no error yet occurred, the description of the error will contain
     * empty string.
     * @param error will be filled by information about last error. If no
     * 		  error has yet occurred, the description of the error will be @c NULL.
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_NOINIT if error is @c NULL
     */
    int getLastError(Error* error);

    /**
     * Initializes the CDC library for use.
     * @param portName COM-port to communicate with. If @c NULL, then default
     * COM port will be used.<br>
     * On Windows is default port set to COM1, on Linux is set to "/dev/ttyS0".
     * @return @c OPER_OK if initialization gets right
     * @return @c OPER_NOINIT if the CDC library is already initialized
     * @return @c OPER_ERROR if some error occurs during initialization
     */
    int init(const char* portName);

    /**
     * Terminates the CDC library run and cleans up used resources.
     */
    void destroy(void);

    /**
     * Performs communication test("> command").
     * @return @c 1 if test is successful
     * @return @c 0 if test is not successful
     * @return @c OPER_ERROR if some error occurred during command sending or during
     *				response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized
     */
    int test(void);

    /**
     * Resets USB device("R-command").
     * @return @c OPER_OK if operation performs successful
     * @return @c OPER_ERROR if some error occurs during command sending or during
     *				response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized
     */
    int resetUSBDevice(void);

    /**
     * Resets TR module("RT-command").
     * @return @c OPER_OK if operation performs successful
     * @return @c OPER_ERROR if some error occurs during command sending
     *				or during response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized
     */
    int resetTRModule(void);

    /**
     * Gets USB device identification("I-command") and stores it in cDevInfo out
     * parameter.
     * @param cDevInfo out parameter, which stores returned USB device
     *		identification. Must not be @c NULL.
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_ERROR if some error occurs during command sending
     *				or during response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized or
     *				cDevInfo is @c NULL
     */
    int getUSBDeviceInfo(C_DeviceInfo* cDevInfo);

    /**
     * Gets identification of TR module inside the USB device ("IT-command") and
     * stores it in cModInfo parameter.
     * @param cModInfo out parameter, which stores returned TR module
     *		identification. Must not be @c NULL.
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_ERROR if some error occurs during command sending
     *				or during response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized or
     *				cModInfo is @c NULL
     */
    int getTRModuleInfo(C_ModuleInfo* cModInfo);

    /**
     * Performs an acoustical or optical indication of USB device
     * ("B-command").
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_ERROR if some error occurs during command sending
     *				or during response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized
     */
    int indicateConnectivity(void);

    /**
     * Gets information about current status of TR module("S-command") and
     * stores it in cSpiStatus parameter.
     * @param cSpiStatus out parameter, which stores returned status of TR
     *		module. Must not be @c NULL.
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_ERROR if some error occurs during command sending
     *				or during response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized or
     *				cSpiStatus is @c NULL
     */
    int getStatus(C_SPIStatus* cSpiStatus);

    /**
     * Sends data to TR module inside the USB device("DS-command") and stores
     * the result of that send in cDSResp parameter.
     * @param data the data to send
     * @param dlen the length of data to send
     * @param cDSResp out parameter, which stores the result of data send.
     *		Must not be @c NULL.
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_ERROR if some error occurs during command sending
     *				or during response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized or
     *				data is @c NULL or cDSResp is @c NULL
     */
    int sendData(unsigned char* data, unsigned int dlen, C_DSResponse* cDSResp);

    /**
     * Switches USB class to Custom and the device is reset 5 s after this
     * command is issued("U-command").
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_ERROR if some error occurs during command sending
     *				or during response reception
     * @return @c OPER_NOINIT if CDC library was not properly initialized
     */
    int switchToCustom(void);

    /**
     * Registers user-defined listener of asynchronous messages("DR-messages")
     * reception during run of this library. Data of each message are passed
     * in the first parameter, length of the data is passed as the second parameter.
     * @param asyncListener user's listener
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_NOINIT if CDC library was not properly initialized
     */
    int registerAsyncMsgListener(C_AsyncMsgListener asyncListener);

    /**
     * Unregisters asynchronous messages reception listener.
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_NOINIT if CDC library was not properly initialized
     */
    int unregisterAsyncMsgListener(void);

    /**
     * Indicates, whether reception of messages from associated COM-port
     * is stopped.
     * @return @c 1 if reception is stopped
     * @return @c 0 if the CDC library is able to receive
     * @return @c OPER_NOINIT if the CDC library was not properly initialized
     */
    int isReceptionStopped(void);

    /**
     * Fills specified output parameter with information about last error, which
     * has occurred during reception of messages from associated COM-port.
     * @param lastReceptionError will be filled by information about cause of
     * 		  last reception error. If no error has yet occurred, the parameter
     * 		  will be @c NULL.
     * @return @c OPER_OK if operation performs successfully
     * @return @c OPER_NOINIT if the CDC library was not properly initialized
     */
    int getLastReceptionError(char** lastReceptionError);

#ifdef __cplusplus
}
#endif

#endif // __InterfaceToC_h
