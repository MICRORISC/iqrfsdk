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
package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Encapsulates state of Autonetwork machine.
 *
 * @author Martin Strouhal
 */
public class AutonetworkState {

   private AutonetworkStateType type;
   private List<Integer> additionalData = new LinkedList<>();

   /**
    * Creates a new AutonetworkState with predefined AutonetwrokStateType.
    *
    * @param type predefined type of state
    */
   public AutonetworkState(AutonetworkStateType type) {
      this.type = type;
   }

   /**
    * Add additional data into AutonetworkState.
    *
    * @param dataToAdd number as integer
    */
   public void addAdditionalData(int dataToAdd) {
      additionalData.add(dataToAdd);
   }

   public int getAdditionalData(int dataIndex) {
      if (dataIndex < additionalData.size()) {
         return additionalData.get(dataIndex);
      } else {
         throw new IllegalArgumentException("Data index is out of range!");
      }
   }

   public AutonetworkStateType getType() {
      return type;
   }

   @Override
   public String toString() {
      // count of parameters which are indetifaceted like %, see AutonetworkStateType
      int countOfDataInInfo = new StringTokenizer(" " + type.getInfo() + " ",
              "%").countTokens() - 1;

      // new string containg text value of this object
      String newString = "";

      newString += type.getInfo();
      int additionalDataIndex = 0;
      while (newString.contains("%") && additionalDataIndex < additionalData.
              size()) {
         newString = newString.replaceFirst("%",
                 additionalData.get(additionalDataIndex++).toString()
         );
      }

      return newString;
   }
}
