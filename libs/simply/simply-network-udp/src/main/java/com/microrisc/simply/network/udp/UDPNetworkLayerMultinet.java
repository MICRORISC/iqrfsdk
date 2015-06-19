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

package com.microrisc.simply.network.udp;

import com.microrisc.simply.network.udp.gweth.GWETH_DataTransformer;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.NetworkLayerListener;
import com.microrisc.simply.network.AbstractNetworkConnectionInfo;
import com.microrisc.simply.network.AbstractNetworkLayer;
import com.microrisc.simply.network.BaseNetworkData;
import com.microrisc.simply.network.NetworkConnectionStorage;
import com.microrisc.simply.network.NetworkLayerException;
import com.microrisc.simply.network.udp.BaseUDPConnectionInfo;
import com.microrisc.simply.network.udp.UDPConnectionInfo;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements network layer built on UDP communication. The layer acts as a
 * UDP client. This network layer is specialized to only usage with GW-ETH-01
 * gateway.
 * <p>
 * Working with multiple UDP networks is supported. 
 * 
 * @author Michal Konopa
 */
public final class UDPNetworkLayerMultinet extends AbstractNetworkLayer {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(UDPNetworkLayerMultinet.class);
    
    /** Registered network listener. */
    private NetworkLayerListener networkListener = null;
    
    /** Local IP address to bind. */
    private InetAddress localAddress = null;
    
    /** Local port number. */
    private int localPort = -1;
    
    /** Socket for communication with server. */
    private DatagramSocket socket = null;
    
    /** Synchronization to socket access. */
    private final Object socketSynchro = new Object();
    
    
    /** Default timeout [in ms] of blocking waiting for reception of packet from the socket. */
    public static int RECEPTION_TIMEOUT_DEFAULT = 100;
    
    /** Default maximal size of received packets [in bytes]. */
    public static int MAX_RECEIVED_PACKET_SIZE = 500;
    
    /** Timeout [in ms] of blocking waiting for reception of packet from the socket. */
    private int receptionTimeout;
    
    /** Maximal size of received packets [in bytes]. */
    private int maxRecvPacketSize;
    
    
    /**
     * Packet data.
     */
    private class PacketData {
        BaseUDPConnectionInfo connInfo;
        short[] data;
        
        PacketData(BaseUDPConnectionInfo connInfo, short[] data) {
            this.connInfo = connInfo;
            this.data = data; 
        }
    }
    
    /** Data received from socket. */
    private Queue<PacketData> dataFromSocket = null;
    
    /** Synchronization between socket reader thread and listener caller thread. */
    private final Object threadSynchro = new Object();
    
    
    /** Frees up used resources. */
    private void freeResources() {
        dataFromSocket.clear();
        dataFromSocket = null;
        socket = null;
        connectionStorage = null;
    }
    
    /**
     * Reading data from connected socket.
     */
    private class SocketReader extends Thread {
        // extracts data from specified packet and returns it
        private PacketData extractDataFromSocket(DatagramPacket packet) {
            byte[] packetData = packet.getData();
            short[] extractedData = new short[packet.getLength()];
            
            for (int item = 0; item < packet.getLength(); item++) {
                extractedData[item] = (short)(packetData[item] & 0xFF);
            }
            
            BaseUDPConnectionInfo connInfo = new BaseUDPConnectionInfo(
                    packet.getAddress(), packet.getPort()
            );
            
            return new PacketData(connInfo, extractedData);
        }
        
        @Override
        public void run() {
            byte[] buffer = new byte[maxRecvPacketSize];
            DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
            boolean newDataReceived = false;
            
            while (true) {
                if (this.isInterrupted()) {
                    logger.info("Socket reader thread interrupted");
                    return;
                }
                
                // receive data from socket
                try {
                    synchronized(socketSynchro) {
                        socket.receive(recvPacket);
                    }
                    newDataReceived = true;
                } catch (SocketTimeoutException ex) {
                    logger.debug("Timeout expired");
                } catch (IOException ex) {
                    logger.error("Error while receiving message from socket", ex);
                    return;
                }  
                
                // if new data has received, extract it from the packet and 
                // add it into queue
                if (newDataReceived) {
                    PacketData extractedData = extractDataFromSocket(recvPacket); 
                    synchronized (threadSynchro) {
                        dataFromSocket.add(extractedData);
                        threadSynchro.notify();
                    }
                }
                
                newDataReceived = false;
            }
        }
    }
    
