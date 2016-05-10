/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa;

import com.microrisc.simply.Node;
import com.microrisc.simply.services.Service;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of DPA Network.
 * 
 * @author Michal Konopa
 */
public final class DPA_NetworkImpl implements DPA_Network {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_NetworkImpl.class);

    /** ID of this network. */
    private final String id;

    /** Nodes map. */
    private final Map<String, DPA_Node> nodesMap;

    /** Services map. */
    private final Map<Class, Service> servicesMap;
    
    
    /**
     * Creates new DPA network. No services will be provided to the user code.
     *
     * @param id ID of the network
     * @param nodesMap mapping of identifiers of nodes to that nodes objects
     */
    public DPA_NetworkImpl(String id, Map<String, DPA_Node> nodesMap) {
        this.id = id;
        this.nodesMap = nodesMap;
        this.servicesMap = new HashMap<>();
    }
    
    /**
     * Creates new DPA network with specified services.
     *
     * @param id ID of the network
     * @param nodesMap mapping of identifiers of nodes to that nodes objects
     * @param servicesMap services map to provide for user code
     */
    public DPA_NetworkImpl(
            String id, Map<String, DPA_Node> nodesMap, Map<Class, Service> servicesMap
    ) {
        this.id = id;
        this.nodesMap = nodesMap;
        this.servicesMap = new HashMap<>(servicesMap);
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public DPA_Node getNode(String nodeId) {
        return nodesMap.get(nodeId);
    }

    @Override
    public Map<String, Node> getNodesMap() {
        Map<String, Node> resultMap = new HashMap<>();
        for ( Map.Entry<String, DPA_Node> entry : nodesMap.entrySet() ) {
            resultMap.put(entry.getKey(), entry.getValue());
        }
        return resultMap;
    }

    @Override
    public DPA_Node[] getNodes(String[] nodeIds) {
        DPA_Node[] nodes = new DPA_Node[nodeIds.length];
        for (int i = 0; i < nodeIds.length; i++) {
            nodes[i] = getNode(nodeIds[i]);
        }
        return nodes;
    }

    /**
     * Clears up used resources.
     */
    public void destroy() {
        logger.debug("destroy - start: ");

        nodesMap.clear();
        logger.info("Destroyed");

        logger.debug("destroy - end");
    }

    @Override
    public <T> T getService(Class<T> service) {
        if ( servicesMap.containsKey(service)) {
            return (T)servicesMap.get(service);
        }
        return null;
    }

    @Override
    public Map<Class, Service> getServicesMap() {
        return new HashMap<>(servicesMap);
    }
}
