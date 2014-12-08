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

package com.microrisc.spi;

/**
 * Encapsulates errors, which occur during calling of SPI functionality.
 * 
 * @author Michal Konopa
 */
public class SPI_Error {
    /** Error type. */
    private final SPI_ErrorType errorType;
    
    /** Description of error, if exists. */
    private final String descr;
    
    /** Reference to related exception object, if exists. */
    private final Exception exception;
    
    
    private SPI_ErrorType checkErrorType(SPI_ErrorType errorType) {
        if ( errorType == null ) {
            throw new IllegalArgumentException("Error type must be specified.");
        }
        return errorType;
    }
    
    /**
     * Creates new SPI Error object.
     * @param errorType type of SPI error
     * @throws IllegalArgumentException if {@code errorType} is {@code null}
     */
    public SPI_Error(SPI_ErrorType errorType) {
        this.errorType = checkErrorType(errorType);
        this.descr = null;
        this.exception = null;
    }
    
    /**
     * Creates new SPI Error object.
     * @param errorType type of SPI error
     * @param descr error description
     * @throws IllegalArgumentException if {@code errorType} is {@code null}
     */
    public SPI_Error(SPI_ErrorType errorType, String descr) {
        this.errorType = errorType;
        this.descr = descr;
        this.exception = null;
    }
    
    /**
     * Creates new SPI Error object.
     * @param errorType type of SPI error
     * @param exception exception object to relate to this error
     * @throws IllegalArgumentException if {@code errorType} is {@code null}
     */
    public SPI_Error(SPI_ErrorType errorType, Exception exception) {
        this.errorType = errorType;
        this.descr = null;
        this.exception = exception;
    }
    
    /**
     * Creates new SPI Error object.
     * @param errorType type of SPI error
     * @param descr error description
     * @param exception exception object to relate to this error
     * @throws IllegalArgumentException if {@code errorType} is {@code null}
     */
    public SPI_Error(SPI_ErrorType errorType, String descr, Exception exception) {
        this.errorType = errorType;
        this.descr = descr;
        this.exception = exception;
    }

    /**
     * @return type of the error
     */
    public SPI_ErrorType getErrorType() {
        return errorType;
    }

    /**
     * @return description of the error, may be {@code null}
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @return exception object related to this error, may be {@code null}
     */
    public Exception getException() {
        return exception;
    }
}
