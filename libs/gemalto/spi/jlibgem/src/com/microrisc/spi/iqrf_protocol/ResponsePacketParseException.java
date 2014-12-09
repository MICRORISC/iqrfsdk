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

package com.microrisc.spi.iqrf_protocol;

/**
 * Exceptions, which encounters by response packets parsing process.
 * 
 * @author Michal Konopa
 */
public final class ResponsePacketParseException extends Exception {
    private final Throwable cause;
    
    /**
     * Creates a new {@code ResponsePacketParseException}.
     */
    public ResponsePacketParseException() {
        super();
        this.cause = null;
    }
    
    /**
     * Constructs a new {@code ResponsePacketParseException} with the specified 
     * detail message.
     * @param message detail message
     */
    public ResponsePacketParseException(String message) {
        super(message);
        this.cause = null;
    }
    
    /**
     * Constructs a new {@code ResponsePacketParseException} with the specified 
     * cause.
     * @param cause cause
     */
    public ResponsePacketParseException(Throwable cause) {
        this.cause = cause;
    }

    /**
     * Returns the cause of this throwable or {@code null} if the cause is 
     * nonexistent or unknown. (The cause is the throwable that caused this 
     * throwable to get thrown.) 
     * @return the cause of this throwable or {@code null} if the cause is 
     *         nonexistent or unknown.
     */
    public Throwable getCause() {
        return cause;
    }
}
