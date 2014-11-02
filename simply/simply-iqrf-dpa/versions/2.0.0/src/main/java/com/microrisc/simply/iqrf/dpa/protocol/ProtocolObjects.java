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

package com.microrisc.simply.iqrf.dpa.protocol;

import com.microrisc.simply.protocol.mapping.ProtocolMapping;

/**
 * Provides access to various objects relating to the protocol implementation, 
 * which are currently in use, especially mapppers etc.
 * 
 * @author Michal Konopa
 */
public final class ProtocolObjects {
    private static PeripheralToDevIfaceMapper _perToDevIfaceMapper = null;
    private static ProtocolMapping _protocolMapping = null;
    
    
    /**
     * Initializes Protocol Objects.
     * @param perToDevIfaceMapper Peripheral to Device Interface mapper
     * @param protocolMapping protocol mapping
     */
    public static void init(
            PeripheralToDevIfaceMapper perToDevIfaceMapper,
            ProtocolMapping protocolMapping
    ) {
        _perToDevIfaceMapper = perToDevIfaceMapper;
        _protocolMapping = protocolMapping;
    }
    
    /**
     * Returns Peripheral to Device Interface mapper.
     * @return Peripheral to Device Interface mapper.
     */
    public static PeripheralToDevIfaceMapper getPeripheralToDevIfaceMapper() {
        return _perToDevIfaceMapper;
    }

    /**
     * Returns protocol mapping.
     * @return protocol mapping
     */
    public static ProtocolMapping getProtocolMapping() {
        return _protocolMapping;
    }
    
}
