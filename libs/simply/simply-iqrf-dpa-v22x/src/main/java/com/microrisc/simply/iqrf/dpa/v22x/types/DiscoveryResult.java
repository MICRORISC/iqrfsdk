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

package com.microrisc.simply.iqrf.dpa.v22x.types;

/**
 * Encapsulates information about result of discovery process of IQMesh network.
 * 
 * @author Michal Konopa
 */
public final class DiscoveryResult {
    /** Number of discovered network nodes. */
    private final int discoveredNodesNum;
    
    
    /**
     * Creates new {@code DiscoveryResult} object.
     * @param discoveredNodesNum number of discovered network nodes
     */
    public DiscoveryResult(int discoveredNodesNum) {
        this.discoveredNodesNum = discoveredNodesNum;
    }

    /**
     * @return number of discovered network nodes
     */
    public int getDiscoveredNodesNum() {
        return discoveredNodesNum;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Discovered network nodes: " + discoveredNodesNum + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
