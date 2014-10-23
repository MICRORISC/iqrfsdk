
package com.microrisc.simply;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class encapsulating connected networks.
 * 
 * @author Michal Konopa
 */
public class BaseNetwork implements Network {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BaseNetwork.class);
    
    /** ID of this network. */
    protected String id;
    
    /** Nodes map. */
    protected Map<String, Node> nodesMap;
    
    
    /**
     * Creates new network.
     * @param nodesMap mapping of identifiers of nodes to that nodes
     */
    public BaseNetwork(String id, Map<String, Node> nodesMap) {
        this.id = id;
        this.nodesMap = nodesMap;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public Node getNode(String nodeId) {
        return nodesMap.get(nodeId);
    }
    
    @Override
    public Map<String, Node> getNodesMap() {
        return new HashMap<>(nodesMap);
    }
    
    /**
     * Clears map of nodes.
     */
    public void destroy() {
        logger.debug("destroy - start: ");
        
        nodesMap.clear();
        logger.info("Destroyed");
        
        logger.debug("destroy - end");
    }
}
