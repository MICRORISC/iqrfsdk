

package com.microrisc.simply.iqrf.dpa.v201.types;

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

}
