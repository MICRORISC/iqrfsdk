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

/**
 * Describes individual types of {@link AutonetworkState}.
 *
 * @author Martin Strouhal
 */
public enum AutonetworkStateType {

   /**Reports state, number of nodes in the network and number of remaining waves (stored at PeripheralRam[0]) */
   S_START(0x00, "\n\nNext wave was started\nnCount of nodes = %, count of remaing waves = %"),
   S_WaitNodesStartPrebonding(0x01,"Waiting on starting for prebonding of nodes."),
   /** reported */
   S_C_StartPrebonding(0x02, "Enable prebonding at coordinator."),
   S_WaitPrebonding(0x03, "Waiting for prebonding of nodes."),
   /** Optionally reports state and lower 2 bytes of MID of node prebonded by coordinator */
   S_DisablePrebondingCoordinator(0x04, "Disable prebonding at coordinator.\nCCoordinator prebonded MID = %, user data = %"), //reported
   S_WaitApproveNodeForCoordinator(0x05, "Waiting for approving nodes on coordinator."),
   S_DisablePrebondingNodes(0x06, "Disabling prebonding of nodes."),
   /** Reports state, address of the node, that provided prebonding and 0 */
   S_ReadPrebondedMid(0x07, "Reading of prebonded MID. Address of the node that provided prebonding is %"),
   /** Reports state and lower 2 bytes of MID of prebonded node */
   S_WaitReadPrebondedMid(0x08, "Waiting for reading of prebonded MID (%, %)"),
   S_ReadPrebondedMidNext(0x09, "Reading next prebonded MID."),
   /** Reports state, new address of the authorized node and 0 */
   S_Authorize(0x0A, "Authorizating node with address %"),
   S_WaitAuthorize(0x0B, "Waiting for authorization result."),
   S_AuthorizeNext(0x0C, "Authorizating next."),
   /** reported */
   S_CheckAuthorized(0x0D, "Running FRC to check new nodes."),
   /** If node does not response to FRC from S_CheckAuthorized then Reports state, address of the node (its bond is removed at coordinator) and 0 */
   S_CheckAuthorizedLoop(0x0E, "Checking authorization loop. Node % didn't response to FRC checking. Node will be unbonded."),
   S_WaitCheckAuthorizedLoop(0x0F, "Waiting for checking authorization loop."),
   S_CheckAuthorizedNext(0x10, "Checking next authorized."),
   /** Reports state, nodes in the network and number of discovered nodes */
   S_Discovery(0x11, "Running discovery.\nCount of nodes in network = %, discovered = %."),
   S_WaitApproveNodeForNode(0x12, "Waiting for approving node for node."),
   S_ApproveNodeForNodeFinish(0x13, "Approving node for finishing node."),
   UNKNOWN(Integer.MAX_VALUE, "Unknown state.");

   private final int stateId;
   private final String info;

   /**
    * Private constructor for creating of states.
    *
    * @param stateId id of state
    * @param info info of state, the info parameter must contain % instead of
    * unknown parameters, this % could be replaced while printing by real values
    */
   private AutonetworkStateType(int stateId, String info) {
      this.stateId = stateId;
      this.info = info;
   }

   /**
    * Returns state for specified stateId.
    *
    * @param stateId for which will be state returned
    * @return {@link AutonetworkState}
    */
   public static AutonetworkStateType getState(int stateId) {
      for (AutonetworkStateType e : values()) {
         if (e.getId() == stateId) {
            return e;
         }
      }
      return UNKNOWN;
   }

   /**
    * Return id of state.
    *
    * @return id as int
    */
   public int getId() {
      return stateId;
   }

   /**
    * Returns info about state.
    *
    * @return info as String
    */
   public String getInfo() {
      return info;
   }
}