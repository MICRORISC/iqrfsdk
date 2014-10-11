
package com.microrisc.simply;

import java.util.Map;

/**
 * Main interface fot access to connected network functionality.
 * 
 * @author Michal Konopa
 */
public interface Network {
    /**
     * Returns identifier of this network.
     * @return identifier of this network.
     */
    String getId();
    
    /**
     * Returns SimpleNode object bounded to specfied identifier.
     * @param nodeId identifier of SimpleNode object to return
     * @return SimpleNode object bounded to specified identifier <br>
     *         {@code null}, if SimpleNode object with specified identifier hasn't been found
     */
    Node getNode(String nodeId);

    /**
     * Returns mapping of identifiers of all nodes to these nodes themselves.
     * @return mapping of identifiers of all nodes to these nodes themselves. 
     */
    Map<String, Node> getNodesMap();
}
