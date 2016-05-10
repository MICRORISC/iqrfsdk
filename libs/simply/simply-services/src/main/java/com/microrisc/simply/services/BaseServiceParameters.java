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

import java.util.HashMap;
import java.util.Map;

/**
 * Base class of implementation of Service Parameters.
 * 
 * @author Michal Konopa
 */
public class BaseServiceParameters implements ServiceParameters {
    /** parameters */
    protected final Map<String, Object> params;
    
    
    private Map<String, Object> checkParams(Map<String, Object> params) {
        if ( params == null ) {
            throw new IllegalArgumentException("Parameters map cannot be null.");
        }
        return params;
    }
    
    private String checkParamName(String paramName) {
        if ( paramName == null ) {
            throw new IllegalArgumentException("Parameter name cannot be null.");
        }
        
        if ( paramName.isEmpty() ) {
            throw new IllegalArgumentException("Parameter name cannot be empty.");
        }
        
        return paramName;
    }
    
    
    /**
     * Creates new object of Service Parameters.
     * @param params map of parameters to use
     * @throws IllegalArgumentException if {@code params} is {@code null}
     */
    public BaseServiceParameters(Map<String, Object> params) {
        this.params = new HashMap<>(checkParams(params));
    }
    
    /**
     * @throws IllegalArgumentException if {@code paramName} is {@code null} or
     *         empty
     */
    @Override
    public Object getParameter(String paramName) {
        return params.get(checkParamName(paramName));
    }
    
    /**
     * @throws IllegalArgumentException if {@code paramName} is {@code null} or
     *         empty
     */
    @Override
    public boolean setParameter(String paramName, Object paramValue) {
        Object param = params.get(checkParamName(paramName));
        if ( param == null ) {
            return false;
        }
        
        params.put(paramName, paramValue);
        return true;
    }

    @Override
    public Map<String, Object> getAllParameters() {
        return new HashMap<>(params);
    }
    
}
