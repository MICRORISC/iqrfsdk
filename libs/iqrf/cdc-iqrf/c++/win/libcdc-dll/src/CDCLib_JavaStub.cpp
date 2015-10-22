#include "com_microrisc_CDC_J_CDCImpl.h"
#include <cdc/CDCImpl.h>
#include <fstream>

/** 
 * Pointer to JavaVM instance. It will be used mainly in asynchronous
 * messaging.
 */
static JavaVM* jvm = NULL;

/**
 * Global reference to J_CDCImpl object.
 */
static jobject jCDC = NULL;


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
				jclass excClass = env->FindClass("java/lang/Exception");
				env->ThrowNew(excClass, "Port Name conversion to UTF failed");
				return 0;
			}
			cdcImp = new CDCImpl(portNameUTF);
			env->ReleaseStringUTFChars(portName, portNameUTF);
		}
	} catch (CDCImplException& e) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCImplException");

		// Unable to find exception class.
		if (excClass == NULL) {
			return 0;
		}

		env->ThrowNew(excClass, e.what());
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
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return false;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return false;
		}
		env->ThrowNew(excClass, re.what());
	}
	
	return testResult;
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1resetUSBDevice
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	try {
		cdcImp->resetUSBDevice();
	} catch (CDCSendException& se) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return;
		}
		env->ThrowNew(excClass, re.what());
	}
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1resetTRModule
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	try {
		cdcImp->resetTRModule();
	} catch (CDCSendException& se) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return;
		}
		env->ThrowNew(excClass, re.what());
	}
}

JNIEXPORT jobject JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getUSBDeviceInfo
(JNIEnv* env, jobject oObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	DeviceInfo* devInfo = NULL;
	try {
		devInfo = cdcImp->getUSBDeviceInfo();
	} catch (CDCSendException& se) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return NULL;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return NULL;
		}
		env->ThrowNew(excClass, re.what());
	}

	jclass jDevClass = env->FindClass("com/microrisc/cdc/J_DeviceInfo");
	if (jDevClass == NULL) {
		return NULL;
	}
	
	jmethodID devConstructor = env->GetMethodID(jDevClass, "<init>", 
		"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	if (devConstructor == NULL) {
		return NULL;
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

	jobject jDevInfo = env->NewObject(jDevClass, devConstructor, devType, 
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
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return NULL;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return NULL;
		}
		env->ThrowNew(excClass, re.what());
	} 

	jclass jModClass = env->FindClass("com/microrisc/cdc/J_ModuleInfo");
	if (jModClass == NULL) {
		return NULL;
	}
	
	jmethodID modConstructor = env->GetMethodID(jModClass, "<init>", 
		"([SSS[S)V");
	if (modConstructor == NULL) {
		return NULL;
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

	jobject jModInfo = env->NewObject(jModClass, modConstructor, jSerNumberArr, 
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
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return;
		}
		env->ThrowNew(excClass, re.what());
	}
}

JNIEXPORT jobject JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1getStatus
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	SPIStatus spiStatus;
	try {
		spiStatus = cdcImp->getStatus();
	} catch (CDCSendException& se) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return NULL;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return NULL;
		}
		env->ThrowNew(excClass, re.what());
	}

	jclass jStatClass = env->FindClass("com/microrisc/cdc/J_SPIStatus");
	if (jStatClass == NULL) {
		return NULL;
	}
	
	jmethodID statConstructor = env->GetMethodID(jStatClass, "<init>", 
		"(ZI)V");
	if (statConstructor == NULL) {
		return NULL;
	}
	
	jboolean jDataReady = (spiStatus.isDataReady == true)? JNI_TRUE : JNI_FALSE;
	jint jMode;
	if (spiStatus.isDataReady) {
		jMode = spiStatus.DATA_READY;
	} else {
		jMode = spiStatus.SPI_MODE;
	}
	
	jobject jStatus = env->NewObject(jStatClass, statConstructor, jDataReady, 
		jMode);
	
	return jStatus;	
}