    /**
     * Calling listener callback method - when new data has arrived from socket.
     */
    private class ListenerCaller extends Thread {
        // already consumed data from socket
        private Queue<PacketData> consumedData = new LinkedList<PacketData>();
        
        // indicates, wheather new data are in socket
        private boolean areDataInSocket() {
            if (dataFromSocket.isEmpty()) {
                return false;
            }
            return true;
        }
        
        // consume data from socket and adds them into buffer
        private void consumeDataFromSocket() {
            while (!dataFromSocket.isEmpty()) {
                PacketData packetData = dataFromSocket.poll();
                consumedData.add(packetData);
            }
        }
        
        /** Frees up used resources. */
        private void freeResources() {
            consumedData.clear();
        }
        
        @Override
        public void run() {
            while (true) {
                if (this.isInterrupted()) {
                    logger.info("Client caller thread interrupted");
                    freeResources();
                    return;
                }
                
                // consuming new data from socket
                synchronized (threadSynchro) {
                    while (!areDataInSocket()) {
                        try {
                            threadSynchro.wait();
                        } catch (InterruptedException ex) {
                            logger.warn("Client caller thread interrupted while "
                                    + "waiting on data from socket.");
                            freeResources();
                            return;
                        }
                    }
                    consumeDataFromSocket();
                }
                 
                // remove data from queue and put send it to listener
                while (!consumedData.isEmpty()) {
                    PacketData packetData = consumedData.poll();
                    
                    if ( networkListener == null ) {
                        continue;
                    }
                    
                    boolean isAsync = false;
                    short[] userData = null;
                    
                    try {
                        isAsync = GWETH_DataTransformer.isAsynchronousMessage(packetData.data);
                        if (isAsync) {
                            userData = GWETH_DataTransformer.getDataFromMessage(packetData.data);
                        }
                    } catch (Exception e) {
                        logger.error("Error while getting data from message: " + e.getMessage());
                        continue;
                    }
                    
                    if ( isAsync ) {
                        String networkId = connectionStorage.getNetworkId(packetData.connInfo);
                        if ( networkId != null ) {
                            networkListener.onGetData(new BaseNetworkData(userData, networkId)); 
                        } else {
                            // if no info about network ID is available, set null 
                            logger.warn("No network found for connection");
                            networkListener.onGetData(new BaseNetworkData(userData, null));
                        }
                    }
                }
            }
        }
    }
    
    
    // socket reader thread;
    private Thread socketReader = null;
    
    // listener caller thread
    private Thread listenerCaller = null;
    
    // creates and starts threads
    private void createAndStartThreads() {
        socketReader = new SocketReader();
        socketReader.start();
        
        listenerCaller = new ListenerCaller();
        listenerCaller.start();
    }
    
    // terminates socket reader and client caller threads
    private void terminateThreads() {
        logger.debug("terminateThreads - start:");
        
        // termination signal to socket reader thread
        socketReader.interrupt();
        
        // termination signal to listener caller thread
        listenerCaller.interrupt();
        
        // Waiting for threads to terminate. Cancelling worker threads has higher 
        // priority than main thread interruption. 
        while (socketReader.isAlive() || listenerCaller.isAlive()) {
            try {
                if (socketReader.isAlive()) {
                    socketReader.join();
                }

                if (listenerCaller.isAlive()) {
                    listenerCaller.join();
                }
            } catch (InterruptedException e) {
                // restoring interrupt status
                Thread.currentThread().interrupt();
                logger.warn("Termination - UDP Client Network Layer interrupted");
            }
        } 
        
        logger.info("UDP Client Network Layer stopped.");
        logger.debug("terminateThreads - end");
    }
    
