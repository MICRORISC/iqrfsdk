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

/**
 * Stores mapping between Device Interfaces classes and classes, which
 * implement these interfaces.
 * 
 * @author Michal Konopa
 */
public interface ImplClassesMapper {

    /**
     * Returns Class object of class, which implements device interface specified
     * by its Class object.
     * @param ifaceClass Class of device interface
     * @return Class object of the implementing class
     */
    Class getImplClass(Class ifaceClass);
    
}
