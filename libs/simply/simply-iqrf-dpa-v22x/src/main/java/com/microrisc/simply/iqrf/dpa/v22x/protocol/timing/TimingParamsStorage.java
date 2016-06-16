/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.protocol.timing;

import com.microrisc.simply.BaseCallResponse;
import com.microrisc.simply.CallRequest;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.errors.CallRequestProcessingErrorType;
import com.microrisc.simply.iqrf.RF_Mode;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v22x.devices.PeripheralInfoGetter;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.FRCStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.PeripheralInfoGetterStandardTransformer;
import static com.microrisc.simply.iqrf.dpa.v22x.protocol.timing.FRC_TimingParams.DEFAULT_BONDED_NODES_NUM;
import static com.microrisc.simply.iqrf.dpa.v22x.protocol.timing.FRC_TimingParams.DEFAULT_RF_MODE;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNode;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNodes;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Configuration;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Configuration.FRC_RESPONSE_TIME;
import com.microrisc.simply.iqrf.dpa.v22x.types.PeripheralEnumeration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage of timing parameters.
 * 
 * @author Michal Konopa
 */
public final class TimingParamsStorage {
    
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(TimingParamsStorage.class);
    
    
    // base abstract clas for mutable versions of "Timing Params" classes
    private static abstract class MutableTimingParams {
        
        // returns timing params (usually an immutable version of this)
        public abstract TimingParams getTimingParams();
    }
    
    // mutable version of FRC_TimingParams class
    private static final class MutableFRC_TimingParams extends MutableTimingParams {
        
        // number of bonded nodes
        private int bondedNodesNum;

        // RF mode
        private RF_Mode rfMode;

        // response time on coordinator
        private FRC_Configuration.FRC_RESPONSE_TIME responseTime;
        
        
        // CACHING
        // indicates, whether last timing params has changed since the last return
        private boolean changedFromLastReturn;
        private FRC_TimingParams lastReturnedTimingParams;
        
        
        public MutableFRC_TimingParams() {
           this.bondedNodesNum = FRC_TimingParams.DEFAULT_BONDED_NODES_NUM;
           this.rfMode = FRC_TimingParams.DEFAULT_RF_MODE;
           this.responseTime = FRC_TimingParams.DEFAULT_RESPONSE_TIME;
           
           this.changedFromLastReturn = false;
           this.lastReturnedTimingParams = null;
        }
        
        public void setBondedNodesNum(int bondedNodesNum) {
            changedFromLastReturn = (this.bondedNodesNum != bondedNodesNum);
            this.bondedNodesNum = bondedNodesNum;
        }
        
        public void setRfMode(RF_Mode rfMode) {
            changedFromLastReturn = (this.rfMode != rfMode);
            this.rfMode = rfMode;
        }
        
        public void setResponseTime(FRC_Configuration.FRC_RESPONSE_TIME responseTime) {
            changedFromLastReturn = (this.responseTime != responseTime);
            this.responseTime = responseTime;
        }
        
        @Override
        public TimingParams getTimingParams() {
            if ( lastReturnedTimingParams != null ) {
                if ( !this.changedFromLastReturn ) {
                    return lastReturnedTimingParams;
                }
            }
            
            lastReturnedTimingParams = new FRC_TimingParams(
                    bondedNodesNum, rfMode, responseTime
            );
            this.changedFromLastReturn = false;
            
            return lastReturnedTimingParams;
        }
        
    }
    
    
    // timing parameters map relating to one concrete network
    private static final class NetworkTimingParamsMap {
        
        // timing parameters map - keys are the Device Interfaces
        private final Map<Class, MutableTimingParams> timingParamsMap;
        
        // previous processed request
        private CallRequest previousRequest = null;
        
        
        public NetworkTimingParamsMap() {
            this.timingParamsMap = new HashMap<>();
            this.previousRequest = null;
        }
        
        public void put(Class paramsType, MutableTimingParams timingParams) {
            timingParamsMap.put(paramsType, timingParams);
        }
        
        public MutableTimingParams get(Class paramsType) {
            return timingParamsMap.get(paramsType);
        }
    }
    
    
    /**
     * Encapsulates initial settings for the storage.
     */
    public static final class InitialSettings {
        
        // bonded nodes
        private final Collection<String> bondedNodes;
        
        // actual RF mode
        private final RF_Mode rfMode;
        
        
        public static final class Builder {
            private final Collection<String> bondedNodes;
            private RF_Mode rfMode;

            public Builder(Collection<String> bondedNodes) {
                this.bondedNodes = bondedNodes;
            }

