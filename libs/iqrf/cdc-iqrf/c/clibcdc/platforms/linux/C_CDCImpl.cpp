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

#include <string.h>
#include <cdc/CDCImpl.h>
#include <cdc/C_CdcInterface.h>

// pointer to library object
static CDCImpl* cdcLib = NULL;

/*
 * From devInfo parameter makes C-version of DeviceInfo structure and
 * returns it in cDevInfo parameter.
 */
static void deviceInfoToC(DeviceInfo* devInfo, C_DeviceInfo* cDevInfo) {
    cDevInfo->typeLen = devInfo->typeLen;
    cDevInfo->type = new char[cDevInfo->typeLen + 1];
    strcpy(cDevInfo->type, devInfo->type);
    cDevInfo->type[cDevInfo->typeLen] = '\0';

    cDevInfo->fvLen = devInfo->fvLen;
    cDevInfo->firmwareVersion = new char[cDevInfo->fvLen + 1];
    strcpy(cDevInfo->firmwareVersion, devInfo->firmwareVersion);
    cDevInfo->firmwareVersion[cDevInfo->fvLen] = '\0';

    cDevInfo->snLen = devInfo->snLen;
    cDevInfo->serialNumber = new char[cDevInfo->snLen + 1];
    strcpy(cDevInfo->serialNumber, devInfo->serialNumber);
    cDevInfo->serialNumber[cDevInfo->snLen] = '\0';
}

/*
 * From modInfo parameter makes C-version of ModuleInfo structure and
 * returns it in cModInfo parameter.
 */
static void moduleInfoToC(ModuleInfo* modInfo, C_ModuleInfo* cModInfo) {
    memcpy(cModInfo->serialNumber, modInfo->serialNumber, MI_SN_SIZE);
    cModInfo->osVersion = modInfo->osVersion;
    cModInfo->PICType = modInfo->PICType;
    memcpy(cModInfo->osBuild, modInfo->osBuild, MI_BUILD_SIZE);
}

/*
 * From spiStatus parameter makes C-version of SPIStatus structure and
 * returns it in cSpiStatus parameter.
 */
static void spiStatusToC(SPIStatus* spiStatus, C_SPIStatus* cSpiStatus) {
    cSpiStatus->isDataReady = spiStatus->isDataReady;
    cSpiStatus->DATA_READY = spiStatus->DATA_READY;
    cSpiStatus->SPI_MODE = (C_SPIModes) spiStatus->SPI_MODE;
}

// last error
static Error lastError;

/*
 * Sets specified error description to specified error.
 */
static void setDescr(Error* err, const char* newDescr) {
    // avoid memory leaking
    if (err == &lastError) {
        char* oldDescr = (char*) err->descr;
        delete oldDescr;
    }

    if (newDescr == NULL) {
        err->descr = NULL;
        return;
    }

    int newDescrLen = strlen(newDescr);
    err->descr = new char[newDescrLen + 1];
    strncpy(err->descr, newDescr, newDescrLen);
    err->descr[newDescrLen] = '\0';
}

/* Sets the description of last error. */
int getLastError(Error* error) {
    if (error == NULL) {
        return OPER_NOINIT;
    }

    setDescr(error, lastError.descr);
    return OPER_OK;
}

/*
 * Inits the CDC-library.
 */
int init(const char* portName) {
    if (cdcLib != NULL) {
        return OPER_NOINIT;
    }

    try {
        if (portName == NULL) {
            cdcLib = new CDCImpl();
        } else {
            cdcLib = new CDCImpl(portName);
        }
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    // no error - NULL
    setDescr(&lastError, NULL);

    return OPER_OK;
}

/* Terminates the CDC library run and cleans up used resources. */
void destroy(void) {
    if (cdcLib == NULL) {
        return;
    }

    delete cdcLib;
    cdcLib = NULL;
}

/* Performs communication test. */
int test(void) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    bool testResult = false;
    try {
        testResult = cdcLib->test();
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    if (testResult) {
        return 1;
    }

    return 0;
}

/* Resets USB device. */
int resetUSBDevice(void) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    try {
        cdcLib->resetUSBDevice();
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    return OPER_OK;
}

/* Resets TR module. */
int resetTRModule(void) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    try {
        cdcLib->resetTRModule();
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    return OPER_OK;
}

int getUSBDeviceInfo(C_DeviceInfo* cDevInfo) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    if (cDevInfo == NULL) {
        return OPER_NOINIT;
    }

    DeviceInfo* devInfo = NULL;
    try {
        devInfo = cdcLib->getUSBDeviceInfo();
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    deviceInfoToC(devInfo, cDevInfo);
    delete devInfo;

    return OPER_OK;
}

int getTRModuleInfo(C_ModuleInfo* cModInfo) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    if (cModInfo == NULL) {
        return OPER_NOINIT;
    }

    ModuleInfo* modInfo = NULL;
    try {
        modInfo = cdcLib->getTRModuleInfo();
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    moduleInfoToC(modInfo, cModInfo);
    delete modInfo;

    return OPER_OK;
}

int indicateConnectivity(void) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    try {
        cdcLib->indicateConnectivity();
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    return OPER_OK;
}

int getStatus(C_SPIStatus* cSpiStatus) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    if (cSpiStatus == NULL) {
        return OPER_NOINIT;
    }

    SPIStatus spiStatus;
    try {
        spiStatus = cdcLib->getStatus();
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    spiStatusToC(&spiStatus, cSpiStatus);

    return OPER_OK;
}

int sendData(unsigned char* data, unsigned int dlen, C_DSResponse* cDSResp) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    if (cDSResp == NULL) {
        return OPER_NOINIT;
    }

    DSResponse dsResp;
    try {
        dsResp = cdcLib->sendData(data, dlen);
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    *cDSResp = (C_DSResponse) dsResp;

    return OPER_OK;
}

int switchToCustom(void) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    try {
        cdcLib->switchToCustom();
    } catch (CDCImplException& e) {
        setDescr(&lastError, e.getDescr());
        return OPER_ERROR;
    }

    return OPER_OK;
}

int registerAsyncMsgListener(C_AsyncMsgListener asyncListener) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    cdcLib->registerAsyncMsgListener(asyncListener);
    return OPER_OK;
}

int unregisterAsyncMsgListener(void) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    cdcLib->unregisterAsyncMsgListener();
    return OPER_OK;
}

int isReceptionStopped() {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    if (cdcLib->isReceptionStopped()) {
        return 1;
    }

    return 0;
}

int getLastReceptionError(char** lastReceptionError) {
    if (cdcLib == NULL) {
        return OPER_NOINIT;
    }

    const char* errorCause = cdcLib->getLastReceptionError();
    if (errorCause == NULL) {
        (*lastReceptionError) = NULL;
        return OPER_OK;
    }

    (*lastReceptionError) = new char[strlen(errorCause) + 1];
    strcpy((*lastReceptionError), errorCause);

    return OPER_OK;
}
