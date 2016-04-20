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
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkValueType;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.RAM;
import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for automatic network building via Autonetwork
 * embedded.
 * <p>
 * @author Martin Strouhal
 */
public final class NetworkBuilder implements
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
   /** Flag for using approver - when isn't accessible correct peripheral, 
    * it can't be used. */
   private boolean disallowedApprover = false;

   /**
    * Creates and init new instance of {@link NetworkBuilder}.<br>
    * <b>Default values are used:</b>
    * <table border="1">
    *    <tr>
    *       <th>Value name</th>
    *       <th>Value</th>
    *    </tr>
    *    <tr>
    *       <td>Discovery TX power</td>
    *       <td>7</td>
    *    </tr>
    *    <tr>
    *       <td>Bonding time</td>
    *       <td>8</td>
    *    </tr>
    *    <tr>
    *       <td>Temporary address timeout</td>
    *       <td>3</td>
    *    </tr>
    *    <tr>
    *       <td>Unbond and restart</td>
    *       <td>true</td>
    *    </tr>
    * </table>
    * 
    * @param sourceNetwork which contains at least Coordinator
    * @param asyncManager for receing async events
    */
   public NetworkBuilder(
           Network sourceNetwork,
           AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> asyncManager
   ) {
      log.debug("<init> - start: sourceNetwork={}, asyncManager={}", 
              sourceNetwork, asyncManager);
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
      // add native listener for dynmaic network building
      buildingListener = new AutonetworkBuildingListener(sourceNetwork);
      autonetworkListeners.add(buildingListener);
      
      // get peripheral for approver, if isn't accesible than disallow his using
      autonetworkPer = coord.getDeviceObject(AutonetworkPeripheral.class);
      if (autonetworkPer == null) {
         String txt = "Autonetwork peripheral doesn't exist on Coordinator! Approver cannot be used!";
         log.warn(txt);
         disallowedApprover = true;
      }
      
      // set default values
      setValue(AutonetworkValueType.DISCOVERY_TX_POWER, 7);
      setValue(AutonetworkValueType.BONDING_TIME, 8);
      setValue(AutonetworkValueType.TEMPORARY_ADDRESS_TIMEOUT, 3);
      setValue(AutonetworkValueType.UNBOND_AND_RESTART, true);
      log.info("Initialization of network builder was completed.");
      log.debug("<init> - end");
   }

   /**
    * Sets value of specified type for autonetwork algorithm.
    * 
    * @param type of value which should be set
    * @param value which should be set
    *
    * @throws IllegalArgumentException thrown when:
    * <ul>
    * <li>{@code value} doesn't have correct data type</li>
    * <li>or if it's setting approver which can be used because doesn't exist
    * autonetwork peripheral on coordinator</li>
    * </ul>
    */
   public void setValue(AutonetworkValueType type, Object value) {
      log.debug("setValue - start");
      if (type.getDataType().isAssignableFrom(value.getClass())) {
         if (type.getDataType().equals(Integer.class)) {
            setNumberValue((int) value, type.getBytePos());
            log.debug("setValue - end");
            return;
         } else if (type.getDataType().equals(Boolean.class)) {
            setBooleanValue((boolean) value, type.getBytePos(), type.getBitPos());
            log.debug("setValue - end");
            return;
         } else if (NodeApprover.class.isAssignableFrom(value.getClass())){
            if(disallowedApprover){
               String txt = "Autonetwork peripheral couldn't get from Coordinator. Approver cannot be used!";
               log.warn(txt);
               log.debug("setValue - end: Exception");
               throw new IllegalArgumentException(txt);
            } else {
               this.approver = (NodeApprover) value;
               setBooleanValue(true, type.getBytePos(), type.getBitPos());
               log.debug("setValue - end");
               return;
            }
         }
      }
      log.debug("setValue - end: Exception - Illegal DataType of config value.");
      throw new IllegalArgumentException("Illegal DataType of config value.");
   }
   
   /**
    * Start autonetwork with specified count of waves and bond nodes approved by
    * {@link NodeApprover} (if was set).
    * 
    * @param countOfWaves count of waves
    *
    */
   public void startAutonetwork(int countOfWaves) {
      log.debug("startAutonetwork - start: countOfWaves={}", countOfWaves);
      // getting RAM DI
      RAM ram = coord.getDeviceObject(RAM.class);
      if (ram == null) {
         String txt = "RAM doesn't exist on Coordinator!";
         log.error(txt);
         log.debug("startAutonetwork - end: {}", txt);
         throw new RuntimeException(txt);
      }

      // start autonetwork on Coordinator
      ram.write(0x00, new short[]{(short) countOfWaves});
      alogirthmState = AlgorithmState.RUNNING;
      log.debug("startAutonetwork - end");
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

   /**
    * Returns actual state of algorithm.
    * @return actual state, see {@link AlgorithmState}
    */
   public AlgorithmState getAlgorithmState() {
      return alogirthmState;
   }

   /** 
    * Create actual copy of built network.
    * @return copy of actual network
    */
   public Network getNetwork() {
      log.debug("getNetwork - start");
      if (alogirthmState != AlgorithmState.FINISHED) {
         log.debug("getNetwork - end: Algoruthm is running still!");
         throw new IllegalStateException("Algorithm is running still!");
      }
      Network network = buildingListener.getNetworkCopy();
      log.debug("getNetwork - end: {}", network);
      return network;
   }

   /** Free up used resources. */
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
      log.debug("checkAlgorithmEnd - start: state={}", state);
      if (state.getType() == AutonetworkStateType.S_START) {
         try {
            if(state.getAdditionalData(1) == 0) {
               alogirthmState = AlgorithmState.FINISHED;
            }
         } catch (IllegalArgumentException ex) {
            log.warn("Received autonetwork message doesn't contain count of remaing waves.");
         }
         log.debug("checkAlgorithmEnd - end: algorithmState={}", alogirthmState);
      }else{
         log.debug("checkAlgorithmEnd - end: AutonetworkState doesn't contain information about count of remaing waves.");
      }
   }
   
   private void setNumberValue(int value, int pos){
      log.debug("setNumberValue - start: value={}, pos={}", value, pos);
      // getting EEEPROM DI
      EEPROM eeprom = coord.getDeviceObject(EEPROM.class);
      if (eeprom == null) {
         String txt = "EEEPROM doesn't exist on Coordinator!";
         log.error(txt);
         throw new RuntimeException(txt);
      }

      // writing configuration
      eeprom.write(pos, new short[]{(short) value});
      //TODO check result
      log.debug("setNumberValue - end");
   }
   
   private void setBooleanValue(boolean value, int bytePos, int bitPos) {
      log.debug("setBooleanValue - start: value={}, bytePos={}, bitPos={}", 
              value, bytePos, bitPos);
      // getting EEEPROM DI
      EEPROM eeprom = coord.getDeviceObject(EEPROM.class);
      if (eeprom == null) {
         String txt = "EEEPROM doesn't exist on Coordinator!";
         log.error(txt);
         throw new RuntimeException(txt);
      }

      short shortValue = value == true ? (short) 1 : (short) 0;
      shortValue <<= bitPos;
      
      // writing configuration
      eeprom.write(bytePos, new short[]{shortValue});
      //TODO check result
      log.debug("setBooleanValue - end");
   }
}