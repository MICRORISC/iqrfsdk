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

/**
 * Simple implementation of {@code MessageSource} interface.
 * 
 * @author Michal Konopa
 */
public final class SimpleMessageSource 
implements AbstractMessage.MessageSource {
    /** Source network. */
    private final String networkId;

    /** Source node. */
    private final String nodeId;

    /**
     * Creates new simple message source data.
     * @param networkId ID of source network
     * @param nodeId ID of source node
     */
    public SimpleMessageSource(String networkId, String nodeId) {
        this.networkId = networkId;
        this.nodeId = nodeId;
    }

    /**
     * @return network ID
     */
    @Override
    public String getNetworkId() {
        return networkId;
    }

    /**
     * @return node ID
     */
    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return ("{ " +
                "networkId=" + networkId +
                ", node ID=" + nodeId + 
                " }");
    }
}
