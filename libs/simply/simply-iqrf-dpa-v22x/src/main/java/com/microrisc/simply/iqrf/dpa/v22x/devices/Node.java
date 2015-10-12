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
import com.microrisc.simply.iqrf.dpa.v22x.types.NodeStatusInfo;
import com.microrisc.simply.iqrf.dpa.v22x.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA Node Device Interface.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if an error has occurred during processing
 * of this method.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface Node 
extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        READ,
        REMOVE_BOND,
        ENABLE_REMOTE_BONDING,
        READ_REMOTELY_BONDED_MODULE_ID,
        CLEAR_REMOTELY_BONDED_MODULE_ID,
        REMOVE_BOND_ADDRESS,
        BACKUP,
        RESTORE
    }
    
    // ASYNCHRONOUS METHODS
    
    /**
     * Sends method call request for reading IQMESH specific node information.
     * @return unique identifier of sent request
     */
    UUID async_read();
    
    /**
     * Sends method call request for removing bond on node side.
     * <p>
     * The bond is marked as unbonded (removed from network) using removeBond() IQRF call. 
     * Bonding state of the node at the coordinator side is not effected at all.
     * @return unique identifier of sent request
     */
    UUID async_removeBond();
    
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
     * Sends method call request for reading module ID of the remotely bonded node.
     * @return unique identifier of sent request
     */
    UUID async_readRemotelyBondedModuleId();
    
    /**
     * Sends method call request for making node to forget module ID of the node
     * that was previously remotely bonded.
     * @return unique identifier of sent request
     */
    UUID async_clearRemotelyBondedModuleId();
    
    /**
     * Sends method call request for removing bond address. The node stays in the 
     * IQMESH network (it is not unbonded) but a temporary address 0xFE is 
     * assigned to it.
     * @return unique identifier of sent request
     */
    UUID async_removeBondAddress();
    
    /**
     * Sends method call request for allowing to read node network info data 
     * that can be then restored to another node in order to make a clone of 
     * the original node.
     * @param index index of the block of data
     * @return unique identifier of sent request
     */
    UUID async_backup(int index);
    
    /**
     * Sends method call request for allowing to write previously backed up node 
     * network data to the same or another node device. To execute the full restore
     * all data blocks (in any order) obtained via Backup commands must be written 
     * to the device.
     * @param networkData one block of the node network info data previously 
     *                    obtained via Backup command.
     * @return unique identifier of sent request
     */
    UUID async_restore(short[] networkData);
    
    
    
    // SYNCHRONOUS WRAPPERS
    
    /**
     * Synchronous wrapper for {@link #async_read() async_read} method.
     * @return node's information
     */
    NodeStatusInfo read();
    
    /**
     * Synchronous wrapper for {@link #async_removeBond() async_removeBond} method.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType removeBond();
    
    /**
     * Synchronous wrapper for {@link #async_enableRemoteBonding(int, int, short[]) 
     * async_enableRemoteBonding} method
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
     * Synchronous wrapper for {@link 
     * #async_readRemotelyBondedModuleId() async_readRemotelyBondedModuleId} method.
     * @return module ID of the remotely bonded node
     */
    RemotelyBondedModuleId readRemotelyBondedModuleId();
    
    /**
     * Synchronous wrapper for {@link 
     * #async_clearRemotelyBondedModuleId()  async_clearRemotelyBondedModuleId} method.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType clearRemotelyBondedModuleId();
    
    /**
     * Synchronous wrapper for {@link #async_removeBondAddress() async_removeBondAddress} 
     * method.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType removeBondAddress();
    
    /**
     * Synchronous wrapper for {@link #async_backup(int) async_backup} method.
     * @param index index of the block of data
     * @return one block of the node network info data
     */
    short[] backup(int index);
    
    /**
     * Synchronous wrapper for {@link #async_restore(short[]) async_restore} method.
     * @param networkData one block of the node network info data previously 
     *                    obtained via Backup command.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType restore(short[] networkData);
}
