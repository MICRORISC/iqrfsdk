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

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple device object factory.
 * 
 * @author Michal Konopa
 */
public final class SimpleDeviceObjectFactory 
extends AbstractDeviceObjectFactory<ConnectorService, Configuration, DeviceObject> {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SimpleDeviceObjectFactory.class);
    
    
    private BaseDeviceObject createBaseDeviceObject(String networkId, 
            String nodeId, Class implClass) throws Exception {
        // Parameter types of the constructor
        Class[] paramsTypes = new Class[] { String.class, String.class };

        // Find the constructor
        java.lang.reflect.Constructor constructor;
        constructor = implClass.getConstructor(paramsTypes);
        
        // Parameters for the constructor
        Object[] params = new Object[] { networkId, nodeId };
        BaseDeviceObject deviceObj = (BaseDeviceObject)constructor.newInstance(params);
        return deviceObj;
    }
    
    /**
     * Creates new results container.
     */
    private CallRequestProcessingInfoContainer createResultsContainer(Configuration configuration) {
        logger.debug("createResultsContainer - start: configuration={}", configuration);
        
        int capacity = configuration.getInt("deviceObject.resultsContainer.capacity", 
                HashMapResultsContainer.DEFAULT_CAPACITY
        );
        long maxTimeDuration = configuration.getLong("deviceObject.resultsContainer.maxTimeDuration", 
                HashMapResultsContainer.DEFAULT_MAX_TIME_DURATION);
        
        CallRequestProcessingInfoContainer resultsContainer = 
                new HashMapCallRequestProcessingInfoContainer(capacity, maxTimeDuration);
        
        logger.debug("createResultsContainer - end: {}", resultsContainer);
        return resultsContainer;
    }
    
    
    /**
     * Implementation class {@code implClass} must be subclass ( direct or indirect )
     * of {@code BaseDeviceObject} class else {@code IllegalArgumentException} is
     * thrown.
     * @param networkId ID of network, which returned device object belongs to.
     * @param nodeId ID of node, which returned device object belongs to.
     * @param implClass Device interface implementation class
     * @param connector connector to use
     * @param configuration configuration settings
     * @return device object
     * @throws IllegalArgumentException if {@code implClass} is not direct or 
     *         indirect subclass of {@code BaseDeviceObject} class.
     */
    @Override
    public DeviceObject getDeviceObject(String networkId, String nodeId, 
            ConnectorService connector, Class implClass, Configuration configuration
    ) throws Exception {
        Object[] logArgs = new Object[5];
        logArgs[0] = networkId;
        logArgs[1] = nodeId;
        logArgs[2] = connector;
        logArgs[3] = implClass;
        logArgs[4] = configuration;
        logger.debug("getDeviceObject - start: networkId={}, nodeId={}, "
                + "connector={}, implClass={}, configuration={}", logArgs
        );
        
        // implementation class must be subclass of BaseDeviceObject
        if ( !(BaseDeviceObject.class.isAssignableFrom(implClass)) ) {
            throw new IllegalArgumentException(
                    "Implementation class " + implClass.getName() + 
                    "is not subclass of " + BaseDeviceObject.class.getName() 
            );
        }
        
        // base device object creation
        if ( !(ConnectedDeviceObject.class.isAssignableFrom(implClass)) ) {
            return createBaseDeviceObject(networkId, nodeId, implClass);
        }
        
        // implClass is connected or asyncCallable
        // ...
        
        // Parameter types of the constructor
        Class[] paramsTypes = new Class[] { String.class, String.class, ConnectorService.class, 
            CallRequestProcessingInfoContainer.class };

        // Find the constructor
        java.lang.reflect.Constructor constructor;
        constructor = implClass.getConstructor(paramsTypes);
        
        // results container - only for connected device objects
        CallRequestProcessingInfoContainer resultsContainer = createResultsContainer(configuration);
        
        // Parameters for the constructor
        Object[] params = new Object[] { networkId, nodeId, connector, resultsContainer };
        BaseDeviceObject deviceObj = (BaseDeviceObject)constructor.newInstance(params);
        
        // if implClass is StandardServicesDeviceObject, configure it
        if ( StandardServicesDeviceObject.class.isAssignableFrom(implClass) ) {
            new SimpleStandardServicesDeviceObjectConfigurator().configure(
                    (StandardServicesDeviceObject)deviceObj, configuration
            );
        }
        
        logger.debug("getDeviceObject - end: {}", deviceObj);
        return deviceObj;
    }
    
}
