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
 * Base class of Services results.
 * 
 * @author Michal Konopa
 * @param <R> type of service's result
 * @param <I> type of service's processing information, e.g. states, errors etc.
 */
public class BaseServiceResult<R, I> implements ServiceResult<R, I> {
    private final ServiceResult.Status status;
    private final R result;
    private final I processingInfo;
    
    
    /**
     * Creates new service result.
     * @param status status
     * @param result result
     * @param processingInfo processing info 
     */
    public BaseServiceResult(ServiceResult.Status status, R result, I processingInfo) 
    {
        this.status = status;
        this.result = result;
        this.processingInfo = processingInfo;
    }

    /**
     * @return the status
     */
    @Override
    public ServiceResult.Status getStatus() {
        return status;
    }

    /**
     * @return the result
     */
    @Override
    public R getResult() {
        return result;
    }

    /**
     * @return the processing info
     */
    @Override
    public I getProcessingInfo() {
        return processingInfo;
    }
}
