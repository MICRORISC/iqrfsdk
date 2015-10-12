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
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Thermometer;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.v22x.types.Thermometer_values;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Example of using THERMOMETER Peripheral - synchronous version.
 * Gets references to thermometer device interfaces from set of nodes and returns
 * actual temperature on them. 
 * 
 * @author Michal Konopa
 * @author Rostislav Spinar
 */
public class GetTemperatureOnNodes {
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
    
    // returns map of thermometers or null, if thermometer is not present at
    // some node
    private static Map<String, Thermometer> getThermometers(
            Network network, List<Integer> nodeIds
    ) {
        Map<String, Thermometer> thermoMap = new LinkedHashMap<>();
        for ( Integer nodeId : nodeIds) {
            Node node = network.getNode(nodeId.toString());
            Thermometer thermometer = node.getDeviceObject(Thermometer.class);
            if ( thermometer == null ) {
                System.out.println("THERMOMETER not present at the node " + nodeId);
                return null;
            }
            thermometer.setDefaultWaitingTimeout(5000);
            thermoMap.put(nodeId.toString(), thermometer);
        }
        return thermoMap;
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
        
        // getting number of bonded nodes
        // getting a master node
        Node master = network1.getNode("0");
        if ( master == null ) {
            printMessageAndExit("Master doesn't exist");
        }
        
        // getting Coordinator interface
        Coordinator coordinator = master.getDeviceObject(Coordinator.class);
        if ( coordinator == null ) {
            printMessageAndExit("Coordinator doesn't exist");
        }
       
        // get all bonded nodes
        BondedNodes bondedNodes = coordinator.getBondedNodes();
        if ( bondedNodes == null ) {
            CallRequestProcessingState procState = coordinator.getCallRequestProcessingStateOfLastCall();
            if ( procState == CallRequestProcessingState.ERROR ) {
                CallRequestProcessingError error = coordinator.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Getting of bonded nodes failed: " + error);
            } else {
                printMessageAndExit("Getting of bonded nodes hasn't been processed yet: " + procState);
            }
        }
        
        // getting map of thermometers on specified nodes
        Map<String, Thermometer> thermos = getThermometers(network1, bondedNodes.getList());
        if ( thermos == null ) {
            printMessageAndExit("Error in getting thermometers on nodes");
        }
        
        // here may come some user code - before we need the results
        // ...
        
        // getting results
        // set up maximal number of cycles according to your needs
        final int MAX_CYCLES = 500;
        int noResponses = 0;
        int failures = 0;
        
        for ( int cycle = 0; cycle < MAX_CYCLES; cycle++ ) {
            for ( String nodeId : thermos.keySet() ) {

                // getting thermometer on the node
                Thermometer thermo = thermos.get(nodeId);
                    
                // blocking for default timeout, waiting timeout result
                Thermometer_values result = thermo.get();
                
                if ( result != null ) {
                        System.out.println("Temperature on the node " + nodeId + ": "
                                + result.getValue() + "." + result.getFractialValue() + " *C"
                        );
                } else {
                    CallRequestProcessingState procState = thermo.getCallRequestProcessingStateOfLastCall();
                    if ( procState == CallRequestProcessingState.ERROR ) {
                        CallRequestProcessingError error = thermo.getCallRequestProcessingErrorOfLastCall();
                        System.out.println("Getting temperature of the node " + nodeId
                                + " failed in processing: " + error.getErrorType()
                        );
                        failures++;
                    } else {
                        System.out.println("Getting temperature of the node " + nodeId
                                + " hasn't arrived, timeouted: " + procState
                        );
                        noResponses++;
                    }
                }
            }
            System.out.println("CYCLE: " + (cycle + 1));
        }
        System.out.println("No responses: " + noResponses);
        System.out.println("Failures: " + failures);
        
        // end of working with Simply
        simply.destroy();
    }
}
