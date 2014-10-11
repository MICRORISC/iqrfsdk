

package com.microrisc.simply.di_services;

/**
 * Performes transformation of object method IDs to String representations used
 * inside Simply.
 * 
 * @author Michal Konopa
 */
public interface MethodIdTransformer {
    /**
     * Transforms specified method ID and returns its String representation.
     * @param methodId method ID to transform
     * @return transformed method ID representation
     */
    String transform(Object methodId); 
}
