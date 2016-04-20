/* 
 * Copyright 2016 MICRORISC s.r.o.
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

package com.microrisc.simply.network.usbcdc;

import com.microrisc.cdc.J_AsyncMsgListener;
import com.microrisc.cdc.J_CDCImpl;
import com.microrisc.cdc.J_CDCImplException;
import com.microrisc.cdc.J_CDCSendException;
import com.microrisc.cdc.J_DSResponse;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.NetworkLayerListener;
import com.microrisc.simply.network.AbstractNetworkConnectionInfo;
import com.microrisc.simply.network.AbstractNetworkLayer;
import com.microrisc.simply.network.BaseNetworkData;
import com.microrisc.simply.network.NetworkConnectionStorage;
import com.microrisc.simply.network.NetworkLayerException;
import com.microrisc.simply.network.comport.BaseCOMPortConnectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements network layer using {@code J_CDCImpl} object.
 * <p>
 * This registers itself like an asynchronous listener of {@code J_CDCImpl}
 * object. All data comming from CDC interface is forwarder to user's registered
 * network listener. All data designated to underlaying network are forwarded to
 * J_CDCImpl's {@code J_CDCImpl} method.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public final class CDCNetworkLayer
        extends AbstractNetworkLayer implements J_AsyncMsgListener {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(CDCNetworkLayer.class);

    
    /** COM-port name for connection. */
    private final String portName;

    /** Reference to CDC-object for communication. */
    private J_CDCImpl cdcImpl = null;

    /** Registered network listener. */
    private NetworkLayerListener networkListener = null;

    /** COM-port connection info. */
    private BaseCOMPortConnectionInfo connectionInfo=  null;
    
    /**
     * Default maximal number of CDC retries.
     */
    public static int MAX_CDC_STATUS_RETRIES = 3;

    /**
     * Maximal number of CDC retries.
     */
    private int maxCdcStatusRetries;

    
    /** Frees up used resources. */
    private void freeResources() {
        cdcImpl = null;
        connectionStorage = null;
    }

    private static NetworkConnectionStorage checkStorage(NetworkConnectionStorage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Network Connection Storage cannot "
                    + "be less null");
        }
        return storage;
    }

    private static String checkPortName(String portName) {
        if ( portName == null ) {
            throw new IllegalArgumentException("Port name cannot be null");
        }

        if ( portName.equals("") ) {
            throw new IllegalArgumentException("Port name cannot be empty string");
        }
        return portName;
    }

    
    /**
     * Creates CDC network layer object.
     * @param connectionStorage storage of network COM-port connections
     * @param portName COM-port name for communication
     * @throws com.microrisc.cdc.J_CDCImplException if some exception has occured
     *         during creating of CDC network layer
     */
    public CDCNetworkLayer(NetworkConnectionStorage connectionStorage, String portName)
            throws J_CDCImplException, Exception 
    {
        super(checkStorage(connectionStorage));
        this.portName = checkPortName(portName);
        this.cdcImpl = new J_CDCImpl(portName);
        this.connectionInfo = new BaseCOMPortConnectionInfo(portName);
        this.maxCdcStatusRetries = MAX_CDC_STATUS_RETRIES;
    }
   
    @Override
    public void registerListener(NetworkLayerListener listener) {
        this.networkListener = listener;
        logger.info("Listener registered");
    }

    @Override
    public void unregisterListener() {
        cdcImpl.unregisterAsyncListener();
        networkListener = null;

        logger.info("Listener unregistered");
    }

    /**
     * Starts receiving data from CDC interface.
     */
    @Override
    public void start() throws NetworkLayerException {
        logger.debug("startReceivingData - start:");

        // register this as a listener of asynchronous messages from network interface
        this.cdcImpl.registerAsyncListener(this);

        logger.debug("startReceivingData - end");
    }

    @Override
    public void sendData(NetworkData networkData) throws NetworkLayerException {
        logger.debug("sendData - start: networkData: netId={}, netData={}", networkData.getNetworkId(), convertDataForLog(networkData.getData()));

        // get connection info for specified request
        AbstractNetworkConnectionInfo connInfo = connectionStorage.getNetworkConnectionInfo(
                networkData.getNetworkId()
        );

        // no connection info
        if ( connInfo == null ) {
            throw new NetworkLayerException(
                    "No connection info for network: " + networkData.getNetworkId()
            );
        }

        // check, if connection infos are equals
        if ( !(this.connectionInfo.equals(connInfo)) ) {
            throw new NetworkLayerException("Connection info mismatch."
                    + "Incomming: " + connInfo
                    + ", required: " + this.connectionInfo
            );
        }

        try {
            
            boolean dataSent = false;
            int attempt = 0;

            while (attempt++ < maxCdcStatusRetries) {
                // have some space before sending another request
                Thread.sleep(50);

                // doesn't care about network ID - sends all data to COM-port set
                // in the constructor
                J_DSResponse response = cdcImpl.sendData(networkData.getData());
                logger.info("Writing thread CDC response: {}", response.getRespValue());

                if (response.getRespValue() == J_DSResponse.OK.getRespValue()) {
                    logger.info("Data successfully sent to CDC");
                    dataSent = true;
                    break;
                } else {
                    logger.info("Data not sent to CDC, gateway is in state {}, retries {} ", response.getRespValue(), attempt);
                }
            }

            if (!dataSent) {
                throw new NetworkLayerException(new J_CDCSendException("Data has not been sent to the module!"));
            }
        } catch ( Exception ex ) {
            throw new NetworkLayerException(ex);
        }

        logger.debug("sendData - end");
    }

    @Override
    public void destroy() {
        logger.debug("destroy - start: ");

        cdcImpl.unregisterAsyncListener();
        freeResources();

        logger.info("Destroyed");
        logger.debug("destroy - end");
    }

    @Override
    public void onGetMessage(short[] data) {
            
        logger.debug("onGetMessage - start: data={}", convertDataForLog(data));
        
        String networkId = connectionStorage.getNetworkId(connectionInfo);
        networkListener.onGetData(new BaseNetworkData(data, networkId));

        logger.debug("onGetMessage - end");
    }
    
    // coverts data to hex values for log
    private String convertDataForLog(short[] data) {
        
        String dataString = null;

        // converts to hex for print in log
        if (logger.isDebugEnabled()) {
            
            dataString = "[";
            
            for (int i = 0; i < data.length; i++) {
                if (i != data.length - 1) {
                    dataString += String.format("%02X", data[i]) + ".";
                } else {
                    dataString += String.format("%02X", data[i]) + "]";
                }
            }
        }
        
        return dataString;
    }
}
