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
 * @author Martin Strouhal
 */
// June 2015 - Martin - added getNodes()
public interface Network {

    /**
     * Returns identifier of this network.
     *
     * @return identifier of this network.
     */
    String getId();

    /**
     * Returns Node object bounded to specfied identifier.
     *
     * @param nodeId identifier of Node object to return
     * @return Node object bounded to specified identifier <br>
     * {@code null}, if Node object with specified identifier hasn't been
     * found
     */
    Node getNode(String nodeId);

    /**
     * Returns mapping of identifiers of all nodes to these nodes themselves.
     *
     * @return mapping of identifiers of all nodes to these nodes themselves.
     */
    Map<String, Node> getNodesMap();

    /**
     * Returns array of Node objects bounded to specified identifiers.
     *
     * @param nodeIds identifiers of Node objects to return
     * @return array of Node objects bounded to speicifed identifiers <br>
     * {@code null} for all Node objects which with specified indetifiers hasn't
     * been found
     */
    Node[] getNodes(String[] nodeIds);
}
