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
package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.user_map.myadc;

import com.microrisc.simply.*;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.user_map.myadc.def.MyPhotoResistor;
import com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.user_map.myadc.def.MyPotentiometer;
import java.io.File;

/**
 * Main class for example of ADC devices.
 * <p>
 * @author Martin Strouhal
 */
public class MyADCExample {

    // reference to Simply
    private static Simply simply = null;

    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if (simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }

    public static void main(String[] args) {
        // getting simply
        try {
            simply = DPA_SimplyFactory.getSimply(
                    "config" + File.separator + "Simply.properties");
        } catch (SimplyException ex) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }

        // getting network "1"
        Network network1 = simply.getNetwork("1", Network.class);
        if (network1 == null) {
            printMessageAndExit("Network 1 doesn't exist");
        }
        
        // getting node 1
        Node node = network1.getNode("1");
        if (node == null) {
            printMessageAndExit("Node 1 doesn't exist");
        }
        
        // getting photoresistor on node 1
        MyPhotoResistor photoResistor = node.getDeviceObject(MyPhotoResistor.class);
        if (photoResistor == null) {
            printMessageAndExit("Error when PhotoResistor was getting from node.");
        }
        
        // getting ADC value
        int photoResult = photoResistor.get();

        if (photoResult == Integer.MAX_VALUE) {
            CallRequestProcessingState procState = photoResistor.getCallRequestProcessingStateOfLastCall();
            if (procState == CallRequestProcessingState.ERROR) {
                CallRequestProcessingError error = photoResistor.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Getting ADC value failed: " + error);
            } else {
                printMessageAndExit("Getting ADC value hasn't been processed yet: " + procState);
            }
        } else {
            // printing result
            System.out.println("Photo value is " + photoResult);
        }

        
        // getting potentiometer on node 1
        MyPotentiometer potentiometer = node.getDeviceObject(MyPotentiometer.class);
        if (potentiometer == null) {
            printMessageAndExit("Error when PhotoResistor was getting from node.");
        }
        
        // getting ADC value
        int potentiometerResult = potentiometer.get();

        if (potentiometerResult == Integer.MAX_VALUE) {
            CallRequestProcessingState procState = potentiometer.getCallRequestProcessingStateOfLastCall();
            if (procState == CallRequestProcessingState.ERROR) {
                CallRequestProcessingError error = potentiometer.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Getting ADC value failed: " + error);
            } else {
                printMessageAndExit("Getting ADC value hasn't been processed yet: " + procState);
            }
        } else {
            // printing result
            System.out.println("Potentiometer is " + potentiometerResult);
        }

        // destroys simply reference
        simply.destroy();
    }
}
