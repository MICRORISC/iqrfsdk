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
 * Provides access to errors, which occur during SPI requests processing.
 * 
 * @author Michal Konopa
 */
public interface ErrorService {
    
    /**
     * Returns information about an error, which has occured during lastly called
     * method of SPI functionality
     * @return error occured during lastly called method of SPI functionality
     * @return {@code null}, if no error has occured
     */
    SPI_Error getLastError();
}
