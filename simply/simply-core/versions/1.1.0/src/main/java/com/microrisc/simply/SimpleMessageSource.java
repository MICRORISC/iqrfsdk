
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
