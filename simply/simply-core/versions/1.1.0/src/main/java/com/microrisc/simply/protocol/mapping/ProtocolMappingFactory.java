
package com.microrisc.simply.protocol.mapping;

/**
 * Protocol mapping factory.
 * 
 * @author Michal Konopa
 */
public interface ProtocolMappingFactory {
    /**
     * Creates protocol mapping implementation.
     * @return protocol mapping implementation.
     * @throws Exception if an error has occured during creation 
     */
    ProtocolMapping createProtocolMapping() throws Exception;
}
