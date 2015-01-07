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

package com.microrisc.simply.connector;

import com.microrisc.simply.CallRequestProcessingInfo;
import com.microrisc.simply.ConnectorListener;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.ManageableObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality of results sending of processing of a call requests to 
 * associated addresses. 
 * 
 * @author Michal Konopa
 */
public final class CallResultsSender implements ManageableObject {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(CallResultsSender.class);
    
    /**
     * Responsible for sending results to addresses.
     */
    private class SenderThread extends Thread {
        @Override
        public void run() {
            CallRequestProcessingInfo procInfo = null;
            
            while ( true ) {
                if ( this.isInterrupted() ) {
                    logger.info("Listener thread end");
                    return;
                }
                
                synchronized( syncProcInfoToListeners ) {
                    while ( procInfoToListeners.isEmpty() ) {
                        try {
                            syncProcInfoToListeners.wait();
                        } catch ( InterruptedException e ) {
                            logger.warn("Listener thread interrupted while waiting", e);
                            return;
                        }
                    }
                    procInfo = procInfoToListeners.poll(); 
                }
                
                // addressee identification
                ConnectorListener adressee = addressees.get( procInfo.getRequestId());
                
                // sending information to the addressee
                adressee.onCallRequestProcessingInfo( procInfo, procInfo.getRequestId() );
                
                // remove addresee from senders map
                addressees.remove(procInfo.getRequestId());
            }
        }
    }
    
    
    /** Sender thread. */
    private Thread senderThread = null;
    
    /** Map of addressee of a call results. */
    private Map<UUID, ConnectorListener> addressees = 
            Collections.synchronizedMap(new HashMap<UUID, ConnectorListener>());
    
    /**
     * Information about processed requests, which will be sent to corresponding 
     * connector listeners. 
     */
    private Queue<CallRequestProcessingInfo> procInfoToListeners = 
            new ConcurrentLinkedQueue<>();
    
    /**
     * Synchronization for access to {@code procInfoToListeners} variable.
     */
    private final Object syncProcInfoToListeners = new Object();
    
    
    /**
     * Terminates sender thread.
     */
    private void terminateSenderThread() {
        logger.debug("terminateSenderThread - start:");
        
        // termination signal to listener thread
        senderThread.interrupt();
        
        // Waiting for sender thread to terminate. Cancelling sender thread has higher 
        // priority than main thread interruption. 
        while ( senderThread.isAlive() ) {
            try {
                if ( senderThread.isAlive( )) {
                    senderThread.join();
                }
            } catch (InterruptedException e) {
                // restoring interrupt status
                Thread.currentThread().interrupt();
                logger.warn("sender terminating - sender thread interrupted");
            }
        } 
        
        logger.info("Sending stopped.");
        logger.debug("terminateSenderThread - end");
    }
    
    
    /**
     * Creates new sender.
     */
    public CallResultsSender() {
        senderThread = new SenderThread();
    } 
    
    @Override
    public void start() throws SimplyException {
        logger.debug("start - start:");
        
        senderThread.start();
        
        logger.info("Sending started");
        logger.debug("start - end");
    }
    
    /**
     * Associates specified call request ID with specified addressee, which to 
     * send a call result of that request to.
     * @param callId ID of a call request
     * @param sender target of a call result of the specified call request
     */
    public void associateCallRequestWithAddressee(
            UUID callId, ConnectorListener sender
    ) {
        addressees.put(callId, sender);
    }
    
    /**
     * Adds specified call request processing info for later sending it to an
     * associated addressee.
     * @param procInfo call request processing info to add
     */
    public void addCallRequestProcessingInfo( CallRequestProcessingInfo procInfo ) {
        synchronized( syncProcInfoToListeners ) {
            procInfoToListeners.offer(procInfo);
            syncProcInfoToListeners.notify();
        }
    }
    
    /**
     * Returns call request processing info about specified call request.
     * @param callId ID of call request, whose processing info to return
     * @return call request processing info about specified call request <br>
     *         {@code null}, if no corresponding processing info was found
     */
    public CallRequestProcessingInfo getCallRequestProcessingInfo( UUID callId ) {
        synchronized ( syncProcInfoToListeners ) {
            for ( CallRequestProcessingInfo procInfo : procInfoToListeners ) {
                if ( procInfo.getRequestId().equals(callId) ) {
                    return procInfo;
                }
            }
        }
        return null;
    }
    
    /**
     * Destroys this sender and frees all used resources.
     */
    @Override
    public void destroy() {
        logger.debug("destroy - start:");
        
        terminateSenderThread();
        
        procInfoToListeners.clear();
        procInfoToListeners = null;
        
        addressees.clear();
        addressees = null;
        
        logger.info("Destroyed.");
        logger.debug("destroy - end");
    }
}
