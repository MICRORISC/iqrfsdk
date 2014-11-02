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

package com.microrisc.simply.init;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of {@code ImplClassesMapper} interface.
 * Immutable.
 * 
 * @author Michal Konopa
 */
public final class SimpleImplClassesMapper implements ImplClassesMapper  {
    /** 
     * Key: device iterface class, 
     * value: class, which implements that device interface. 
     */
    private final Map<Class, Class> ifaceToImpl;
    
    
    /**
     * Creates new implementing classes mapper. 
     * @param ifaceToImpl 
     */
    public SimpleImplClassesMapper(Map<Class, Class> ifaceToImpl) {
        this.ifaceToImpl = new HashMap<Class, Class>(ifaceToImpl);
    }        
    
    /**
     * Returns Class object of class, which implements device interface specified
     * by its Class object.
     * @param ifaceClass Class of device interface
     * @return Class object of implementing class
     */
    @Override
    public Class getImplClass(Class ifaceClass) {
        return ifaceToImpl.get(ifaceClass);
    }
}
