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
