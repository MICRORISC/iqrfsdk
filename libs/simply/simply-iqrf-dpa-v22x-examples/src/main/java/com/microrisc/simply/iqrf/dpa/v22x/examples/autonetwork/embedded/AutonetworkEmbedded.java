/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.examples.autonetwork.embedded;

import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkValueType;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.logic.NetworkBuilder;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v22x.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;

/**
 * Example of using Autonetwork embedded system. <br>
 * This example works with allowed autonetwork embedded peripheral and with at
 * least 2 TR modules with Autonetwork custom handler uploaded.<br>
 * Second node should turn on after the LED on first node is light on.
 *
 * @author Martin Strouhal
 */
public class AutonetworkEmbedded {

    /** reference to Simply */
    private static DPA_Simply simply;
    /** reference to network */
    private static Network network1;

    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if (simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }

    public static void main(String[] args) {
        // creating the Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply(
                    "config" + File.separator + "Simply.properties");
        } catch (SimplyException ex) {
            printMessageAndExit("Error while creating Simply: " + ex);
        }

        // getting network 1
        network1 = simply.getNetwork("1", Network.class);
        if (network1 == null) {
            printMessageAndExit("Network 1 doesn't exist");
        }

        // getting node 0
        Node coord = network1.getNode("0");
        if (coord == null) {
            printMessageAndExit("Coordinator doesn't exist");
        }

        // getting Coordinator interface
        Coordinator coordinator = coord.getDeviceObject(Coordinator.class);
        if (coordinator == null) {
            printMessageAndExit("Coordinator doesn't exist");
        }
        
        // clearing the coordinator
        VoidType result = coordinator.clearAllBonds();
        if (result == null) {
            printMessageAndExit("Clearing the coordinator network memory failed");
        }
        
        NetworkBuilder builder = new NetworkBuilder(network1, 
                simply.getAsynchronousMessagingManager()
        );

        // max. power => 7
        int discoveryTXPower = 7;
        // bonding time => 2.56*8 = 20.48 s
        int bondingTime = 8;
        // temporary address timeout => 25.6 * 3 = 76.8 s
        int temporaryAddressTimeout = 3;
        // yes unbond and restart 0xFE nodes
        boolean unbondAndRestart = true;
        
        // optional setting - see default values in constructor of builder
        builder.setValue(AutonetworkValueType.DISCOVERY_TX_POWER, discoveryTXPower);
        builder.setValue(AutonetworkValueType.BONDING_TIME, bondingTime);
        builder.setValue(AutonetworkValueType.TEMPORARY_ADDRESS_TIMEOUT, temporaryAddressTimeout);
        builder.setValue(AutonetworkValueType.UNBOND_AND_RESTART, unbondAndRestart);
        
        // setting of node approver which deciding if should new node bonded
        builder.setValue(AutonetworkValueType.APPROVER, new SimpleNodeApprover());
        
        // start first part algorithm with 2 waves
        System.out.println("First part:");
        int countOfWaves = 0x02;
        builder.startAutonetwork(countOfWaves);

        // waiting to end of algorithm
        while (builder.getAlgorithmState() != NetworkBuilder.AlgorithmState.FINISHED) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }

        // get actual state of nodes in network
        network1 = builder.getNetwork();

        // light on LED on new node "1"
        lightOnLED("1");

        // generate 5 seconds time for turn on next unbonded module after 
        // LED on node 1 was light on
        try {
          Thread.sleep(5000);
       } catch (InterruptedException ex) {
          System.out.println(ex);
       }

        
        System.out.println("Second part:");
        countOfWaves = 0x0A;
        builder.startAutonetwork(countOfWaves);
        
        // waiting to end algorithm
        while (builder.getAlgorithmState() != NetworkBuilder.AlgorithmState.FINISHED) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }

        // get actual state of nodes in network
        network1 = builder.getNetwork();
        
        // light on LED on new node "2"
        lightOnLED("2");
        
        // free up resources
        builder.destroy();
        simply.destroy();
    }
    
    private static void lightOnLED(String nodeId) {
      // getting node with nodeId
      Node node1 = network1.getNode(nodeId);
      if (node1 == null) {
         printMessageAndExit("Node " + nodeId + " doesn't exist");
      }

      // getting LEDG interface
      LEDG ledg = node1.getDeviceObject(LEDG.class);
      if (ledg == null) {
         printMessageAndExit("LEDG doesn't exist or is not enabled");
      }

      // setting state of LEDG to 'ON'
      VoidType setResult = ledg.set(LED_State.ON);
      if (setResult == null) {
         System.out.println("Setting LEDG state ON failed");
      }
   }
}
