
package com.microrisc.simply.protocol.mapping;

import com.microrisc.simply.SimplyException;

/**
 * Describes exceptions, which can occur during process of protocol mapping. 
 * 
 * @author Michal Konopa
 */
public class ProtocolMappingException extends SimplyException {
    public ProtocolMappingException(String descr) {
        super(descr);
    }
}
