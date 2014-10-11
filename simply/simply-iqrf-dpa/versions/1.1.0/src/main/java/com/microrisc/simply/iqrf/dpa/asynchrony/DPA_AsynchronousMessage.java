

package com.microrisc.simply.iqrf.dpa.asynchrony;

import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.asynchrony.BaseAsynchronousMessage;

/**
 * DPA asynchronous message.
 * 
 * @author Michal Konopa
 */
public final class DPA_AsynchronousMessage extends BaseAsynchronousMessage {
    
    /**
     * Message source extended with information about peripheral ID. 
     */
    public static interface DPA_AsynchronousMessageSource 
    extends AbstractMessage.MessageSource 
    {
       /**
        * Returns number of source peripheral.
        * @return number of source peripheral
        */
       int getPeripheralNumber();
    }
    
    
    public DPA_AsynchronousMessage(
            Object mainData, Object additionalData, DPA_AsynchronousMessageSource messageSource
    ) {
        super(mainData, additionalData, messageSource);
    }
    
    @Override
    public DPA_AsynchronousMessageSource getMessageSource() {
        return (DPA_AsynchronousMessageSource) messageSource;
    }
    
}
