

package com.microrisc.simply.asynchrony;

import com.microrisc.simply.AbstractMessage.MessageSource;

/**
 * Access to properties of asynchronous messages.
 * 
 * @author Michal Konopa
 */
public interface AsynchronousMessageProperties {
    /**
     * Returns source of a message.
     * @return source of a message.
     */
    MessageSource getMessageSource();
    
    /**
     * Returns data type of main data.
     * @return data type of main data.
     */
    Class getTypeOfMainData();
    
    /**
     * Returns data type of additional data.
     * @return data type of additional data.
     */
    Class getTypeOfAdditionalData();
}
