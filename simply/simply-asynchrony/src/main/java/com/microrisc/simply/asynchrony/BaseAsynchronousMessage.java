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
