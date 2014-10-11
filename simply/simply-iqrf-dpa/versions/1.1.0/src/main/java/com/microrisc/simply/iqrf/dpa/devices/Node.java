
package com.microrisc.simply.iqrf.dpa.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.types.NodeStatusInfo;
import com.microrisc.simply.iqrf.dpa.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.types.VoidType;

/**
 * DPA Node Device Interface.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if some error has occurred during processing
 * of this method.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface Node 
extends DPA_Device, DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
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
    
    /**
     * Returns IQMESH specific node information.
     * Synchronous wrapper for {@link #async_read() 
     * async_read} method.
     * @return node's information
     */
    NodeStatusInfo read();
    
    /**
     * Removes bond on node side.
     * Synchronous wrapper for {@link #async_removeBond() 
     * async_removeBond} method.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType removeBond();
    
    /**
     * Puts node into a mode, that provides a remote bonding of maximum one new node.
     * @param bondingMask see IQRF OS User's and Reference guides (remote bonding, 
     *        function bondNewNodeRemote).
     * @param control 0 enables remote bonding mode. If enabled then previously 
     *        bonded node module ID is forgotten.
     * @param userData optional data that can be used at Reset Custom DPA 
     *        Handler event.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType enableRemoteBonding(int bondingMask, int control, short[] userData);
    
    /**
     * Returns module ID of the remotely bonded node.
     * @return module ID of the remotely bonded node. <br>
     *         {@code null}, if some error occurrs during processing
     */
    RemotelyBondedModuleId readRemotelyBondedModuleId();
    
    /**
     * Makes node to forget module ID of the node that was previously remotely bonded.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType clearRemotelyBondedModuleId();
    
    /**
     * The node stays in the IQMESH network (it is not unbonded) but a temporary 
     * address 0xFE is assigned to it.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType removeBondAddress();
    
    /**
     * Allows to read node network info data that can be then restored 
     * to another node in order to make a clone of the original node.
     * @param index index of the block of data
     * @return one block of the node network info data
     */
    Short[] backup(int index);
    
    /**
     * Allows to write previously backed up node network data to the same 
     * or another node device. To execute the full restore all data blocks 
     * (in any order) obtained via Backup commands must be written to the device.
     * @param networkData one block of the node network info data previously 
     *                    obtained via Backup command.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType restore(short[] networkData);
    
    /**
     * See {@link #restore(short[]) restore} method.
     * @param networkData one block of the node network info data previously 
     *                    obtained via Backup command.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType restore(Short[] networkData);
}
