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

package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.async;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.di_services.CallErrorsService;
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
import java.util.UUID;


/**
 * Example of getting and setting a state of a green LED using asynchronous method calling.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class GetAndSetStateOfLEDG {
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
    
    // prints out specified error description with additional information about
    // an error, destroys the Simply and exits
    private static void printErrorAndExit(String errString, 
            CallErrorsService devObject, UUID callUId
    ) {
        CallRequestProcessingError error = devObject.getCallRequestProcessingError(callUId);
        printMessageAndExit(errString + ":" + error);
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
            printMessageAndExit("Node 0 doesn't exist");
        }
        
        // getting LEDG interface
        LEDG ledg = node1.getDeviceObject(LEDG.class);
        if (ledg == null) {
            printMessageAndExit("LEDG doesn't exist");
        }
        
        // set LEDG on
        System.out.println("Setting LEDG on...");
        
        UUID setOnRequestUid = ledg.async_set(LED_State.ON);
        VoidType setOnResult = ledg.getCallResultInDefaultWaitingTimeout(setOnRequestUid, VoidType.class);
        if (setOnResult == null) {
            printErrorAndExit("Setting LEDG on failed", ledg, setOnRequestUid);
        }
        
        // get state of LEDG
        UUID getStateRequestUid = ledg.async_get();
        LED_State getStateResult = ledg.getCallResultInDefaultWaitingTimeout(getStateRequestUid, LED_State.class);
        if ( getStateResult == null ) {
            processNullResult(ledg, "Getting LEDG state failed", 
                    "Getting LEDG state hasn't been processed yet"
            );
        }
        System.out.println("LEDG is " + getStateResult.name());
        
        // pause to see LEDG on
        Thread.sleep(5000);
        
        // set LEDG off
        System.out.println("Setting LEDG off...");
        
        UUID setOffRequestUid = ledg.async_set(LED_State.OFF);
        VoidType setOffResult = ledg.getCallResultInDefaultWaitingTimeout(setOffRequestUid, VoidType.class);
        if ( setOffResult == null ) {
            processNullResult(ledg, "Setting LEDG off failed", 
                    "Setting LEDG off hasn't been processed yet"
            );
        }
        System.out.println("LEDG is off");
        
        // end working with Simply
        simply.destroy();
    }
}
