/* 
 * Copyright 2014 MICRORISC s.r.o.
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

package com.microrisc.simply.iqrf.dpa.v22x.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v22x.types.AddressingInfo;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNode;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Parameter;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveredNodes;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveryParams;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveryResult;
import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.dpa.v22x.types.RoutingHops;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA Coordinator Device Interface.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if an error has occurred during processing
 * of this method.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface Coordinator
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        GET_ADDRESSING_INFO,
        GET_DISCOVERED_NODES,
        GET_BONDED_NODES,
        CLEAR_ALL_BONDS,
        BOND_NODE,
        REMOVE_BONDED_NODE,
        REBOND_NODE,
        RUN_DISCOVERY,
        SET_DPA_PARAM,
        SET_HOPS,
        DISCOVERY_DATA,
        BACKUP,
        RESTORE,
        AUTHORIZE_BOND,
        ENABLE_REMOTE_BONDING,
        READ_REMOTELY_BONDED_MODULE_ID,
        CLEAR_REMOTELY_BONDED_MODULE_ID
    }
    
    // ASYNCHRONOUS METHODS
    
    /**
     * Sends method call request for returning information about addressing.
     * @return unique identifier of sent request
     */
    UUID async_getAddressingInfo();
    
    /**
     * Sends method call request for returning information about discovered nodes.
     * @return unique identifier of sent request
     */
    UUID async_getDiscoveredNodes();
    
    /**
     * Sends method call request for returning information about bonded nodes.
     * @return unique identifier of sent request
     */
    UUID async_getBondedNodes();
    
    /**
     * Sends method call request for clearing all bonds.
     * @return unique identifier of sent request
     */
    UUID async_clearAllBonds();
    
    /**
     * Sends method call request for bond specified node.
     * @param address a requested address for the bonded node. The address must 
     *        not be used (bonded) yet. If this parameter equals to 0, then 1st 
     *        free address is assigned to the node.
     * @param bondingMask bonding mask. See IQRF OS User's and Reference guides 
     *        (remote bonding, function bondNewNodeRemote)
     * @return unique identifier of sent request
     */
    UUID async_bondNode(int address, int bondingMask);
    
    /**
     * Sends method call request for removing already bonded node from the list
     * of bonded nodes at coordinator memory.
     * @param address address of the node to remove the bond to
     * @return unique identifier of sent request
     */
    UUID async_removeBondedNode(int address);
    
    /**
     * Sends method call request for putting specified node back to the list 
     * of boded nodes in the coordinator memory.
     * @param address address of the node to be re-bonded
     * @return unique identifier of sent request
     */
    UUID async_rebondNode(int address);
    
    /**
     * Sends method call request for running IQHESM discovery process.
     * @param discoveryParams discovery parameters
     * @return unique identifier of sent request
     */
    UUID async_runDiscovery(DiscoveryParams discoveryParams);
    
    /**
     * Sends method call request for setting DPA Parameters to specified value.
     * @param dpaParam DPA Parameter to use for settting
     * @return unique identifier of sent request
     */
    UUID async_setDPA_Param(DPA_Parameter dpaParam);
    
    /**
     * Sends method call request for allowing to specify fixed number of routing 
     * hops used to send the DPA request/response or to specify an optimization 
     * algorithm to compute number of routing hops.
     * @param hops routing hops to set <br>
     *        0x00, 0xFF: See a description of the parameter of function 
     *                    optimizeHops() in the IQRF documentation. <br>
     *        0x01 - 0xEF: Sets number of hops to value Requested/ResponseHops - 1. 
     *        Result of Discovery data command can be used to find out an optimal 
     *        number of hops based on destination node logical address or virtual 
     *        routing number respectively.
     * @return unique identifier of sent request
     */
    UUID async_setHops(RoutingHops hops);
    
    /**
     * Sends method call request for allowing to read coordinator internal discovery 
     * data. Discovery data can be used for instance for IQMESH network visualization 
     * and traffic optimization. <br>
     * Discovery data structure is documented at IQRF OS Operating System User's Guide.
     * @param address address of discovery data to read. Discovery data is read 
     *        from address 16 * Address from the external EEPROM.
     * @return unique identifier of sent request
     */
    UUID async_discoveryData(int address);
    
    /**
     * Sends method call request for allowing to read coordinator network info data 
     * that can be then restored to another coordinator in order to make a clone 
     * of the original coordinator.
     * @param index index of the block of data
     * @return unique identifier of sent request
     */
    UUID async_backup(int index);
    
    /**
     * Sends method call request for allowing to write previously backed up 
     * coordinator network data to the same  or another coordinator device. 
     * To execute the full restore all data blocks (in any order) obtained via 
     * Backup commands must be written to the device.
     * @param networkData one block of the coordinator network info data previously 
     *                    obtained via Backup command.
     * @return unique identifier of sent request
     */
    UUID async_restore(short[] networkData);
    
    /**
     * Sends method call request for authorization previously remotely bonded node. 
     * This gives the node the final network address. See IQRF documentation for 
     * more information about remote bonding concept.
     * @param address a requested address for the bonded node. The address must 
     *        not be used (bonded) yet. If this parameter equals to 0, then 1st 
     *        free address is assigned to the node.
     * @param moduleId Module ID (the lowest 2 bytes) of the node to be authorized.
     *        Module ID is obtained by calling 
     *        {@link Node#readRemotelyBondedModuleId() Read remotely bonded module ID }.
     * @return unique identifier of sent request
     */
    UUID async_authorizeBond(int address, short[] moduleId);
    
    /**
     * Sends method call request for putting node into a mode, that provides 
     * a remote bonding of maximum one new node.
     * @param bondingMask see IQRF OS User's and Reference guides (remote bonding, 
     *        function bondNewNodeRemote).
     * @param control bit.0 enables remote bonding mode. If enabled then previously 
     *        bonded node module ID is forgotten.
     * @param userData optional data that can be used at Reset Custom DPA 
     *        Handler event.
     * @return unique identifier of sent request
     */
    UUID async_enableRemoteBonding(int bondingMask, int control, short[] userData);
    
    /**
     * Sends method call request for returning module ID of the remotely bonded node.
     * @return unique identifier of sent request
     */
    UUID async_readRemotelyBondedModuleId();
    
    /**
     * Sends method call request for making node to forget module ID of the node
     * that was previously remotely bonded.
     * @return unique identifier of sent request
     */
    UUID async_clearRemotelyBondedModuleId();
    
    
    
    // SYNCHRONOUS WRAPPERS
    
    /**
     * Synchronous wrapper for {@link #async_getAddressingInfo() async_getAddressingInfo}
     * method.
     * @return addressing information
     */
    AddressingInfo getAddressingInfo();
    
    /**
     * Synchronous wrapper for {@link #async_getDiscoveredNodes() async_getDiscoveredNodes}
     * method.
     * @return information about discovered nodes
     */
    DiscoveredNodes getDiscoveredNodes();
    
    /**
     * Synchronous wrapper for {@link #async_getBondedNodes() async_getBondedNodes}
     * method.
     * @return information about bonded nodes
     */
    BondedNodes getBondedNodes();
    
    /**
     * Synchronous wrapper for {@link #async_clearAllBonds() async_clearAllBonds}
     * method.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType clearAllBonds();
    
    /**
     * Synchronous wrapper for {@link #async_bondNode(int, int)  async_bondNode}
     * method.
     * @param address a requested address for the bonded node. The address must 
     *        not be used (bonded) yet. If this parameter equals to 0, then 1st 
     *        free address is assigned to the node.
     * @param bondingMask bonding mask. See IQRF OS User's and Reference guides 
     *        (remote bonding, function bondNewNodeRemote)
     * @return information about bonded node
     */
    BondedNode bondNode(int address, int bondingMask);
    
    /**
     * Synchronous wrapper for {@link #async_removeBondedNode(int)  async_removeBondedNode}
     * method.
     * @param address address of the node to remove the bond to
     * @return Number of bonded network nodes
     */
    Integer removeBondedNode(int address);
    
    /**
     * Synchronous wrapper for {@link #async_rebondNode(int)  async_rebondNode}
     * method.
     * @param address address of the node to be re-bonded
     * @return Number of bonded network nodes
     */
    Integer rebondNode(int address);
    
    /**
     * Synchronous wrapper for {@link #async_runDiscovery(
     * com.microrisc.simply.iqrf.dpa.v220.types.DiscoveryParams) async_runDiscovery}
     * method.
     * @param discoveryParams discovery parameters
     * @return results of discovery process
     */
    DiscoveryResult runDiscovery(DiscoveryParams discoveryParams);
    
    /**
     * Synchronous wrapper for {@link #async_setDPA_Param(
     * com.microrisc.simply.iqrf.dpa.v220.types.DPA_Parameter) async_setDPA_Param}
     * method.
     * @param dpaParam DPA Parameter to use for settting
     * @return previous value of DPA Parameter
     */
    DPA_Parameter setDPA_Param(DPA_Parameter dpaParam);
    
    /**
     * Synchronous wrapper for {@link #async_setHops(
     * com.microrisc.simply.iqrf.dpa.v220.types.RoutingHops) async_setHops}
     * method.
     * @param hops routing hops to set <br>
     *        0x00, 0xFF: See a description of the parameter of function 
     *                    optimizeHops() in the IQRF documentation. <br>
     *        0x01 - 0xEF: Sets number of hops to value Requested/ResponseHops - 1. 
     *        Result of Discovery data command can be used to find out an optimal 
     *        number of hops based on destination node logical address or virtual 
     *        routing number respectively.
     * @return previous values of routing hops
     */
    RoutingHops setHops(RoutingHops hops);
    
    /**
     * Synchronous wrapper for {@link #async_discoveryData(int) async_discoveryData}
     * method.
     * @param address address of discovery data to read. Discovery data is read 
     *        from address 16 * Address from the external EEPROM.
     * @return Discovery data read from the coordinator private storage
     */
    short[] discoveryData(int address);
    
    /**
     * Synchronous wrapper for {@link #async_backup(int) async_backup}
     * method.
     * @param index index of the block of data
     * @return one block of the coordinator network info data
     */
    short[] backup(int index);
    
    /**
     * Synchronous wrapper for {@link #async_restore(short[]) async_restore}
     * method.
     * @param networkData one block of the coordinator network info data previously 
     *                    obtained via Backup command.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType restore(short[] networkData);
    
    /**
     * Synchronous wrapper for {@link #async_authorizeBond(int, short[]) async_authorizeBond}
     * method.
     * @param address a requested address for the bonded node. The address must 
     *        not be used (bonded) yet. If this parameter equals to 0, then 1st 
     *        free address is assigned to the node.
     * @param moduleId Module ID (the lowest 2 bytes) of the node to be authorized.
     *        Module ID is obtained by calling 
     *        {@link Node#readRemotelyBondedModuleId() Read remotely bonded module ID }.
     * @return information about bonded node
     */
    BondedNode authorizeBond(int address, short[] moduleId);
    
    /**
     * Synchronous wrapper for {@link #async_enableRemoteBonding(int, int, short[]) 
     * async_enableRemoteBonding} method.
     * @param bondingMask see IQRF OS User's and Reference guides (remote bonding, 
     *        function bondNewNodeRemote).
     * @param control bit.0 enables remote bonding mode. If enabled then previously 
     *        bonded node module ID is forgotten.
     * @param userData optional data that can be used at Reset Custom DPA 
     *        Handler event.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType enableRemoteBonding(int bondingMask, int control, short[] userData);
    
    /**
     * Synchronous wrapper for {@link #async_readRemotelyBondedModuleId() 
     * async_readRemotelyBondedModuleId} method.
     * @return module ID of the remotely bonded node.
     */
    RemotelyBondedModuleId readRemotelyBondedModuleId();
    
    /**
     * Synchronous wrapper for {@link #async_clearRemotelyBondedModuleId() 
     * async_clearRemotelyBondedModuleId} method.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType clearRemotelyBondedModuleId();
}
