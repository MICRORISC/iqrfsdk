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

package com.microrisc.simply.iqrf.dpa.v22x.di_services;

import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_AdditionalInfo;
import java.util.UUID;

/**
 * Provides access to additional DPA information from incomming messages.
 * 
 * @author Michal Konopa
 */
public interface DPA_AdditionalInfoService {
    /**
     * Returns additional DPA information relating to the specified processed call.
     * @param callId ID of method call, which get additional information to
     * @return additional DPA information relating to the specified processed call.
     */
    DPA_AdditionalInfo getDPA_AdditionalInfo(UUID callId);
    
    /**
     * Returns additional DPA information relating to the last processed call.
     * @return additional DPA information relating to the last processed call.
     */
    DPA_AdditionalInfo getDPA_AdditionalInfoOfLastCall();
}
