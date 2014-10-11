
package com.microrisc.simply.iqrf.dpa.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.types.DPA_Request;
import com.microrisc.simply.iqrf.dpa.types.HWP_Configuration;
import com.microrisc.simply.iqrf.dpa.types.OsInfo;
import com.microrisc.simply.iqrf.dpa.types.SleepInfo;
import com.microrisc.simply.iqrf.types.VoidType;

/**
 * DPA OS Device Interface.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if some error has occurred during processing
 * of this method.
 * 
 * @author Michal Konopa
 */
@DeviceInterface
public interface OS 
extends DPA_Device, DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {
    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {
        READ,
        RESET,
        READ_HWP_CONFIGURATION,
        RUN_RFPGM,
        SLEEP,
        BATCH,
        SET_USEC,
        SET_MID
    }
    
    /**
     * Returns some useful system information about the node.
     * @return information about module and OS
     */
    OsInfo read();
    
    /**
     * Forces (DC)TR transceiver module to carry out reset.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType reset();
    
    /**
     * Reads a raw HWP configuration memory.
     * @return configuration memory data read.
     */
    HWP_Configuration readHWPConfiguration();
    
    /**
     * Puts device into RFPGM mode configured at HWP Configuration. The device 
     * is reset when RFPGM process is finished or if it ends due to timeout. 
     * RFPGM runs at same channels (configured at HWP configuration) the network
     * is using.
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType runRFPGM();
    
    /**
     * Puts device into sleep (power saving) mode. This command is not 
     * implemented at the device having coordinator functionality i.e. [C] and [CN].
     * @param sleepInfo information about sleeping
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType sleep(SleepInfo sleepInfo);
    
    /**
     * Allows to execute more individual DPA requests within one original 
     * DPA request. It is not allowed to embed Batch command itself within series 
     * of individual DPA requests. Using Run discover is not allowed inside 
     * batch command list too.
     * @param requests DPA requests to be executed
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType batch(DPA_Request[] requests);
    
    /**
     * Sets value of User Security Code (USEC). USEC is used for an additional 
     * authorization to enter maintenance DPA Service Mode.
     * @param value USEC value. The initial value for a new device is 0xFFFF (65,535 decimal). 
     *              Value is coded using little-endian style..
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType setUSEC(int value);
    
    /**
     * Sets a unique device Module ID (MID). This can be useful for creating a 
     * backup HW of the coordinator device (also see coordinator Backup and Restore).
     * A special encrypted 24 byte long key obtained from device manufacturer is 
     * needed. Nevertheless the very last 4 bytes equal to the current MID, and 
     * the previous 4 bytes equal to the new MID to be set.
     * @param key value to set
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType setMID(short[] key);
}
