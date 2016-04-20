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

import com.microrisc.simply.Network;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides network allowing dynamic changes in its structure.
 *
 * @author Martin Strouhal
 */
public class DynamicNetwork implements Network {

   private final String networkId;
   private final Map<String, com.microrisc.simply.Node> nodesMap;


   public DynamicNetwork(String id, Map<String, com.microrisc.simply.Node> nodesMap) {
      this.networkId = id;
      this.nodesMap = nodesMap;
   }

   @Override
   public String getId() {
      return networkId;
   }

   @Override
   public com.microrisc.simply.Node getNode(String nodeId) {
      return nodesMap.get(nodeId);
   }

   @Override
   public Map<String, com.microrisc.simply.Node> getNodesMap() {
      return new HashMap<>(nodesMap);
   }

   @Override
   public com.microrisc.simply.Node[] getNodes(String[] nodeIds) {
      com.microrisc.simply.Node[] nodes = new com.microrisc.simply.Node[nodeIds.length];
      for (int i = 0; i < nodeIds.length; i++) {
         nodes[i] = getNode(nodeIds[i]);
      }
      return nodes;
   }

   public void addNode(com.microrisc.simply.Node node) {
      nodesMap.put(node.getId(), node);
   }
   
   public void removeNode(String nodeId){
      nodesMap.remove(nodeId);
   }

   public void destroy() {
      nodesMap.clear();
   }
}