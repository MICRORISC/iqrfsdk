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

import java.util.UUID;

/**
 * Listener of information about a processing of a call requests sended by 
 * various objects ( mainly by Device Objects ) to underlaying network via 
 * {@code ConnectorService } interface.
 * 
 * @author Michal Konopa
 */
public interface ConnectorListener {
    /**
     * Will be called by connector to inform listener about processing of a call request 
     * executed by that DO.
     * @param procInfo information about processing of executed call request.
     * @param callId unique ID of the executed call request
     */
    void onCallRequestProcessingInfo(CallRequestProcessingInfo procInfo, UUID callId);
}
