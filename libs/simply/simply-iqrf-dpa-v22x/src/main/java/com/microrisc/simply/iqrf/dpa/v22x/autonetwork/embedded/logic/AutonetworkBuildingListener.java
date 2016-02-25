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
package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.logic;

import com.microrisc.simply.BaseNetwork;
import com.microrisc.simply.Network;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkState;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.AutonetworkStateType;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.embedded.def.DynamicNetwork;
import com.microrisc.simply.iqrf.dpa.v22x.init.NodeFactory;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides services for creating dynamic network while autonetwork embedded is
 * running.
 *
 * @author Martin Strouhal
 */
public class AutonetworkBuildingListener implements AutonetworkStateListener {

   private static final Logger log = LoggerFactory.getLogger(
           AutonetworkBuildingListener.class);

   private DynamicNetwork network;

   public AutonetworkBuildingListener(Network srcNetwork) {
      if (srcNetwork == null) {
         String txt = "Source network cannot be null!";
         log.warn(txt);
         throw new IllegalArgumentException(txt);
      }
      network = new DynamicNetwork(srcNetwork.getId(), new HashMap<>(srcNetwork.
              getNodesMap()));
   }

   @Override
   public void onAutonetworkState(AutonetworkState state) {
      log.debug("onAutonetworkState - start: state={}", state);
      if (state.getType() == AutonetworkStateType.S_Authorize) {
         addNodeWithAllPeripherals(state.getAdditionalData(0));
      } else if (state.getType() == AutonetworkStateType.S_CheckAuthorizedLoop) {
         removeNode(state.getAdditionalData(0));
      }
      log.debug("onAutonetworkState - end");
   }

   private void addNodeWithAllPeripherals(int nodeIndex) {
      log.debug("addNodeWithAllPeripherals - start: nodeIndex={}", nodeIndex);
      try {
         network.addNode(NodeFactory.createNodeWithAllPeripherals(
                 network.getId(),
                 Integer.toString(nodeIndex))
         );
      } catch (Exception ex) {
         log.error("addNodeWithAllPeripherals - error: " + ex);
      }
      log.debug("addNodeWithAllPeripherals - end");
   }

   private void removeNode(int nodeIndex) {
      String stringNodeIndex = Integer.toString(nodeIndex);
      network.removeNode(stringNodeIndex);
   }

   public Network getNetworkCopy() {
      return new BaseNetwork(network.getId(), new HashMap<>(network.
              getNodesMap()));
   }

   @Override
   public void destroy() {
      network.destroy();
   }
}
