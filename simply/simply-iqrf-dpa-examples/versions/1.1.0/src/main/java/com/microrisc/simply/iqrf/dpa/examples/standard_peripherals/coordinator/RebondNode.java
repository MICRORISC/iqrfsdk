

package com.microrisc.simply.iqrf.dpa.examples.standard_peripherals.coordinator;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.devices.Coordinator;
import java.io.File;

/**
 * Examples of using Coordinator peripheral - synchronous version.
 * 
 * @author Michal Konopa
 */
public class RebondNode {
    
    // reference to Simply
    private static Simply simply = null;
    
    // prints out specified error description, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if ( simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }
    
    public static void main(String[] args) {
        
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply-standard_per.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }
        
        // getting network 1
        Network network1 = simply.getNetwork("1", Network.class);
        if ( network1 == null ) {
            printMessageAndExit("Network 1 doesn't exist");
        }
        
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
       
        // rebond node 1
        Integer bondedNodesNum = coordinator.rebondNode(1);
        if ( bondedNodesNum == null ) {
            CallRequestProcessingState procState = coordinator.getCallRequestProcessingStateOfLastCall();
            if ( procState == CallRequestProcessingState.ERROR ) {
                CallRequestProcessingError error = coordinator.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Rebonding failed: " + error);
            } else {
                printMessageAndExit("Rebonding hasn't been processed yet: " + procState);
            }
        }
        
        // bonded nodes
        System.out.println("Number of bonded nodes: " + bondedNodesNum);
        
        // end working with Simply
        simply.destroy();
    }
}
