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

package com.microrisc.simply.iqrf.dpa.di_services;

/**
 * Manipulation of HW profile value of requests.
 * 
 * @author Michal Konopa
 */
public interface HwProfileService {
    /**
     * Sets HW profile on specified value, which will be used for all next
     * requests.
     * @param hwProfile new HW profile value for all next requests 
     */
    void setRequestHwProfile(int hwProfile);
    
    /**
     * Returns actual HW profile value of all next requests.
     * @return actual HW profile value of all next requests
     */
    int getRequestHwProfile();
}
