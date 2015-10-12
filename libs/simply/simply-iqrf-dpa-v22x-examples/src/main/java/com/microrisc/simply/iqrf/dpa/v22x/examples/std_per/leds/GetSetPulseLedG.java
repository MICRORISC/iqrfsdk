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

package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.leds;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v22x.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;


/**
 * Example of using LEDG peripheral - synchronous version.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class GetSetPulseLedG {
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
    private static void processNullResult(LEDG ledg, String errorMsg, String notProcMsg) 
    {
        CallRequestProcessingState procState = ledg.getCallRequestProcessingStateOfLastCall();
        if ( procState == CallRequestProcessingState.ERROR ) {
            CallRequestProcessingError error = ledg.getCallRequestProcessingErrorOfLastCall();
            printMessageAndExit(errorMsg + ": " + error);
        } else {
            printMessageAndExit(notProcMsg + ": " + procState);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }
        
        // getting network 1
        Network network1 = simply.getNetwork("1", Network.class);
        if (network1 == null) {
            printMessageAndExit("Network 1 doesn't exist");
        }
        
        // getting node 1
        Node node1 = network1.getNode("1");
        if (node1 == null) {
            printMessageAndExit("Node 1 doesn't exist");
        }
        
        // getting LEDG interface
        LEDG ledg = node1.getDeviceObject(LEDG.class);
        if (ledg == null) {
            printMessageAndExit("LEDG doesn't exist or is not enabled");
        }
        
        // setting state of LEDG to 'ON'
        VoidType setResult = ledg.set(LED_State.ON);
        if ( setResult == null ) {
            processNullResult(ledg, "Setting LEDG state ON failed", 
                    "Setting LEDG state ON hasn't been processed yet"
            );
        }
        
        // getting actual state of LEDG -  should be 'ON'
        LED_State actualState = ledg.get();
        if ( actualState == null ) {
            processNullResult(ledg, "Getting LEDG state failed", 
                    "Getting LEDG state hasn't been processed yet"
            );
        }
        
        // printing the actual state of LEDG - it should be "ON"
        System.out.println("LEDG is " + actualState.name());
        
        // pause to see LEDG on
        System.out.println("Waiting 5s...");
        Thread.sleep(5000);
        
        // set LEDG off
        System.out.println("Setting LEDG OFF...");
        
        setResult = ledg.set(LED_State.OFF);
        if ( setResult == null ) {
            processNullResult(ledg, "Setting LEDG state OFF failed", 
                    "Setting LEDG state OFF hasn't been processed yet"
            );
        }
        System.out.println("LEDG is OFF");
        
        // pulse LEDG
        System.out.println("Pulsing LEDG ...");
        
        setResult = ledg.pulse();
        if ( setResult == null ) {
            processNullResult(ledg, "Pulsing LEDG failed", 
                    "Pulsing LEDG hasn't been processed yet"
            );
        }
        
        // end working with Simply
        simply.destroy();
    }
}
