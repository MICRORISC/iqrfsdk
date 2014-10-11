package com.microrisc.simply.init;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of {@code ImplClassesMapper} interface.
 * Immutable.
 * 
 * @author Michal Konopa
 */
public final class SimpleImplClassesMapper implements ImplClassesMapper  {
    /** 
     * Key: device iterface class, 
     * value: class, which implements that device interface. 
     */
    private final Map<Class, Class> ifaceToImpl;
    
    
    /**
     * Creates new implementing classes mapper. 
     * @param ifaceToImpl 
     */
    public SimpleImplClassesMapper(Map<Class, Class> ifaceToImpl) {
        this.ifaceToImpl = new HashMap<Class, Class>(ifaceToImpl);
    }        
    
    /**
     * Returns Class object of class, which implements device interface specified
     * by its Class object.
     * @param ifaceClass Class of device interface
     * @return Class object of implementing class
     */
    @Override
    public Class getImplClass(Class ifaceClass) {
        return ifaceToImpl.get(ifaceClass);
    }
}
