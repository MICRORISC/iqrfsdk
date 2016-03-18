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
import com.microrisc.simply.asynchrony.AsynchronousMessagesListener;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkPeripheral;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkState;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkStateType;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.RAM;
import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides funcionality for automatic network building via Autonetwork
 * embedded.
 * <p>
 * @author Martin Strouhal
 */
public class NetworkBuilder implements
        AsynchronousMessagesListener<DPA_AsynchronousMessage> {

   /** Identify actual state of network building. */
   public static enum AlgorithmState {
      NON_STARTED, RUNNING, FINISHED;
   }

   /** Logger */
   private static final Logger log = LoggerFactory.getLogger(NetworkBuilder.class);

   private AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> asyncManager;
   private List<AutonetworkStateListener> autonetworkListeners;
   private Node coord;
   private AutonetworkPeripheral autonetworkPer;
   private NodeApprover approver;
   private AlgorithmState alogirthmState = AlgorithmState.NON_STARTED;
   private AutonetworkBuildingListener buildingListener;

   public NetworkBuilder(
           Network sourceNetwork,
           AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> asyncManager
   ) {
      if (sourceNetwork == null) {
         throw new RuntimeException("Source network doesn't exist");
      }
      if(asyncManager == null){
         throw new RuntimeException("Async manager doesn't exist");
      }

      // getting coordinator
      coord = sourceNetwork.getNode("0");
      if (coord == null) {
         throw new RuntimeException("Coordinator doesn't exist");
      }

      // register the listener of asynchronous messages
      this.asyncManager = asyncManager;
      this.asyncManager.registerAsyncMsgListener(this);
      
      autonetworkListeners = new LinkedList<>();
      // add native listener for dnymaic network building
      buildingListener = new AutonetworkBuildingListener(sourceNetwork);
      autonetworkListeners.add(buildingListener);
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

      // prepare config byte from booleans
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
      eeprom.write(0x00, new short[]
        {(short) discoveryTXPower,
         (short) bondingTime,
         (short) temporaryAddressTimeout, 
         (short) configByte
        });

      // getting RAM DI
      RAM ram = coord.getDeviceObject(RAM.class);
      if (ram == null) {
         String txt = "RAM doesn't exist on Coordinator!";
         log.error(txt);
         throw new RuntimeException(txt);
      }

      // start autonetwork on Coordinator
      ram.write(0x00, new short[]{ 0x0A });
      alogirthmState = AlgorithmState.RUNNING;
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
      log.debug("onAsynchronousMessage - start: message={}", message);
      
      if (message.getMainData() instanceof AutonetworkState) {
         AutonetworkState actualState = (AutonetworkState)message.getMainData();
         log.info("Autonetwork message: " + actualState);

         // checks if algorithm is on the end
         checkAlgorithmEnd(actualState);
         
         // call autonetwork listeners
         for (AutonetworkStateListener listener: autonetworkListeners) {
            listener.onAutonetworkState(actualState);
         }
      }
      
      // if required approving from NodeApprover
      if (message.getMainData() instanceof RemotelyBondedModuleId && approver != null) {

         boolean approveResult = approver.approveNode(
                 (RemotelyBondedModuleId) message.getMainData());

         if (approveResult) {
            autonetworkPer.async_approve();
         } else {
            autonetworkPer.async_disapprove();
         }
      }

      log.debug("onAsynchronousMessage - end");
   }

   public AlgorithmState getAlgorithmState() {
      return alogirthmState;
   }

   public Network getNetwork() {
      if (alogirthmState != AlgorithmState.FINISHED) {
         throw new IllegalStateException("Algorithm is running still!");
      }
      return buildingListener.getNetworkCopy();
   }

   public void destroy() {
      log.debug("destroy - start");
      asyncManager.unregisterAsyncMsgListener(this);
      for (AutonetworkStateListener autonetworkListener : autonetworkListeners) {
         autonetworkListener.destroy();
      }
      autonetworkListeners.clear();
      log.debug("destroy - end");
   }

   // checks if algorithm was succesfully ended
   private void checkAlgorithmEnd(AutonetworkState state) {
      if (state.getType() == AutonetworkStateType.S_START) {

         try {
            if(state.getAdditionalData(1) == 0) {
               alogirthmState = AlgorithmState.FINISHED;
            }
         } catch (IllegalArgumentException ex) {
            log.warn("Received autonetwork message doesn't contain count of remaing waves.");
         }
      }
   }
}
