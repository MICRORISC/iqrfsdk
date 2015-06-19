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

package com.microrisc.simply.iqrf.dpa.v210.examples.user_peripherals.myadc;

import com.microrisc.simply.*;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v210.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v210.examples.user_peripherals.myadc.def.MyADC;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Main class for example of ADC device.
 * 
 * @author Martin Strouhal
 */
public class MyADCExample {

    // reference to Simply
    private static Simply simply = null;

    private static Map<String, MyADC> getADCDevices(Network network, String[] nodeIds) {
        Map<String, MyADC> MyADCMap = new LinkedHashMap<>();
        for ( String nodeId : nodeIds ) {
            Node node = network.getNode(nodeId);
            MyADC adcDevice = node.getDeviceObject(MyADC.class);
            
            if ( adcDevice == null ) {
                System.out.println("ADC is not on node " + nodeId + ", return.");
                return null;
            }
            MyADCMap.put(nodeId, adcDevice);
        }
        return MyADCMap;
    }

    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if (simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }
    
    public static void main(String[] args) {
        // get simply
        try {
            simply = DPA_SimplyFactory.getSimply(
                "config" + File.separator + "Simply.properties"
            );
        } catch (SimplyException ex) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }

        // get network "1"
        Network network1 = simply.getNetwork("1", Network.class);
        if ( network1 == null ) {
            printMessageAndExit("Network 1 doesn't exist");
        }

        // get ADC from all nodes
        Map<String, MyADC> adcDevices = getADCDevices(network1, new String[]{"1"});
        if ( adcDevices == null ) {
            printMessageAndExit("Error when ADC was getting from node.");
        }

        // get ADC "1"
        MyADC adc1 = adcDevices.get("1");
        
        // get result
        int result = adc1.get();
        
        if ( result == Integer.MAX_VALUE ) {
            CallRequestProcessingState procState = adc1.getCallRequestProcessingStateOfLastCall();
            if (procState == CallRequestProcessingState.ERROR) {
                CallRequestProcessingError error = adc1.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Getting ADC value failed: " + error);
            } else {
                printMessageAndExit("Getting ADC value hasn't been processed yet: " + procState);
            }
        } else {
            System.out.println("ADC value is " + result);
        }

        simply.destroy();
    }
}
