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

package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.additionalinfo;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_AdditionalInfo;
import com.microrisc.simply.iqrf.dpa.v22x.types.LED_State;
import java.io.File;

/**
 * Example of getting an additional information.
 * 
 * @author Michal Konopa
 */
public class GetAddInfo {
    // reference to Simply
    private static Simply simply = null;
    
    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if ( simply != null ) {
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
        
        // getting LEDG interface
        LEDG ledg = node1.getDeviceObject(LEDG.class);
        if ( ledg == null ) {
            printMessageAndExit("LEDG doesn't exist or is not enabled");
        }
        
        // getting actual state of LEDG
        LED_State actualState = ledg.get();
        if ( actualState == null ) {
            CallRequestProcessingState procState = ledg.getCallRequestProcessingStateOfLastCall();
            if ( procState == CallRequestProcessingState.ERROR ) {
                CallRequestProcessingError error = ledg.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Getting LEDG state failed: " + error);
            } else {
                printMessageAndExit("Getting LEDG state hasn't been processed yet: " + procState);
            }
        }
        
        // getting additional info of the last call
        DPA_AdditionalInfo dpaAddInfo = ledg.getDPA_AdditionalInfoOfLastCall();
        if ( dpaAddInfo != null ) {
            System.out.println("DPA additional info: \n" + dpaAddInfo.toPrettyFormattedString());
        }
        
        // end working with Simply
        simply.destroy();
    }
}
