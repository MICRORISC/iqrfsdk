#include "com_microrisc_CDC_J_CDCImpl.h"
#include <cdc/CDCImpl.h>
#include <fstream>

#include <iostream>
#ifdef _DEBUG
#define DEBUG_TRC(msg) std::cout << std::endl << "{CPPDBG} " << __FUNCTION__ << ":  " << msg << std::endl;
#define PAR(par)                #par "=\"" << par << "\" "
#else
#define DEBUG_TRC(msg)
#define PAR(par)
#endif

/** 
 * Pointer to JavaVM instance. It will be used mainly in asynchronous
 * messaging.
 */
static JavaVM* jvm = NULL;

/**
 * Global reference to J_CDCImpl object.
 */
static jobject jCDC = NULL;

/**
* Global references to cdc classes.
*/
static jobject cLoader(NULL);
static jclass classJavaLangException(NULL);
static jclass classCDCImplException(NULL);
static jclass classCDCSendException(NULL);
static jclass classCDCReceiveException(NULL);
static jclass classDeviceInfo(NULL);
static jclass classModuleInfo(NULL);
static jclass classSPIStatus(NULL);
static jclass classCDCImpl(NULL);
static jclass classAsyncMsgListener(NULL);

static jfieldID jListID(NULL);
static jmethodID getMsgID(NULL);
static jmethodID devConstructor(NULL);
static jmethodID modConstructor(NULL);
static jmethodID statConstructor(NULL);

