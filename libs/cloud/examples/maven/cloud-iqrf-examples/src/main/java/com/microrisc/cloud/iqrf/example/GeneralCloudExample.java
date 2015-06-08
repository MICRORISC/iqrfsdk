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

import com.microrisc.cloud.iqrf.Cloud;
import com.microrisc.cloud.iqrf.SimpleCloud;
import com.microrisc.cloud.iqrf.message.CloudResponse;
import com.microrisc.cloud.iqrf.message.TimeSettings;
import java.util.Arrays;

/**
 * This class contains how register Gateway on IQRF cloud server and example of
 * writing and reading data from IQRF cloud server.
 *
 * @author Martin Strouhal
 */
public class GeneralCloudExample {

    private static Cloud cloud;

    public static void main(String[] args) {
        // create reference for cloud control
        cloud = new SimpleCloud();

        //example with registering Gw
        registerGw();

        //example with uploading data
        uploadData();

        //example with downloading data
        downloadData();
    }

    private static void registerGw() {
        //user AES key for crypting communication
        short[] userAESKey = new short[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10};
        //create setting of time on the user gateway
        TimeSettings settings = new TimeSettings(TimeSettings.TimeZone.UTC_plus_1, true);

        //send register packet to cloud
        CloudResponse response = cloud.registerGw(userAESKey, settings);

        //print info about register processing
        System.out.println(response.getProcessingInfo());       
    }

    private static void uploadData() {
        //prepare data which will be written on cloud server
        byte[] bytesData = "Data to upload on IQRF cloud.".getBytes();
        short[] dataToUpload = new short[bytesData.length];
        System.arraycopy(bytesData, 0, dataToUpload, 0, bytesData.length);

        //send data on cloud server
        CloudResponse uploadResponse = cloud.dataUpload(dataToUpload);

        //print result
        if (uploadResponse.hasError()) {
            System.out.println(uploadResponse.getProcessingInfo());
        }
    }

    private static void downloadData() {
        //download data from server
        CloudResponse downloadResponse = cloud.dataDownload();

        //error detection
        if (downloadResponse.hasError()) {
            //print error
            System.out.println(downloadResponse.getProcessingInfo());
        } else {
            //print data
            System.out.println(Arrays.toString(downloadResponse.getData()));
        }
    }
}