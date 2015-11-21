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

package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.os;

import com.microrisc.simply.CallRequestProcessingState;
import static com.microrisc.simply.CallRequestProcessingState.ERROR;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.types.SleepInfo;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;

/**
 * Example of using OS Peripheral - synchronous version.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 * @author Martin Strouhal
 */
// November (Martin Strouhal) - extended for 2 sleep examples
public class Sleep {
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
            printMessageAndExit("Network 1 doesn't not exist");
        }
        
        // getting node 1
        Node node1 = network1.getNode("1");
        if (node1 == null) {
            printMessageAndExit("Node 1 doesn't exist");
        }
        
        // get access to OS peripheral
        OS os = node1.getDeviceObject(OS.class);
        if (os == null) {
            printMessageAndExit("OS doesn't exist or is not enabled");
        }
        
        //sleepEg1(os);
        sleepEg2(os);
        
        // end working with Simply
        simply.destroy();
    }
    
    private static void sleepEg1(OS os){
        // set of commands available on OS peripheral
        // time = 300, one unit is 32.768ms 
        // control = 7
        // bit0 = 1 ... wakeup on PIN negative change
        // bit1 = 1 ... run calibration process before going to sleep
        // bit2 = 1 ... green LED flash after wake up
        // bit3 = 0 ... wakeup on PIN positive change
        // bit4 = 1 ... use unit 32.768ms instead of default 2.097s
        SleepInfo sleepInfo = new SleepInfo(300, 0b00010111);
        VoidType sleepResult = os.sleep(sleepInfo);
        if (sleepResult == null) {            
            CallRequestProcessingState procState = os.getCallRequestProcessingStateOfLastCall();
            if ( procState == ERROR ) {
                CallRequestProcessingError error = os.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Entering sleep mode failed: " + error);
            } else {
                printMessageAndExit("Entering sleep mode hasn't been processed yet: " + procState);
            }
        }
    }
    
    private static void sleepEg2(OS os){       
       // time in ms; wakeup on PIN negative change; run calibration; green LED flash after wake up; wakeup on PIN positive change
        SleepInfo sleepInfo = new SleepInfo(5*1000, true, true, true, false);
        VoidType sleepResult = os.sleep(sleepInfo);
        if (sleepResult == null) {            
            CallRequestProcessingState procState = os.getCallRequestProcessingStateOfLastCall();
            if ( procState == ERROR ) {
                CallRequestProcessingError error = os.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Entering sleep mode failed: " + error);
            } else {
                printMessageAndExit("Entering sleep mode hasn't been processed yet: " + procState);
            }
        }
    }
}
