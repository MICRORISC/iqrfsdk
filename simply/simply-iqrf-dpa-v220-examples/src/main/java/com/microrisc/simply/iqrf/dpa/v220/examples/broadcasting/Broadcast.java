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

package com.microrisc.simply.iqrf.dpa.v220.examples.broadcasting;

import com.microrisc.simply.Network;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.v220.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import com.microrisc.simply.iqrf.dpa.v220.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v220.devices.LEDR;
import com.microrisc.simply.iqrf.dpa.v220.types.LED_State;
import java.io.File;
import java.util.UUID;

/**
 * Example of using broadcasting - synchronous version.
 * 
 * @author Michal Konopa
 */
public class Broadcast {
    // reference to Simply
    private static DPA_Simply simply = null;
    
    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if ( simply != null ) {
            simply.destroy();
        }
        System.exit(1);
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
        if ( network1 == null ) {
            printMessageAndExit("Network doesn't not exist");
        }
        
        // getting access to broadcast services
        BroadcastServices broadcast = simply.getBroadcastServices();
        LED_State lStateOn = LED_State.ON;
        LED_State lStateOff = LED_State.OFF;
        
        for (int i = 0; i < 10; i++) {
            UUID requestId1 = broadcast.sendRequest( network1.getId(), LEDR.class, 
                    LEDG.MethodID.SET, new Object[] { lStateOn } 
            );
            
            Thread.sleep(1000);
    
            UUID requestId2 = broadcast.sendRequest( network1.getId(), LEDR.class, 
                    LEDG.MethodID.SET, new Object[] { lStateOff } 
            );
            
            Thread.sleep(1000);
            
            // getting broadcast result in max. 5000 ms timeout
            BroadcastResult broadcastResult1 = broadcast.getBroadcastResult(requestId1, 5000);
            BroadcastResult broadcastResult2 = broadcast.getBroadcastResult(requestId2, 5000);
            
            if ( broadcastResult1 == BroadcastResult.OK  &&  broadcastResult2 == BroadcastResult.OK ) {
                System.out.println("Broadcast cycle performed OK.");
            } else {
                System.out.println("Broadcast cycle performed with error.");
            }            
        }
        
        // end working with Simply
        simply.destroy();
    }
}