            public Builder rfMode(RF_Mode rfMode) {
                this.rfMode = rfMode;
                return this;
            }

            public InitialSettings build() {
                return new InitialSettings(this);
            }
        }
        
        /**
         * Creates new object of initial settings.
         * @param builder builder to get values from
         */
        private InitialSettings(Builder builder) {
            this.bondedNodes = builder.bondedNodes;
            this.rfMode = builder.rfMode;
        }

        /**
         * @return the bonded nodes
         */
        public Collection<String> getBondedNodes() {
            return bondedNodes;
        }

        /**
         * @return the RF mode
         */
        public RF_Mode getRfMode() {
            return rfMode;
        }
    }
    
    
    // updates timing parameters according to request-response info
    private abstract class TimingParamsUpdater {
        public abstract void update(CallRequest request, BaseCallResponse response);
    }
    
    // updates timing parameters according to coordinator request-response info
    private class Coordinator_Updater extends TimingParamsUpdater {

        // determines the called method from Coordinator Device Interface
        private Coordinator.MethodID getCalledMethod(String methodId) {
            for ( Coordinator.MethodID method : Coordinator.MethodID.values() ) {
                if ( FRCStandardTransformer.getInstance().transform(method).equals(methodId)) {
                    return method;
                }
            }
            return null;
        }
        
        private Integer getRemovedNodeId(CallRequest request) {
            Object nodeId = request.getArgs()[request.getArgs().length-1];
            if ( !(nodeId instanceof Integer) ) {
                return null;
            }
            return (Integer)nodeId;
        }
        
        private Integer getRebondedNodeId(CallRequest request) {
            Object nodeId = request.getArgs()[request.getArgs().length-1];
            if ( !(nodeId instanceof Integer) ) {
                return null;
            }
            return (Integer)nodeId;
        }
        
        private void processGetBondedNodes(
            CallRequest request, BaseCallResponse response, MutableFRC_TimingParams frcTimingParams
        ) {
            BondedNodes bondedNodes = (BondedNodes)response.getMainData();
            frcTimingParams.setBondedNodesNum(bondedNodes.getNodesNumber());
        }
        
        private void processClearAllBonds(
            CallRequest request, BaseCallResponse response, MutableFRC_TimingParams frcTimingParams
        ) {
            frcTimingParams.setBondedNodesNum(0);
        }
        
        private void processBondNode(
            CallRequest request, BaseCallResponse response, MutableFRC_TimingParams frcTimingParams
        ) {
            BondedNode bondedNode = (BondedNode)response.getMainData();
            frcTimingParams.setBondedNodesNum(bondedNode.getBondedNodesNum());
        }
        
        private void processRemoveBondedNode(
            CallRequest request, BaseCallResponse response, MutableFRC_TimingParams frcTimingParams
        ) {
            Integer bondedNodesNum = (Integer)response.getMainData();
            frcTimingParams.setBondedNodesNum(bondedNodesNum);
        }
        
        private void processRebondNode(
            CallRequest request, BaseCallResponse response, MutableFRC_TimingParams frcTimingParams
        ) {
            int bondedNodesNum = (Integer)response.getMainData();
            frcTimingParams.setBondedNodesNum(bondedNodesNum);
        }
        
        @Override
        public void update(CallRequest request, BaseCallResponse response) {
            NetworkTimingParamsMap networkTimingParamsMap 
                    = timingParamsForAllNetworks.get(request.getNetworkId());
            if ( networkTimingParamsMap == null ) {
                networkTimingParamsMap = new NetworkTimingParamsMap();
                networkTimingParamsMap.put(FRC.class, new MutableFRC_TimingParams());
                timingParamsForAllNetworks.put(request.getNetworkId(), networkTimingParamsMap);
            }
            
            MutableFRC_TimingParams frcTimingParams = (MutableFRC_TimingParams)networkTimingParamsMap.get(FRC.class);

            Coordinator.MethodID method = getCalledMethod(request.getMethodId());
            if ( method == null ) {
                logger.error("Coordinator method not recognized: {}", request.getMethodId());
                return;
            }
            
            switch ( method ) {
                case GET_BONDED_NODES:
                    processGetBondedNodes(request, response, frcTimingParams);
                    break;
                case CLEAR_ALL_BONDS:
                    processClearAllBonds(request, response, frcTimingParams);
                    break;
                case BOND_NODE:
                    processBondNode(request, response, frcTimingParams);
                    break;
                case REMOVE_BONDED_NODE:
                    processRemoveBondedNode(request, response, frcTimingParams);
                    break;
                case REBOND_NODE:
                    processRebondNode(request, response, frcTimingParams);
                    break;
                default:
                    break;
            }
        }
    }
    
