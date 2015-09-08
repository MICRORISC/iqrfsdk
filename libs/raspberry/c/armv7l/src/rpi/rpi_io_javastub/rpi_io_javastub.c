#include <stdint.h>
#include <stdlib.h>
#include <string.h>

#include "com_microrisc_rpi_io_SimpleIO.h"
#include <rpi/rpi_io.h>

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
 * Throws IO Exception.
 * Copied from: The Java Native Interface. Programmer's Guide and Specification.
 * @param ioErr error object, which describes the IO error
 */
static void JNU_ThrowIOException(JNIEnv* env, errors_OperError* ioErr) {
    jclass cls = (*env)->FindClass(env, "com/microrisc/rpi/io/IOException");
     // if cls is NULL, an exception has already been thrown
     if (cls != NULL) {
         if (ioErr != NULL) {
            (*env)->ThrowNew(env, cls, ioErr->descr);
         } else {
            (*env)->ThrowNew(env, cls, "Exact cause was not found");
         }
     }
     // free the local ref
     (*env)->DeleteLocalRef(env, cls);
}


/*
 * Initialization.
 */
JNIEXPORT void JNICALL Java_com_microrisc_rpi_io_SimpleIO_stub_1init
  (JNIEnv* env, jobject obj) {
    int operResult = rpi_io_init();
    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* err = rpi_io_getLastError();
        JNU_ThrowIOException(env, err);
    }
}

/*
 * Pin setting.
 */
JNIEXPORT void JNICALL Java_com_microrisc_rpi_io_SimpleIO_stub_1set
  (JNIEnv* env, jobject jObj, jint pin, jint direction) {
    int operResult = rpi_io_set(pin, direction);
    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* err = rpi_io_getLastError();
        JNU_ThrowIOException(env, err);
    }
}

/*
 * Write data to pin.
 */
JNIEXPORT void JNICALL Java_com_microrisc_rpi_io_SimpleIO_stub_1write
  (JNIEnv* env, jobject jObj, jint pin, jint value) {
    int operResult = rpi_io_write(pin, value);
    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* err = rpi_io_getLastError();
        JNU_ThrowIOException(env, err);
    }
}

/*
 * Read from pin.
 */
JNIEXPORT jint JNICALL Java_com_microrisc_rpi_io_SimpleIO_stub_1read
  (JNIEnv* env, jobject jObj, jint pin) {
    int operResult = rpi_io_read(pin);
    if ( operResult < 0 ) {
        errors_OperError* err = rpi_io_getLastError();
        JNU_ThrowIOException(env, err);
    }
    return operResult;
}

/*
 * Reset of TR module.
 */
JNIEXPORT void JNICALL Java_com_microrisc_rpi_io_SimpleIO_stub_1resetTr
  (JNIEnv* env, jobject jObj) {
    int operResult = rpi_io_resetTr();
    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* err = rpi_io_getLastError();
        JNU_ThrowIOException(env, err);
    }
}

/*
 * Destroy.
 */
JNIEXPORT void JNICALL Java_com_microrisc_rpi_io_SimpleIO_stub_1destroy
  (JNIEnv* env, jobject jObj) {
    int operResult = rpi_io_destroy();
    if ( operResult != BASE_TYPES_OPER_OK ) {
        errors_OperError* err = rpi_io_getLastError();
        JNU_ThrowIOException(env, err);
    }
}
