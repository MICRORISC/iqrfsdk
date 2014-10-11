
package com.microrisc.simply.types;


/**
 * Base class for conversion functionality between generic primitive types and 
 * Java built-in primitive types. 
 * 
 * @author Michal Konopa
 */
public abstract class PrimitiveConvertor extends AbstractConvertor {
    /**
     * Returns size(in bytes) of corresponding generic type.
     * @return size(in bytes) of corresponding generic type.
     */
    abstract public int getGenericTypeSize();
}
