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

package com.microrisc.simply.protocol;

import com.microrisc.simply.AbstractMessage;
import com.microrisc.simply.BaseCallResponse;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.NetworkData;
import com.microrisc.simply.NetworkLayerService;
import com.microrisc.simply.SimpleMessageSource;
import com.microrisc.simply.SimpleMethodMessageSource;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.network.BaseNetworkData;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of {@code AbstractProtocolLayer}.
 * 
 * @author Michal Konopa
 */
public final class SimpleProtocolLayer extends AbstractProtocolLayer {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SimpleProtocolLayer.class);
    
    /**
     * Binds sent requests with theirs time of sending.
     */
    private class TimeRequest {
        // sent request
        CallRequest request;
        
        // time, at which the request was sent
        long sentTime;
        
        TimeRequest(CallRequest request, long sentTime) {
            this.request = request;
            this.sentTime = sentTime;
        }
    }
    
    
    /** List of all requests, which was sent to network layer. */
    private List<TimeRequest> sentRequests = new LinkedList<TimeRequest>();
    
    /** Synchronization object for {@code sentRequest} data structure. */
    private final Object synchroSentRequest = new Object();
   
    
    /** Default maximal time duration [in ms] of sent requests in the protocol layer. */
    public static final long MAX_REQUEST_DURATION_DEFAULT = 10000;
    
    /** Maximal time duration [in ms] of sent requests in the protocol layer. */
    private long maxRequestDuration = MAX_REQUEST_DURATION_DEFAULT;
    

    
    /** 
     * Deletes invalid requests. Request is invalid, if: <br> 
     * - its presence in the list exceeds maximal time duration limit
     * - is equal to specified new request
     */
    private void deleteInvalidRequests(CallRequest newRequest) {
        logger.debug("deleteInvalidRequests - start: newRequest={}", newRequest);
        
        Iterator<TimeRequest> requestIt = sentRequests.iterator();
        while ( requestIt.hasNext() ) {
            TimeRequest sentRequest = requestIt.next();
            long requestDuration = System.currentTimeMillis() - sentRequest.sentTime;
            
            if ( requestDuration > maxRequestDuration ) {
                logger.debug("removed request ( time exceed ): {}", sentRequest);
                requestIt.remove();
                continue;
            }
            
            if ( CallRequestComparator.areEqual(sentRequest.request, newRequest) ) {
                logger.debug("removed request ( equality ) : {}", sentRequest);
                requestIt.remove();
            }
        }
        
        logger.debug("deleteInvalidRequests - end");
    }
    
    /**
     * Maintenace of sent request list.
     * @param callRequest new incomming request
     */
    private void maintainSentRequest(CallRequest callRequest) {
        deleteInvalidRequests(callRequest);
    }
    
    /**
     * Creates error response according to specified request. Adds specified error
     * information into response to create.
     * @param request request, whose processing encountered error
     * @param error error encountered during processing of specified request
     * @return response carrying information about specified error
     */
    private BaseCallResponse createErrorResponse(
            CallRequestProcessingError error, CallRequest request
    ) {
        BaseCallResponse.MethodMessageSource source = 
                new SimpleMethodMessageSource(new SimpleMessageSource(
                request.getNetworkId(), request.getNodeId()), 
                request.getDeviceInterface(), request.getMethodId()
        );
        BaseCallResponse errorResponse = new BaseCallResponse(source, error);
        return errorResponse;
    }
    
    /**
     * Processes specified error response. This method works as follows: <br>
     * 1. Uid of specified error response is set to that of specified request.
     * 2. Listener is called with the error response.
     * 
     * @param errorResponse error response to process
     * @param causeRequest request, which causes the error
     */
    private void processErrorResponse(
            BaseCallResponse errorResponse, CallRequest causeRequest
    ) {
        errorResponse.setRequestId(causeRequest.getId());
        listener.onGetMessage(errorResponse);
    }
    
    /**
     * Returns {@code true} if the specified response can be a response for 
     * specified request. Otherwise returns {@code false}.
     * @param request request to check
     * @param callResponse response to check
     * @return {@code true} if the specified response can be a reponse for 
     *                      specified request <br>
     *         {@code false}, otherwise
     */
    private boolean match(CallRequest request, BaseCallResponse callResponse) {
        return SimpleRequestToResponseMatcher.match(request, callResponse);
    }
    
    /**
     * Returns call request, which is the specified response the response on
     * that request. If no such request exists, returns {@code null}.
     * @param response
     * @return call request, which is the specified response the response on
     *         that request
     */
    private TimeRequest getCauseRequest(BaseCallResponse response) {
        logger.debug("getCauseRequest - start: response={}", response);
        
        for (TimeRequest timeRequest : sentRequests) {
            if (match(timeRequest.request, response)) {
                logger.debug("getCauseRequest - end: {}", timeRequest.request);
                return timeRequest;
            }
        }
        
        logger.debug("getCauseRequest - end: null");
        return null;
    }
    
    /**
     * Processes specified response.
     * @param response response to process
     */
    private void processResponse(BaseCallResponse response) {
        logger.debug("processResponse - start: response={}", response);
        
        synchronized ( synchroSentRequest ) {
            TimeRequest causeRequest = getCauseRequest(response);
            if ( causeRequest != null ) {
                response.setRequestId(causeRequest.request.getId());
                sentRequests.remove(causeRequest);
            } else {
                logger.warn("No cause request found, response={} discared", response);
                logger.debug("processResponse - end");
                return;
            }
        }
        
        // call listener
        listener.onGetMessage(response);
        
        logger.debug("processResponse - end");
    }
    
    
    
    /**
     * Creates new simple protocol layer.
     * @param networkLayerService network layer service to use
     * @param msgConvertor message convertor
     */
    public SimpleProtocolLayer(NetworkLayerService networkLayerService, 
            MessageConvertor msgConvertor
    ) {
        super(networkLayerService, msgConvertor);
    }
    
    /**
     * This method works as follows: <br>
     * 1. Converts specified request into sequence of bytes. If an error has
     *    occured during conversion, error of type {@code ProcessingRequestAtProtocolLayerError} 
     *    is created, error response is created and {@code processErrorResponse} 
     *    method is called.
     * 2. New {@code NetworkData} object is created and send into the network layer. 
     * 3. New {@code TimeRequest} object is created and stored into {@code sentRequest}.
     */
    @Override
    public void sendRequest(CallRequest request) throws SimplyException {
        logger.debug("sendRequest - start: request={}", request);
        
        // conversion to format used by application protocol
        short[] protoMsg = msgConvertor.convertToProtoFormat(request);
        
        // must be performed altogether
        // if response comes before sentRequest.add(), error encounters
        synchronized ( synchroSentRequest ) {
            // maintenance of already sent requests
            maintainSentRequest(request);
            networkLayerService.sendData( new BaseNetworkData(protoMsg, request.getNetworkId()) );
            sentRequests.add( new TimeRequest(request, System.currentTimeMillis()) );
        }
        
        logger.debug("sendRequest - end");
    }
    
    @Override
    public void sendRequest(CallRequest request, long procTime) throws SimplyException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void destroy() {
        logger.debug("destroy - start:");
        
        super.destroy();
        sentRequests.clear();
        
        logger.info("Destroyed");
        logger.debug("destroy - end");
    }
    
    /**
     * This method works as follows: <br>
     * 1. Checks, if the listener is registered. If no listener is registered, 
     *    the method finishes.
     * 2. Converts network data using {@code msgConvertor} to AbstractMessage
     *    instance. If an error has encountered during conversion process, the
     *    method finishes.
     * 3. Calls {@code processResponse} method
     * 
     * @param networkData data comming from network layer
     */
    @Override
    public void onGetData(NetworkData networkData) {
        logger.debug("onGetData - start: {}", networkData);
        
        // no listener - nothing to do
        if ( listener == null ) {
            logger.debug("onGetData - end: no listener connected");
            return;
        }
        
        AbstractMessage message = null;
        try {
            message = msgConvertor.convertToDOFormat(networkData);
        } catch (SimplyException e) {
            logger.error("Conversion error on incomming data", e);
            return;
        }
        
        // response processing
        processResponse(( BaseCallResponse) message);
        
        logger.debug("onGetData - end");
    }
    
    /** Checks specified maximal time duration. */
    private long checkMaxRequestDuration(long maxRequestDuration) {
        if ( maxRequestDuration < 0 ) {
            throw new IllegalArgumentException("Maximal duration of sent request"
                    + "must be nonnegative");
        }
        return maxRequestDuration;
    }
    
    /**
     * Sets maximal time duration [in ms] of sent requests in this protocol layer.
     * @param maxRequestDuration duration [in ms] to set
     * @throws IllegalArgumentException if the value is less then {@code 0}
     */
    public void setMaxRequestDuration(long maxRequestDuration) {
        this.maxRequestDuration = checkMaxRequestDuration(maxRequestDuration);
    }
}
