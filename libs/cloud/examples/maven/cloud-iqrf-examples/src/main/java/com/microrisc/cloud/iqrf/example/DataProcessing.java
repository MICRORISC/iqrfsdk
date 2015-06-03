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

import com.microrisc.cloud.iqrf.*;
import com.microrisc.cloud.iqrf.message.CloudResponse;
import com.microrisc.cloud.iqrf.message.TimeSettings;
import java.util.Arrays;

/**
 * Example how processing a big count of data effectively with cloud library.
 *
 * @author Martin Strouhal
 */
public class DataProcessing {

    //reference for cloud control
    private Cloud cloud;

    public static void main(String[] args) {
        //instance of main
        DataProcessing processor = new DataProcessing();
        // create reference for cloud control
        processor.cloud = new SimpleCloud();

        //register Gw
        processor.registerGw();

        int poolingTime = 2000;//in miliseconds

        //repeating of sending requests for new data
        while (true) {
            //sending download request
            CloudResponse downloadResponse = processor.cloud.dataDownload();
            

            //check if data download was succesful
            int dataCount = 0;
            switch (downloadResponse.getErrorType()) {
                case BAD_INTERNET_CONNECTION:
                    System.out.println("Error: bad internet connection. Check if PC connected to network.");
                    break;
                case INACCESSIBLE_CRYPT_KEY:
                    System.out.println("From config file couldn't load crypting key.");
                    break;
                case GENERAL_CLOUD_ERROR:
                    System.out.println("Some error in cloud library has occured.");
                    break;
                case INVALID_CRC:
                    System.out.println("Invalid CRC");
                    break;
                case WITHOUT_ERROR:
                    System.out.println("data: " + Arrays.toString(downloadResponse.getData()));
                    //detect how much packets is on cloud
                    dataCount = downloadResponse.getParameter(CloudResponse.PARAMETER_COUNT_NON_PICKED_PACKETS);
            }           

            //sleep if isn't any packet on cloud
            if (dataCount == 0) {
                try {
                    Thread.sleep(poolingTime);
                } catch (InterruptedException ex) {
                    System.out.println("Some error has been occured. " + ex);
                }
            }
        }
    }

    private void registerGw() {
        //user AES key for crypting communication betwen user and gateway
        short[] userAESKey = new short[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10};
        //create setting of time on the user gateway
        TimeSettings settings = new TimeSettings(TimeSettings.TimeZone.UTC_plus_1, true);

        //send register packet to cloud
        CloudResponse response = cloud.registerGw(userAESKey, settings);

        //print info about register processing
        System.out.println(response.getProcessingInfo());
    }

}
