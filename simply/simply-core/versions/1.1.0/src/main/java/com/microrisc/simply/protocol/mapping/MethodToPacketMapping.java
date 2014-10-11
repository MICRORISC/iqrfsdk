
package com.microrisc.simply.protocol.mapping;

import java.util.List;

/**
 * Describes mapping between Device Interface's method into packet of  
 * protocol layer.
 * 
 * @author Michal Konopa
 */
public final class MethodToPacketMapping {
    /** Constant mappings */
    private List<ConstValueToPacketMapping> constantMappings = null; 
    
    /** Method argument mappings. */
    private List<ValueToPacketMapping> argMappings = null; 
    
    
    /**
     * Constructor.
     * @param constantMappings constant mappings
     * @param argMappings method argument mappings
     */
    public MethodToPacketMapping(List<ConstValueToPacketMapping> constantMappings, 
            List<ValueToPacketMapping> argMappings) {
        this.constantMappings = constantMappings;
        this.argMappings = argMappings;
    }
    
    /**
     * @return constant mappings
     */
    public List<ConstValueToPacketMapping> getConstantMappings() {
        return constantMappings;
    }

    /**
     * @return the argument mappings
     */
    public List<ValueToPacketMapping> getArgMappings() {
        return argMappings;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "constant mappings=" + constantMappings + 
                ", arg mappings=" + argMappings + 
                " }");
    }
}
