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

package com.microrisc.udp.examples;

import com.microrisc.udp.examples.gweth.GWETH_DataTransformer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Writes and reads DPA telegrams to/from the UDP port.
 * 
 * @author Rostislav Spinar
 */
public final class WriteRead {

    private static ReadThread readThread;
    private static DatagramSocket socket;
    
    private final static int RX_BYTES = 128;
    
    private final static int TX_PORT = 55300;
    private final static int RX_PORT = 55000;
    
    // Local IP address
    private final static String IPL = "10.1.10.224";
    // Remote IP address
    private final static String IPR = "10.1.30.62";
    
    // converts short array into byte array
    // all values of the input array must fit into 1 byte 
    private static byte[] toByteArr(short[] arr) {
        byte[] byteArr = new byte[arr.length];
        for ( int item = 0; item < arr.length; item++ ) {
            if ( arr[item] > 0xFF ) {
                throw new IllegalArgumentException("Value to large for converting to byte");
            }
            byteArr[item] = (byte) (arr[item] & 0xFF);
        }
        return byteArr;
    }
    
    // converts short array into hex string and returns it
    private static String shortArrayToHexString(short arr[]) {
        if ( (arr == null) || (arr.length == 0) ) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for ( short item : arr ) {
            sb.append(Integer.toHexString(item) + " ");
        }
        return sb.toString();
    }
    
    // reader
    private static class ReadThread extends Thread {
        // to stop the thread
        private volatile boolean stopFlag;
        
        public ReadThread() {
            this.stopFlag = false;
        }
        
        // extracts data from specified packet and returns it
        private short[] extractDataFromSocket(DatagramPacket packet) {
            byte[] packetData = packet.getData();
            short[] extractedData = new short[packet.getLength()];
            
            for ( int item = 0; item < packet.getLength(); item++ ) {
                extractedData[item] = (short)(packetData[item] & 0xFF);
            }
            return extractedData;
        }

        @Override
        public void run() {
            while ( !stopFlag ) {
                byte[] buffer = new byte[RX_BYTES];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                try {
                    socket.receive(packet);
                } catch ( SocketException e ) {
                    if(stopFlag) {
                        System.out.println("Socket closed.");
                    } else {
                        System.err.println("Socket error: " + e);
                    }
                    continue;
                } catch ( UnknownHostException e ) {
                    System.err.println("Uknown host error: " + e);
                    continue;
                } catch ( IOException ex ) {
                    System.err.println("IO error " + ex);
                    continue;
                } 
                    
                // we are interested only in asynchronous messages, not in GW own answers
                short[] data = extractDataFromSocket(packet);
                System.out.println("ReceivedMsgs from GW = " + shortArrayToHexString(data));                         

                boolean isAsync = GWETH_DataTransformer.isAsynchronousMessage(data);
                if ( isAsync ) {
                    short[] userData = GWETH_DataTransformer.getDataFromMessage(data);
                    System.out.println("ReceivedDataMsg from GW = " + shortArrayToHexString(userData));
                }
            }
        }

        public void kill() {
            stopFlag = true;
        }
    }
    
    
    public static void main(String args[]) {
        int cycles = 10;
        
        if ( args.length > 0 ) {
            try {
                cycles = Integer.parseInt(args[0]);
           } catch ( NumberFormatException e ) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
            
            if ( cycles < 0 ) {
                System.err.println("Number of cycles must be nonnegative.");
                System.exit(1);
            }
        }
        
        // data to write to UDP port - DPA packets
        short[] ledron  = { 0x00, 0x00, 0x06, 0x01, 0xFF, 0xFF };
        
        // transforms request's data to protocol format defined by GW
        short[] transLedron = GWETH_DataTransformer.transformRequestData(ledron);
        
        // getting byte[] representation to fit into DatagramPacket interface
        byte[] ledronBuff = toByteArr(transLedron);
        
        short[] ledroff = { 0x00, 0x00, 0x06, 0x00, 0xFF, 0xFF };
        short[] transLedroff = GWETH_DataTransformer.transformRequestData(ledroff);
        byte[] ledroffBuff = toByteArr(transLedroff);
        
        InetAddress host = null;
        try {
            host = InetAddress.getByName(IPR);
            InetAddress localAddress = InetAddress.getByName(IPL);
            socket = new DatagramSocket (RX_PORT, localAddress);                
            socket.setSoTimeout(45000);
        } catch ( UnknownHostException ex ) {
            System.err.println("Unknown host: " + ex);
            System.exit(1);
        } catch ( SocketException ex ) {
            System.err.println("Socket error: " + ex);
            System.exit(1);
        }
        
        DatagramPacket ledrOnPacket = new DatagramPacket(
            ledronBuff, ledronBuff.length, host, TX_PORT
        );
        
        DatagramPacket ledrOffPacket = new DatagramPacket(
            ledroffBuff, ledroffBuff.length, host, TX_PORT
        );
        
        // thread for reading msg from GW
        readThread = new ReadThread();
        readThread.start();
        
        for ( int cycle = 0; cycle < cycles; cycle++ ) {
            try {
                socket.send(ledrOnPacket);
                Thread.sleep(1000);
                socket.send(ledrOffPacket);
                Thread.sleep(1000);
            } catch ( IOException e ) {
                System.err.println("Error while sending data: " + e);
            } catch ( InterruptedException e ) {
                System.err.println("Sleeping interruped: " + e);
            }
        }
        
        readThread.kill();
        socket.close();
    }
}
