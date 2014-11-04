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

/**
 * Call request processing states.
 * 
 * @author Michal Konopa
 */
public enum CallRequestProcessingState {
    /** Request is waiting for processing. */
    WAITING_FOR_PROCESSING,
    
    /** Request has been already processed and it is currently waiting for a result. */
    WAITING_FOR_RESULT,
    
    /** Result for a waiting request has arrived. */
    RESULT_ARRIVED,
    
    /** Processing of a call request has been cancelled. */
    CANCELLED,
    
    /** an error has encountered during processing of a call request. */
    ERROR
}
