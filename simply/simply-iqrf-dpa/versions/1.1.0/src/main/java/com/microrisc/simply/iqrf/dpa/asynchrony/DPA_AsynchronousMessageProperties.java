

package com.microrisc.simply.iqrf.dpa.asynchrony;

import com.microrisc.simply.asynchrony.AsynchronousMessageProperties;

/**
 * Access to properties of DPA asynchronous messages.
 * 
 * @author Michal Konopa
 */
public interface DPA_AsynchronousMessageProperties 
extends AsynchronousMessageProperties 
{
    /** Peripheral number not defined. */
    public static final int NOT_DEFINED = -1;
    
    /**
     * Returns peripheral number.
     * @return peripheral number
     */
    int getPeripheralNumber();
}
