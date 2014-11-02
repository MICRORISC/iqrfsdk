/* 
 * Copyright 2014 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
