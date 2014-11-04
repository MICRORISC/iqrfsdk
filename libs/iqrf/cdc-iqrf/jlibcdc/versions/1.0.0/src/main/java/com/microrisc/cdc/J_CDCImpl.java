/* 
 * Copyright 2014 MICRORISC s.r.o.
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

package com.microrisc.cdc;

/**
 * Wrapper class for accessing CDCLib static library (originally written in C++) 
 * from within Java code.
 * 
 * @version     1.0
 */
public class J_CDCImpl {
    /**
     * Reference to underlaying CDCImpl object.
     */
    private long cdcImpl;
    
    /**
     * Listener of asynchronous message reception. 
     */
    private J_AsyncMsgListener msgListener;
    
    
    /**
     * Creates object of CDCImpl class and returns reference to it.
     * @param portName port which will be used for communication with USB device
     *      if <code>portName</code> is empty string, then default port will be 
     *      used. On Windows is default port set to COM1, on Linux is set to 
     * /dev/ttyACM0.
     * @return reference to new created CDCImpl object.
     *         <code>0</code> if creation fails 
     */
    private native long createCDCImpl(String portName) throws J_CDCImplException, Exception;
    
    /**
     * Destroys specified CDCImpl object and frees all it's used resources. 
     * @param cdcRef reference to CDCImpl object. 
     */
    private native void destroyCDCImpl(long cdcRef);
    
    
    /** Stub methods. */
    private native boolean stub_test(long cdcRef);
    
    private native void stub_resetUSBDevice(long cdcRef);
    
    private native void stub_resetTRModule(long cdcRef);
    
    private native J_DeviceInfo stub_getUSBDeviceInfo(long cdcRef);
    
    private native J_ModuleInfo stub_getTRModuleInfo(long cdcRef);
    
    private native void stub_indicateConnectivity(long cdcRef);
    
    private native J_SPIStatus stub_getStatus(long cdcRef);
    
    private native int stub_sendData(long cdcRef, short[] data);
    
    private native void stub_switchToCustomlong(long cdcRef);
    
    
    private native void stub_registerAsyncListener(long cdcRef);
    
    private native void stub_unregisterAsyncListener(long cdcRef);
    
    
    private native boolean stub_isReceptionStopped(long cdcRef);
    
    private native String stub_getLastReceptionError(long cdcRef);
    
    
    /**
     * Loads supporting Java Stub dll library.
     */
    static {
        System.loadLibrary("CDCLib_JavaStub");
    }
    
   
    /**
     * Initializes CDCLib static library so it can be used for performing 
     * operations. Communication with USB device will be going on default port.
     * On Windows is default port set to COM1, on Linux is set to "/dev/ttyACM0".
     * @throws J_CDCImplException if an error occurs during initialization of 
     *         CDCImpl object inside underlaying CDCLib library.
     * @throws Exception if other error occurs during initialization
     */
    public J_CDCImpl() throws J_CDCImplException, Exception {
        this("");
    }
    
    /**
     * Initializes CDCLib static library so it can be used for performing 
     * operations. Communication with USB device will be going on specified 
     * port.
     * @param portName port used for communication with USB device
     * @throws J_CDCImplException if an error occurs during initialization of 
     *         CDCImpl object inside underlaying CDCLib library.
     * @throws Exception if other error occurs during initialization
     */
    public J_CDCImpl(String portName) throws J_CDCImplException, Exception {
        cdcImpl = createCDCImpl(portName);
        msgListener = null;
    }
    
    
    /**
     * Terminates CDCLib run and frees up all it's used resources.
     */
    public void destroy() {
        if (cdcImpl == 0) {
            return;
        }
         
        destroyCDCImpl(cdcImpl);
        cdcImpl = 0;
        
        msgListener = null;
    }
    
