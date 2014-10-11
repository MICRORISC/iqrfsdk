
package com.microrisc.simply.protocol.mapping;

import java.util.List;
import java.util.Map;

/**
 * Describes Device Interface mapping into packet of used protocol layer.
 * 
 * @author Michal Konopa
 */
public final class InterfaceToPacketMapping{ 
    /** Constant mappings */
    private List<ConstValueToPacketMapping> constantMappings = null;
    
    /** method mappings */
    private Map<String, MethodToPacketMapping> methodMappings = null;
    
    
    
    /**
     * Constructor. 
     * @param constantMappings constant mappings
     * @param methodMappings method mappings
     */
    public InterfaceToPacketMapping(List<ConstValueToPacketMapping> constantMappings, 
            Map<String, MethodToPacketMapping> methodMappings) {
        this.constantMappings = constantMappings;
        this.methodMappings = methodMappings;
    }
    
    /**
     * Returns method mapping correponding to specified method ID.
     * @param methodId method ID
     * @return method mapping correponding to specified method ID.
     * @return {@code null}, if such mapping was not found
     */
    public MethodToPacketMapping getMethodMapping(String methodId) {
        return methodMappings.get(methodId);
    } 
    
    /**
     * @return constant mappings.
     */
    public List<ConstValueToPacketMapping> getConstantMappings() {
        return constantMappings;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "constant mappings=" + constantMappings + 
                ", method mappings=" + methodMappings + 
                " }");
    }
}
