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
package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def;

import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.logic.NodeApprover;

/**
 * Identify possible values to settings in autonetwork.
 * 
 * @author Martin Strouhal
 */
public enum AutonetworkValueType {
   DISCOVERY_TX_POWER(Integer.class, 0, 0), 
   BONDING_TIME(Integer.class, 1, 0), 
   TEMPORARY_ADDRESS_TIMEOUT(Integer.class, 2, 0), 
   UNBOND_AND_RESTART(Boolean.class, 3, 1), 
   /** Identify if should use approver and which approver. */
   APPROVER(NodeApprover.class, 3, 0);

   private Class<?> dataType;
   private int bytePos, bitPos;
   
   private AutonetworkValueType(Class<?> dataType, int bytePos, int bitPos){
      this.dataType = dataType;
      this.bytePos = bytePos;
      this.bitPos = bitPos;
   }
   
   public Class<?> getDataType(){
      return dataType;
   }
   
   public int getBytePos(){
      return bytePos;
   }
   
   public int getBitPos(){
      return bitPos;
   }
}
