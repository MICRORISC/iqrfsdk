/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_microrisc_cdc_J_CDCImpl */

#ifndef _Included_com_microrisc_cdc_J_CDCImpl
#define _Included_com_microrisc_cdc_J_CDCImpl
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    createCDCImpl
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_microrisc_cdc_J_1CDCImpl_createCDCImpl
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    destroyCDCImpl
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_destroyCDCImpl
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_test
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1test
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_resetUSBDevice
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1resetUSBDevice
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_resetTRModule
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1resetTRModule
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_getUSBDeviceInfo
 * Signature: (J)Lcom/microrisc/cdc/J_DeviceInfo;
 */
JNIEXPORT jobject JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getUSBDeviceInfo
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_getTRModuleInfo
 * Signature: (J)Lcom/microrisc/cdc/J_ModuleInfo;
 */
JNIEXPORT jobject JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getTRModuleInfo
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_indicateConnectivity
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1indicateConnectivity
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_getStatus
 * Signature: (J)Lcom/microrisc/cdc/J_SPIStatus;
 */
JNIEXPORT jobject JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getStatus
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_sendData
 * Signature: (J[S)I
 */
JNIEXPORT jint JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1sendData
  (JNIEnv *, jobject, jlong, jshortArray);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_switchToCustomlong
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1switchToCustomlong
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_registerAsyncListener
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1registerAsyncListener
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_unregisterAsyncListener
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1unregisterAsyncListener
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_isReadingStopped
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1isReceptionStopped
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_microrisc_cdc_J_CDCImpl
 * Method:    stub_getLastReceptionError
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getLastReceptionError
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif
