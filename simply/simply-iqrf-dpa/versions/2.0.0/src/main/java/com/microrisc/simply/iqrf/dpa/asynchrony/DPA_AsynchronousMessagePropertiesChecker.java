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

import com.microrisc.simply.asynchrony.AsynchronousMessagePropertiesChecker;
import com.microrisc.simply.asynchrony.BaseAsynchronousMessagePropertiesChecker;

/**
 * DPA asynchronous message properties checker.
 * <p>
 * Its functionality is the same as the functionality of Base asynchronous
 * message properties checker - for {@code BaseAsynchronousMessage} and
 * {@code AsynchronousMessageProperties} parts of the message and of the 
 * required properties.
 * <p>
 * <b>Peripheral number checking is as follows: </b> <br>
 * - if the properties's peripheral number is not defined, then the result of 
 *    peripheral number checking is {@code true}. <br>
 * - if both message's peripheral number and properties's peripheral number are 
 *   defined, then the result of peripheral number checking {@code true}, if both
 *   numbers are equal. Otherwise, the result is {@code false}.
 * 
 * 
 * @author Michal Konopa
 */
public final class DPA_AsynchronousMessagePropertiesChecker
implements AsynchronousMessagePropertiesChecker<
            DPA_AsynchronousMessage, 
            DPA_AsynchronousMessageProperties
        >
{
    // checks peripheral number
    private boolean checkPeripheralNumber(int msgPerNumber, int reqPerNumber) {
        if ( reqPerNumber == DPA_AsynchronousMessageProperties.NOT_DEFINED ) {
            return true;
        }
        return ( msgPerNumber == reqPerNumber );
    }
    
    /** For checking of message's base properties. */ 
    private final BaseAsynchronousMessagePropertiesChecker baseChecker;
    
    
    /**
     * Creates new DPA asynchronous message properties checker.
     */
    public DPA_AsynchronousMessagePropertiesChecker() {
        this.baseChecker = new BaseAsynchronousMessagePropertiesChecker();
    }
    
    @Override
    public boolean messageHasRequiredProperties(
            DPA_AsynchronousMessage message, DPA_AsynchronousMessageProperties reqProps
    ) {
        if ( reqProps == null ) {
            return true;
        }
        
        if ( !baseChecker.messageHasRequiredProperties(message, reqProps)) {
            return false;
        }
        
        return checkPeripheralNumber(
                message.getMessageSource().getPeripheralNumber(), reqProps.getPeripheralNumber()
        );
    }
    
}
