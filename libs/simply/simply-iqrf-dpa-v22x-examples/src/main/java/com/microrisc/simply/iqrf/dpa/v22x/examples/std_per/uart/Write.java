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

package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.uart;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.UART;
import com.microrisc.simply.iqrf.dpa.v22x.types.BaudRate;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;

/**
 * Example of using UART Peripheral - synchronous version.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class Write {
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
    private static void processNullResult(UART uart, String errorMsg, String notProcMsg) 
    {
        CallRequestProcessingState procState = uart.getCallRequestProcessingStateOfLastCall();
        if ( procState == CallRequestProcessingState.ERROR ) {
            CallRequestProcessingError error = uart.getCallRequestProcessingErrorOfLastCall();
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
        
        // get access to UART peripheral
        UART uart = node1.getDeviceObject(UART.class);
        if ( uart == null ) {
            printMessageAndExit("UART doesn't exist or is not enabled");
        }
        
        // Open UART peripheral 
        VoidType openResult = uart.open(BaudRate.BR9600);
        if ( openResult == null ) {
            processNullResult(uart, "Opening UART on a node failed", 
                    "Opening UART on a node hasn't been processed yet"
            );
        }
        
        // Write UART peripheral
        int timeout = 0xFF; // no reading, only writing
        short[] data = {0x48, 0x65, 0x6C, 0x6C, 0x6F };
        
        short[] writeResult  = uart.writeAndRead(timeout, data);
        if ( writeResult == null ) {
            processNullResult(uart, "Writing to UART on a node failed", 
                    "Writing to UART on a node hasn't been processed yet"
            );
        }
        System.out.println("UART data written correctly");
        
        // Close UART peripheral 
        VoidType result = uart.close();
        if ( result == null ) {
            processNullResult(uart, "Closing UART on a node failed", 
                    "Closing UART on a node hasn't been processed yet"
            );
        }

        // end working with Simply
        simply.destroy();
    }
}
