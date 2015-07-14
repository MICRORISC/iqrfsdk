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

package com.microrisc.cdc.examples;

import com.microrisc.cdc.*;
import java.util.Arrays;

/**
 * Example demonstrates sending requests for actual temperature on node's sensor 
 * and asynchronous reception of actual temperature's values.  
 */
public class WriteRead implements J_AsyncMsgListener {
    
    @Override
    public void onGetMessage(short[] data) {
        // positions of fields
        final int INT_VALUE_POS = 8;
        final int FULL_VALUE_POS = 9;
        final int FULL_VALUE_LENGTH = 2;
        
        // for DPA request read temperature
        final short[] CONFIRMATION_HEADER = { 0x01, 0x00, 0x0A, 0x00 };
        final short[] RESPONSE_HEADER = { 0x01, 0x00, 0x0A, 0x80 };
        
        // no data
        if (data == null || data.length == 0) {
            System.out.println("No data received\n");
            return;
        }
        
        // data length
        System.out.println("data length: " + data.length);
        
        // response flag
        boolean responseReceived = false;
        
        // header of data
        short[] dataHeader = Arrays.copyOfRange(data, 0, 4);
        
        if (Arrays.equals(dataHeader, CONFIRMATION_HEADER)) {
            System.out.print("confirmation received: ");
        } else if (Arrays.equals(dataHeader, RESPONSE_HEADER)) {
            System.out.print("response received: ");
            responseReceived = true;
        } else {
            System.out.print("unknown type of message received: ");
        }
        
        // display raw data
        for (short sh : data) {
            System.out.print(Integer.toHexString(sh) + " ");
        }
        System.out.println();
        
        // parse data to get temperature
        if (responseReceived) {
            short value = data[INT_VALUE_POS];
            
            short[] fullTemperatureValue = new short[FULL_VALUE_LENGTH];
            System.arraycopy(data, FULL_VALUE_POS, fullTemperatureValue, 0, FULL_VALUE_LENGTH);
            byte fractialPart = (byte)(fullTemperatureValue[0] & 0x0F);
        
            System.out.println("Temperature = " + value + "." + fractialPart + " C");
        }
        
        System.out.println();
    }
    
    
    static public void main(String[] args) {
        J_CDCImpl myCDC = null;
        try {
            // creating CDC object, which will communicate via /dev/ttyACM0
            //myCDC = new J_CDCImpl("/dev/ttyACM0");
            myCDC = new J_CDCImpl("COM4");
            
            // communication testing
            if (myCDC.test()) {
                System.out.println("Test OK");
            } else {
                System.out.println("Test FAILED");
                myCDC.destroy();
                return;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            if (myCDC != null) {
                myCDC.destroy();
            }
            return;
        }
        
        // listener of asynchronous messages
        WriteRead example = new WriteRead();
        
        // register to receiving asynchronous messages
        myCDC.registerAsyncListener(example);
            
        // Send DPA read temperature request
        // NAdr=0x01 0x00 PNum=0x0A PCmd=0x00
        short[] temperatureRequest = { 0x01, 0x00, 0x0A, 0x00, 0xFF, 0xFF };
        
        for (int sendCounter = 0; sendCounter < 10; sendCounter++) {
            try {
                // sending read temperature request and checking response of the device
                J_DSResponse response = myCDC.sendData(temperatureRequest);
                if (response != J_DSResponse.OK) {
                    // processing of bad response ...
                    System.out.println("Response not OK: " + response);
                }
            } catch (J_CDCSendException ex) {
                System.out.println("Send error occurred: " + ex.toString());
                // send exception processing...
            } catch (J_CDCReceiveException ex) {
                System.out.println("Receive error occurred: " + ex.toString());
                // receive exception processing...
            } catch (Exception ex) {
                System.out.println("Other error occurred: " + ex.toString());
                // other exception processing...
            }
            
            // if reception is stopped, is not further possible to send and 
            // to receive any next messages
            if (myCDC.isReceptionStopped()) {
                myCDC.destroy();
                return;
            }
            
            // sending another message and waiting for the answer
            // we should wait for a while - to give USB device time to get ready 
            // to succesfully process next message
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Thread interrupted\n");
                break;
            }
        }
        
        // terminate library and free up used resources
        myCDC.destroy();
    }
}
