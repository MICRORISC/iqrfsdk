package com.microrisc.simply;

import java.util.UUID;

/**
 * Encapsulates information about executed Device Object method call.
 * 
 * @author Michal Konopa
 */
public class CallRequest { 
    /** Identifier of this request. */
    private final UUID id;
    
    /** Network identifier. */
    private final String networkId;
    
    /** Node identifier. */
    private final String nodeId;
    
    /** Device interface identifier. */
    private final Class devInterface;
    
    /** Method identifier. */
    private final String methodId;
    
    /** Method arguments values. */
    private final Object[] args;
    
    
    /**
     * Returns String representation of arguments.
     * @return String representation of arguments.
     */
    private String getArgsString() {
        StringBuilder sbArgs = new StringBuilder("[");
        for (int argId = 0; argId < args.length; argId++) {
            sbArgs.append(args[argId].toString());
            if (argId != (args.length - 1)) {
                sbArgs.append( ", ");
            }
        }
        sbArgs.append("]");
        return sbArgs.toString();
    }
    
    /**
     * Creates new call request according to specified parameters.
     * @param uid ID of this request
     * @param networkId ID of network
     * @param nodeId ID of node in the network
     * @param devInterface device interface
     * @param methodId ID of method, which has been called
     * @param args arguments of the called method
     */
    public CallRequest(UUID uid, String networkId, String nodeId, Class devInterface, 
            String methodId, Object[] args
    ) {
        this.id = uid;
        this.networkId = networkId;
        this.nodeId = nodeId;
        this.devInterface = devInterface;
        this.methodId = methodId;
        this.args = (args == null)? new Object[0] : args;
    }
    
    /**
     * @return identifier of this request 
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * @return network ID 
     */
    public String getNetworkId() {
        return networkId;
    }
    
    /**
     * @return node ID 
     */
    public String getNodeId() {
        return nodeId;
    }
    
    /**
     * @return device interface 
     */
    public Class getDeviceInterface() {
        return devInterface;
    }
    
    /**
     * @return ID of called method 
     */
    public String getMethodId() {
        return methodId;
    }
    
    /**
     * @return arguments of called method 
     */
    public Object[] getArgs() {
        return args;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "id=" + id +
                ", network ID=" + networkId + 
                ", node ID=" + nodeId + 
                ", device interface=" + devInterface.getName() + 
                ", method ID=" + methodId +
                ", arguments=" + getArgsString() + 
                " }");
    }
}
