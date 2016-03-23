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
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.logic.NetworkBuilder;
import com.microrisc.simply.iqrf.dpa.v22x.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v22x.types.LED_State;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;

/**
 * Example of using Autonetwork embedded system.
 *
 * @author Martin Strouhal
 */
public class AutonetworkEmbedded {

   /** reference to Simply */
   private static DPA_Simply simply;

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

      // getting network 1 (with connected coordinator)
      Network network1 = simply.getNetwork("1", Network.class);
      if (network1 == null) {
         printMessageAndExit("Network 1 doesn't exist");
      }

      // creating network builder for autonetwork embedded
      NetworkBuilder builder = new NetworkBuilder(network1, 
              simply.getAsynchronousMessagingManager()
      );

      // config params
      int discoveryTXPower = 0x07;
      int bondingTime = 0X08;
      int temporaryAddressTimeout = 0x03;
      boolean unbondAndRestart = true;
      
      // starting autonetworking
      builder.startAutonetwork(discoveryTXPower, bondingTime, 
              temporaryAddressTimeout, unbondAndRestart, new SimpleNodeApprover()
      );

      // sleeping until isn't algorithm finished
      while (builder.getAlgorithmState() != NetworkBuilder.AlgorithmState.FINISHED) {
         try {
            Thread.sleep(3000);
         } catch (InterruptedException ex) {
            System.out.println(ex);
         }
      }

      // getting cpoy of created dynamic network and replacing previous network, 
      // which contained only coordinator
      network1 = builder.getNetwork();

      // getting node 1
      Node node1 = network1.getNode("1");
      if (node1 == null) {
         printMessageAndExit("Node 1 doesn't exist");
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

      // free up used resources
      builder.destroy();
      simply.destroy();
   }

}
