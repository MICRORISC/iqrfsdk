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
