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
 * Interface for implementing communication between Windows system and GW-USB-04
 * device. This communication is based on USB CDC class implementation of 
 * IQRF platform. 
 * 
 * @author		Michal Konopa
 * @version		1.0.0
 * @date		16.12.2011
 * @file		CdcInterface.h
 */

#ifndef __CDCInterface_h_
#define __CDCInterface_h_



/**
 * USB device identification. Response information of "I-command"
 * (get usb device info). 
 */
struct DeviceInfo {
	char* type;					/**< device type */
	unsigned int typeLen;		/**< length of device type information */
	char* firmwareVersion;		/**< firmware version */
	unsigned int fvLen;			/**< length of firmware version information */
	char* serialNumber;			/**< serial number */
	unsigned int snLen;			/**< length of serial number information */
};

/**
 * Information about TR module identification inside the USB device. 
 * IQRF OS User's guide (chapter Identification -> Module Data).
 */
struct ModuleInfo {
	static const unsigned int SN_SIZE = 4;
	static const unsigned int BUILD_SIZE = 2;
	unsigned char serialNumber[SN_SIZE];		/**< serial number */
	unsigned char osVersion;					/**< OS version */
	unsigned char PICType;						/**< PIC type */
	unsigned char osBuild[BUILD_SIZE];			/**< OS build */
};

/** 
 * Values according to the table in IQRF SPI User's guide (chapter SPI status).
 */
enum SPIModes {DISABLED = 0x0, SUSPENDED = 0x07, BUFF_PROTECT = 0x3F, 
	CRCM_ERR = 0x3E, READY_COMM = 0x80, READY_PROG = 0x81, READY_DEBUG = 0x82,
	SLOW_MODE = 0x83, HW_ERROR = 0xFF
};

/**
 * Current status. Response information of "S-command" (get status). 
 */
struct SPIStatus {
	bool isDataReady;	/**< determines, that DATA_READY is used */
	union {
		SPIModes SPI_MODE;
		int DATA_READY;		/**< SPI data ready */
	};
};

/**
 * Response information of "DS-command"(send data). Its precise meaning
 * is according to: "CDC Implementation in IQRF USB devices User Guide".
 */
enum DSResponse {OK, ERR, BUSY};

/** 
 * Read listener, which will be called by asynchronous message reception. 
 * The first parameter is a pointer to received message data.
 * The second parameter is a length of received message data. 
 */
typedef void (*AsyncMsgListener)(unsigned char*, unsigned int);


/**
 * Abstract class - communication commands specifications. If a user needs
 * to receive informations of asynchronous messages, it must register its
 * own listener-function(according to AsyncMsgListener type), which will be 
 * called after asynchronous message reception. Data of the message will be 
 * passed to that function parameters. 
 */
class CDCInterface {
	public:
		/** 
		 * Performs communication test("> command"). 
		 * @return @c true if the test succeeds
		 * @return @c false otherwise
		 */
		virtual bool test(void) = 0;
		
		/**
		 * Resets USB device("R-command"). 
		 */
		virtual void resetUSBDevice(void) = 0;
		
		/** 
		 * Resets TR module("RT-command"). 
		 */
		virtual void resetTRModule(void) = 0;
		
		/**
		 * Returns USB device identification("I-command"). 
		 * @return USB device identification.
		 */
		virtual DeviceInfo* getUSBDeviceInfo(void) = 0;
		
		/** 
		 * Returns identification of TR module inside the USB device
		 * ("IT-command").
		 * @return TR module identification.
		 */
		virtual ModuleInfo* getTRModuleInfo(void) = 0;
		
		/** 
		 * Performs an acoustical or optical indication of USB device
		 * ("B-command"). 
		 */
		virtual void indicateConnectivity(void) = 0;
		
		/** 
		 * Returns information about current status of TR module("S-command").
		 * @return current status of TR module.
		 */
		virtual SPIStatus getStatus(void) = 0;
		
		/** 
		 * Sends data to TR module inside the USB device("DS-command").
		 * @param data the data to send
		 * @param dlen the length of data to send
		 * @return result of data send
		 */
		virtual DSResponse sendData(unsigned char* data, unsigned int dlen) = 0;
		
		/** 
		 * Switches USB class to Custom and the device is reset 5 s after this 
		 * command is issued("U-command"). 
		 */
		virtual void switchToCustom(void) = 0;
		
		/** 
		 * Registers user-defined listener of asynchronous messages("DR-messages")
		 * reception during run of this library. Data of each message are passed
		 * in the first parameter, length of the data is passed as the second parameter.
		 * @param asyncListener user's listener
		 */
		virtual void registerAsyncMsgListener(AsyncMsgListener asyncListener) = 0;
		
		/** 
		 * Unregisters asynchronous messages reception listener. 
		 */
		virtual void unregisterAsyncMsgListener(void) = 0;
};

#endif