    // updates timing parameters according to Peripheral Info Getter request-response info
    private class PeripheralInfoGetter_Updater extends TimingParamsUpdater {

        // determines the called method from Coordinator Device Interface
        private PeripheralInfoGetter.MethodID getCalledMethod(String methodId) {
            for ( PeripheralInfoGetter.MethodID method : PeripheralInfoGetter.MethodID.values() ) {
                if ( PeripheralInfoGetterStandardTransformer.getInstance().transform(method).equals(methodId)) {
                    return method;
                }
            }
            return null;
        }

        @Override
        public void update(CallRequest request, BaseCallResponse response) {
            NetworkTimingParamsMap networkTimingParamsMap 
                    = timingParamsForAllNetworks.get(request.getNetworkId());
            if ( networkTimingParamsMap == null ) {
                networkTimingParamsMap = new NetworkTimingParamsMap();
                networkTimingParamsMap.put(FRC.class, new MutableFRC_TimingParams());
                timingParamsForAllNetworks.put(request.getNetworkId(), networkTimingParamsMap);
            }
            
            MutableFRC_TimingParams frcTimingParams = (MutableFRC_TimingParams)networkTimingParamsMap.get(FRC.class);

            PeripheralInfoGetter.MethodID method = getCalledMethod(request.getMethodId());
            if ( method == null ) {
                logger.error("Peripheral Info Getter method not recognized: {}", request.getMethodId());
                return;
            }
            
            switch ( method ) {
                case GET_PERIPHERAL_ENUMERATION:
                    PeripheralEnumeration perEnumeration = (PeripheralEnumeration)response.getMainData();
                    RF_Mode rfMode = (perEnumeration.getFlags() == 1)? RF_Mode.STD : RF_Mode.LP;
                    frcTimingParams.setRfMode(rfMode);
                    break;
                default:
                    break;
            }
        }
    }
    
    // updates timing parameters according to FRC request-response info
    private class FRC_Updater extends TimingParamsUpdater {
        
        // determines the called method from Coordinator Device Interface
        private FRC.MethodID getCalledMethod(String methodId) {
            for ( FRC.MethodID method : FRC.MethodID.values() ) {
                if ( FRCStandardTransformer.getInstance().transform(method).equals(methodId)) {
                    return method;
                }
            }
            return null;
        }
        
        // extracts response time set by the request and checks the response - 
        // if the response time has been correctly set
        private FRC_RESPONSE_TIME getResponseTime(CallRequest request, BaseCallResponse response) {
            FRC_Configuration frcConfig = null;
            for ( Object arg : request.getArgs() ) {
                if ( arg instanceof FRC_Configuration ) {
                    frcConfig = (FRC_Configuration) arg;
                    break;
                }
            }
            
            if ( frcConfig == null ) {
                logger.error("FRC Configuration not found in Set FRC Params request: {}", request);
                return null;
            }
            
            CallRequestProcessingError responseError = response.getProcessingError();
            if ( responseError != null ) {
                if ( responseError.getErrorType() == CallRequestProcessingErrorType.NETWORK_INTERNAL ) {
                    logger.error(
                        "Some internal network error in processing of the Set FRC Params request: {}", request
                    );
                    return null;
                }
            }
            
            return frcConfig.getResponseTime();
        }
        
        private void processSetFRC_Params(
            CallRequest request, BaseCallResponse response, MutableFRC_TimingParams frcTimingParams
        ) {
            FRC_RESPONSE_TIME responseTime = getResponseTime(request, response);
            if ( responseTime != null ) {
                frcTimingParams.setResponseTime(responseTime);
            }
        }
        
        @Override
        public void update(CallRequest request, BaseCallResponse response) {
            NetworkTimingParamsMap networkTimingParamsMap 
                    = timingParamsForAllNetworks.get(request.getNetworkId());
            if ( networkTimingParamsMap == null ) {
                networkTimingParamsMap = new NetworkTimingParamsMap();
                networkTimingParamsMap.put(FRC.class, new MutableFRC_TimingParams());
                timingParamsForAllNetworks.put(request.getNetworkId(), networkTimingParamsMap);
            }
            
            MutableFRC_TimingParams frcTimingParams = (MutableFRC_TimingParams)networkTimingParamsMap.get(FRC.class);

            FRC.MethodID method = getCalledMethod(request.getMethodId());
            if ( method == null ) {
                logger.error("FRC method not recognized: {}", request.getMethodId());
                return;
            }
            
            switch ( method ) {
                case SET_FRC_PARAMS:
                    processSetFRC_Params(request, response, frcTimingParams);
                    break;
                default:
                    break;
            }
        }
    }
    
