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

package com.microrisc.simply.network.serial.v2;

import com.microrisc.hdlcframing.v2.HDLC_DataTransformer;
import com.microrisc.hdlcframing.v2.HDLC_FormatException;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.NetworkLayerListener;
import com.microrisc.simply.network.AbstractNetworkConnectionInfo;
import com.microrisc.simply.network.AbstractNetworkLayer;
import com.microrisc.simply.network.BaseNetworkData;
import com.microrisc.simply.network.NetworkConnectionStorage;
import com.microrisc.simply.network.NetworkLayerException;
import com.microrisc.simply.network.comport.BaseCOMPortConnectionInfo;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements network layer using {@code SerialPort} object.
 * <p>
 * This is an user of jssc library and uses {@code SerialPort} object to read
 * and write data. All data comming from Serial interface is transformed from
 * HDLC packets and forwarder to user's registered network listener. All data 
 * designated to underlaying network are transformed to HDLC frames and 
 * send via {@code SerialPort.writeBytes} method.
 * 
 * @author Rostislav Spinar
 */

public final class SerialNetworkLayerJssc extends AbstractNetworkLayer {
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SerialNetworkLayerJssc.class);

    /**
     * Registered network listener.
     */
    private NetworkLayerListener networkListener = null;

    /**
     * Connection info.
     */
    private BaseCOMPortConnectionInfo connectionInfo = null;

    /**
     * listener caller thread
     */
    private Thread listenerCaller = null;
    
    /**
     * Serial interface
     */
    private SerialPort serialPort = null;

    /**
     * Serial-port name for connection.
     */
    private String portName = null;
    
    /**
     * Serial-baudrate for connection.
     */
    private int serialBaudrate = 0;

    /**
     * Data received from Serial.
     */
    private Queue<byte[]> dataFromSerial = null;
    
    /**
     * Synchronization between socket reader thread and listener caller thread.
     */
    private final Object threadsSynchro = new Object();
    
    /**
     * Reading data from Serial.
     */
    private class SerialReader implements SerialPortEventListener {

        // chunk delimiter
        private static final byte CHUNK_SEPAR = 0x7E; 
        
        // remainder of last data read, which does not comprise a complete chunk
        byte[] dataRemainder =  null;
               
        // reads data chunk from specified position from specified array
        private byte[] readDataChunk(int startPos, byte[] arr) {
            if ( arr[startPos] != CHUNK_SEPAR ) {
                throw new IllegalStateException(
                        "Bad format of input data. It must begin with " + CHUNK_SEPAR + " byte."
                );
            }
            
            ByteArrayOutputStream dataChunk = new ByteArrayOutputStream();
            for ( int pos =  startPos; pos < arr.length; pos++ ) {
                dataChunk.write(arr[pos] & 0xFF);
                if ( arr[pos] == CHUNK_SEPAR ) {
                    if ( pos != startPos ) {
                        break;
                    }
                }
            }
            return dataChunk.toByteArray();
        }
        
        // writes specified data chunk into global storage
        private void writeDataChunkIntoGlobalStorage(byte[] dataChunk) {
            synchronized ( threadsSynchro ) {
                dataFromSerial.add(dataChunk);
            }
        }
        
        public void serialEvent(SerialPortEvent event) {
            
            // if the event is not reception of bytes, nothing to do
            if ( !event.isRXCHAR() ) {
                return;
            }
            
            // if no data has been received, nothing to do
            int dataLen = event.getEventValue();
            if ( dataLen <= 0 ) {
                return;
            }
        
            // local data buffer to store data for this call only
            byte buffer[] = null;
            try {
                buffer = serialPort.readBytes();
            } catch ( SerialPortException ex ) {
                System.out.println("Reading data failed: " + ex);
                return;
            }
            
            byte[] allDataArr = null;
            
            // if there is data remainder, it is needed to add it for processing
            if ( dataRemainder != null ) {
                allDataArr = new byte[ dataRemainder.length + buffer.length];
                System.arraycopy(dataRemainder, 0, allDataArr, 0, dataRemainder.length);
                System.arraycopy(buffer, 0, allDataArr, dataRemainder.length, buffer.length);
                dataRemainder = null;
            } else {
                allDataArr = new byte[buffer.length];
                System.arraycopy(buffer, 0, allDataArr, 0, buffer.length);
            }
            
            // list of data chunks - each chunk is embraced by CHUNK_SEPAR bytes
            // with the exception of the last one - it can be without the last CHUNK_SEPAR
            List<byte[]> dataChunks = new LinkedList<>();
            
            int chunkPos = 0;
            while ( chunkPos < (allDataArr.length-1) ) {
                byte[] dataChunk = readDataChunk(chunkPos, allDataArr);
                dataChunks.add(dataChunk);
                chunkPos += dataChunk.length; 
            }
            
            // write all data chunks into the shared storage, with the exception
            // of the last one, if it is incomplete
            boolean lastIsComplete = false;
            for ( int chunkId = 0; chunkId < dataChunks.size(); chunkId++ ) {
                byte[] dataChunk = dataChunks.get(chunkId);
                
                if ( chunkId != (dataChunks.size()-1) ) {
                    writeDataChunkIntoGlobalStorage(dataChunk);
                } else {
                    if ( dataChunk[dataChunk.length-1] == CHUNK_SEPAR ) {
                        writeDataChunkIntoGlobalStorage(dataChunk);
                        lastIsComplete = true;
                    }
                }
            }
            
            if ( lastIsComplete ) {
                logger.info("New data from serial interface: {}", dataFromSerial.toArray());
                
                synchronized ( threadsSynchro ) {
                    dataChunks.clear();
                    threadsSynchro.notify();
                }
            } else {
                dataRemainder = dataChunks.get(dataChunks.size()-1);
                dataChunks.clear();
            }
        }
    }

    /**
     * Calling listener callback method - when new data has arrived from socket.
     */
    private class ListenerCaller extends Thread {

        // already consumed data from Serial
        private Queue<byte[]> consumedData = new LinkedList<byte[]>();

        // indicates, wheather new data are from Serial
        private boolean areDataFromSerial() {
            return !dataFromSerial.isEmpty();
        }

        // consume data from serial and adds them into buffer
        private void consumeDataFromSerial() {
            while ( !dataFromSerial.isEmpty() ) {
                byte[] packetData = dataFromSerial.poll();
                consumedData.add(packetData);
            }
        }

        /**
         * Frees up used resources.
         */
        private void freeResources() {
            consumedData.clear();
        }
        
        /** 
         * Converts specified byte array to its short representations.
         * All negative values in the byte array will be converted to its 
         * positive counterparts.
         */
        private short[] toShortArr(byte[] byteArr) {
            short[] shortArr = new short[ byteArr.length ];
            for ( int i = 0; i < byteArr.length; i++ ) {
                shortArr[i] = (short) (byteArr[i] & 0xff);
            } 
            return shortArr;
        }
        
        @Override
        public void run() {
            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("Serial caller thread interrupted");
                    freeResources();
                    return;
                }

                // consuming new data from Serial
                synchronized ( threadsSynchro ) {
                    while ( !areDataFromSerial() ) {
                        try {
                            threadsSynchro.wait();
                        } catch (InterruptedException ex) {
                            logger.warn("Serial caller thread interrupted while "
                                    + "waiting on data from Serial.");
                            freeResources();
                            return;
                        }
                    }
                    consumeDataFromSerial();
                }

                // remove data from queue and put send it to listener
                while ( !consumedData.isEmpty() ) {
                    short[] packetData = toShortArr(consumedData.poll());
                    logger.info("Converted data from Serial: {}", packetData);
                    
                    short[] userData = null;
                    try {
                        userData = HDLC_DataTransformer.getDataFromFrame(packetData);
                    } catch ( HDLC_FormatException e ) {
                        logger.error("Error while reading data from HDLC format: ", e);
                        continue;
                    }

                    if ( networkListener != null ) {
                        String networkId = connectionStorage.getNetworkId(connectionInfo);
                        networkListener.onGetData( new BaseNetworkData(userData, networkId) );
                    }
                }
            }
        }
    }

    // creates and starts threads
    private void createAndStartThreads( ) {
        listenerCaller = new ListenerCaller();
        listenerCaller.start();
    }

    // terminates Serial reader and client caller threads
    private void terminateThreads() {
        logger.debug("terminateThreads - start:");

        // termination signal to listener caller thread
        listenerCaller.interrupt();

        // Waiting for threads to terminate. Cancelling worker threads has higher 
        // priority than main thread interruption. 
        while ( listenerCaller.isAlive() ) {
            try {
                if (listenerCaller.isAlive()) {
                    listenerCaller.join();
                }
            } catch (InterruptedException e) {
                // restoring interrupt status
                Thread.currentThread().interrupt();
                logger.warn("Termination - Serial Network Layer interrupted");
            }
        }

        logger.info("Serial Network Layer stopped.");
        logger.debug("terminateThreads - end");
    }

    private static NetworkConnectionStorage checkStorage(NetworkConnectionStorage storage) {
        if ( storage == null ) {
            throw new IllegalArgumentException(
                    "Network Connection Storage cannot be  null"
            );
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
    
    private static int checkSerialBaudrate(int serialBaudrate) {
        if ( serialBaudrate <= 0 ) {
            throw new IllegalArgumentException("Baudrate must be positibe number");
        }
        return serialBaudrate;
    }

    /**
     * Creates new Serial network layer object.
     *
     * @param connectionStorage storage of network Serial-port connections
     * @param portName Serial-port name for communication
     * @param serialBaudrate baud rate
     */
    public SerialNetworkLayerJssc(
            NetworkConnectionStorage connectionStorage, String portName, 
            int serialBaudrate
    ) {
        super(checkStorage(connectionStorage));
        this.portName = checkPortName(portName);
        this.serialBaudrate = checkSerialBaudrate(serialBaudrate);
        this.connectionInfo = new BaseCOMPortConnectionInfo(portName);
    }

    public void start() throws NetworkLayerException {
        logger.debug("startReceivingData - start:");
        
        serialPort = new SerialPort( portName );
        
        try {
            serialPort.openPort();
            serialPort.setParams(
                            serialBaudrate, 
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE
            );
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
            serialPort.setEventsMask(mask);
            serialPort.addEventListener( new SerialReader() );
        } 
        catch (SerialPortException ex) {
            throw new NetworkLayerException(ex);
        }

        // init queue of data comming from Serial
        //dataFromSerial = new LinkedList<byte[]>();
        dataFromSerial = new ConcurrentLinkedDeque<>();

        // creating and starting threads
        createAndStartThreads( );

        logger.info("Receiving data started");
        logger.debug("startReceivingData - end");
    }

    public void registerListener(NetworkLayerListener listener) {
        this.networkListener = listener;
        logger.info("Listener registered");
    }

    public void unregisterListener() {
        networkListener = null;
        logger.info("Listener unregistered");
    }

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
        if ( !(this.connectionInfo.equals(connInfo))) {
            throw new NetworkLayerException("Connection info mismatch."
                    + "Incomming: " + connInfo
                    + ", required: " + this.connectionInfo
            );
        }

        // transforms request's data to Serial protocol format
        short[] dataForSerial = HDLC_DataTransformer.transformToHLDCFormat(networkData.getData());
        
        byte[] buffer = new byte[dataForSerial.length];
        for ( int index = 0; index < dataForSerial.length; index++ ) {
            buffer[index] = (byte) dataForSerial[index];
        }
        
        try {  
            logger.info("Data will be sent to Serial...");
            serialPort.writeBytes(buffer);
        }
        catch ( SerialPortException ex ) {
            throw new NetworkLayerException(ex);
        }
    }

    public void destroy() {
        logger.debug("destroy - start: ");
        
        unregisterListener();
        terminateThreads();
        dataFromSerial.clear();
        
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            logger.error("Error while closing SerialPort", ex);
        }
        serialPort = null;
        
        logger.info("Destroyed");
        logger.debug("destroy - end");
    }
}
