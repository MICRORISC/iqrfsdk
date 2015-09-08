#include <stdint.h>
#include <stdlib.h>
#include <string.h>

#include "com_microrisc_rpi_spi_iqrf_SimpleSPI_Master.h"
#include <rpi/rpi_spi_iqrf.h>

/*
 * Throws exception specified by its name.
 * Copied from: The Java Native Interface. Programmer's Guide and Specification.
 * @param name class name of the exception to be thrown
 * @param msg message string of the exception
 */
static void JNU_ThrowByName(JNIEnv* env, const char* name, const char* msg) {
     jclass cls = (*env)->FindClass(env, name);
     // if cls is NULL, an exception has already been thrown
     if (cls != NULL) {
         (*env)->ThrowNew(env, cls, msg);
     }
     // free the local ref
     (*env)->DeleteLocalRef(env, cls);
 }

/*
 * Throws SPI Exception according to specified SPI error.
 * Copied from: The Java Native Interface. Programmer's Guide and Specification.
 * @param spiErr error object, which describes the SPI error
 */
static void JNU_ThrowSpiException(JNIEnv* env, errors_OperError* spiErr) {
    jclass cls = (*env)->FindClass(env, "com/microrisc/rpi/spi/SPI_Exception");
     // if cls is NULL, an exception has already been thrown
     if (cls != NULL) {
         if (spiErr != NULL) {
            (*env)->ThrowNew(env, cls, spiErr->descr);
         } else {
            (*env)->ThrowNew(env, cls, "Exact cause was not found");
         }
     }
     // free the local ref
     (*env)->DeleteLocalRef(env, cls);
}

/*
 * SPI library initialization.
 * @param masterId ID of SPI master
 */
JNIEXPORT void JNICALL Java_com_microrisc_rpi_spi_iqrf_SimpleSPI_1Master_stub_1initialize
  (JNIEnv* env, jobject jObj, jstring masterId) {
    const char* masterIdUTF = (*env)->GetStringUTFChars(env, masterId, NULL);

    if ( masterIdUTF == NULL ) {
        (*env)->ReleaseStringUTFChars(env, masterId, masterIdUTF);
        jthrowable jException = (*env)->ExceptionOccurred(env);
        if ( jException == NULL ) {
            JNU_ThrowByName(env, "java/lang/IllegalStateException", "GetStringUTFChars failed");
        }
        return;
    }

    // calling initialization function
    int operResult = rpi_spi_iqrf_init(masterIdUTF);
    (*env)->ReleaseStringUTFChars(env, masterId, masterIdUTF);

    // if an error has occurred, throw exception
    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* spiErr = rpi_spi_iqrf_getLastError();
        JNU_ThrowSpiException(env, spiErr);
    }
}

/*
 * Gets SPI slave status.
 */
JNIEXPORT jobject JNICALL Java_com_microrisc_rpi_spi_iqrf_SimpleSPI_1Master_stub_1getSlaveStatus
  (JNIEnv* env, jobject jObj) {
    rpi_spi_iqrf_SPIStatus spiStatus = {
        .isDataReady = 0, .dataNotReadyStatus = RPISPIIQRF_SPI_DISABLED
    };

    int operResult = rpi_spi_iqrf_getSPIStatus(&spiStatus);
    if ( operResult == BASE_TYPES_OPER_OK ) {
        jclass jStatClass = (*env)->FindClass(env, "com/microrisc/rpi/spi/iqrf/SPI_Status");
        if ( jStatClass == NULL ) {
            JNU_ThrowByName(env, "com/microrisc/rpi/spi/SPI_Exception",
                "Cannot find com/microrisc/rpi/spi/iqrf/SPI_Status class"
            );
            return NULL;
        }

        jmethodID jStatConstructor = (*env)->GetMethodID(env, jStatClass, "<init>", "(IZ)V");
        if ( jStatConstructor == NULL ) {
            JNU_ThrowByName(env, "com/microrisc/rpi/spi/SPI_Exception",
                "Cannot find the (IZ)V constructor for com/microrisc/rpi/spi/iqrf/SPI_Status class"
            );
            return NULL;
        }

        jboolean jDataReady = ( spiStatus.isDataReady >= 1 )? JNI_TRUE : JNI_FALSE;
        jint jStatValue = ( spiStatus.isDataReady >= 1 )? spiStatus.dataReady : spiStatus.dataNotReadyStatus;
        jobject jSpiStatus = (*env)->NewObject(
            env, jStatClass, jStatConstructor,jStatValue, jDataReady
        );
        return jSpiStatus;
    }

    errors_OperError* spiErr = rpi_spi_iqrf_getLastError();
    JNU_ThrowSpiException(env, spiErr);
    return NULL;
}


