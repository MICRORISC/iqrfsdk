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

import java.util.UUID;

/**
 * Generic interface to container, which stores results of various call requests 
 * comming from a connector.
 * 
 * @author Michal Konopa
 * @param <T> type of results - items stored in the container
 */
public interface ResultsContainer<T extends Object> {
    /**
     * Returns call result associated with specified identifier.
     * @param uid identifier of call result to return
     * @return call result associated with specified identifier
     */
    T get(UUID uid);
    
    /**
     * Puts specified call result at specified key into container.
     * @param uid identifier, at which to put specified call result
     * @param callResult call result to put into container
     */
    void put(UUID uid, T callResult);
    
    /**
     * Removes call result with specified key from container.
     * @param uid key of call result to remove from container
     */
    void remove(UUID uid);
}
