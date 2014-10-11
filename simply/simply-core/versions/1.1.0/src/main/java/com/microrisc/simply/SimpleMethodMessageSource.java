
package com.microrisc.simply;

/**
 * Simple implementation of {@code MethodMessageSource} interface.
 * 
 * @author Michal Konopa
 */
public final class SimpleMethodMessageSource 
implements BaseCallResponse.MethodMessageSource {
    /** Reference to object, which implements Message Source. */
    private final AbstractMessage.MessageSource source;
    
    /** Device interface. */
    private final Class devInterface;

    /** Device interface method identifier. */
    private final String methodId;

    
    /**
     * Creates new method sender data.
     * @param source "basic" message source
     * @param devInterface source device interface
     * @param methodId source method ID
     */
    public SimpleMethodMessageSource(
            AbstractMessage.MessageSource source, Class devInterface, String methodId
    ) {
        this.source = source;
        this.devInterface = devInterface;
        this.methodId = methodId;
    }
    
    /**
     * @return network ID
     */
    @Override
    public String getNetworkId() {
        return source.getNetworkId();
    }

    /**
     * @return node ID
     */
    @Override
    public String getNodeId() {
        return source.getNodeId();
    }
    
    /**
     * @return device interface
     */
    @Override
    public Class getDeviceInterface() {
        return devInterface;
    }

    /**
     * @return method identifier
     */
    @Override
    public String getMethodId() {
        return methodId;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                source.toString() + 
                "dev interface=" + devInterface +
                ", method ID=" + methodId + 
                " }");
    }
}