JNIEXPORT jint JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1sendData
(JNIEnv* env, jobject jObj, jlong cdcRef, jshortArray jData) {
	jsize jDataLen = env->GetArrayLength(jData);
	jshort* jDataBuff = new jshort[jDataLen];
	env->GetShortArrayRegion(jData, 0, jDataLen, jDataBuff);

	unsigned char* cdcData = new unsigned char[jDataLen];
	for (int i = 0; i < jDataLen; i++) {
		cdcData[i] = jDataBuff[i] & 0xFF;
	}
	
	delete jDataBuff;

	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	DSResponse dsResp;
	try {
		dsResp = cdcImp->sendData(cdcData, jDataLen);
	} catch (CDCSendException& se) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return NULL;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return NULL;
		}
		env->ThrowNew(excClass, re.what());
	}
	
	delete cdcData;

	jint jResp = dsResp;
	return jResp;		
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1switchToCustomlong
(JNIEnv* env, jobject jObj, jlong cdcRef) {
	CDCImpl* cdcImp = (CDCImpl*)cdcRef;
	try {
		cdcImp->switchToCustom();
	} catch (CDCSendException& se) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCSendException");
		if (excClass == NULL) {
			return;
		}
		env->ThrowNew(excClass, se.what());
	} catch (CDCReceiveException& re) {
		jclass excClass = env->FindClass("com/microrisc/cdc/J_CDCReceiveException");
		if (excClass == NULL) {
			return;
		}
		env->ThrowNew(excClass, re.what());
	} 
}


/**
 * Stub for registered listeners of asynchronous messages.
 */
void stubListener(unsigned char data[], unsigned int dataLen) {
	JNIEnv* env = NULL;
	jint attachRes = 0;
	jclass cdcClass = NULL;
	jfieldID jListID = NULL;
	jobject jListObj = NULL;
	jclass listClass = NULL;
	jmethodID getMsgID = NULL;
	jshortArray jMsgDataArr = NULL;
	jshort* jMsgBuffer = NULL;

	attachRes = jvm->AttachCurrentThread((void **)&env, NULL);
	if (attachRes != JNI_OK) {
		goto END;
	}

	cdcClass = env->FindClass("com/microrisc/cdc/J_CDCImpl");
	if (cdcClass == NULL) {
		goto END;
	}

	jListID = env->GetFieldID(cdcClass, "msgListener",
		"Lcom/microrisc/cdc/J_AsyncMsgListener;");
	if (jListID == NULL) {
		goto END;
	}

	jListObj = env->GetObjectField(jCDC, jListID);
	if (jListObj == NULL) {
		goto END;
	}
	
	listClass = env->FindClass("com/microrisc/cdc/J_AsyncMsgListener");
	if (listClass == NULL) {
		goto END;
	}
	
	getMsgID = env->GetMethodID(listClass, "onGetMessage", "([S)V");
	if (getMsgID == NULL) {
		goto END;
	}

	jMsgDataArr = env->NewShortArray(dataLen);
	if (jMsgDataArr == NULL) {
		goto END;
	}

	jMsgBuffer = new jshort[dataLen];
	for (int i = 0; i < dataLen; i++) {
		jMsgBuffer[i] = data[i];
	}

	env->SetShortArrayRegion(jMsgDataArr, 0, dataLen, jMsgBuffer);
	if (env->ExceptionCheck()) {
		delete jMsgBuffer;
		goto END;
	}

	delete jMsgBuffer;
	env->CallVoidMethod(jListObj, getMsgID, jMsgDataArr);
	
END:
	jvm->DetachCurrentThread();
}

JNIEXPORT void JNICALL Java_com_microrisc_cdc_J_1CDCImpl_stub_1registerAsyncListener
(JNIEnv* env, jobject jObj, jlong cdcRef) {
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