    private static NetworkConnectionStorage checkStorage(NetworkConnectionStorage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Network Connection Storage cannot "
                    + "be less null");
        }
        return storage;
    } 
    
    private static int checkMaxRecvPacketSize(int maxRecvPacketSize) {
        if (maxRecvPacketSize <= 0) {
            throw new IllegalArgumentException("Maximal size of received packet "
                    + "cannot be less then or equal to 0");
        }
        return maxRecvPacketSize;
    }
    
    private static int checkReceptionTimeout(int receptionTimeout) {
        if (receptionTimeout < 0) {
            throw new IllegalArgumentException("Reception timeout cannot be less then 0");
        }
        return receptionTimeout;
    }
    
    
    
    /**
     * Creates new UDP client network layer object.
     * @param connectionStorage storage of network UDP connections
     * @param localHostName local host name, or {@code null} for the loopback address
     * @param localPort local port number
     * @param maxRecvPacketSize maximal size of received packets [in bytes].
     * @param receptionTimeout timeout [in ms] of blocking waiting for reception 
     *                        of packet from the socket. {@code 0} means infinity
     *                        waiting.
     */
    public UDPNetworkLayerMultinet(
            NetworkConnectionStorage connectionStorage,
            String localHostName, 
            int localPort, 
            int maxRecvPacketSize, 
            int receptionTimeout
     ) {
        super(checkStorage(connectionStorage));
        try {
            this.localAddress = InetAddress.getByName(localHostName);
        } catch (UnknownHostException ex) {
            throw new IllegalArgumentException("Hostname not valid: " + ex.getMessage());
        }
        this.localPort = localPort;
        
        this.maxRecvPacketSize = checkMaxRecvPacketSize(maxRecvPacketSize);
        this.receptionTimeout = checkReceptionTimeout(receptionTimeout);
    }
    
    /**
     * Creates new UDP client network layer object. Maximal received packet size
     * is limited to {@code MAX_RECEIVED_PACKET_SIZE} and blocking waiting for 
     * packet reception is set to {@code RECEPTION_TIMEOUT_DEFAULT}.
     * @param connectionStorage storage of network UDP connections 
     * @param localHostName local host name, or {@code null} for the loopback address
     * @param localPort local port number
     */
    public UDPNetworkLayerMultinet(
            NetworkConnectionStorage connectionStorage, 
            String localHostName, 
            int localPort
            ) {
        this(connectionStorage, localHostName, localPort, 
                MAX_RECEIVED_PACKET_SIZE, RECEPTION_TIMEOUT_DEFAULT
        );
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
    public void start() throws NetworkLayerException {
        logger.debug("startReceivingData - start:");
        
        try {
            socket = new DatagramSocket(localPort, localAddress);
            socket.setSoTimeout(receptionTimeout);
        } catch (IOException ex) {
            throw new NetworkLayerException(ex);
        }
        
        // init queue of data comming from socket
        dataFromSocket = new LinkedList<PacketData>();
        
        // creating and starting threads
        createAndStartThreads();
        
        logger.info("Receiving data started");
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
        if (connInfo == null) {
            throw new NetworkLayerException("No connection info for network: "
                    + networkData.getNetworkId());
        }
        
        // bad type of connection info
        if ( !(connInfo instanceof UDPConnectionInfo) ) {
            throw new NetworkLayerException("Bad format of connection info "
                    + "for network: " + networkData.getNetworkId());
        }
        
        UDPConnectionInfo udpInfo = (UDPConnectionInfo) connInfo;
        
        // transforms request's data to protocol format defined by GW
        short[] dataForGW = GWETH_DataTransformer.transformRequestData(networkData.getData());
        
        // conversion data to bytes
        byte[] buf = new byte[dataForGW.length];
        for (int item = 0; item < dataForGW.length; item++) {
            buf[item] = (byte)(dataForGW[item] & 0xFF);
        }
        
        DatagramPacket packet = new DatagramPacket(buf, buf.length, 
                    udpInfo.getAddress(), udpInfo.getPort()
        );
        
        try {
            synchronized(socketSynchro) {
                socket.send(packet);
            }
        } catch (IOException ex) {
            logger.error("Sending data to socket failed: " + ex.getMessage());
            throw new NetworkLayerException(ex);
        }
       
        logger.debug("sendData - end");
    }

    @Override
    public void destroy() {
        logger.debug("destroy - start: ");
        
        unregisterListener();
        terminateThreads();
        socket.close();
        freeResources();
        
        logger.info("Destroyed");
        logger.debug("destroy - end");
    }
}
