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

import java.util.Map;

/**
 * Initial interface to access to services's parameters.
 * 
 * @author Michal Konopa
 */
public interface ServiceParameters {
    
    /**
     * Returns parameter according to its specified name.
     * @param paramName name of the parameter to return
     * @return parameter <br>
     *         {@code null} if parameter doesn't exist
     */
    Object getParameter(String paramName);
    
    /**
     * Sets parameter of specified name to specified value.
     * @param paramName name of the parameter to set
     * @param paramValue new value of parameter
     * @return {@code true} if the parameter has been successfully set to the new value
     *         {@code false} otherwise
     */
    boolean setParameter(String paramName, Object paramValue);
    
    /**
     * Returns map of all parameters. 
     * @return map of all parameters
     */
    Map<String, Object> getAllParameters();
}
