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

package com.microrisc.simply.asynchrony;

import com.microrisc.simply.AbstractMessage;

/**
 * Asynchronous message properties checker for base asynchronous messages and
 * their required properties.
 * <p>
 * If there is NO object of asynchronous message required properties, then 
 * the result of checking is {@code true}. <br>
 * 
 * Else, the process of message checking against specified properties is as follows: <br>
 * 1. <b>Message's source is checked for agreement with a properties's 
 *    message source.</b> <br> 
 *    - if the properties does not define a message source, then the result of 
 *    the checking is {@code true}. <br>
 *    - if the message's source is not defined but properties's message source 
 *    is defined, then the result of the checking is {@code false}.<br>
 *    - if both message's source and properties' message source are defined, 
 *    then network ID and node ID fields of {@code AbstractMessage.MessageSource}
 *    are checked for agreement: <br>
 *    Network ID checking is as follows: <br>
 *      - if the properties does not define network ID, then the result of 
 *         network ID checking is {@code true}. <br>
 *      - if the message's network ID is not defined but the properties' network
 *         ID is, then the result of network ID checking is {@code false}. <br>
 *      - if both message's network ID and properties's network ID are defined, then
 *         the result of network ID checking is equal to the result of a String 
 *         comparision of these both IDs
 *      <br>
 *    Node ID checking is as follows: <br>
 *      - if the properties does not define node ID, then the result of 
 *         node ID checking is {@code true}. <br>
 *      - if the message's node ID is not defined but the properties' node
 *         ID is, then the result of node ID checking is {@code false}. <br>
 *      - if both message's node ID and properties's node ID are defined, then
 *         the result of node ID checking is equal to the result of a String 
 *         comparision of these both IDs
 *      <br>  
 * 2. <b>Message's type of main data is checked for agreement with the
 *    properties's type of main data. </b> <br> 
 *    - if the properties does not define the type of main data, then the result of 
 *    the checking is {@code true}. <br>
 *    - if the message's type of main data is not defined but properties's type of
 *    main data is, then the result of the checking is {@code false}.<br>
 *    -if both messages' type of main data and properties's type of main data
 *    are defined, then the result of checking is {@code true}, if the properties's
 *    type of main data is either the same as, or is a superclass or superinterface of, 
 *    the class or interface represented by the message's type of main data. 
 *    Otherwise the result of checking is {@code false}.
 * <br>
 * 3. <b>Type of additional data of the message is checked for agreement with the
 *    properties's type of additional data. </b> <br> 
 *    -if the properties does not define the type of additional data, then the result of 
 *    the checking is {@code true}. <br>
 *    -if the message's type of additional data is not defined but properties's type of
 *    additional data is, then the result of the checking is {@code false}.<br>
 *    -if both messages' type of additional data and properties's type of additional data
 *    are defined, then the result of checking is {@code true}, if the properties's
 *    type of additional data is either the same as, or is a superclass or superinterface of, 
 *    the class or interface represented by the message's type of additional data. 
 *    Otherwise the result of checking is {@code false}.
 * 
 * 
 * @author Michal Konopa
 */
public final class BaseAsynchronousMessagePropertiesChecker 
implements AsynchronousMessagePropertiesChecker<BaseAsynchronousMessage, AsynchronousMessageProperties> 
{
    private boolean checkNetworkId(String msgNetworkId, String reqNetworkId) {
        if ( reqNetworkId == null ) {
            return true;
        }
        
        if ( msgNetworkId == null ) {
            return false;
        }
        
        return msgNetworkId.equals(reqNetworkId);
    }
    
    private boolean checkNodeId(String msgNodeId, String reqNodeId) {
        if ( reqNodeId == null ) {
            return true;
        }
        
        if ( msgNodeId == null ) {
            return false;
        }
        
        return msgNodeId.equals(reqNodeId);
    }
    
    private boolean checkMessageSource(
        AbstractMessage.MessageSource msgSource, AbstractMessage.MessageSource reqSource
    ) {
        if ( reqSource == null ) {
            return true;
        }
        
        if ( msgSource == null ) {
            return false;
        }
      
        // both message source are not null in this place
        if ( !checkNetworkId(msgSource.getNetworkId(), reqSource.getNetworkId()) ) {
            return false;
        }
        
        return checkNodeId(msgSource.getNodeId(), reqSource.getNodeId());
    }
    
    private boolean checkTypeOfMainData(Object msgMainData, Class typeOfMainData) {
        if ( typeOfMainData == null ) {
            return true;
        }
        
        if ( msgMainData == null ) {
            return false;
        }
        
        return typeOfMainData.isAssignableFrom(msgMainData.getClass());
    }
    
    private boolean checkTypeOfAdditionalData(
            Object msgAdditionalData, Class typeOfAdditionalData
    ) {
        if ( typeOfAdditionalData == null ) {
            return true;
        }
        if ( msgAdditionalData == null ) {
            return false;
        }
        
        return typeOfAdditionalData.isAssignableFrom(msgAdditionalData.getClass());
    }
    
    
    @Override
    public boolean messageHasRequiredProperties(
            BaseAsynchronousMessage message, AsynchronousMessageProperties reqProps
    ) { 
        if ( reqProps == null ) {
            return true;
        }
        
        AbstractMessage.MessageSource msgSource = message.getMessageSource();
        AbstractMessage.MessageSource reqSource = reqProps.getMessageSource();
        
        if ( !checkMessageSource(msgSource, reqSource) ) {
            return false;
        }
        
        if ( !(checkTypeOfMainData(message.getMainData(), reqProps.getTypeOfMainData())) ) {
            return false;
        }
        
        if ( !(checkTypeOfAdditionalData(message.getAdditionalData(), reqProps.getTypeOfAdditionalData())) ) {
            return false;
        }
        
        return true;
    }
    
}
