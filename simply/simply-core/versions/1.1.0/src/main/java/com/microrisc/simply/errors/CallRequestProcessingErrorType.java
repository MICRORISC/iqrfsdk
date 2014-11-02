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

package com.microrisc.simply.errors;

/**
 * Types of errors, which encounter during processing of a call requests.
 * 
 * @author Michal Konopa
 */
public enum CallRequestProcessingErrorType {
    /** Dispatching call request to connector failed. */
    DISPATCHING_REQUEST_TO_CONNECTOR,
    
    /** Dispatching call request to protocol layer failed. */
    DISPATCHING_REQUEST_TO_PROTOCOL_LAYER,
    
    /** Processing of a call request at protocol layer failed. */
    PROCESSING_REQUEST_AT_PROTOCOL_LAYER,
    
    /** Processing of a response at protocol layer failed. */
    PROCESSING_RESPONSE_AT_PROTOCOL_LAYER,
    
    /** Network internal error. */
    NETWORK_INTERNAL;
}
