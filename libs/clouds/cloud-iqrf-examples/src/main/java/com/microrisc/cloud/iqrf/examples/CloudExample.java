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
package com.microrisc.cloud.iqrf.examples;

import com.microrisc.cloud.iqrf.Cloud;
import com.microrisc.cloud.iqrf.SimpleCloud;
import com.microrisc.cloud.iqrf.message.CloudResponse;
import java.util.Arrays;

/**
 * This class shows how to register Gateway on IQRF cloud server and example of
 * writing and reading data from IQRF cloud server.
 *
 * @author Martin Strouhal
 */
public class CloudExample {

    private static Cloud cloud;

    public static void main(String[] args) {
        // creates reference for cloud control
        cloud = new SimpleCloud();

        // example with registering Gw
        registerGw();

        // example with uploading data
        uploadData();

                         
        int poolingTime = 2000;// in miliseconds

        // repeating sending of requests for new data
        while (true) {
            // sending download request
            CloudResponse downloadResponse = cloud.dataDownload();
            

            // checking if data download was succesful
            int dataCount = 0;
            switch (downloadResponse.getErrorType()) {
                case BAD_INTERNET_CONNECTION:
                    System.out.println("Error: bad internet connection. Check if PC connected to network.");
                    break;
                case INACCESSIBLE_CONFIG_DATA:
                    System.out.println("From config file couldn't load any data.");
                    break;
                case GENERAL_CLOUD_ERROR:
                    System.out.println("Some error in cloud library has occured.");
                    break;
                case INVALID_CRC:
                    System.out.println("Invalid CRC");
                    break;
                case WITHOUT_ERROR:
                    //printing data
                    System.out.println("data: " + Arrays.toString(downloadResponse.getData()));
                    // detecting how much packets is remaining on cloud
                    dataCount = downloadResponse.getParameter(CloudResponse.PARAMETER_COUNT_NON_PICKED_PACKETS);
            }           

            // sleeping if isn't any packet on cloud
            if (dataCount == 0) {
                try {
                    Thread.sleep(poolingTime);
                } catch (InterruptedException ex) {
                    System.out.println("Some error has been occured. " + ex);
                }
            }               
        }               
    }

    private static void registerGw() {
        // sending register packet to cloud
        CloudResponse response = cloud.registerGw();

        // printing info about register processing
        System.out.println(response.getProcessingInfo());       
    }

    private static void uploadData() {
        // preparing data which will be written on cloud server
        byte[] bytesData = "Data to upload on IQRF cloud.".getBytes();
        short[] dataToUpload = new short[bytesData.length];
        for (int i = 0; i < bytesData.length; i++) {
            dataToUpload[i] = bytesData[i];
        }

        // sending data on cloud server
        CloudResponse uploadResponse = cloud.dataUpload(dataToUpload);

        // printing result of upload operation
        if (uploadResponse.hasError()) {
            System.out.println(uploadResponse.getProcessingInfo());
        }
    }
}
