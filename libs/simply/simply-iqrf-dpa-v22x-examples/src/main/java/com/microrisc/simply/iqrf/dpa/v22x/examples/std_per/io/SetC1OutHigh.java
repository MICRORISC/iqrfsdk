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
import com.microrisc.simply.iqrf.dpa.v22x.types.IO_Command;
import com.microrisc.simply.iqrf.dpa.v22x.types.IO_DirectionSettings;
import com.microrisc.simply.iqrf.dpa.v22x.types.IO_OutputValueSettings;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;

/**
 * Example of using IO Peripheral - synchronous version.
 * 
 * @author Michal Konopa
 */
public class SetC1OutHigh {
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
    
    // processes NULL result
    private static void processNullResult(IO io, String errorMsg, String notProcMsg) 
    {
        CallRequestProcessingState procState = io.getCallRequestProcessingStateOfLastCall();
        if ( procState == CallRequestProcessingState.ERROR ) {
            CallRequestProcessingError error = io.getCallRequestProcessingErrorOfLastCall();
            printMessageAndExit(errorMsg + ": " + error);
        } else {
            printMessageAndExit(notProcMsg + ": " + procState);
        }
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
        if (io == null) {
            printMessageAndExit("IO not present or enabled at node 1");
        }
        
        // set C1 OUT
        IO_DirectionSettings[] dirSettings = new IO_DirectionSettings[] {
            new IO_DirectionSettings(0x00, 0x01, 0x00),
        };

        VoidType result = io.setDirection(dirSettings);
        if ( result == null ) {
            processNullResult(io, "Setting IO direction failed", 
                    "Setting IO direction hasn't been processed yet"
            );
        }
        
        // set C1 HIGH
        IO_Command[] iocs = new IO_OutputValueSettings[] {
            new IO_OutputValueSettings(0x00, 0x01, 0x01),
        };

        result = io.setOutputState(iocs);
        if ( result == null ) {
            processNullResult(io, "Setting IO output state failed", 
                    "Setting IO output state hasn't been processed yet"
            );
        }
        
        // end of working with Simply
        simply.destroy();
    }
}
