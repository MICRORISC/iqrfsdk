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

package com.microrisc.simply.iqrf.dpa.v22x.examples.broadcasting;

import com.microrisc.simply.Network;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDR;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.types.LED_State;
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
        
        // demonstrate how the broadcast can be used 
        // timing is handled by the simply 
        for (int i = 0; i < 10; i++) {
            // send broadcast
            UUID requestIdReset = broadcast.sendRequest(
                    network1.getId(),
                    OS.class,
                    OS.MethodID.READ,
                    new Object[]{}
            );

            // check if the broadcast was sent
            if (requestIdReset == null) {
                printMessageAndExit("Error while sending request for reseting nodes");
            }
        }
            
        // allow time for the packets to be sent to the network
        System.out.println("Sending broadcasts ...");
        Thread.sleep(5000);

        // led test
        LED_State lStateOn = LED_State.ON;
        LED_State lStateOff = LED_State.OFF;
        
        for (int i = 0; i < 10; i++) {
            UUID requestId1 = broadcast.sendRequest( 
                    network1.getId(), 
                    LEDR.class, 
                    LEDG.MethodID.SET, 
                    new Object[] { lStateOn } 
            );
            
            Thread.sleep(500);
    
            UUID requestId2 = broadcast.sendRequest( 
                    network1.getId(), 
                    LEDR.class, 
                    LEDG.MethodID.SET, 
                    new Object[] { lStateOff } 
            );
            
            Thread.sleep(500);
            
            // based on received confirmation
            // getting broadcast result in max. 1000 ms timeout
            BroadcastResult broadcastResult1 = broadcast.getBroadcastResult(requestId1, 1000);
            BroadcastResult broadcastResult2 = broadcast.getBroadcastResult(requestId2, 1000);
            
            if ( broadcastResult1 == BroadcastResult.OK  &&  broadcastResult2 == BroadcastResult.OK ) {
                System.out.println("Broadcast led cycle performed OK.");
            } else {
                System.out.println("Broadcast led cycle performed with error, check log.");
            }
        }
        
        // end working with Simply
        simply.destroy();
    }
}
