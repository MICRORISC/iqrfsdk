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

import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.services.ServicesAccessor;
import java.util.Map;

/**
 * DPA extension of Network.
 * 
 * @author Michal Konopa
 */
public interface DPA_Network 
extends Network, ServicesAccessor {
    
    /**
     * Returns DPA Node bounded to specfied identifier.
     *
     * @param nodeId identifier of DPA Node to return
     * @return DPA Node bounded to specified identifier <br>
     *         {@code null}, if DPA Node with specified identifier hasn't been found
     */
    @Override
    DPA_Node getNode(String nodeId);

    /**
     * Returns mapping of identifiers of all DPA nodes to these nodes themselves.
     * All the Nodes in the returned map implements {@link DPA_Node} interface. 
     * 
     * @return mapping of identifiers of all nodes to these nodes themselves.
     */
    @Override
    Map<String, Node> getNodesMap();

    /**
     * Returns array of DPA Nodes bounded to specified identifiers.
     *
     * @param nodeIds identifiers of DPA Nodes to return
     * @return array of DPA Nodes bounded to specified identifiers <br>
     */
    @Override
    DPA_Node[] getNodes(String[] nodeIds);
}
