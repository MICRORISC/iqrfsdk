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

#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <time.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <linux/spi/spidev.h>
#include <rpi/rpi_spi_iqrf.h>
#include <rpi/rpi_io.h>


/*
 * Indicates, whether this library is initialized.
 * 0 - not initialized
 * 1 - initialized
 */
static int libIsInitialized = 0;

/* Designates, that file descriptor is not used. */
static const int NO_FILE_DESCRIPTOR = -1;

/* File descriptor of device special file. */
static int fd = -1;


/*
 * Device's mode (according to document: "SPI. Implementation in IQRF TR modules"):
 * Idle clock polarity: low
 * Clock edge: output data on SCK rising edge
 */
static const uint8_t SPI_MODE = SPI_MODE_0;

/* Device's wordsize. */
static const uint8_t BITS_PER_WORD = 8;

/* Device's max. bitrate [Hz]. */
static const uint32_t SPI_MAX_SPEED = 250000;


/*
 * Delay between 2 consecutive bytes.
 * T2 period
 */
static const struct timespec T2_DELAY = {.tv_sec = 0, .tv_nsec = 800000};

/*
 * how long to delay [microseconds] after the last bit transfer
 * before optionally deselecting the device before next transfer
 * T1 period
 */
static const uint16_t DELAY_AFTER_TRANSFER = 10;


/* SPI checking packet indication. */
static const uint8_t RPISPIIQRF_SPI_CHECK = 0x00;

/* SPI command packet indication. */
static const uint8_t RPISPIIQRF_SPI_CMD = 0xF0;

typedef enum {
    CTYPE_BUFFER_CHANGED,
    CTYPE_BUFFER_UNCHANGED
} rpiiqrf_SPICtype;


/* Last error. */
static errors_OperError* lastError = NULL;

/* Null byte transfer. */
static struct spi_ioc_transfer nullTransfer;

/*
 * Initializes nullTransfer structure.
 * It is used for application of T1 delay - see document:
 * "SPI. Implementation if IQRF modules. User Guide", version: 130430
 */
static void initNullTransfer(void) {
    nullTransfer.tx_buf = 0;
    nullTransfer.rx_buf = 0;
    nullTransfer.len = 0;
    nullTransfer.delay_usecs = DELAY_AFTER_TRANSFER;
    nullTransfer.speed_hz = SPI_MAX_SPEED;
    nullTransfer.bits_per_word = BITS_PER_WORD;
    nullTransfer.cs_change = 0;
}

/* Sets SPI mode. */
static int setMode() {
    int setResult = 0;
    uint8_t rdMode = -1;

    setResult = ioctl(fd, SPI_IOC_WR_MODE, &SPI_MODE);
    if (setResult < 0) {
        return setResult;
    }

    setResult = ioctl(fd, SPI_IOC_RD_MODE, &rdMode);
    if (setResult < 0) {
        return setResult;
    }

    return 0;
}

/* Sets bits per word. */
static int setBitsPerWord() {
    int setResult = 0;
    uint8_t rdBits = -1;

    setResult = ioctl(fd, SPI_IOC_WR_BITS_PER_WORD, &BITS_PER_WORD);
    if (setResult < 0) {
        return setResult;
    }

    setResult = ioctl(fd, SPI_IOC_RD_BITS_PER_WORD, &rdBits);
    if (setResult < 0) {
        return setResult;
    }

    return 0;
}

/* Sets max. speed. */
static int setMaxSpeed() {
    int setResult = 0;
    uint8_t rdSpeed = -1;

    setResult = ioctl(fd, SPI_IOC_WR_MAX_SPEED_HZ, &SPI_MAX_SPEED);
    if (setResult < 0) {
        return setResult;
    }

    setResult = ioctl(fd, SPI_IOC_RD_MAX_SPEED_HZ, &rdSpeed);
    if (setResult < 0) {
        return setResult;
    }

    return 0;
}

