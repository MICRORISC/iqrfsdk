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

package com.microrisc.spi.cinterion_protocol;

/**
 * Base abstract class of all response message types. 
 * 
 * @author Michal Konopa
 */
public abstract class AbstractResponseMessage {
    /** Message ID. */
    private final char id;
    
    
    /**
     * Creates new response message with specified identifier.
     * @param id ID of this message
     */
    protected AbstractResponseMessage(char id) {
        this.id = id;
    }

    /**
     * @return the ID of this message
     */
    public char getId() {
        return id;
    }
    
}
