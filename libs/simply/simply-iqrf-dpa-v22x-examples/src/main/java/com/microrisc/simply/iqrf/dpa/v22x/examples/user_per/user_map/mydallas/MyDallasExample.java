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

package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.user_map.mydallas;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.user_map.mydallas.def.MyDallas18B20;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Example getting of temperature from MyDallas18B20
 * 
 * @author Martin Strouhal
 */
public class MyDallasExample {
    // reference to Simply
    private static Simply simply = null;

    private static Map<String, MyDallas18B20> getDallas18B20S(
            Network network, String[] nodeIds) 
    {
        Map<String, MyDallas18B20> dallas18B20Map = new LinkedHashMap<>();

        for ( String nodeId : nodeIds ) {
            Node node = network.getNode(nodeId);
            MyDallas18B20 dallas = node.getDeviceObject(MyDallas18B20.class);
            
            if ( dallas == null ) {
                System.out.println(
                    "Sensor Dallas18B20 is not on node " + nodeId + ", return."
                );
                return null;
            }
            dallas18B20Map.put(nodeId, dallas);
        }
        return dallas18B20Map;
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

        // getting devices
        Map<String, MyDallas18B20> dallas18B20 = getDallas18B20S(network1, new String[]{"1", "2"});
        if ( dallas18B20 == null ) {
            printMessageAndExit("Error when Dallas18B20 was getting from node.");
        }

        // get device on which we know temperature
        MyDallas18B20 dallas = dallas18B20.get("1");

        // get result - temperature from sensor
        float result = dallas.get();
        
        if ( result == Float.MAX_VALUE ) {
            CallRequestProcessingState procState = dallas.getCallRequestProcessingStateOfLastCall();
            if (procState == CallRequestProcessingState.ERROR) {
                CallRequestProcessingError error = dallas.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Getting temperature failed: " + error);
            } else {
                printMessageAndExit(
                       "Getting temperature hasn't been processed yet: " + procState);
            }
        } else {
            System.out.println("Temperature is " + result);
        }
           
        simply.destroy();
    }
}
