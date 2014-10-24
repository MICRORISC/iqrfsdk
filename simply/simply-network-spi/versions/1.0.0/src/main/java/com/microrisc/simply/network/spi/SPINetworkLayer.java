package com.microrisc.simply.network.spi;

import com.microrisc.rpi.spi.SPI_Exception;
import com.microrisc.rpi.spi.iqrf.SPI_Master;
import com.microrisc.rpi.spi.iqrf.SPI_Status;
import com.microrisc.rpi.spi.iqrf.SimpleSPI_Master;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.NetworkLayerListener;
import com.microrisc.simply.network.AbstractNetworkConnectionInfo;
import com.microrisc.simply.network.AbstractNetworkLayer;
import com.microrisc.simply.network.BaseNetworkData;
import com.microrisc.simply.network.NetworkConnectionStorage;
import com.microrisc.simply.network.NetworkLayerException;
import java.util.LinkedList;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements network layer using {@code com.microrisc.rpi.spi.iqrf.SimpleSPI_Master} object.
 * 
 * @author Rostislav Spinar
 */
public final class SPINetworkLayer extends AbstractNetworkLayer {
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SPINetworkLayer.class);

    /**
     * Registered network listener.
     */
    private NetworkLayerListener networkListener = null;

    /**
     * Connection info.
     */
    private BaseSPIPortConnectionInfo connectionInfo = null;

    /**
     * SPI reader thread
     */
    private Thread spiReader = null;

    /**
     * listener caller thread
     */
    private Thread listenerCaller = null;

    /**
     * SPI master
     */
    private SPI_Master spiMaster = null;

    /**
     * SPI-port name for connection.
     */
    private String portName = null;

    /**
     * Data received from SPI.
     */
    private Queue<short[]> dataFromSPI = null;

    /**
     * Synchronization to SPI access.
     */
    private final Object spiSynchro = new Object();

    /**
     * Synchronization between socket reader thread and listener caller thread.
     */
    private final Object threadsSynchro = new Object();

    /**
     * Default maximal size of received packets [in bytes].
     */
    public static int MAX_RECEIVED_PACKET_SIZE = 128;

    /**
     * Maximal size of received packets [in bytes].
     */
    private int maxRecvPacketSize;

    /**
     * Reading data from SPI.
     */
    private class SPIReader extends Thread {

        @Override
        public void run() {
            short[] buffer = new short[maxRecvPacketSize];
            boolean newDataReceived = false;
            int dataLen = 0;

            while ( true ) {

                if ( this.isInterrupted() ) {
                    logger.info("SPI reader thread interrupted");
                    return;
                }

                try {
                    synchronized ( spiSynchro ) {
                        SPI_Status spiStatus = spiMaster.getSlaveStatus();
                        //logger.info("Reading thread SPI status: {}", spiStatus.getValue());

                        if ( spiStatus.isDataReady() ) {
                            logger.info("Data ready!");
                            if ( spiStatus.getValue() == 0x40 ) {
                                dataLen = 64;
                            } else {
                                dataLen = spiStatus.getValue() - 0x40;
                            }

                            buffer = spiMaster.readData(dataLen);
                            newDataReceived = true;
                        }
                    }

                    // if new data has received add it into the queue
                    if ( newDataReceived ) {
                        logger.info("New data from SPI: {}", buffer);

                        synchronized ( threadsSynchro ) {
                            dataFromSPI.add(buffer);
                            threadsSynchro.notify();
                        }
                        newDataReceived = false;
                    }

                    Thread.sleep(10);
                } catch (SPI_Exception ex) {
                    logger.error("Error while receiving SPI interface: ", ex);
                } catch (InterruptedException ex) {
                    logger.warn("SPI reader thread interrupted while sleeping.");
                    return;
                }
            }
        }
    }

    /**
     * Calling listener callback method - when new data has arrived from socket.
     */
    private class ListenerCaller extends Thread {

        // already consumed data from socket
        private Queue<short[]> consumedData = new LinkedList<>();

        // indicates, wheather new data are from SPI
        private boolean areDataReadyFromSPI() {
            return ( !dataFromSPI.isEmpty() );
        }

        // consume data from spi and adds them into buffer
        private void consumeDataFromSPI() {
            while ( !dataFromSPI.isEmpty() ) {
                short[] packetData = dataFromSPI.poll();
                consumedData.add(packetData);
            }
        }

        /**
         * Frees up used resources.
         */
        private void freeResources() {
            consumedData.clear();
        }

        @Override
        public void run() {
            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("SPI caller thread interrupted");
                    freeResources();
                    return;
                }

                // consuming new data from SPI
                synchronized ( threadsSynchro ) {
                    while ( !areDataReadyFromSPI() ) {
                        try {
                            threadsSynchro.wait();
                        } catch ( InterruptedException ex ) {
                            logger.warn("SPI caller thread interrupted while "
                                    + "waiting on data from SPI.");
                            freeResources();
                            return;
                        }
                    }
                    consumeDataFromSPI();
                }

                // remove data from queue and send it to listener
                while ( !consumedData.isEmpty() ) {
                    short[] userData = consumedData.poll();

                    if ( networkListener != null ) {
                        String networkId = connectionStorage.getNetworkId(connectionInfo);
                        networkListener.onGetData(new BaseNetworkData(userData, networkId));
                    }
                }
            }
        }
    }

    // creates and starts threads
    private void createAndStartThreads() {
        spiReader = new SPIReader();
        spiReader.start();

        listenerCaller = new ListenerCaller();
        listenerCaller.start();
    }

    // terminates SPI reader and client caller threads
    private void terminateThreads() {
        logger.debug("terminateThreads - start:");

        // termination signal to socket reader thread
        spiReader.interrupt();

        // termination signal to listener caller thread
        listenerCaller.interrupt();

        // Waiting for threads to terminate. Cancelling worker threads has higher 
        // priority than main thread interruption. 
        while (spiReader.isAlive() || listenerCaller.isAlive()) {
            try {
                if (spiReader.isAlive()) {
                    spiReader.join();
                }

                if (listenerCaller.isAlive()) {
                    listenerCaller.join();
                }
            } catch (InterruptedException e) {
                // restoring interrupt status
                Thread.currentThread().interrupt();
                logger.warn("Termination - SPI Network Layer interrupted");
            }
        }

        logger.info("SPI Network Layer stopped.");
        logger.debug("terminateThreads - end");
    }

    private static NetworkConnectionStorage checkStorage(NetworkConnectionStorage storage) {
        if ( storage == null ) {
            throw new IllegalArgumentException(
                    "Network Connection Storage cannot be null");
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
     * Creates new SPI network layer object.
     * @param connectionStorage storage of network SPI-port connections
     * @param portName SPI-port name for communication
     */
    public SPINetworkLayer(NetworkConnectionStorage connectionStorage, String portName) {
        super(checkStorage(connectionStorage));
        this.portName = checkPortName(portName);
        this.connectionInfo = new BaseSPIPortConnectionInfo(portName);
    }

    @Override
    public void start() throws NetworkLayerException {
        logger.debug("startReceivingData - start:");

        try {
            // initialization
            spiMaster = new SimpleSPI_Master(portName);
        } catch (SPI_Exception ex) {
            throw new NetworkLayerException(ex);
        }

        // init queue of data comming from SPI
        dataFromSPI = new LinkedList<>();

        // creating and starting threads
        createAndStartThreads();

        logger.info("Receiving data started");
        logger.debug("startReceivingData - end");
    }

    @Override
    public void registerListener(NetworkLayerListener listener) {
        this.networkListener = listener;
        logger.info("Listener registered");
    }

    @Override
    public void unregisterListener() {
        networkListener = null;
        logger.info("Listener unregistered");
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
            throw new NetworkLayerException("No connection info for network: "
                    + networkData.getNetworkId()
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
            logger.info("Data will be sent to SPI...");
            
            synchronized (spiSynchro) {
                // getting slave status
                SPI_Status spiStatus = spiMaster.getSlaveStatus();
                logger.info("Writing thread SPI status: {}", spiStatus.getValue());
                
                if ( spiStatus.getValue() == SPI_Status.READY_COMM_MODE ) {
                    // sending some data to device
                    spiMaster.sendData(networkData.getData());
                    logger.info("Data successfully sent to SPI");
                }
                else {
                    logger.info("Data not sent to SPI, module is not in READY_COMM_MODE");
                }
            }
        } catch (SPI_Exception ex) {
            throw new NetworkLayerException(ex);
        }
    }

    @Override
    public void destroy() {
        logger.debug("destroy - start: ");
        
        unregisterListener();
        terminateThreads();
        dataFromSPI.clear();
        spiMaster.destroy();
        spiMaster = null;
        
        logger.info("Destroyed");
        logger.debug("destroy - end");
    }
}
