
package com.microrisc.simply;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class of Simply implementations.
 * 
 * @author Michal Konopa
 */
public class BaseSimply implements Simply {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BaseSimply.class);
 
    
    /** Connection stack. */
    protected ConnectionStack connStack;
    
    /** Networks map. */
    protected Map<String, Network> networksMap;
    
    
    /**
     * Creates new base Simply object.
     * @param connStack connection stack
     * @param networksMap map of networks IDs to networks themselves
     */
    public BaseSimply(
            ConnectionStack connStack, 
            Map<String, Network> networksMap
    ) {
        this.connStack = connStack;
        this.networksMap = new HashMap<String, Network>(networksMap);
    }
    
    /**
     * @return {@code null} if the network hasn't been found
     * @throws UnsupportedOperationException if the network doesn't support
     *         specified network interface
     */
    @Override
    public <T> T getNetwork(String networkId, Class<T> type) {
        logger.debug("getMapOfNetwork - start: networkId={}, type={}", 
                networkId ,type);
        
        Network network = networksMap.get(networkId);
        if (network == null) {
            logger.debug("getMapOfNetwork - end: null");
            return null;
        }
        
        if (type.isInstance(network)) {
            T typedNetwork = type.cast(network);
            logger.debug("getMapOfNetwork - end: {}", typedNetwork);
            return typedNetwork;
        }
        throw new UnsupportedOperationException(
                "Required network doesn't support specified interface"
        );
    }
    
    /**
     * @throws UnsupportedOperationException if any of the networks doesn't support
     *         specified network interface
     */
    @Override
    public <T> Map<String, T> getMapOfNetworks(Class<T> type) {
        logger.debug("getMapOfNetworks - start: type={}", type);
        
        Map<String, T> mapToReturn = new HashMap<String, T>();
        for (Map.Entry<String, Network> pair : networksMap.entrySet()) {
            if (type.isInstance(pair.getValue())) {
                mapToReturn.put(pair.getKey(), type.cast(pair.getValue()));
            } else {
                throw new UnsupportedOperationException("Network" + 
                        pair.getKey() + " doesn't support specified interface");
            }
        }
        
        logger.debug("getMapOfNetworks - end: {}", mapToReturn);
        return mapToReturn;
    }
    
    @Override
    public void destroy() {
        logger.debug("destroy - start: ");
        
        // destroy the stack
        connStack.destroy();
        
        // destroy networks
        for (Network network : networksMap.values()) {
            if (network instanceof BaseNetwork) {
                ((BaseNetwork)network).destroy();
            }
        }
        networksMap.clear();
        
        logger.info("Destroy complete");
        logger.debug("destroy - end ");
    }
}
