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

package com.microrisc.simply.protocol;

import com.microrisc.simply.protocol.mapping.ProtocolMapping;

/**
 * Abstract base class for messge convertors.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractMessageConvertor implements MessageConvertor {
    /** Protocol mapping. */
    protected ProtocolMapping protocolMapping = null;
    
    
    /**
     * Protected constructor. 
     * @param protocolMapping protocol mapping
     */
    protected AbstractMessageConvertor(ProtocolMapping protocolMapping) {
        this.protocolMapping = protocolMapping;
    }
}
