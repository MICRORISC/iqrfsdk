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
 * Errors, which encounters during processing of a method call requests comming 
 * from connector to application protocol packets.
 * 
 * @author Michal Konopa
 */
public class ProcessingRequestAtProtocolLayerError extends AbstractCallRequestProcessingError {
    private final CallRequestProcessingErrorType errorType = 
            CallRequestProcessingErrorType.PROCESSING_REQUEST_AT_PROTOCOL_LAYER; 
    
    public ProcessingRequestAtProtocolLayerError() {
    }
    
    public ProcessingRequestAtProtocolLayerError(String message) {
        super(message);
    }
    
    public ProcessingRequestAtProtocolLayerError(Throwable cause) {
        super(cause);
    }
    
    @Override
    public CallRequestProcessingErrorType getErrorType() {
        return errorType;
    }
}
