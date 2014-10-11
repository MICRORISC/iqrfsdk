
package com.microrisc.simply.protocol;

import com.microrisc.simply.protocol.mapping.ProtocolMapping;

/**
 * Abstract base class for messge convertors.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractMessageConvertor implements MessageConvertor {
    /** Protocol mapping. */
    protected ProtocolMapping protocolMapping = null;
    
    
    /**
     * Protected constructor. 
     * @param protocolMapping protocol mapping
     */
    protected AbstractMessageConvertor(ProtocolMapping protocolMapping) {
        this.protocolMapping = protocolMapping;
    }
}