/*
 * Sends data, stored in @c dataToSend buffer, to SPI device.
 * @param dataToSend data to send
 * @param len length (in bytes) of data to send
 * @return @c BASE_TYPES_OPER_OK, if operation performed successfully
 * @return @c BASE_TYPES_OPER_ERROR, if some error occurred during operation
 */
static int sendData(void* dataToSend, unsigned int len) {
    int result = 0;
    int trans_id = 0;
    uint8_t* tx = NULL;
    struct spi_ioc_transfer completeTransfer[2];

    tx = malloc(len * sizeof (uint8_t));
    memcpy(tx, dataToSend, len * sizeof (uint8_t));

    struct spi_ioc_transfer dataTransfer = {
        .tx_buf = (unsigned long) tx,
        .rx_buf = NULL,
        .len = 1,
        .delay_usecs = DELAY_AFTER_TRANSFER,
        .speed_hz = SPI_MAX_SPEED,
        .bits_per_word = BITS_PER_WORD,
        .cs_change = 1
    };

    completeTransfer[0] = nullTransfer;
    completeTransfer[1] = dataTransfer;

    for (trans_id = 0; trans_id < len; trans_id++) {
        completeTransfer[1].tx_buf = (unsigned long) &(tx[trans_id]);

        result = ioctl(fd, SPI_IOC_MESSAGE(2), &completeTransfer);
        if (result == -1) {
            errors_setError(&lastError, "rpi_spi_iqrf_sendData", errno, 1, 1);
            free(tx);
            return BASE_TYPES_OPER_ERROR;
        }

        // delay T3
        int sleepResult = nanosleep(&T2_DELAY, NULL);
        if (sleepResult == -1) {
            errors_setError(&lastError, "rpi_spi_iqrf_sendData", errno, 1, 1);
            free(tx);
            return BASE_TYPES_OPER_ERROR;
        }
    }

    free(tx);
    return BASE_TYPES_OPER_OK;
}

/*
 * Sends data, stored in @c dataToSend buffer, to SPI device and receives data
 * from SPI device to @c recvBuffer buffer. Maximal length of both buffers
 * must not exceed @c len.
 * @param dataToSend data to send
 * @param recvBuffer buffer to store received data
 * @param len length (in bytes) of buffers
 * @return @c BASE_TYPES_OPER_OK, if operation performs successfully
 * @return @c BASE_TYPES_OPER_ERROR, if some error occurred during operation
 */
static int sendAndReceive(void* dataToSend, void* recvBuffer, unsigned int len) {
    int result = 0;
    int trans_id = 0;
    uint8_t* tx = NULL;
    uint8_t* rx = NULL;
    struct spi_ioc_transfer completeTransfer[2];

    tx = malloc(len * sizeof (uint8_t));
    memcpy(tx, dataToSend, len * sizeof (uint8_t));

    rx = malloc(len * sizeof (uint8_t));
    memset(rx, 0, len * sizeof (uint8_t));

    struct spi_ioc_transfer dataTransfer = {
        .tx_buf = (unsigned long) tx,
        .rx_buf = (unsigned long) rx,
        .len = 1,
        .delay_usecs = DELAY_AFTER_TRANSFER,
        .speed_hz = SPI_MAX_SPEED,
        .bits_per_word = BITS_PER_WORD,
        .cs_change = 1
    };

    completeTransfer[0] = nullTransfer;
    completeTransfer[1] = dataTransfer;

    for (trans_id = 0; trans_id < len; trans_id++) {
        completeTransfer[1].tx_buf = (unsigned long) &(tx[trans_id]);
        completeTransfer[1].rx_buf = (unsigned long) &(rx[trans_id]);

        result = ioctl(fd, SPI_IOC_MESSAGE(2), &completeTransfer);
        if (result == -1) {
            errors_setError(&lastError, "rpi_spi_iqrf_sendAndReceive", errno, 1, 1);
            free(tx);
            free(rx);
            return BASE_TYPES_OPER_ERROR;
        }

        // delay T3
        int sleepResult = nanosleep(&T2_DELAY, NULL);
        if (sleepResult == -1) {
            errors_setError(&lastError, "rpi_spi_iqrf_sendAndReceive", errno, 1, 1);
            free(tx);
            free(rx);
            return BASE_TYPES_OPER_ERROR;
        }
    }

    free(tx);

    memcpy(recvBuffer, rx, len * sizeof (uint8_t));
    free(rx);

    return BASE_TYPES_OPER_OK;
}

