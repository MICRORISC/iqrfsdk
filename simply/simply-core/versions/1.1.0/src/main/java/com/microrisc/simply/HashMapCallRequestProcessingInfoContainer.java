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
 * Hash map implementation of {@code CallRequestProcessingInfoContainer} interface.
 * <p>
 * This implementation is based on 
 * {@link com.microrisc.simply.HashMapResultsContainer HashMapResultsContainer} 
 * class.
 * This results container implementation has 2 main fields: <br>
 * 1. Capacity - maximal number of items present in the conainer. Default = 10 <br>
 * 2. Maximal time[in ms] of existence of each item in the container. 
 *    Default = not used.<br>
 * 
 * @author Michal Konopa
 */
public class HashMapCallRequestProcessingInfoContainer 
implements CallRequestProcessingInfoContainer {
    /** Worker container to use. */
    private final HashMapResultsContainer<CallRequestProcessingInfo> container;
  
    
    /** Default capacity. */
    public static int DEFAULT_CAPACITY = 10;
    
    /** 
     * Default maximal time[in ms] of existence each item in the container. After
     * that will be that item disposed from container. 
     * 0 means not usage of this property
     */ 
    public static long DEFAULT_MAX_TIME_DURATION = 0;
    
    /** results capacity */
    private int capacity = DEFAULT_CAPACITY;

    /** maximal time duration of existence of each item in items */ 
    private long maxTimeDuration = DEFAULT_MAX_TIME_DURATION;
    
    
    /**
     * Creates new results container with default values of parameters: <br>
     * - capacity = 10 <br>
     * - max time duration = not used
     */
    public HashMapCallRequestProcessingInfoContainer() {
        container = new HashMapResultsContainer<CallRequestProcessingInfo>(
                DEFAULT_CAPACITY, DEFAULT_MAX_TIME_DURATION
        );
    }
    
    /**
     * Creates new results container with specified value of its capacity. Maximal
     * time duration paramter will not be used.
     * @param capacity container capacity
     */
    public HashMapCallRequestProcessingInfoContainer(int capacity) { 
        container = new HashMapResultsContainer<CallRequestProcessingInfo>(
                capacity, DEFAULT_MAX_TIME_DURATION
        );
    }
    
    /**
     * Creates new results container with specified value of maximal time duration.
     * Capacity will be set to 10 items.
     * @param maxTimeDuration maximal time duration of existence of each 
     *        call result in container. 0 means that this capability is not used
     *        Must be >= 0.
     */
    public HashMapCallRequestProcessingInfoContainer(long maxTimeDuration) { 
        container = new HashMapResultsContainer<CallRequestProcessingInfo>(
                DEFAULT_CAPACITY, maxTimeDuration
        );
    }
    
    /**
     * Creates new result container with parameters set to specified values.
     * @param capacity capacity of container ( in number of results ). Must be
     *                 > 0
     * @param maxTimeDuration maximal time duration of existence of each 
     *        call result in container. 0 means that this capability is not used
     *        Must be >= 0.
     */
    public HashMapCallRequestProcessingInfoContainer(int capacity, long maxTimeDuration) {
        container = new HashMapResultsContainer<CallRequestProcessingInfo>(
                capacity, maxTimeDuration
        );
    }
    
    @Override
    public CallRequestProcessingInfo get(UUID uid) {
        return container.get(uid);
    }

    @Override
    public void put(UUID uid, CallRequestProcessingInfo callResult) {
        container.put(uid, callResult);
    }

    @Override
    public void remove(UUID uid) {
        container.remove(uid);
    }
    
}
