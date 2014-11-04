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

package com.microrisc.simply;

/**
 * Simple implementation of {@code MethodMessageSource} interface.
 * 
 * @author Michal Konopa
 */
public final class SimpleMethodMessageSource 
implements BaseCallResponse.MethodMessageSource {
    /** Reference to object, which implements Message Source. */
    private final AbstractMessage.MessageSource source;
    
    /** Device interface. */
    private final Class devInterface;

    /** Device interface method identifier. */
    private final String methodId;

    
    /**
     * Creates new method sender data.
     * @param source "basic" message source
     * @param devInterface source device interface
     * @param methodId source method ID
     */
    public SimpleMethodMessageSource(
            AbstractMessage.MessageSource source, Class devInterface, String methodId
    ) {
        this.source = source;
        this.devInterface = devInterface;
        this.methodId = methodId;
    }
    
    /**
     * @return network ID
     */
    @Override
    public String getNetworkId() {
        return source.getNetworkId();
    }

    /**
     * @return node ID
     */
    @Override
    public String getNodeId() {
        return source.getNodeId();
    }
    
    /**
     * @return device interface
     */
    @Override
    public Class getDeviceInterface() {
        return devInterface;
    }

    /**
     * @return method identifier
     */
    @Override
    public String getMethodId() {
        return methodId;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                source.toString() + 
                "dev interface=" + devInterface +
                ", method ID=" + methodId + 
                " }");
    }
}
