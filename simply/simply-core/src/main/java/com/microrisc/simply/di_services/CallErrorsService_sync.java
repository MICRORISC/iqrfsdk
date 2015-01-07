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

import com.microrisc.simply.errors.CallRequestProcessingError;

/**
 * Service relating to error information of DI method calls.
 * For SYNCHRONOUS access to DI method calling.
 * 
 * @author Michal Konopa
 */
public interface CallErrorsService_sync {
    /**
     * Returns error information relating to the last processed method call.
     * @return error information relating to the last processed method call.
     */
    CallRequestProcessingError getCallRequestProcessingErrorOfLastCall();
}
