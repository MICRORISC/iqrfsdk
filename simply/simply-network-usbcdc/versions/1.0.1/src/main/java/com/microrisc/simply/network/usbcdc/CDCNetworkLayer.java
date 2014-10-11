package com.microrisc.simply.network.usbcdc;

import com.microrisc.cdc.J_AsyncMsgListener;
import com.microrisc.cdc.J_CDCImpl;
import com.microrisc.cdc.J_CDCImplException;
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
     * This methods last at least for 5 seconds due to reseting connected USB device.
     */
    @Override
    public void start() throws NetworkLayerException {
        logger.debug("startReceivingData - start:");
        
        /*
        final long RESET_DELAY = 5000;
        final long MARGIN_DELAY = 5000;
        
        try {
            // reseting GW 
            // 5 s after receiving of this command USB device is reset - program must deal with this !!!
            cdcImpl.resetUSBDevice();
            Thread.sleep(RESET_DELAY + MARGIN_DELAY);
        } catch (Exception ex) {
            logger.error("Cannot reset USB device: " + ex.getMessage());
            throw new NetworkLayerException(ex);
        }
        */
        // register this as a listener of asynchronous messages from network interface
        this.cdcImpl.registerAsyncListener(this);
        
        logger.debug("startReceivingData - end");
    }
    
    @Override
    public void sendData(NetworkData networkData) throws NetworkLayerException {
        logger.debug("sendData - start: networkData={}", networkData);
        
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
            // doesn't care about network ID - sends all data to COM-port set
            // in the constructor
            cdcImpl.sendData(networkData.getData());
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
        logger.debug("onGetMessage - start: data={}", data);
        
        String networkId = connectionStorage.getNetworkId(connectionInfo);
        networkListener.onGetData(new BaseNetworkData(data, networkId));
        
        logger.debug("onGetMessage - end");
    }
}