/*
 * Writes data to SPI slave.
 * @param dataToSend data to be sent
 */
JNIEXPORT void JNICALL Java_com_microrisc_rpi_spi_iqrf_SimpleSPI_1Master_stub_1sendData
  (JNIEnv* env, jobject jObj, jshortArray dataToSend) {
    jsize dataLen = (*env)->GetArrayLength(env, dataToSend);
    if ( dataLen > RPISPIIQRF_MAX_DATA_LENGTH ) {
        JNU_ThrowByName(env, "java/lang/IllegalArgumentException",
            "Length of data to send cannot be greather then RPISPIIQRF_MAX_DATA_LENGTH"
        );
        return;
    }

    // getting pointer to data to be sent
    jshort* jArr = (*env)->GetShortArrayElements(env, dataToSend, NULL);
    if ( jArr == NULL ) {
        JNU_ThrowByName(env, "java/lang/IllegalStateException",
            "GetShortArrayElements failed"
        );
        return;
    }

    // auxiliary buffer of data to be sent
    unsigned char* dataToWrite = malloc(dataLen * sizeof(unsigned char));
    if ( dataToWrite == NULL ) {
        JNU_ThrowByName(env, "java/lang/OutOfMemoryError",
            "Cannot allocate buffer for data to be sent"
        );
        return;
    }

    // copy the data into auxiliary buffer
    int i = 0;
    for (i = 0; i < dataLen; i++) {
        dataToWrite[i] = jArr[i] & 0xFF;
    }

    (*env)->ReleaseShortArrayElements(env, dataToSend, jArr, 0);

    // send data to SPI slave
    int operResult = rpi_spi_iqrf_write(dataToWrite, dataLen);
    free(dataToWrite);

    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* spiErr = rpi_spi_iqrf_getLastError();
        JNU_ThrowSpiException(env, spiErr);
    }
}


/*
 * Reads data from SPI slave.
 * @param dataLen length of data to read
 */
JNIEXPORT jshortArray JNICALL Java_com_microrisc_rpi_spi_iqrf_SimpleSPI_1Master_stub_1readData
  (JNIEnv* env, jobject jObj, jint dataLen) {
    if ( dataLen > RPISPIIQRF_MAX_DATA_LENGTH ) {
        JNU_ThrowByName(env, "java/lang/IllegalArgumentException",
            "Length of data to read cannot be greather then RPISPIIQRF_MAX_DATA_LENGTH"
        );
        return NULL;
    }

    // creating read buffer
    unsigned char* readBuffer = (unsigned char*)malloc(dataLen * sizeof(short));
    if ( readBuffer == NULL ) {
        JNU_ThrowByName(env, "java/lang/OutOfMemoryError", "Cannot allocate read buffer");
        return NULL;
    }

    // initializing read buffer to '0'
    memset(readBuffer, 0, dataLen);

    // reading data from SPI slave into buffer
    int operResult = rpi_spi_iqrf_read(readBuffer, dataLen);

    // checking, if some error occurred
    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* spiErr = rpi_spi_iqrf_getLastError();
        free(readBuffer);
        JNU_ThrowSpiException(env, spiErr);
        return NULL;
    }

    // creating structure to return
    jshortArray readData = (*env)->NewShortArray(env, dataLen);
    if ( readData == NULL ) {
        JNU_ThrowByName(env, "java/lang/IllegalStateException", "NewShortArray failed");
        return NULL;
    }

    // auxiliary buffer - for use in SetShortArrayRegion function
    jshort* jBuffer = malloc(dataLen * sizeof(jshort));
    if ( readBuffer == NULL ) {
        JNU_ThrowByName(env, "java/lang/OutOfMemoryError", "Cannot allocate read buffer");
        return NULL;
    }

    // initialization of auxiliary buffer to read data
    int i = 0;
    for (i = 0; i < dataLen; i++) {
        jBuffer[i] = readBuffer[i];
    }

    free(readBuffer);

    // setting structure to return
    (*env)->SetShortArrayRegion(env, readData, 0, dataLen, jBuffer);

    free(jBuffer);

    if ((*env)->ExceptionCheck(env)) {
        JNU_ThrowByName(env, "java/lang/IllegalStateException", "SetShortArrayRegion failed");
        return NULL;
    }

    return readData;
}

/*
 * SPI library deinitialization and resources freeing.
 */
JNIEXPORT void JNICALL Java_com_microrisc_rpi_spi_iqrf_SimpleSPI_1Master_stub_1destroy
  (JNIEnv* env, jobject jObj) {
    int operResult = rpi_spi_iqrf_destroy();

    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* spiErr = rpi_spi_iqrf_getLastError();
        JNU_ThrowSpiException(env, spiErr);
    }
}





