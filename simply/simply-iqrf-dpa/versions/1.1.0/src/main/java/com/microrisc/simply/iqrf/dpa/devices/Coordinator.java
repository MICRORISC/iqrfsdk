
package com.microrisc.simply.iqrf.dpa.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.types.AddressingInfo;
import com.microrisc.simply.iqrf.dpa.types.BondedNode;
import com.microrisc.simply.iqrf.dpa.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.types.DPA_Parameter;
import com.microrisc.simply.iqrf.dpa.types.DiscoveredNodes;
import com.microrisc.simply.iqrf.dpa.types.DiscoveryParams;
import com.microrisc.simply.iqrf.dpa.types.DiscoveryResult;
import com.microrisc.simply.iqrf.dpa.types.RemotelyBondedModuleId;
import com.microrisc.simply.iqrf.dpa.types.RoutingHops;
import com.microrisc.simply.iqrf.dpa.types.SubDPARequest;
import com.microrisc.simply.iqrf.types.VoidType;

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
extends DPA_Device, DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    
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
        BRIDGE,
        ENABLE_REMOTE_BONDING,
        READ_REMOTELY_BONDED_MODULE_ID,
        CLEAR_REMOTELY_BONDED_MODULE_ID
    }
    
    /**
     * Returns information about addressing information.
     * @return information about addressing information
     */
    AddressingInfo getAddressingInfo();
    
    /**
     * Returns information about discovered nodes.
     * @return information about discovered nodes
     */
    DiscoveredNodes getDiscoveredNodes();
    
    /**
     * Returns information about bonded nodes.
     * @return information about bonded nodes
     */
    BondedNodes getBondedNodes();
    
    /**
     * Clears all bonds.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType clearAllBonds();
    
    /**
     * Bonds specified address.
     * @param address a requested address for the bonded node. The address must 
     *        not be used (bonded) yet. If this parameter equals to 0, then 1st 
     *        free address is assigned to the node.
     * @param bondingMask bonding mask. See IQRF OS User's and Reference guides 
     *        (remote bonding, function bondNewNodeRemote)
     * @return information about bonded node
     */
    BondedNode bondNode(int address, int bondingMask);
    
    /**
     * Removes specified bonded address.
     * @param address address to remove
     * @return Number of bonded network nodes
     */
    Integer removeBondedNode(int address);
    
    /**
     * Rebonds specified bonded address.
     * @param address address to rebond
     * @return Number of bonded network nodes
     */
    Integer rebondNode(int address);
    
    /**
     * Runs discovery process.
     * @param discoveryParams discoveryParams
     * @return results of discovery process
     */
    DiscoveryResult runDiscovery(DiscoveryParams discoveryParams);
    
    /**
     * Sets DPA Parameters to specified value.
     * @param dpaParam DPA Parameter to use for settting
     * @return previous value of DPA Parameter
     */
    DPA_Parameter setDPA_Param(DPA_Parameter dpaParam);
    
    /**
     * Allows specifying fixed number of routing hops used to send the DPA 
     * request/response or to specify an optimization algorithm to compute 
     * number of routing hops.
     * @param hops routing hops to set
     * @return previous values of routing hops
     */
    RoutingHops setHops(RoutingHops hops);
    
    /**
     * Allows to read coordinator internal discovery data. Discovery data can 
     * be used for instance for IQMESH network visualization and optimization.
     * @param address address of discovery data
     * @return Discovery data read from the coordinator private storage
     */
    Short[] discoveryData(int address);
    
    /**
     * Allows to read coordinator network info data that can be then restored 
     * to another coordinator in order to make a clone of the original coordinator.
     * @param index index of the block of data
     * @return one block of the coordinator network info data
     */
    Short[] backup(int index);
    
    /**
     * Allows to write previously backed up coordinator network data to the same 
     * or another coordinator device. To execute the full restore all data blocks 
     * (in any order) obtained via Backup commands must be written to the device.
     * @param networkData one block of the coordinator network info data previously 
     *                    obtained via Backup command.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType restore(short[] networkData);
    
    /**
     * See {@link #restore(short[]) restore} method.
     * @param networkData one block of the coordinator network info data previously 
     *                    obtained via Backup command.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType restore(Short[] networkData);
    
    /**
     * Authorizes previously remotely bonded node. This give the node the final 
     * network address. See IQRF documentation for more information about remote 
     * bonding concept.
     * @param address a requested address for the bonded node. The address must 
     *        not be used (bonded) yet. If this parameter equals to 0, then 1st 
     *        free address is assigned to the node.
     * @param moduleId Module ID (the lowest 2 bytes) of the node to be authorized.
     * @return information about bonded node
     */
    BondedNode authorizeBond(int address, short[] moduleId);
    
   /**
    * This command supported by [NC] devices allows to send and receive DPA requests 
    * and responses to and from the nested networks, respectively.
    * @param subRequest full DPA request for nested network
    * @return Short[] full response from nested network
    */
    Short[] bridge(SubDPARequest subRequest);
    
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
     * @return module ID of the remotely bonded node.
     */
    RemotelyBondedModuleId readRemotelyBondedModuleId();
    
    /**
     * Makes node to forget module ID of the node that was previously remotely bonded.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType clearRemotelyBondedModuleId();
}
