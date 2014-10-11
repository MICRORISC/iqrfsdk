
package com.microrisc.simply.asynchrony;

import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.AbstractMessage.MessageSource;

/**
 * Base class of asynchronous messages. Encapsulates information about asynchronous 
 * messages comming from underlaying networks.
 * 
 * @author Michal Konopa
 */
public class BaseAsynchronousMessage extends AbstractMessage {
    /**
     * Creates new asynchronous message with specicfied data.
     * @param mainData effective main data of this message
     * @param additionalData effective additional data of this message
     * @param messageSource source of this message
     */
    public BaseAsynchronousMessage(
            Object mainData, Object additionalData, MessageSource messageSource
    ) {
        super(mainData, additionalData, messageSource);
    }
}
