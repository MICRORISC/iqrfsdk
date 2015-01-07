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

import java.util.HashMap;
import java.util.Map;

/**
 * Base class of network connection storages.
 * 
 * @author Michal Konopa
 */
public class BaseNetworkConnectionStorage implements NetworkConnectionStorage {
    /** Mapping of networks IDs to theirs connection information. */
    protected Map<String, AbstractNetworkConnectionInfo> idToConnInfoMap;
    
    /** Mapping of connection information of networks to theirs IDs. */
    protected Map<AbstractNetworkConnectionInfo, String> connInfoToIdMap;
    
    
    /**
     * Protected constructor.
     * @param idToConnInfoMap map of networks IDs to theirs connection information
     * @param connInfoToIdMap map of connection information of networks to theirs IDs.
     */
    public BaseNetworkConnectionStorage(
            Map<String, AbstractNetworkConnectionInfo> idToConnInfoMap, 
            Map<AbstractNetworkConnectionInfo, String> connInfoToIdMap
    ) {
        this.idToConnInfoMap = new HashMap<String, AbstractNetworkConnectionInfo>(idToConnInfoMap);
        this.connInfoToIdMap = new HashMap<AbstractNetworkConnectionInfo, String>(connInfoToIdMap);
    }
    
    @Override
    public AbstractNetworkConnectionInfo getNetworkConnectionInfo(String networkId) {
        return idToConnInfoMap.get(networkId);
    }
    
    @Override
    public String getNetworkId(AbstractNetworkConnectionInfo connectionInfo) {
        return connInfoToIdMap.get(connectionInfo);
    }

    @Override
    public Map<String, AbstractNetworkConnectionInfo> getAllNetworkConnectionInfo() {
        Map<String, AbstractNetworkConnectionInfo> mapToReturn = 
                new HashMap<String, AbstractNetworkConnectionInfo>(idToConnInfoMap);
        return mapToReturn;
    }
}
