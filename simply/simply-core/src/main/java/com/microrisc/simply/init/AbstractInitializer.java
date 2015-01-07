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

import com.microrisc.simply.Network;
import java.util.Map;

/**
 * Base class of Simply initializers.
 * 
 * @author Michal Konopa
 * @param <T> type of initialiazation objects
 * @param <U> type of initialized networks
 */
public abstract class AbstractInitializer
<T extends InitObjects, U extends Network> {
    /**
     * Creates and returns map of initialized networks to use in Simply. 
     * @param initObjects objects needed in the initialization's process of Simply
     * @return map of networks
     * @throws Exception if an error has occured during initialization process
     */
    public abstract Map<String, U> initialize(T initObjects) throws Exception;
}
