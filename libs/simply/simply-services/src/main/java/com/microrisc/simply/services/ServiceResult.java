/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.services;

/**
 * Access to service's results.
 * 
 * @author Michal Konopa
 * @param <R> type of service's result
 * @param <I> type of service's processing information, e.g. states, errors etc.
 */
public interface ServiceResult<R, I> {
    
    /** Status of the service. */
    public static enum Status {
        
        /** Service successfully completed. */
        SUCCESSFULLY_COMPLETED,

        /** Some error occured during service's run. */
        ERROR
    }
    
    /**
     * Returns status of the service.
     * @return status of the service.
     */
    Status getStatus();
    
    /**
     * Returns result of the service's run. 
     * @return result of the service's run. 
     */
    R getResult();
    
    /**
     * Returns information about processing of the service. 
     * @return information about processing of the service. 
     */
    I getProcessingInfo();
}
