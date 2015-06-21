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
package com.microrisc.simply;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class encapsulating connected networks.
 *
 * @author Michal Konopa
 * @author Martin Strouhal
 */
// June 2015 - Martin - implemented getNodes()
public class BaseNetwork implements Network {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BaseNetwork.class);

    /** ID of this network. */
    protected String id;

    /** Nodes map. */
    protected Map<String, Node> nodesMap;

    /**
     * Creates new network.
     *
     * @param id ID of the network
     * @param nodesMap mapping of identifiers of nodes to that nodes objects
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

    @Override
    public com.microrisc.simply.Node[] getNodes(String[] nodeIds) {
        com.microrisc.simply.Node[] nodes = new com.microrisc.simply.Node[nodeIds.length];
        for (int i = 0; i < nodeIds.length; i++) {
            nodes[i] = getNode(nodeIds[i]);
        }
        return nodes;
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
