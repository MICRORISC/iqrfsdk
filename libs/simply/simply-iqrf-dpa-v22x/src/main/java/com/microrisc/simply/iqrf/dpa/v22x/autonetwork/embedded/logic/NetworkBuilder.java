/*
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.logic;

import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.asynchrony.AsynchronousMessagesListener;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkPeripheral;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkState;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.RAM;
import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides funcionality for automatic network building via Autonetwork embedded.
 * <p>
 * @author Martin Strouhal
 */
public class NetworkBuilder implements
        AsynchronousMessagesListener<DPA_AsynchronousMessage> {

   /** Logger */
   private static final Logger log = LoggerFactory.getLogger(
           NetworkBuilder.class);

   // reference to Simply
   private DPA_Simply simply;
   // reference to async manager
   private AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> asyncManager;

   // reference to Coordinator node
   private Node coord;
   private NodeApprover approver;
   private AutonetworkPeripheral autonetworkPer;


   public NetworkBuilder(String configurationFile) {
      // getting simply
      try {
         simply = DPA_SimplyFactory.getSimply(configurationFile);
      } catch (SimplyException ex) {
         throw new RuntimeException("Error while creating Simply: " + ex);
      }

      // getting network "1"
      Network network1 = simply.getNetwork("1", Network.class);
      if (network1 == null) {
         throw new RuntimeException("Network 1 doesn't exist");
      }

      // getting coordinator
      coord = network1.getNode("0");
      if (coord == null) {
         throw new RuntimeException("Coordinator doesn't exist");
      }

      // getting access to asynchronous messaging manager
      asyncManager = simply.getAsynchronousMessagingManager();

      // register the listener of asynchronous messages
      asyncManager.registerAsyncMsgListener(this);
   }

   /**
    * Start autonetwork with specified parameters and bond nodes approved by
    * {@code approver}.
    *
    * @param discoveryTXPower discovery TX power in interval  <0,7>
    * @param bondingTime nominal bonding time in 2.56 s units. Must be longer
    * then ( NumberOfNodes + 4 ) * 60 ms.
    * @param temporaryAddressTimeout is node temporary address timeout in 25.6 s
    * units. Must be long enough the temporary address does not timeout till all
    * previous nodes are authorized.
    * @param unbondAndRestart If is set then all nodes with temporary address be
    * node bonded 0xFE are unbonded and restarted before discovery
    * @param approver if it's not null, it's determined via this approver if
    * can, approver can be null - in this case it won't be used
    */
   public void startAutonetwork(int discoveryTXPower, int bondingTime,
           int temporaryAddressTimeout, boolean unbondAndRestart,
           NodeApprover approver) {

      boolean forwardBondedMid = (approver == null) ? false : true;

      // checking params
      if (discoveryTXPower < 0 || discoveryTXPower > 7) {
         String txt = "TX power must be in interval <0,7>.";
         log.error(txt);
         throw new IllegalArgumentException(txt);
      }
      if (temporaryAddressTimeout < 0 || bondingTime < 0) {
         String txt = "Node temporary address timeout and bonding time cannot be negative!";
         log.error(txt);
         throw new IllegalArgumentException(txt);
      }

      // prepare config byte form booleans
      int configByte = (unbondAndRestart == true) ? 1 : 0;
      configByte <<= 1;
      configByte += (forwardBondedMid == true) ? 1 : 0;

      // getting Autonetwork peripheral if it's need
      if (forwardBondedMid) {
         autonetworkPer = coord.getDeviceObject(AutonetworkPeripheral.class);
         if (autonetworkPer == null) {
            String txt = "Autonetwork peripheral doesn't exist on Coordinator!";
            log.error(txt);
            throw new RuntimeException(txt);
         }
         this.approver = approver;
      }

      // getting RAM DI
      EEPROM eeprom = coord.getDeviceObject(EEPROM.class);
      if (eeprom == null) {
         String txt = "RAM doesn't exist on Coordinator!";
         log.error(txt);
         throw new RuntimeException(txt);
      }

      // writing configuration
      eeprom.write(0x0, new short[]{(short) discoveryTXPower,
         (short) bondingTime,
         (short) temporaryAddressTimeout, (short) configByte});

      // getting RAM DI
      RAM ram = coord.getDeviceObject(RAM.class);
      if (ram == null) {
         String txt = "RAM doesn't exist on Coordinator!";
         log.error(txt);
         throw new RuntimeException(txt);
      }

      // start autonetwork on Coordinator
      ram.write(0x0, new short[]{0x0A});
   }

   /**
    * Start autonetwork with specified parameters and bond all nodes.
    *
    * @param discoveryTXPower discovery TX power in interval  <0,7>
    * @param bondingTime nominal bonding time in 2.56 s units. Must be longer
    * then ( NumberOfNodes + 4 ) * 60 ms.
    * @param temporaryAddressTimeout is node temporary address timeout in 25.6 s
    * units. Must be long enough the temporary address does not timeout till all
    * previous nodes are authorized.
    * @param unbondAndRestart If is set then all nodes with temporary address be
    * node bonded 0xFE are unbonded and restarted before discovery
    */
   public void startAutonetwork(int discoveryTXPower, int bondingTime,
           int temporaryAddressTimeout, boolean unbondAndRestart) {
      NetworkBuilder.this.startAutonetwork(discoveryTXPower, bondingTime,
              temporaryAddressTimeout, unbondAndRestart, null);
   }

   @Override
   public void onAsynchronousMessage(DPA_AsynchronousMessage message) {
      System.out.println(message.getMainData());
      if (message.getMainData() instanceof AutonetworkState) {
         log.info("Autonetwork message: " + message.getMainData());
      }
      //TODO call method form interface which providing acces to received network states

      // requeire approving if its's need
      if (message.getMainData() instanceof RemotelyBondedModuleId && approver != null) {

         boolean approveResult = approver.approveNode(
                 (RemotelyBondedModuleId) message.getMainData());

         if (approveResult) {
            autonetworkPer.async_approve();
         } else {
            autonetworkPer.async_disapprove();
         }
      }
   }

   public void destroy() {
      asyncManager.unregisterAsyncMsgListener(this);
      simply.destroy();
   }
}
