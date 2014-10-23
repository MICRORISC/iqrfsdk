
package com.microrisc.simply.network;

import java.util.Map;

/**
 * Interface to information about connections to used networks.
 * 
 * @author Michal Konopa
 */
public interface NetworkConnectionStorage {
    /**
     * Returns connection information for access to the network specified by
     * {@code networkId} parameter.
     * @param networkId ID of network, which 
     * @return connection information for access to the network specified by
     *         {@code networkId} parameter.
     */
    AbstractNetworkConnectionInfo getNetworkConnectionInfo(String networkId); 
    
    /**
     * Returns ID of network, which uses specified connection information.
     * @param connectionInfo connection information of network, whose ID to return
     * @return ID of network, which uses specified connection information.
     */
    String getNetworkId(AbstractNetworkConnectionInfo connectionInfo);
    
    /**
     * Returns map of connection information for all networks.
     * @return map of connection information for all networks. Keys are IDs of
     *         networks, values represents connection information for corresponding
     *         networks
     */
    Map<String, AbstractNetworkConnectionInfo> getAllNetworkConnectionInfo();
}
