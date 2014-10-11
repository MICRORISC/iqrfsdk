

package com.microrisc.simply.di_services;

import java.util.UUID;

/**
 * Bridge between asynchronous and synchronous access to services. 
 * 
 * @author Michal Konopa
 */
public interface ServiceAccessBridge {
    /**
     * Returns identifier of the last executed call request.
     * This method server as a bridge between asynchronous and synchronous
     * version of this interface.
     * @return identifier of last executed call request
     */
    UUID getIdOfLastExexutedCallRequest();
}
