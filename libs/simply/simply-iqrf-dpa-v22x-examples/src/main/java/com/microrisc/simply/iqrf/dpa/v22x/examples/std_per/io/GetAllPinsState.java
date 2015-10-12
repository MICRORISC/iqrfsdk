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

package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.io;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.IO;
import java.io.File;

/**
 * Example of using IO Peripheral - synchronous version.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class GetAllPinsState {
    // reference to Simply
    private static Simply simply = null;
    
    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if ( simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }
    
    public static void main(String[] args) {
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }
        
        // getting network 1
        Network network1 = simply.getNetwork("1", Network.class);
        if ( network1 == null ) {
            printMessageAndExit("Network 1 doesn't exist");
        }
        
        // getting node 1
        Node node1 = network1.getNode("1");
        if ( node1 == null ) {
            printMessageAndExit("Node 1 doesn't exist");
        }
        
        IO io = node1.getDeviceObject(IO.class);
        if ( io == null ) {
            printMessageAndExit("IO not present at node 1");
        }
        
        // get state of IO
        short[] ioState = io.get();
        if ( ioState == null ) {            
            CallRequestProcessingState procState = io.getCallRequestProcessingStateOfLastCall();
            if ( procState == CallRequestProcessingState.ERROR ) {
                CallRequestProcessingError error = io.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Getting IO state failed: " + error);
            } else {
                printMessageAndExit("Getting IO state hasn't been processed yet: " + procState);
            }   
        }
        
        // printing the actual state of IO
        System.out.print("IO state: ");
        for (short stateItem : ioState) {
            System.out.printf("%1$02X ", stateItem); 
        }
        System.out.println();
        
        // end of working with Simply
        simply.destroy();
    }
}