    /**
     * Performs 'test' operation. 
     * @return <code>true</code> if test is successfull <br>
     *         <code>false</code> otherwise
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     */
    public boolean test() throws J_CDCSendException, J_CDCReceiveException {
        return stub_test(cdcImpl);
    }
    
    /**
     * Performs reseting USB device.
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     * @throws Exception if other error occurs
     */
    public void resetUSBDevice() throws J_CDCSendException, J_CDCReceiveException,
            Exception {
       stub_resetUSBDevice(cdcImpl);
    }
    
    /**
     * Performs reseting TR-module inside USB device.
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     * @throws Exception if other error occurs
     */
    public void resetTRModule() throws J_CDCSendException, J_CDCReceiveException,
            Exception {
       stub_resetTRModule(cdcImpl);
    }
    
    /**
     * Returns information about connected USB device.
     * @return connected USB device identification
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     * @throws Exception if other error occurs
     */
    public J_DeviceInfo getUSBDeviceInfo() throws J_CDCSendException, J_CDCReceiveException,
            Exception {
       return stub_getUSBDeviceInfo(cdcImpl);
    }
    
    /**
     * Returns information about TR module inside the connected USB device.
     * @return TR module identification
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     * @throws Exception if other error occurs
     */
    public J_ModuleInfo getTRModuleInfo() throws J_CDCSendException, J_CDCReceiveException,
            Exception {
       return stub_getTRModuleInfo(cdcImpl);
    }
    
    /**
     * Performs indication of USB device.
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     * @throws Exception if other error occurs
     */
    public void indicateConnectivity() throws J_CDCSendException, J_CDCReceiveException,
            Exception {
       stub_indicateConnectivity(cdcImpl);
    }
    
    /**
     * Returns SPI status of TR module inside the connected USB device.
     * @return SPI status of module
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     * @throws Exception if other error occurs
     */
    public J_SPIStatus getSPIStatus() throws J_CDCSendException, J_CDCReceiveException,
            Exception {
       return stub_getStatus(cdcImpl);
    }
    
    /**
     * Sends specified data to module and returns response.
     * @param data data to send
     * @return response on sending specified data to module 
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     * @throws Exception if other error occurs
     */
    public J_DSResponse sendData(short[] data) throws J_CDCSendException, J_CDCReceiveException,
            Exception {
       int respVal = stub_sendData(cdcImpl, data);
       for (J_DSResponse jResp : J_DSResponse.values()) {
           if (jResp.getRespValue() == respVal) {
               return jResp;
           }
       }
       
       throw new Exception("Unknown response value.");
    }
    
    /**
     * Switches USB class to Custom.
     * @throws J_CDCSendException if some error occurs during sending command
     * @throws J_CDCReceiveException if some error occurs during response reception
     * @throws Exception if other error occurs
     */
    public void switchToCustom() throws J_CDCSendException, J_CDCReceiveException,
            Exception {
       stub_switchToCustomlong(cdcImpl);
    }
    
    /**
     * Registers listener of asynchronous messages reception.
     * @param asyncListener listener to register
     */
    public void registerAsyncListener(J_AsyncMsgListener asyncListener) {
        msgListener = asyncListener;
        stub_registerAsyncListener(cdcImpl);
    }
    
    /**
     * Unregisters listener of asynchronous messages reception.
     */
    public void unregisterAsyncListener() {
        msgListener = null;
        stub_unregisterAsyncListener(cdcImpl);
    }
    
    /**
     * Indicates, wheather CDC library stops(permanently) reception of incomming
     * messages (as a consequence of error).
     * @return <code>true</code> if reception is stopped <br>
     *         <code>false</code> otherwise
     */
    public boolean isReceptionStopped() {
        return stub_isReceptionStopped(cdcImpl);
    }
    
    /**
     * Returns description of last reception error.
     * @return description of last reception error <br>
     *         empty string, if no reception error yet occurred
     */
    public String getLastReceptionError() {
        return stub_getLastReceptionError(cdcImpl);
    }
}

