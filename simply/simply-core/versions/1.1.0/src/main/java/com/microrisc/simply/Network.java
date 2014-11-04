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
