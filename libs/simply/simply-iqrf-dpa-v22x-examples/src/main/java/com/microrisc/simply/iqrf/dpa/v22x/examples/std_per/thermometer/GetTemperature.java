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

package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.thermometer;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.errors.CallRequestProcessingErrorType;
import com.microrisc.simply.iqrf.dpa.DPA_ResponseCode;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Thermometer;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_AdditionalInfo;
import com.microrisc.simply.iqrf.dpa.v22x.types.Thermometer_values;
import java.io.File;
import java.util.UUID;

/**
 * Example of getting temperature on some node in a network.
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class GetTemperature {
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
        if ( network1 == null ) {
            printMessageAndExit("Network 1 doesn't exist");
        }
        
        // getting node 1
        Node node1 = network1.getNode("1");
        if ( node1 == null ) {
            printMessageAndExit("Node 1 doesn't exist");
        }
        
        // getting Thermometer interface
        Thermometer thermo = node1.getDeviceObject(Thermometer.class);
        if ( thermo == null ) {
            printMessageAndExit("Thermometer doesn't exist on node 1");
        }
        
        // getting actual temperature
        Thermometer_values thermoValues = null; 
        UUID tempRequestUid = thermo.async_get();
        
        // maximal number of attempts of getting a result
        final int MAX_RESULT_GETTING = 10;
        int attempt = 0;
        while ( attempt++ < MAX_RESULT_GETTING ) {
            
            CallRequestProcessingState procState = thermo.getCallRequestProcessingState(
                    tempRequestUid
            );
            
            if ( procState == CallRequestProcessingState.RESULT_ARRIVED ) {
                thermoValues = thermo.getCallResultImmediately(tempRequestUid, Thermometer_values.class);
                //result = thermo.getCallResultInDefaultWaitingTimeout(getStateRequestUid, LED_State.class);
                break;
            }

            if ( procState == CallRequestProcessingState.ERROR ) {
                
                // general call error
                CallRequestProcessingError error = thermo.getCallRequestProcessingErrorOfLastCall();
                
                if(error.getErrorType() == CallRequestProcessingErrorType.NETWORK_INTERNAL){
                  // specific DPA call error
                  DPA_AdditionalInfo dpaAddInfo = thermo.getDPA_AdditionalInfoOfLastCall();
                  DPA_ResponseCode dpaResponseCode = dpaAddInfo.getResponseCode();
                  
                  printMessageAndExit("Getting temperature failed: " + error + ", DPA error: " + dpaResponseCode);
                }else{
                  printMessageAndExit("Getting temperature failed: " + error); 
                }
                

            } else {
                System.out.println("Getting temperature hasn't been processed yet: " + procState);
            }
            
            Thread.sleep(500);
        }
        
        if ( thermoValues != null ) {
            // printing results        
            System.out.println("Temperature on the node " + node1.getId() + ": " + 
                thermoValues.getValue() + "." + thermoValues.getFractialValue() + " *C"
            );
            //System.out.println("Temperature: \n" + thermoValues.toPrettyFormattedString());
        } else {
            System.out.println("Result has not arrived.");
        }
        
        // end working with Simply
        simply.destroy();
    }
}