/*
 * Returns 1, if the specified SPI status indicates
 * Data Ready. Otherwise returns 0.
 */
static int isSPIDataReady(uint8_t spiStatus) {
    if ((spiStatus >= 0x40) && (spiStatus < RPISPIIQRF_SPI_READY_COMM)) {
        return 1;
    }
    return 0;
}

/*
 * Sets PTYPE of message packet.
 */
static void setPTYPE(uint8_t* pType, rpiiqrf_SPICtype ctype, unsigned int dataLen) {
    *pType = dataLen;
    if (ctype == CTYPE_BUFFER_CHANGED) {
        *pType |= 128;
    }
}

static void bufferXor(uint8_t* crcm, uint8_t* buffer, unsigned int buffSize) {
    uint8_t dataId = 0;

    for (dataId = 0; dataId < buffSize; dataId++) {
        *crcm ^= buffer[dataId];
    }
}

/*
 * Calculates and returns CRCM.
 */
static uint8_t getCRCM(uint8_t ptype, uint8_t* data, unsigned int dataLen) {
    uint8_t crcm = 0;

    crcm ^= RPISPIIQRF_SPI_CMD;
    crcm ^= ptype;
    bufferXor(&crcm, data, dataLen);
    crcm ^= 0x5F;

    return crcm;
}

/*
 * Calculates and returns CRCS.
 */
static uint8_t getCRCS(uint8_t ptype, uint8_t* data, unsigned int dataLen) {
    uint8_t crcs = 0;

    crcs ^= ptype;
    bufferXor(&crcs, data, dataLen);
    crcs ^= 0x5F;

    return crcs;
}

/*
 * Verify CRCS. Returns 1, if OK. Otherwise returns 0.
 */
static int verifyCRCS(uint8_t ptype, uint8_t* recvData, unsigned int dataLen,
        uint8_t crcsToVerify
        ) {
    uint8_t countedCrcs = 0;

    countedCrcs = getCRCS(ptype, recvData, dataLen);
    if (crcsToVerify == countedCrcs) {
        return 1;
    }
    return 0;
}

/*
 * Returns 1, if the specified SPI status indicates valid value of
 * IQRF_SPIStatus_DataNotReady enum. Otherwise returns 0.
 */
static int isSPINoDataReady(uint8_t spiStatus) {
    switch (spiStatus) {
        case RPISPIIQRF_SPI_DISABLED:
        case RPISPIIQRF_SPI_SUSPENDED:
        case RPISPIIQRF_SPI_BUFF_PROTECT:
        case RPISPIIQRF_SPI_CRCM_ERR:
        case RPISPIIQRF_SPI_READY_COMM:
        case RPISPIIQRF_SPI_READY_PROG:
        case RPISPIIQRF_SPI_READY_DEBUG:
        case RPISPIIQRF_SPI_SLOW_MODE:
        case RPISPIIQRF_SPI_HW_ERROR:
            return 1;
        default:
            return 0;
    }
    return 0;
}

/*
 * Sends 10 zero bytes to the module.
 */

/*
static int send10ZeroBytes(void) {
    int sendRes = 0;
    uint8_t* zeroBytes = NULL;

    zeroBytes = malloc(10 * sizeof (uint8_t));
    memset(zeroBytes, 0, 10);

    sendRes = rpibase_sendData(zeroBytes, 10);
    free(zeroBytes);

    return sendRes;
}
 */

