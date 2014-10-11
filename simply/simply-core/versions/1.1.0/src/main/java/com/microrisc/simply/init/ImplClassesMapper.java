
package com.microrisc.simply.init;

/**
 * Stores mapping between Device Interfaces classes and classes, which
 * implement these interfaces.
 * 
 * @author Michal Konopa
 */
public interface ImplClassesMapper {

    /**
     * Returns Class object of class, which implements device interface specified
     * by its Class object.
     * @param ifaceClass Class of device interface
     * @return Class object of the implementing class
     */
    Class getImplClass(Class ifaceClass);
    
}
