

package com.microrisc.simply.iqrf.dpa;

import com.microrisc.simply.StandardServicesDeviceObject;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.iqrf.dpa.devices.DPA_Device;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.types.DPA_AdditionalInfo;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Device Object for DPA specific needs.
 * 
 * @author Michal Konopa
 */
public class DPA_DeviceObject 
extends StandardServicesDeviceObject 
implements DPA_Device, DPA_StandardServices {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_DeviceObject.class);
    
    /** Default HW profile. */
    public static int DEFAULT_HW_PROFILE = 0xFFFF;
    
    /** HW profile. */
    private int hwProfile = DEFAULT_HW_PROFILE;
    
    
    private int checkHwProfile( int hwProfile ) {
        if ( (hwProfile < 0x0000) || (hwProfile > 0xFFFF) ) {
            throw new IllegalArgumentException("Invalid value of HW profile: " + hwProfile);
        }
        return hwProfile;
    }
    
    
    public DPA_DeviceObject(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public void setHwProfile(int hwProfile) {
        this.hwProfile = checkHwProfile( hwProfile );
    }
    
    @Override
    public int getHwProfile() {
        return hwProfile;
    }
    
    
    @Override
    public DPA_AdditionalInfo getDPA_AdditionalInfo(UUID callId) {
        logger.debug(logPrefix + "getDPA_AdditionalInfo - start: callId={}", callId);
        
        Object addInfo = getCallResultAdditionalInfo(callId);
        if (addInfo == null) {
            logger.debug(logPrefix + "getDPA_AdditionalInfo - end: null");
            return null;
        }
        
        if ( !(addInfo instanceof DPA_AdditionalInfo) ) {
            throw new IllegalStateException("Wrong additional info type.");
        }
        
        DPA_AdditionalInfo dpaAddInfo = (DPA_AdditionalInfo) addInfo;
        
        logger.debug(logPrefix + "getDPA_AdditionalInfo - end: {}", dpaAddInfo);
        return dpaAddInfo;
    }

    @Override
    public DPA_AdditionalInfo getDPA_AdditionalInfoOfLastCall() {
        logger.debug(logPrefix + "getDPA_AdditionalInfoOfLastCall - start: ");
        
        Object addInfo = getCallResultAdditionalInfoOfLastCall();
        if (addInfo == null) {
            logger.debug(logPrefix + "getDPA_AdditionalInfoOfLastCall - end: null");
            return null;
        }
        
        if ( !(addInfo instanceof DPA_AdditionalInfo) ) {
            throw new IllegalStateException("Wrong additional info type.");
        }
        
        DPA_AdditionalInfo dpaAddInfo = (DPA_AdditionalInfo) addInfo;
        
        logger.debug(logPrefix + "getDPA_AdditionalInfoOfLastCall - end: {}", dpaAddInfo);
        return dpaAddInfo;
    }

}