/**
* Function to get a class global ref from classLoaderGlobal reference to cdc classes.
*/
jclass MyFindClass(JNIEnv* env, jobject cdcLoader, jmethodID method_loadClass, const char* className)
{
  jstring strClassName = env->NewStringUTF(className);
  DEBUG_TRC("Calling load: " << className);
  jclass tmp_clazz = (jclass)env->CallObjectMethod(cdcLoader, method_loadClass, strClassName);
  jclass clazz = (jclass)(env->NewGlobalRef(tmp_clazz));
  DEBUG_TRC("Loaded: " << className << ": " << PAR(clazz));
  return clazz;
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_init
(JNIEnv *env, jclass mc, jobject cld)
{
  cLoader = (jobject)(env->NewGlobalRef(cld));
  DEBUG_TRC(PAR(cLoader));

  // Get a class of the ClassLoader object
  jclass cLoaderClass = env->GetObjectClass(cLoader);
  if (NULL == cLoaderClass)
    return;
  DEBUG_TRC(PAR(cLoaderClass));

  // Get the method LoadClass
  jmethodID mloadClass = env->GetMethodID(cLoaderClass,
    "loadClass", "(Ljava/lang/String;Z)Ljava/lang/Class;");
  if (NULL == mloadClass)
    return;
  DEBUG_TRC(PAR(mloadClass));

  // Now we have everything to cache all necessary classes, methods and fields for the rest of cdc lifetime
  if (NULL == (classJavaLangException = MyFindClass(env, cLoader, mloadClass, "java.lang.Exception")))
    return;
  if (NULL == (classCDCImplException = MyFindClass(env, cLoader, mloadClass, "com.microrisc.cdc.J_CDCImplException")))
    return;
  if (NULL == (classCDCSendException = MyFindClass(env, cLoader, mloadClass, "com.microrisc.cdc.J_CDCSendException")))
    return;
  if (NULL == (classCDCReceiveException = MyFindClass(env, cLoader, mloadClass, "com.microrisc.cdc.J_CDCReceiveException")))
    return;
  if (NULL == (classDeviceInfo = MyFindClass(env, cLoader, mloadClass, "com.microrisc.cdc.J_DeviceInfo")))
    return;
  if (NULL == (classModuleInfo = MyFindClass(env, cLoader, mloadClass, "com.microrisc.cdc.J_ModuleInfo")))
    return;
  if (NULL == (classSPIStatus = MyFindClass(env, cLoader, mloadClass, "com.microrisc.cdc.J_SPIStatus")))
    return;
  if (NULL == (classCDCImpl = MyFindClass(env, cLoader, mloadClass, "com.microrisc.cdc.J_CDCImpl")))
    return;
  if (NULL == (classAsyncMsgListener = MyFindClass(env, cLoader, mloadClass, "com.microrisc.cdc.J_AsyncMsgListener")))
    return;

  if (NULL == (jListID = env->GetFieldID(classCDCImpl, "msgListener", "Lcom/microrisc/cdc/J_AsyncMsgListener;")))
    return;
  DEBUG_TRC(PAR(jListID));

  if (NULL == (getMsgID = env->GetMethodID(classAsyncMsgListener, "onGetMessage", "([S)V")))
    return;
  DEBUG_TRC(PAR(getMsgID));

  if (NULL == (devConstructor = env->GetMethodID(classDeviceInfo, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")))
    return;
  DEBUG_TRC(PAR(devConstructor));

  if (NULL == (modConstructor = env->GetMethodID(classModuleInfo, "<init>", "([SSS[S)V")))
    return;
  DEBUG_TRC(PAR(modConstructor));

  if (NULL == (statConstructor = env->GetMethodID(classSPIStatus, "<init>", "(ZI)V")))
    return;
  DEBUG_TRC(PAR(statConstructor));

}

JNIEXPORT jlong JNICALL Java_com_microrisc_cdc_J_1CDCImpl_createCDCImpl
(JNIEnv* env, jobject jObj, jstring portName) {

  CDCImpl* cdcImp = NULL;

	try {
		jsize nameLength = env->GetStringLength(portName);
		if (nameLength == 0) {
			cdcImp = new CDCImpl();
		} else {
			const char* portNameUTF = env->GetStringUTFChars(portName, NULL);
			if (portNameUTF == NULL) {
        env->ThrowNew(classJavaLangException, "Port Name conversion to UTF failed");
				return 0;
			}
			cdcImp = new CDCImpl(portNameUTF);
			env->ReleaseStringUTFChars(portName, portNameUTF);
		}
	} catch (CDCImplException& e) {
    env->ThrowNew(classCDCImplException, e.what());
		return 0;
	}
	
	// getting pointer to javaVM api
	if (env->GetJavaVM(&jvm) < 0) {
		return 0;
	}
	
	jCDC = env->NewGlobalRef(jObj);
	if (jCDC == NULL) {
		return 0;
	}

  DEBUG_TRC(PAR(cdcImp));
  return (jlong)cdcImp;
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_destroyCDCImpl
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	delete cdcImp;
}

JNIEXPORT jboolean JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1test
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	bool testResult = false;
	try {
		testResult = cdcImp->test();
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	}
	
	return testResult;
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1resetUSBDevice
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	try {
		cdcImp->resetUSBDevice();
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	}
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1resetTRModule
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	try {
		cdcImp->resetTRModule();
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	}
}

JNIEXPORT jobject JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getUSBDeviceInfo
(JNIEnv* env, jobject oObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	DeviceInfo* devInfo = NULL;
	try {
		devInfo = cdcImp->getUSBDeviceInfo();
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	}

	jstring devType = env->NewStringUTF(devInfo->type);
	if (devType == NULL) {
		return NULL;
	}

	jstring firmwareVersion = env->NewStringUTF(devInfo->firmwareVersion);
	if (firmwareVersion == NULL) {
		return NULL;
	}

	jstring serialNumber = env->NewStringUTF(devInfo->serialNumber);
	if (serialNumber == NULL) {
		return NULL;
	}

  jobject jDevInfo = env->NewObject(classDeviceInfo, devConstructor, devType,
		firmwareVersion, serialNumber);

	return jDevInfo;
}

JNIEXPORT jobject JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getTRModuleInfo
(JNIEnv* env, jobject jOjb, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	ModuleInfo* modInfo = NULL;
	try {
		modInfo = cdcImp->getTRModuleInfo();
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	} 
	
	jshortArray jSerNumberArr = env->NewShortArray(ModuleInfo::SN_SIZE);
	if (jSerNumberArr == NULL) {
		return NULL;
	}

	jshort* jBuffer = new jshort[ModuleInfo::SN_SIZE];
	for (int i = 0; i < ModuleInfo::SN_SIZE; i++) {
		jBuffer[i] = modInfo->serialNumber[i];
	}
	env->SetShortArrayRegion(jSerNumberArr, 0, ModuleInfo::SN_SIZE, jBuffer);
	if (env->ExceptionCheck()) {
		return NULL;
	}

	jshort jOsVersion = modInfo->osVersion;
	jshort jPICType = modInfo->PICType;

	delete jBuffer;

	jshortArray jOsBuildArr = env->NewShortArray(ModuleInfo::BUILD_SIZE);
	if (jOsBuildArr == NULL) {
		return NULL;
	}

	jBuffer = new jshort[ModuleInfo::BUILD_SIZE];
	for (int i = 0; i < ModuleInfo::BUILD_SIZE; i++) {
		jBuffer[i] = modInfo->osBuild[i];
	}
	env->SetShortArrayRegion(jOsBuildArr, 0, ModuleInfo::BUILD_SIZE, jBuffer);
	if (env->ExceptionCheck()) {
		return NULL;
	}

  jobject jModInfo = env->NewObject(classModuleInfo, modConstructor, jSerNumberArr,
		jOsVersion, jPICType, jOsBuildArr);
	
	delete jBuffer;
	return jModInfo;
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1indicateConnectivity
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	try {
		cdcImp->indicateConnectivity();
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	}
}

JNIEXPORT jobject JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getStatus
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	SPIStatus spiStatus;
	try {
		spiStatus = cdcImp->getStatus();
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	}

	jboolean jDataReady = (spiStatus.isDataReady == true)? JNI_TRUE : JNI_FALSE;
	jint jMode;
	if (spiStatus.isDataReady) {
		jMode = spiStatus.DATA_READY;
	} else {
		jMode = spiStatus.SPI_MODE;
	}
	
  jobject jStatus = env->NewObject(classSPIStatus, statConstructor, jDataReady,
		jMode);
	
	return jStatus;	
}

JNIEXPORT jint JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1sendData
(JNIEnv* env, jobject jObj, jlong cdcRef, jshortArray jData) {
	jsize jDataLen = env->GetArrayLength(jData);

  DEBUG_TRC(PAR(cdcRef));

  jshort* jDataBuff = new jshort[jDataLen];
	env->GetShortArrayRegion(jData, 0, jDataLen, jDataBuff);

	unsigned char* cdcData = new unsigned char[jDataLen];
	for (int i = 0; i < jDataLen; i++) {
		cdcData[i] = jDataBuff[i] & 0xFF;
	}
	
	delete jDataBuff;

	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	DSResponse dsResp(ERR);
	try {
		dsResp = cdcImp->sendData(cdcData, jDataLen);
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	}
	
	delete cdcData;

	jint jResp = dsResp;
  
  DEBUG_TRC(PAR(jResp));
  return jResp;
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1switchToCustomlong
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	try {
		cdcImp->switchToCustom();
	} catch (CDCSendException& se) {
    env->ThrowNew(classCDCSendException, se.what());
	} catch (CDCReceiveException& re) {
    env->ThrowNew(classCDCReceiveException, re.what());
	} 
}

/**
 * Stub for registered listeners of asynchronous messages.
 */
void stubListener(unsigned char data[], unsigned int dataLen) {
	JNIEnv* env = NULL;
	jint attachRes = 0;
	jobject jListObj = NULL;
	jshortArray jMsgDataArr = NULL;
	jshort* jMsgBuffer = NULL;

  DEBUG_TRC(PAR(data) << PAR(dataLen));

  jMsgBuffer = new jshort[dataLen];
  attachRes = jvm->AttachCurrentThread((void **)&env, NULL);
  
  while (attachRes == JNI_OK) {

    if (NULL == (jListObj = env->GetObjectField(jCDC, jListID)))
      break;
    
    if (NULL == (jMsgDataArr = env->NewShortArray(dataLen)))
      break;

    for (int i = 0; i < dataLen; i++) {
      jMsgBuffer[i] = data[i];
    }

    env->SetShortArrayRegion(jMsgDataArr, 0, dataLen, jMsgBuffer);
    if (env->ExceptionCheck())
      break;

    env->CallVoidMethod(jListObj, getMsgID, jMsgDataArr);
    break;
  }

  jvm->DetachCurrentThread();
  delete jMsgBuffer;

  DEBUG_TRC("");
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1registerAsyncListener
(JNIEnv* env, jobject jObj, jlong cdcRef) {
  DEBUG_TRC(PAR(cdcRef));
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
    cdcImp->registerAsyncMsgListener(stubListener);
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1unregisterAsyncListener
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	cdcImp->unregisterAsyncMsgListener();
}

JNIEXPORT jboolean JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1isReceptionStopped
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	return cdcImp->isReceptionStopped();
}

JNIEXPORT jstring JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getLastReceptionError
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	char* lastReceptionError = cdcImp->getLastReceptionError();
	jstring jErrorCause = env->NewStringUTF(lastReceptionError);
	return jErrorCause;
}
