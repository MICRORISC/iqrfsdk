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

import com.microrisc.simply.NetworkData;
import java.util.Arrays;

/**
 * Base class of network data exchanged between protocol and network layers.
 * 
 * @author Michal Konopa
 */
public class BaseNetworkData implements NetworkData {
    /** Effective data. */
    protected short[] data;
    
    /** ID of destination network. */
    protected String networkId;
    
    
    /**
     * Creates new network data object.
     * @param data effective data to send
     * @param networkId ID of destination network
     */
    public BaseNetworkData(short[] data, String networkId) {
        this.data = data;
        this.networkId = networkId;
    }

    /**
     * @return effective data
     */
    @Override
    public short[] getData() {
        return data;
    }

    /**
     * @return ID of destination network
     */
    @Override
    public String getNetworkId() {
        return networkId;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "data=" + Arrays.toString(data) +
                ", network ID=" + networkId + 
                " }");
    }
}
