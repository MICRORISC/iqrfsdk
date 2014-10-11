

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
