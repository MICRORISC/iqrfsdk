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

package com.microrisc.spi.cinterion_protocol;

/**
 * Result of a parsing of read data incomming from SPI device.
 * 
 * @author Michal Konopa
 */
public final class ResponseDataParsingResult {
    
    /**
     * Result of a parsing process.
     */
    public static class ParsingResultType {
        
        private ParsingResultType() {}

        public static final ParsingResultType OK = new ParsingResultType();
        public static final ParsingResultType FORMAT_ERROR = new ParsingResultType();
        public static final ParsingResultType INCOMPLETE = new ParsingResultType();
    }

    private final ParsingResultType result;
    private final AbstractResponseMessage response;
    
    
    /**
     * Creates data of a result of parsing of a reponse data, including 
     * constructed response, if the parsing process performed without errors. 
     * @param result result of parsing alone
     * @param response constructed response
     */
    public ResponseDataParsingResult(
            ParsingResultType result, AbstractResponseMessage response
    ) {
        this.result = result;
        this.response = response;
    }

    /**
     * @return the result of parsing
     */
    public ParsingResultType getResult() {
        return result;
    }

    /**
     * @return the response, can be {@code null}, if parsing ended with an error
     */
    public AbstractResponseMessage getResponse() {
        return response;
    }
}
