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

import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.logic.NodeApprover;
import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;

/**
 * Simple implementation of {@link NodeApprover}.
 *
 * @author Martin Strouhal
 */
public class SimpleNodeApprover implements NodeApprover {

   @Override
   public boolean approveNode(RemotelyBondedModuleId approvingMsg) {
       
      // there can be implemented logic of approving based on parameters,
      // for more details {@see RemotelyBondedModuleId}
      
      // if it's returned true, node with specified parameters will be approved
      return true;
   }
}
