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

package com.microrisc.simply.iqrf.dpa.v22x.types;

import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.MethodIdTransformer;


/**
 * Encapsulates individual DPA Request within a batch of a more requests.
 *  
 * @author Michal Konopa
 */
public final class DPA_Request {
    private final Class deviceIface;
    private final DeviceInterfaceMethodId methodId;
    private final Object[] args;
    private final int hwProfile;
    private final MethodIdTransformer methodTransformer;
    
    private Class checkDeviceInterface(Class deviceIface) {
        if ( deviceIface == null ) {
            throw new IllegalArgumentException("Device Interface cannot be null");
        }
        return deviceIface;
    }
    
    private DeviceInterfaceMethodId checkMethodId(DeviceInterfaceMethodId methodId) {
        if ( methodId == null ) {
            throw new IllegalArgumentException("Method ID cannot be null");
        }
        return methodId;
    }
    
    private int checkHwProfile(int hwProfile) {
        if ( (hwProfile < 0) || (hwProfile > 0xFFFF) ) {
            throw new IllegalArgumentException(
                    "HW profile cannot be less then 0 or greather then 0xFFFF"
            );
        }
        return hwProfile;
    }
    
    
    /**
     * Creates new DPA request.
     * @param deviceIface device interface
     * @param methodId ID of method to call
     * @param args arguments of the called method
     * @param methodTransformer method ID transformer. Mainly for usage with
     *        user defined device interfaces. If set to {@code null}, Simply
     *        will try to find out the transformer itself.
     * @param hwProfile HW profile
     * @throws IllegalArgumentException if {@code deviceIface} or {@code methodId}
     *         is {@code null}
     */
    public DPA_Request(
            Class deviceIface, DeviceInterfaceMethodId methodId, Object[] args,
            int hwProfile, MethodIdTransformer methodTransformer
    ) {
        this.deviceIface = checkDeviceInterface(deviceIface);
        this.methodId = checkMethodId(methodId);
        if ( args == null ) {
            this.args = new Object[0];
        } else {
            this.args = new Object[args.length];
            System.arraycopy(args, 0, this.args, 0, args.length);
        }
        this.hwProfile = checkHwProfile(hwProfile);
        this.methodTransformer = methodTransformer;
    }
    
    /**
     * Creates new DPA request. <br>
     * Method ID transformer will be set to {@code null}, which means that Simply
     * will try to find out the transformer itself.
     * @param deviceIface device interface
     * @param methodId ID of method to call
     * @param args arguments of the called method
     * @param hwProfile HW profile
     * @throws IllegalArgumentException if {@code deviceIface} or {@code methodId}
     *         is {@code null}
     */
    public DPA_Request(
            Class deviceIface, DeviceInterfaceMethodId methodId, Object[] args,
            int hwProfile
    ) {
        this(deviceIface, methodId, args, hwProfile, null);
    }
    
    /**
     * @return device interface
     */
    public Class getDeviceInterface() {
        return deviceIface;
    }

    /**
     * @return method ID
     */
    public DeviceInterfaceMethodId getMethodId() {
        return methodId;
    }

    /**
     * @return method's arguments
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @return the HW profile
     */
    public int getHwProfile() {
        return hwProfile;
    }

    /**
     * @return method transformer
     */
    public MethodIdTransformer getMethodTransformer() {
        return methodTransformer;
    }
    
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Device Interface: " + deviceIface + NEW_LINE);
        strBuilder.append(" Method ID: " + methodId + NEW_LINE);
        strBuilder.append(" Method arguments: " + args + NEW_LINE);
        strBuilder.append(" HW profile: " + hwProfile + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
