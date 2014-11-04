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
 * Base abstract class for Simply initialization objects factories.
 * 
 * @author Michal Konopa
 * @param <T> type of configuration
 * @param <U> type of initialization objects
 */
public abstract class AbstractInitObjectsFactory
<T extends Object, U extends InitObjects> {
    
    /**
     * Returns Simply initialization objects.
     * @param configuration configuration of initialization objects
     * @return Simply initialization objects
     * @throws Exception if an error has occured
     */
    public abstract U getInitObjects(T configuration) throws Exception;
}
