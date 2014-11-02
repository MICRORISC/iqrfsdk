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

import java.util.UUID;

/**
 * Service relating to additional information returned from DI method calls.
 * For ASYNCHRONOUS access to DI method calling.
 * 
 * @author Michal Konopa
 */
public interface AdditionalInfoService_async {
    /**
     * Returns additional information returned from the specified method call.
     * @param callId DO method call about which to get additional information
     * @return additional information returned from the specified method call
     */
    Object getCallResultAdditionalInfo(UUID callId);
}
