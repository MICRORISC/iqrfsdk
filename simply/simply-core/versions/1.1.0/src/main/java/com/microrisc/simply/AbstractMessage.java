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

/**
 * Base class for message, which comes from underlaying network.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractMessage {
    /** Message effective main data - carrying the result. */
    protected Object mainData;
    
    /** Additional effective data. */
    protected Object additionalData;
    
    
    /**
     * Information about source of this message.
     */
    public static interface MessageSource {
        /**
         * @return network ID
         */
        public String getNetworkId();

        /**
         * @return node ID
         */
        public String getNodeId();
    }
    
    
    /** Message source. */
    protected MessageSource messageSource;
    
    
    /**
     * Protected constructor. 
     * @param mainData effective main data of this message
     * @param additionalData effective additional data of this message
     * @param messageSource source of this message
     */
    protected AbstractMessage(
            Object mainData, Object additionalData, MessageSource messageSource
    ) {
        this.mainData = mainData;
        this.additionalData = additionalData;
        this.messageSource = messageSource;
    }
    
    /**
     * Returns main data.
     * @return main data.
     */
    public Object getMainData() {
        return mainData;
    }
    
    /**
     * Returns additional data.
     * @return additional data.
     */
    public Object getAdditionalData() {
        return additionalData;
    }
    
    /**
     * Returns source of this message.
     * @return source of this message.
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "main data=" + mainData +
                ", additional data=" + additionalData +
                ", source=" + messageSource +
                " }");
    }
}