    // timing parameters updaters
    private Map<Class, TimingParamsUpdater> timingParamsUpdaters;
    
    // map of timing parameters for all networks
    // indexed by ID of network relating to timing parameters
    private Map<String, NetworkTimingParamsMap> timingParamsForAllNetworks; 
    
    
    private Map<String, InitialSettings> checkInitialSettings(
            Map<String, InitialSettings> networksInitialSettings
    ) {
        if ( networksInitialSettings == null ) {
            throw new IllegalArgumentException("Initial settings cannot be null.");
        }
        return networksInitialSettings;
    }
    
    private void initTimingParamsUpdaters() {
        timingParamsUpdaters = new HashMap<>();
        timingParamsUpdaters.put(Coordinator.class, new Coordinator_Updater());
        timingParamsUpdaters.put(PeripheralInfoGetter.class, new PeripheralInfoGetter_Updater());
        timingParamsUpdaters.put(FRC.class, new FRC_Updater());
    }
    
    private void initTimingParamsMap() {
        timingParamsForAllNetworks = new HashMap<>();
    }
    
    private void configureFRC_TimingParams(
        MutableFRC_TimingParams mutFrcTimingParams, InitialSettings initialSettings) 
    {
        if ( initialSettings.getBondedNodes() != null ) {
            mutFrcTimingParams.setBondedNodesNum(initialSettings.getBondedNodes().size());
        }
        
        if ( initialSettings.getRfMode() != null ) {
            mutFrcTimingParams.setRfMode(initialSettings.getRfMode());
        }
    }
    
    private void initTimingParamsMap(Map<String, InitialSettings> networksInitialSettings) {
        for ( String networkId : networksInitialSettings.keySet() ) {
            InitialSettings networkInitSetting = networksInitialSettings.get(networkId);
            if ( networkInitSetting == null ) {
                continue;
            }
            
            MutableFRC_TimingParams mutFrcTimingParams=  new MutableFRC_TimingParams();
            configureFRC_TimingParams(mutFrcTimingParams, networkInitSetting);
            
            NetworkTimingParamsMap networkTimingParamsMap = new NetworkTimingParamsMap();
            networkTimingParamsMap.put(FRC.class, mutFrcTimingParams);
            
            timingParamsForAllNetworks.put(networkId, networkTimingParamsMap);
        }
    }
    
    private void applyInitialSettings(Map<String, InitialSettings> networksInitialSettings) {
        initTimingParamsMap(networksInitialSettings);
    }
    
    
    /**
     * Creates new object of timing parameters storage.
     */
    public TimingParamsStorage() {
        initTimingParamsUpdaters();
        initTimingParamsMap();
    }
    
    /**
     * Creates new object of timing parameters storage and applies specified
     * initial settings on that object.
     * @param networksInitialSettings initial settings for networks
     * @throws IllegalArgumentException if {@code initialSettings} is {@code null}
     */
    public TimingParamsStorage(Map<String, InitialSettings> networksInitialSettings) {
        checkInitialSettings(networksInitialSettings);
        initTimingParamsUpdaters();
        applyInitialSettings(networksInitialSettings);
    }
    
    /**
     * Returns timing parameters for specified request, or {@code null} if no
     * timing parameters is found for specified request.
     * @param request request, which to find the timing parameters for
     * @return timing parameters found for {@code request}, or {@code null}
     */ 
    public synchronized TimingParams getTimingParams(CallRequest request) {
        NetworkTimingParamsMap networkTimingParams = timingParamsForAllNetworks.get(request.getMethodId());
        if ( networkTimingParams == null ) {
            return null;
        }
        
        MutableTimingParams mutTimingParams = networkTimingParams.get(request.getDeviceInterface());
        if ( mutTimingParams == null ) {
            return null;
        }
        
        return mutTimingParams.getTimingParams();
    }

    /**
     * Updates timing parameters according to specified request - response pair.
     * @param request sent request
     * @param response response on {@code request} request
     */
    public synchronized void updateTimingParams(CallRequest request, BaseCallResponse response) {
        TimingParamsUpdater timingParamsUpdater = timingParamsUpdaters.get(request.getDeviceInterface());
        if ( timingParamsUpdater == null ) {
            return;
        }

        timingParamsUpdater.update(request, response);
        
        NetworkTimingParamsMap networkTimingParamsMap = timingParamsForAllNetworks.get(request.getNetworkId());
        networkTimingParamsMap.previousRequest = request;
    }
    
}
