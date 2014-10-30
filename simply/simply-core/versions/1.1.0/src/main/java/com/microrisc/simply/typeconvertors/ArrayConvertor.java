package com.microrisc.simply.typeconvertors;


/**
 * Base class for conversion functionality between generic array types and Java
 * built-in array types. 
 * 
 * @author Michal Konopa
 */
public abstract class ArrayConvertor extends AbstractConvertor {
    /** Convertor, which will be used for elements of the converted array. */
    protected PrimitiveConvertor elemConvertor;
    
    /**
     * Returns convertor for elements of the array.
     * @return convertor for elements of the array.
     */
    public PrimitiveConvertor getElemConvertor() {
        return elemConvertor;
    }
}
