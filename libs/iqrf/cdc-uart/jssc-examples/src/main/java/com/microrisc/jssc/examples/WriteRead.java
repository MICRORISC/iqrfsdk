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

package com.microrisc.jssc.examples;

import com.microrisc.hdlcframing.v2.HDLC_DataTransformer;
import com.microrisc.hdlcframing.v2.HDLC_FormatException;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Writing some data to serial port and reading echo.
 * 
 * @author Rostislav Spinar
 */
public final class WriteRead {
    
    private static SerialPort serialPort = null;
    private static final Object synchroSerialPort = new Object();
    
    // contains data - in the form of packets - incomming from UART 
    private static Queue<byte[]> dataFromUART = new ConcurrentLinkedDeque<>();
    private static final Object synchroDataFromUART = new Object();
    
    // converts specified short array into corresponding int array
    private static int[] toIntArray(short[] shArr) {
        int[] intArr = new int[shArr.length];
        for ( int i = 0; i < shArr.length; i++ ) {
            intArr[i] = shArr[i];
        }
        return intArr;
    }
    
    
    // prints data from UART into standard output
    private static void printDataFromUART() {
        synchronized ( synchroDataFromUART ) {
            if ( dataFromUART.size() == 0 ) {
                System.out.println("No data in input buffer.");
            }
            
            for ( byte[] packet : dataFromUART ) {
                short[] packetData = new short[packet.length];
                for ( int i = 0; i < packet.length; i++ ) {
                    packetData[i] = (short)(packet[i] & 0xFF);
                }
                
                short[] userData = null;
                try {
                    userData = HDLC_DataTransformer.getDataFromFrame(packetData);
                } catch ( HDLC_FormatException e ) {
                    System.out.println("Error while reading data from HDLC format: " + e);
                    return;
                }
                
                System.out.print("Packet: ");
                for ( short dataItem : userData ) {
                    System.out.print(Integer.toHexString(dataItem).toUpperCase() + " ");
                }
                System.out.println();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        serialPort = new SerialPort("COM5");
        if ( serialPort == null ) {
            throw new IllegalStateException("Serial port inicialization failed.");
        }
        
        try {
            // open port
            serialPort.openPort();
            
            // set params
            serialPort.setParams(
                    SerialPort.BAUDRATE_19200, 
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            );
            
            // prepare mask
            serialPort.setEventsMask( SerialPort.MASK_RXCHAR );
            
            // add SerialPortEventListener
            serialPort.addEventListener( new SerialPortReader() );
            
            // data to write to serial port - DPA packets
            short[] ledron  = { 0x01, 0x00, 0x06, 0x01, 0xFF, 0xFF };
            short[] ledroff = { 0x01, 0x00, 0x06, 0x00, 0xFF, 0xFF };
            
            while ( true ) { 
                // transforms request's data to Serial protocol format
                short[] dataForUART = HDLC_DataTransformer.transformToHLDCFormat(ledron);
                synchronized ( synchroSerialPort ) {
                    serialPort.writeIntArray(toIntArray(dataForUART));
                }
                
                // waiting for data from UART
                Thread.sleep(1000);
                
                System.out.println("Response on LED on: ");
                printDataFromUART();
                System.out.println();
                
                dataFromUART.clear();
                
                // transforms request's data to Serial protocol format
                dataForUART = HDLC_DataTransformer.transformToHLDCFormat(ledroff);
                synchronized ( synchroSerialPort ) {
                    serialPort.writeIntArray(toIntArray(dataForUART));
                }
                
                // waiting for data from UART
                Thread.sleep(1000);
                
                System.out.println("Response on LED off: ");
                printDataFromUART();
                System.out.println();
                
                dataFromUART.clear();
            }
        } catch ( SerialPortException ex ) {
            System.out.println(ex);
        }
    }
    
    /*
     * In this class must implement the method serialEvent, through it we learn about 
     * events that happened to our port. But we will not report on all events but only 
     * those that we put in the mask. In this case the arrival of the data and change the 
     * status lines CTS and DSR
     */
    private static class SerialPortReader implements SerialPortEventListener {
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
            synchronized ( synchroDataFromUART ) {
                dataFromUART.add(dataChunk);
            }
        }
        
        
        @Override
        public void serialEvent(SerialPortEvent event) {
            // if the event is not reception of bytes, nothing to do
            if ( !event.isRXCHAR() ) {
                return;
            }
            
            // if no data has been received, nothing to do
            if ( event.getEventValue() <= 0 ) {
                System.out.println("No data received.");
                return;
            }
            
            // local data buffer to store data for this call only
            byte buffer[] = null;
            try {
                synchronized ( synchroSerialPort ) {
                    buffer = serialPort.readBytes();    
                }
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
                dataChunks.clear();
            } else {
                dataRemainder = dataChunks.get(dataChunks.size()-1);
                dataChunks.clear();
            }
        }
    }
}
