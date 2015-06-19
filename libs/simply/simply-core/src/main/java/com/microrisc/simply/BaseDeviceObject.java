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
 * Base implementation class for Device Objects. 
 * 
 * @author Michal Konopa
 */
public class BaseDeviceObject implements DeviceObject {
    /** Identifier of network, which this device object belongs to. */
    protected final String networkId;
    
    /** Identifier of node, which this device object belongs to. */
    protected final String nodeId;
    
    /** Device interface implemented by this device object. */
    protected final Class implementedDeviceInterface;
    
    
    private static String checkNetworkId(String networkId) {
        if ( networkId == null ) {
            throw new IllegalArgumentException("Network ID cannot be null");
        }
        return networkId;
    }
    
    private static String checkNodeId(String nodeId) {
        if ( nodeId == null ) {
            throw new IllegalArgumentException("Node ID cannot be null");
        }
        return nodeId;
    }
    
    /**
     * Discovers device interface implemented by this object.
     * @return device interface implemented by this object
     * @return {@code null} if no device interface has been found
     */
    private Class discoverImplementedDeviceInterface() {
        Class[] implIfaces = this.getClass().getInterfaces();
        for ( Class implIface : implIfaces ) {
            if (implIface.isAnnotationPresent(DeviceInterface.class)) {
                return implIface;
            }
        }
        return null;
    }
    
    private static Class checkImplementedDeviceInterface(Class devIface) {
        if ( devIface == null ) {
            throw new IllegalStateException(
                    "Device object doesn't implement no device interface"
            );
        }
        return devIface;
    }
    
    
    /**
     * Creates new device object with defined network ID and node ID.
     * @param networkId identifier of network, which this device object belongs to.
     * @param nodeId identifier of node, which this device object belongs to.
     * @throws IllegalArgumentException if {@code networkId} or {@code nodeId} 
     *         is {@code null}
     * @throws IllegalStateException if this device object doesn't implement no
     *         device interface
     */
    public BaseDeviceObject(String networkId, String nodeId) {
        this.networkId = checkNetworkId(networkId);
        this.nodeId = checkNodeId(nodeId);
        this.implementedDeviceInterface = checkImplementedDeviceInterface(
                discoverImplementedDeviceInterface()
        );
    }

    @Override
    public String getNetworkId() {
        return networkId;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public Class getImplementedDeviceInterface() {
        return implementedDeviceInterface;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Network ID: " + networkId + NEW_LINE);
        strBuilder.append(" Node ID: " + nodeId + NEW_LINE);
        strBuilder.append(
                " Implemented device interface: " 
                + implementedDeviceInterface.getCanonicalName() + NEW_LINE
        );
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
}
