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

package com.microrisc.simply.di_services;

/**
 * Access to waiting timeout for DO method calls. 
 * 
 * @author Michal Konopa
 */
public interface WaitingTimeoutService {
    /** Unlimited waiting timeout. */
    long UNLIMITED_WAITING_TIMEOUT = -1;
    
    /**
     * Returns default timeout ( in ms ) to wait for result from DO method calls.
     * @return default timeout ( in ms ) to wait for result from DO method calls.
     */
    long getDefaultWaitingTimeout();

    /**
     * Sets default timeout ( in ms ) to wait for result from DO method calls. 
     * @param timeout default waiting timeout to set
     */
    void setDefaultWaitingTimeout(long timeout);
}
