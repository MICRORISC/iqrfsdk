
package com.microrisc.simply;

import java.util.Map;

/**
 * Main interface for access to Simply functionality.
 * 
 * @author Michal Konopa
 */
public interface Simply {
    /**
     * Returns reference to network as specified by its identifier.
     * @param <T> type of network's interface
     * @param networkId identifier of network to return
     * @param type Class object of the interface, which is implemented by the returned network
     * @return reference to network as specified by its identifier
     */
    <T> T getNetwork(String networkId, Class<T> type);
    
    /**
     * Returns mapping of identifiers of all networks to these networks themselves.
     * @param <T> type of network's interface
     * @param type Class object of the interface, which is implemented by all of 
             the networks in this Simply
     * @return a mapping of identifiers of all networks to these networks themselves.
     */
    <T> Map<String, T> getMapOfNetworks(Class<T> type);
    
    /**
     * Terminates this Simply run and frees up its used resources.
     */
    void destroy();
}
