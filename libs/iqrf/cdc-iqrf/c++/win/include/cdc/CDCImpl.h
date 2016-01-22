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
 * Declaration of class, which implements CDCInterface abstract class
 * in order to provide for a concrete mean for communication between
 * Windows and MICRORISC USB device. Communication is based on USB CDC class
 * implementation in IQRF platform.
 *
 * @author		Michal Konopa
 * @file		CDCImpl.h
 * @version		1.0
 * @date		16.12.2011
 */

#ifndef __CDCImpl_h_
#define __CDCImpl_h_

#include <cdc/CdcInterface.h>
#include <cdc/CDCImplException.h>
#include <cdc/CDCSendException.h>
#include <cdc/CDCReceiveException.h>


/**
 * Forward declaration of CDCImpl implementation class.
 */
class CDCImplPrivate;

/**
 * Implements public interface of CDCInterface abstract class for communication
 * support between PC and GW-USB-04 device.
 *
 * Properties:
 * - Dedicated thread for reading from COM-port( COM1 is default ).
 * - Exception mechanism for dealing with some type of errors.
 * - Simple validation mechanism for incomming message data.
 * - Inner timeout settings(usualy 5000 ms) for waiting for operations,
 *     user-defined timeout settings are not currently supported.
 * - If some serious error occurs during reading from COM-port, the reading thread is
 *   automaticly stopped. Stopped thread is currently not possible to start
 *   again - for continuous working you must destruct the object and
 *   construct the new one. Activity of reading thread can be tested via
 *   @c isReceptionStopped function. If some method from public interface is
 *   called after the reading thread was stopped, exception will be thrown.
 * - Information about reading errors can be received via @c getLastReceptionError
 	 function.
 * - If any error in input asynchronous message is discovered, @c NULL will be
 *   returned as a first parameter to asynchronous listener, and @c 0 as the
 *   second one.
 */
class CDCImpl : public CDCInterface {
private:
	// Pointer to implementation object(d-pointer).
	CDCImplPrivate* implObj;


public:
		/**
		 * Creates new instance with COM-port set to COM1.
		 * @throw CDCImplException if some error occurs during initialization
		 */
		CDCImpl();

		/**
		 * Creates new instance with specified COM-port.
		 * @param commPort COM-port to communicate with
		 * @throw CDCImplException if some error occurs during initialization
		 */
		CDCImpl(const char* commPort);

		/**
		 * Destroys communication object and frees all needed resources.
		 */
		~CDCImpl();

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		bool test(void);

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		void resetUSBDevice(void);

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		void resetTRModule(void);

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		DeviceInfo* getUSBDeviceInfo(void);

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		ModuleInfo* getTRModuleInfo(void);

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		void indicateConnectivity(void);

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		SPIStatus getStatus(void);

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		DSResponse sendData(unsigned char* data, unsigned int dlen);

		/**
		 * @throw CDCSendException if some error occurs during sending command
		 * @throw CDCReceiveException if some error occurs during response reception
		 */
		void switchToCustom(void);

		void registerAsyncMsgListener(AsyncMsgListener asyncListener);

		void unregisterAsyncMsgListener(void);

		/**
		 * Indicates, wheather reception of messages from associated COM-port
		 * is stopped.
		 * @return reception stop indication
		 */
		bool isReceptionStopped(void);

		/**
		 * Returns description of last error, which has occurred during
		 * reception of messages from associated COM-port. If no error has
		 * occurred, @c NULL is returned.
		 * @return last reception error
		 * @return @c NULL, if no error yet occurred
		 */
        char* getLastReceptionError(void);
};

#endif //__CDCImpl_h_
