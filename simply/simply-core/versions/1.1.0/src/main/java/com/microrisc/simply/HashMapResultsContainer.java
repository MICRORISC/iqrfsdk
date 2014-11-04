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

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hash map implementation of {@code ResultsContainer} interface.
 * This results container implementation has 2 main fields: <br>
 * 1. Capacity - maximal number of items present in the conainer. Default = 10 <br>
 * 2. Maximal time[in ms] of existence of each item in the container. 
 *    Default = not used.<br>
 * 
 * @author Michal Konopa
 * @param <T> type results int the container
 */
public final class HashMapResultsContainer<T extends Object> 
implements ResultsContainer<T> {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(HashMapResultsContainer.class);
    
    
    // call result with time, when it was put into
    private class TimedCallResult {
        long time = 0;
        T callResult = null;

        public TimedCallResult(T callResult, long time) {
            this.time = time;
            this.callResult = callResult;
        }
    }
    
    // for removing the eldest entries automatically
    private class Container<K, V> extends java.util.LinkedHashMap {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return (size() > capacity);
        }
    }
    
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
    

    // time of first valid result not exceeding max time duration
    private long maxValidTime = 0;

    private final Map<UUID, TimedCallResult> items = new Container<UUID, TimedCallResult>();
    
    
    // checking of construction parameters
    private static int checkCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        return capacity;
    } 
    
    private static long checkMaxTimeResultDuration(long maxTimeResultDuration) {
        if (maxTimeResultDuration < 0) {
            throw new IllegalArgumentException("Max time result duration cannot be < 0");
        }
        return maxTimeResultDuration;
    } 
    
    
    /**
     * Updates items according to maximal time duration.
     */
    private void update() {
        logger.debug("update - start: " );
        
        // check, if the time duration is used
        if (maxTimeDuration == 0) {
            logger.debug("update - end: " );
            return;
        }

        long actualTime = System.currentTimeMillis();
        if ((actualTime - maxValidTime) <= maxTimeDuration ) {
            logger.debug("update - end: " );
            return;
        }
        
        logger.debug("update - possible removing old items" );
        
        // because of possible removing old items, it must be used iterator
        Iterator it = items.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, TimedCallResult> item = (Map.Entry<UUID, TimedCallResult>)it.next();
            long timeDiff = actualTime - item.getValue().time;
            if (timeDiff > maxTimeDuration) {
                it.remove();
            } else {
                maxValidTime = item.getValue().time;
                // can immediatly return, because items are sorted according 
                // to the time
                logger.debug("update - end: " );
                return;
            }
        }
        
        logger.debug("update - end: " );
    }

    /**
     * Creates new results container with default values of parameters: <br>
     * - capacity = 10 <br>
     * - max time duration = not used
     */
    public HashMapResultsContainer() { 
    }
    
    /**
     * Creates new results container with specified value of its capacity. Maximal
     * time duration paramter will not be used.
     * @param capacity container capacity
     */
    public HashMapResultsContainer(int capacity) { 
        this.capacity = checkCapacity(capacity);
    }
    
    /**
     * Creates new results container with specified value of maximal time duration.
     * Capacity will be set to 10 items.
     * @param maxTimeDuration maximal time duration of existence of each 
     *        call result in container. 0 means that this capability is not used
     *        Must be >= 0.
     */
    public HashMapResultsContainer(long maxTimeDuration) { 
        this.maxTimeDuration = checkMaxTimeResultDuration(maxTimeDuration);
    }
    
    /**
     * Creates new result container with parameters set to specified values.
     * @param capacity capacity of container ( in number of results ). Must be
     *                 > 0
     * @param maxTimeDuration maximal time duration of existence of each 
     *        call result in container. 0 means that this capability is not used
     *        Must be >= 0.
     */
    public HashMapResultsContainer(int capacity, long maxTimeDuration) {
        this.capacity = checkCapacity(capacity);
        this.maxTimeDuration = checkMaxTimeResultDuration(maxTimeDuration);
    }

    @Override
    public synchronized T get(UUID uid) {
        logger.debug("get - start: uid={}", uid );
        
        update();
        TimedCallResult tmCallResult = items.get(uid);
        if (tmCallResult == null){
            logger.warn("No result found.");
            logger.debug("get - end: null" );
            return null;
        }
        
        logger.debug("get - end: {}", tmCallResult.callResult );
        return tmCallResult.callResult;
    }

    @Override
    public synchronized void put(UUID uid, T callResult) {
        logger.debug("put - start: uid={}, callResult={}", uid, callResult );
        
        update();
        TimedCallResult tmCallResult = new TimedCallResult(callResult, System.currentTimeMillis());
        items.put(uid, tmCallResult);
        
        logger.debug("put - end: " );
    }
        
    @Override
    public synchronized void remove(UUID uid) {
        logger.debug("remove - start: uid={}", uid );
        
        update();
        items.remove(uid);
        
        logger.debug("remove - end: " );
    }
}
