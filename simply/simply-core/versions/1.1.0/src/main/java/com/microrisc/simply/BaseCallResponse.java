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

import com.microrisc.simply.errors.CallRequestProcessingError;
import java.util.UUID;

/**
 * Base class of response. Encapsulates information about response from 
 * underlaying network.
 * 
 * @author Michal Konopa
 */
public class BaseCallResponse extends AbstractMessage {
    /** Information about processing errors. */
    private CallRequestProcessingError procError;
    
    /** Unique identifier of request, which is this response the response on. */
    private UUID requestId;
    
    
    /**
     * Message source extended with information about called method. 
     */
    public static interface MethodMessageSource extends MessageSource {
       /**
        * Returns device interface, which the called method belongs to.
        * @return device interface, which the called method belongs to
        */
       Class getDeviceInterface();

       /**
        * Returns identifier of called method.
        * @return identifier of called method
        */
       String getMethodId();
    }

    
    /**
     * Protected constructor. 
     * @param data effective main data
     * @param additionalData effective additional data
     * @param source source of this message
     * @param procError processing error.
     */
    public BaseCallResponse(
            Object data, Object additionalData, MethodMessageSource source, 
            CallRequestProcessingError procError
    ) {
        super(data, additionalData, source);
        this.procError = procError;
    }
    
    /**
     * Protected constructor.
     * Processing error will be set to {@code null}.
     * @param data effective main data
     * @param additionalData effective additional data
     * @param source sender of this message
     */
    public BaseCallResponse(
            Object data, Object additionalData, MethodMessageSource source
    ) {
        this(data, additionalData, source, null);
    }
    
    /**
     * Protected constructor. 
     * Effective main data will be set to {@code null}.
     * @param additionalData effective additional data
     * @param source sender of this message
     * @param procError processing error.
     */
    public BaseCallResponse(
            Object additionalData, MethodMessageSource source, CallRequestProcessingError procError
    ) {
        this(null, additionalData, source, procError);
    }
    
    /**
     * Protected constructor. 
     * Effective main data and effective additional data will be set to {@code null}, 
     * method return code will be set to {@code FAIL}.
     * @param source sender of this message
     * @param procError processing error
     */
    public BaseCallResponse(
            MethodMessageSource source, CallRequestProcessingError procError
    ) {
        this(null, null, source, procError);
    }
    
    
    @Override
    public MethodMessageSource getMessageSource() {
        return (MethodMessageSource)messageSource;
    }
    
    /**
     * @return information about processing error<br>
     *         {@code null} if no processing error has occured 
     */
    public CallRequestProcessingError getProcessingError() {
        return procError;
    }
    
    /**
     * Binds this response to correponding request with specified unique identifier.
     * @param requestId to set
     */
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }
    
    /**
     * @return unique identifier of request, which is this response on
     */
    public UUID getRequestId() {
        return requestId;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                super.toString() + 
                ", proc error=" + procError +
                ", request ID=" + requestId +
                " }");
    }
}
