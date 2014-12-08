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

package com.microrisc.spi;

/**
 * Provides access to timeout functionality of responses.
 * 
 * @author Michal Konopa
 */
public interface ResponseTimeoutService {
    /**
     * Sets default response timeout to a specified value. 
     * @param responseTimeout response timeout value to use (in ms)
     * @throws IllegalArgumentException if {@code responseTimeout} is less then 0
     */
    void setDefaultResponseTimeout(long responseTimeout);
    
    /**
     * Returns actual value of default response timeout (in ms).
     * @return actual value of default response timeout (in ms)
     */
    long getDefaultResponseTimeout();
}