// checks specified value of length of data to write or read

static int checkDataLen(unsigned int dataLen) {
    if (dataLen <= 0) {
        errors_setError(
                &lastError, "Number of bytes must be greather then 0",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    if (dataLen > RPISPIIQRF_MAX_DATA_LENGTH) {
        errors_setError(
                &lastError, "Number of bytes exceeds upper limit",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    return BASE_TYPES_OPER_OK;
}

/*
 * Initializes SPI device to use.
 * @return @c BASE_TYPES_OPER_OK, if initialization performed successfully
 * @return @c BASE_TYPES_OPER_ERROR, if some error occurred during initialization
 */
int rpi_spi_iqrf_init(const char* dev) {
    uint8_t ioResult = 0;
    int initResult = 0;

    if (libIsInitialized == 1) {
        errors_setError(
                &lastError, "Library is already initialized", BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    // IO initialization
    ioResult = rpi_io_init();
    if (ioResult < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_init: IO initialization error",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return ioResult;
    }

    //enable CE0 for TR communication
    ioResult = rpi_io_set(RPIIO_PIN_CE0, RPIIO_DIR_OUTPUT);
    if (ioResult < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_init: CEO output setting error",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return ioResult;
    }

    ioResult = rpi_io_write(RPIIO_PIN_CE0, RPIIO_PINLEVEL_LOW);
    if (ioResult < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_init: CEO writing error",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return ioResult;
    }

    // enable PWR for TR communication
    ioResult = rpi_io_set(RPIIO_PIN_RESET, RPIIO_DIR_OUTPUT);
    if (ioResult < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_init: RST output setting error",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return ioResult;
    }

    ioResult = rpi_io_write(RPIIO_PIN_RESET, RPIIO_PINLEVEL_HIGH);
    if (ioResult < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_init: RST writing error",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return ioResult;
    }

    initNullTransfer();

    if (fd != NO_FILE_DESCRIPTOR) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_init: Invalid file descriptor",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    fd = open(dev, O_RDWR);
    if (fd < 0) {
        errors_setError(&lastError, "rpi_spi_iqrf_init", errno, 1, 1);
        return BASE_TYPES_OPER_ERROR;
    }

    // set SPI mode
    initResult = setMode();
    if (initResult < 0) {
        errors_setError(&lastError, "rpi_spi_iqrf_init", errno, 1, 1);
        return BASE_TYPES_OPER_ERROR;
    }

    // set bits per word
    initResult = setBitsPerWord();
    if (initResult < 0) {
        errors_setError(&lastError, "rpi_spi_iqrf_init", errno, 1, 1);
        return BASE_TYPES_OPER_ERROR;
    }

    // set max. speed
    initResult = setMaxSpeed();
    if (initResult < 0) {
        errors_setError(&lastError, "rpi_spi_iqrf_init", errno, 1, 1);
        return BASE_TYPES_OPER_ERROR;
    }

    libIsInitialized = 1;
    return BASE_TYPES_OPER_OK;
}

// initialization of default SPI device

int rpi_spi_iqrf_initDefault() {
    return rpi_spi_iqrf_init(RPISPIIQRF_DEFAULT_SPI_DEVICE);
}

/*
 * Returns SPI status of TR module.
 * @return @c BASE_TYPES_OPER_OK, if operation performs successfully
 * @return @c BASE_TYPES_OPER_ERROR, if some error occurred during operation
 * @return @c RPISPIIQRF_ERROR_BAD_STATUS, if status value is incorrect
 */
int rpi_spi_iqrf_getSPIStatus(rpi_spi_iqrf_SPIStatus* spiStatus) {
    uint8_t spiCheck = RPISPIIQRF_SPI_CHECK;
    uint8_t spiResultStat = 0;
    int checkResult = 0;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    if (spiStatus == NULL) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_getSPIStatus: Argument spiStatus cannot be NULL",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    if (fd < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_getSPIStatus: Invalid file descriptor",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    checkResult = sendAndReceive((void*) &spiCheck, (void*) &spiResultStat, 1);
    if (checkResult == BASE_TYPES_OPER_ERROR) {
        return BASE_TYPES_OPER_ERROR;
    }

    // if checking is OK
    if (isSPIDataReady(spiResultStat)) {
        spiStatus->isDataReady = 1;
        spiStatus->dataReady = spiResultStat;
        return BASE_TYPES_OPER_OK;
    }

    // check, if the return value of SPI status is correct
    if (isSPINoDataReady(spiResultStat)) {
        spiStatus->isDataReady = 0;
        spiStatus->dataNotReadyStatus = spiResultStat;
        return BASE_TYPES_OPER_OK;
    } else {
        errors_setError(
                &lastError, "rpi_spi_iqrf_getSPIStatus: Unknown status",
                RPISPIIQRF_ERROR_BAD_STATUS, 0, 1
                );
        return RPISPIIQRF_ERROR_BAD_STATUS;
    }
}

/*
 * Writes specified data to the module.
 * @param dataToWrite data to write
 * @param dataLen length (in bytes) of data to write
 * @return @c BASE_TYPES_OPER_OK, if operation performs successfully
 * @return @c BASE_TYPES_OPER_ERROR, if some error occurred during operation
 */
int rpi_spi_iqrf_write(void* dataToWrite, unsigned int dataLen) {
    uint8_t* dataToSend = NULL;
    uint8_t ptype = 0;
    uint8_t crcm = 0;
    uint8_t sendResult = 0;
    int dataLenCheckRes = BASE_TYPES_OPER_ERROR;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    if (fd < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_writeSPI: Invalid file descriptor",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    // checking input parameters
    if (dataToWrite == NULL) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_writeSPI: Data to write is NULL",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    dataLenCheckRes = checkDataLen(dataLen);
    if (dataLenCheckRes == BASE_TYPES_OPER_ERROR) {
        return BASE_TYPES_OPER_ERROR;
    }

    dataToSend = malloc((dataLen + 3) * sizeof (uint8_t));

    // set command indication
    dataToSend[0] = RPISPIIQRF_SPI_CMD;

    // set PTYPE
    setPTYPE(&ptype, CTYPE_BUFFER_CHANGED, dataLen);
    dataToSend[1] = ptype;

    // copy data
    memcpy(dataToSend + 2, dataToWrite, dataLen);

    // set crcm
    crcm = getCRCM(ptype, dataToWrite, dataLen);
    dataToSend[dataLen + 2] = crcm;

    // send data to module
    sendResult = sendData(dataToSend, dataLen + 3);
    free(dataToSend);
    if (sendResult == BASE_TYPES_OPER_ERROR) {
        return BASE_TYPES_OPER_ERROR;
    }

    return BASE_TYPES_OPER_OK;
}

/*
 * Read specified number of bytes from the module.
 * @param readBuffer buffer to store data read from module
 * @param dataLen length (in bytes) of data to read
 * @return @c BASE_TYPES_OPER_OK, if operation performs successfully
 * @return @c BASE_TYPES_OPER_ERROR, if some error occurred during operation
 * @return @c RPISPIIQRF_ERROR_CRCS, if CRSC of returned data doesn't match
 */
int rpi_spi_iqrf_read(void* readBuffer, unsigned int dataLen) {
    uint8_t* dummyData = NULL;
    uint8_t* receiveBuffer = NULL;
    uint8_t ptype = 0;
    uint8_t crcm = 0;
    uint8_t sendResult = 0;
    int dataLenCheckRes = BASE_TYPES_OPER_ERROR;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    if (fd < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_readSPI: Invalid file descriptor",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    // checking input parameters
    if (readBuffer == NULL) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_readSPI: Read buffer is NULL",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    dataLenCheckRes = checkDataLen(dataLen);
    if (dataLenCheckRes == BASE_TYPES_OPER_ERROR) {
        return BASE_TYPES_OPER_ERROR;
    }

    dummyData = malloc((dataLen + 3) * sizeof (uint8_t));
    receiveBuffer = malloc((dataLen + 3) * sizeof (uint8_t));

    // set command indication
    dummyData[0] = RPISPIIQRF_SPI_CMD;

    // set PTYPE
    setPTYPE(&ptype, CTYPE_BUFFER_UNCHANGED, dataLen);
    dummyData[1] = ptype;

    // dummy values
    memset(dummyData + 2, 0, dataLen);

    // set crcm
    crcm = getCRCM(ptype, dummyData + 2, dataLen);
    dummyData[dataLen + 2] = crcm;

    // send data to module
    sendResult = sendAndReceive(dummyData, receiveBuffer, dataLen + 3);
    free(dummyData);
    if (sendResult == BASE_TYPES_OPER_ERROR) {
        free(receiveBuffer);
        return BASE_TYPES_OPER_ERROR;
    }

    //printf("Data: %s \n", receiveBuffer);

    // verify CRCS
    if (!verifyCRCS(ptype, receiveBuffer + 2, dataLen, receiveBuffer[dataLen + 2])) {
        free(receiveBuffer);
        errors_setError(
                &lastError, "rpi_spi_iqrf_readSPI: CRSC verification failed",
                RPISPIIQRF_ERROR_CRCS, 0, 1
                );
        return RPISPIIQRF_ERROR_CRCS;
    }

    // copy received data into user buffer
    memcpy(readBuffer, receiveBuffer + 2, dataLen);
    free(receiveBuffer);

    return BASE_TYPES_OPER_OK;
}

/*
 * Terminates the library and frees up used resources.
 * @return @c BASE_TYPES_OPER_OK, if destroy performed successfully
 * @return @c SPI_OPER_NOINIT, if the library was not initialized
 * @return @c BASE_TYPES_OPER_ERROR, if some error occurred during initialization
 */
int rpi_spi_iqrf_destroy(void) {
    int ioDestroyRes = BASE_TYPES_OPER_ERROR;
    int closeRes = BASE_TYPES_OPER_ERROR;

    if (libIsInitialized == 0) {
        errors_setError(
                &lastError, "Library is not initialized", BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    // after calling this method, the behaviour of the library will be
    // like if the library was not initialized
    libIsInitialized = 0;

    // destroy used rpi_io library
    ioDestroyRes = rpi_io_destroy();
    if (ioDestroyRes < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_destroy: Error during destroying used IO library",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    if (fd == NO_FILE_DESCRIPTOR) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_destroy: Library is not currently initialized",
                BASE_TYPES_LIB_NOT_INITIALIZED, 0, 1
                );
        return BASE_TYPES_LIB_NOT_INITIALIZED;
    }

    if (fd < 0) {
        errors_setError(
                &lastError, "rpi_spi_iqrf_destroy: Invalid file descriptor",
                BASE_TYPES_OPER_ERROR, 0, 1
                );
        return BASE_TYPES_OPER_ERROR;
    }

    closeRes = close(fd);
    fd = NO_FILE_DESCRIPTOR;

    if (closeRes == -1) {
        errors_setError(&lastError, "rpi_spi_iqrf_destroy", errno, 1, 1);
        return BASE_TYPES_OPER_ERROR;
    }

    return BASE_TYPES_OPER_OK;
}

/*
 * Returns information about last error or @c NULL, if no error yet occurred.
 * @return information about last error
 * @return @c NULL if no error yet occurred
 */
errors_OperError* rpi_spi_iqrf_getLastError(void) {
    return errors_getErrorCopy(lastError);
}
