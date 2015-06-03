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
package com.microrisc.cloud.iqrf.example;

import com.microrisc.cdc.J_AsyncMsgListener;
import com.microrisc.cdc.J_CDCImpl;
import com.microrisc.cdc.J_CDCReceiveException;
import com.microrisc.cdc.J_CDCSendException;
import com.microrisc.cdc.J_DSResponse;
import com.microrisc.cloud.iqrf.Cloud;
import com.microrisc.cloud.iqrf.SimpleCloud;
import com.microrisc.cloud.iqrf.message.CloudResponse;
import com.microrisc.cloud.iqrf.utils.CloudException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This example shows how send commands from IQRF Cloud into IQRF network
 * directly. Example using generic detection of commands and their types.
 *
 * @author Martin Strouhal
 */
public class GenericCommunication implements J_AsyncMsgListener {

    /** Logger */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Instance of {@link Cloud} by which are events sended to Cloud server. */
    private final Cloud cloud;

    /** Instance of used CDC. */
    private final J_CDCImpl myCDC;

    //flags about packet
    private short addr1, addr2, periph, cmdVal;

    // response flag
    private boolean responseReceived;
    
    //pooling time
    private final int POOLING_TIME = 10000;

    @Override
    public void onGetMessage(short[] data) {
        // for DPA request read temperature
        final short[] CONFIRMATION_HEADER = {addr1, addr2, periph, cmdVal, 0xFF, 0xFF};
        final short[] RESPONSE_HEADER = {addr1, addr2, periph, (short) (cmdVal + 0x80), 0x00, 0x00};

        //data null?
        if (data == null || data.length == 0) {
            logger.info("No data received\n");
            return;
        }

        // header of data
        short[] dataHeader = Arrays.copyOfRange(data, 0, 6);

        if (Arrays.equals(dataHeader, CONFIRMATION_HEADER)) {
            System.out.println("confirmation received: "+ Arrays.toString(data));
        } else if (Arrays.equals(dataHeader, RESPONSE_HEADER)) {
            System.out.println("response received: " + Arrays.toString(data));
            responseReceived = true;
        } else {
            System.out.println("unknown type of message received: " + Arrays.toString(data));
            byte[] asciiByte = "Received unknown IQRF packet".getBytes();
            short[] asciiData = new short[asciiByte.length];
            for (int i = 0; i < asciiByte.length; i++) {
                asciiData[i] = asciiByte[i];
            }
            cloud.dataUpload(asciiData);
            cloud.dataUpload(data);
        }        

        if (responseReceived) {
            //send data to cloud
            //cloud.dataUpload(data);

            //send data to cloud in ASCII
            byte[] asciiByte = Arrays.toString(data).getBytes();
            short[] asciiData = new short[asciiByte.length];
            for (int i = 0; i < asciiByte.length; i++) {
                asciiData[i] = asciiByte[i];
            }
            cloud.dataUpload(asciiData);
        }
    }

    public GenericCommunication(String comPortName) throws Exception {
        this.cloud = new SimpleCloud();

        // creating CDC object, which will communicate via /dev/ttyACM0
        myCDC = new J_CDCImpl(comPortName);
        if (myCDC == null) {
            System.out.println("CDC cannot be created.");
            throw new Exception("CDC was not created successfuly.");
        }

        // communication testing
        if (myCDC.test()) {
            System.out.println("Test OK");
        } else {
            System.out.println("Test FAILED");
            myCDC.destroy();
            throw new Exception("Test failed. CDC was destroyed.");
        }
    }

    public static void main(String[] args) {
        // creates instance with utils for communicating and listener of asynchronous messages
        GenericCommunication example = null;
        try {
            example = new GenericCommunication("COM4");
        } catch (Exception ex) {
            System.out.println(ex);
            System.exit(1);
        }

        // register to receiving asynchronous messages
        example.myCDC.registerAsyncListener(example);       

        //repeatly read request from cloud and their processing by CDC
        while (true) {
            example.responseReceived = false;
            CloudResponse cloudResponse = example.cloud.dataDownload();
            if (cloudResponse.getErrorType() != CloudException.CloudError.WITHOUT_ERROR) {
                System.out.println("Received response from cloud with error: " + cloudResponse.getErrorType());
                example.sleep();
                continue;
            }
            if (cloudResponse.getData().length == 0) {
                System.out.println("Received empty data.");
                example.sleep();
                continue;
            }

            //read request from cloud
            short[] request = cloudResponse.getData();
            if (request.length < 4) {
                System.out.println("Request cannot be smaller than 4.");
                example.sendErrorToCloud("Request cannot be smaller than 4.");
            }
            example.addr1 = request[0];
            example.addr2 = request[1];
            example.periph = request[2];
            example.cmdVal = request[3];

            boolean succesfulCDC = example.sendCDC(request);
            //if sending of packet to IQRF network was unsuccessful, continue with next packet
            if (!succesfulCDC) {
                System.out.println("Send packet to IQRF via CDC was unsuccessful. Next packet will be processed.");
                continue;
            }

            // we should wait for a while - to give USB device time to get ready 
            // to succesfully process next message
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Thread interrupted\n");
                break;
            }

            //wait some time, until CDC proccess response or send timeout left packet
            int FOR_MAX = 10;
            for (int i = 0; i < FOR_MAX; i++) {
                if (!example.responseReceived) {
                    if (i == FOR_MAX) {
                        //send error response
                        System.out.println("Timeout left.");
                        example.sendErrorToCloud("Timeout left.");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        example.logger.warn("Interrupted exception.");
                        break;
                    }
                } else {
                    break;
                }
            }

            //sleep if isn't any packet on cloud
            if (cloudResponse.getParameter(CloudResponse.PARAMETER_COUNT_NON_PICKED_PACKETS) == 0) {
                example.sleep();
            }

        }
    }

    /* Sends data to IQRF network via CDC and returns true if it's successful, 
     otherwise returns false and send error message to cloud. */
    public boolean sendCDC(short[] data) {
        boolean isSuccessful = true;
        try {
            // sending request and checking response of the device
            J_DSResponse response = myCDC.sendData(data);
            if (response != J_DSResponse.OK) {
                // processing of bad response ...
                System.out.println("Response not OK: " + response);
                isSuccessful = false;
            }
        } catch (J_CDCSendException ex) {
            System.out.println("Send error occurred: " + ex.toString());
            isSuccessful = false;
            // send exception processing...
        } catch (J_CDCReceiveException ex) {
            System.out.println("Receive error occurred: " + ex.toString());
            isSuccessful = false;
            // receive exception processing...
        } catch (Exception ex) {
            System.out.println("Other error occurred: " + ex.toString());
            isSuccessful = false;
            // other exception processing...
        }

        // if reception is stopped, is not further possible to send and 
        // to receive any next messages
        if (myCDC.isReceptionStopped()) {
            myCDC.destroy();
            System.exit(1);
        }

        return isSuccessful;
    }

    public void sleep() {
        try {
            Thread.sleep(POOLING_TIME);
        } catch (InterruptedException ex) {
            System.out.println("Some error has been occured. " + ex);
        }
    }

    public void sendErrorToCloud(String error) {
        byte[] errorByte = error.getBytes();
        short[] errorData = new short[errorByte.length];
        for (int i = 0; i < errorByte.length; i++) {
            errorData[i] = errorByte[i];
        }
        cloud.dataUpload(errorData);
    }
}
