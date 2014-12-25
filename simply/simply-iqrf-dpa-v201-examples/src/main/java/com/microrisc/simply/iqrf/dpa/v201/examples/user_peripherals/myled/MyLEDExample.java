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

package com.microrisc.simply.iqrf.dpa.v201.examples.user_peripherals.myled;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v201.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v201.examples.user_peripherals.myled.def.MyLED;
import com.microrisc.simply.iqrf.dpa.v201.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;

/**
 * Example of using MyLED.
 * 
 * @author Michal Konopa
 */
public class MyLEDExample {
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
    private static void processNullResult(MyLED device, String errorMsg, String notProcMsg) 
    {
        CallRequestProcessingState procState = device.getCallRequestProcessingStateOfLastCall();
        if ( procState == CallRequestProcessingState.ERROR ) {
            CallRequestProcessingError error = device.getCallRequestProcessingErrorOfLastCall();
            printMessageAndExit(errorMsg + ": " + error);
        } else {
            printMessageAndExit(notProcMsg + ": " + procState);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply-user_per.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }
        
        // getting set of networks
        Network network = simply.getNetwork("1", Network.class);
        if ( network == null ) {
            printMessageAndExit("Network doesn't not exist");
        }
        
        // getting node 1
        Node node1 = network.getNode("1");
        if ( node1 == null ) {
            printMessageAndExit("Node 1 doesn't exist");
        }
        
        // get access to MyLED peripheral
        MyLED myLed = node1.getDeviceObject(MyLED.class);
        if ( myLed == null ) {
            printMessageAndExit("MyLED doesn't exist or is not enabled");
        }
        
        // indicate how to change Hw profile
        //myLed.setHwProfile(0x0101);
        
        VoidType setResult = myLed.set(LED_State.ON);
        if ( setResult == null ) {
            processNullResult(myLed, "Setting LEDs state ON failed", 
                    "Setting LEDs state ON hasn't been processed yet"
            );
        }
        
        // getting actual state of LEDs -  should be 'ON'
        LED_State actualState = myLed.get();
        if ( actualState == null ) {
            processNullResult(myLed, "Getting LEDs state failed", 
                    "Getting LEDs state hasn't been processed yet"
            );
        }
        
        // printing the actual state of LEDG - it should be "ON"
        System.out.println("LEDs are " + actualState.name());
        
        // pause to see LEDs on
        System.out.println("Waiting 5s...");
        Thread.sleep(5000);
        
        // performing pulse on LEDs
        VoidType pulseResult = myLed.pulse();
        if ( pulseResult == null ) {
            processNullResult(myLed, "Pulsing LEDs failed", 
                    "Pulsing LEDs hasn't been processed yet"
            );
        }
        
        // end working with Simply
        simply.destroy();
    }
}
