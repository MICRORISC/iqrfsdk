
package com.microrisc.simply.iqrf.dpa.protocol;

import com.microrisc.simply.iqrf.dpa.DPA_ResponseCode;
import com.microrisc.simply.types.AbstractConvertor;
import com.microrisc.simply.types.ValueConversionException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Encapsulates DPA_ProtocolProperties application protocol properties. Implemented according to
 "IQRF DPA_ProtocolProperties" document.
 <p>
 * Request message format: <br>
 * NAdr | PNum | PCmd | HW Profile | DATA
 * 
 * <p>
 * Response message format: <br>
 NAdr | PNum | PCmd | HW Profile | RESPONSE_CODE | DPA_ProtocolProperties Value | RESPONSE_DATA
 
 <p>
 * Fields: <br>
 NAdr - Network device address ( 0 Coordinator, 1-0xEF Node address, 0xFF Broadcast )
        length: 2 bytes
 PNum - Perihperal number ( 0 IQMESH, 1 OS, 2-0x6F other peripherals )
 PCmd - Command specifying an action to be taken. Actual allowed value range 
        depends on the peripheral type. The most significant bit indicates DPA_ProtocolProperties 
        response message.
 HW Profile - HW Profile, length: 2B
 DATA - array of bytes ( only optionally )
 RESPONSE_CODE - response code, including error code
                  length: 2 bytes
 RESPONSE_DATA - optional response data. Node response data is sent in a case
              of error. 
 * 
 * @author Michal Konopa
 */
public final class DPA_ProtocolProperties {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DPA_ProtocolProperties.class);
    
    
    /** Start index of node address field. */
    static private final int NADR_START = 0;
    
    /** Length of node address field. */
    static private final int NADR_LENGTH = 2;
    
    
    /** Start index of perihperal number field. */
    static private final int PNUM_START = 2;
    
    /** Length of method perihperal number field. */
    static private final int PNUM_LENGTH = 1;
    
    
    /** Start index of data address field. */
    static private final int PCMD_START = 3;
    
    /** Length of data address field. */
    static private final int PCMD_LENGTH = 1;
    
    
    /** Start of HW Profile. */
    static private final int HW_PROFILE_START = 4;
    
    /** Length of HW Profile field. */
    static private final int HW_PROFILE_LENGTH = 2;
    
    
    /** Start index of optional data field. */
    static private final int DATA_START = 6;
    
    /** Start index of response code field. */
    static private final int RESPONSE_CODE_START = 6;
    
    /** Length of response code field. */
    static private final int RESPONSE_CODE_LENGTH = 1;
    
    
    /** Start index of DPA_ProtocolProperties Value field. */
    static private final int DPA_VALUE_START = 7;
    
    /** Length of DPA_ProtocolProperties Value field. */
    static private final int DPA_VALUE_LENGTH = 1;
    
    
    /** Start index of response data. */
    static private final int RESPONSE_DATA_START = DPA_VALUE_START + DPA_VALUE_LENGTH;
    
    
    /** IQMESH Coordinator address. */
    static public final int IQMESH_COORDINATOR_ADDRESS = 0x00;
    
    /** Local ( over SPI ) device address. */
    static public final int LOCAL_DEVICE_ADDRESS = 0xFC;
    
    /** IQMESH Broadcast address. */
    static public final int BROADCAST_ADDRESS = 0xFF;
    
    
    // Suppress default constructor for noninstantiability
    private DPA_ProtocolProperties() {
        throw new AssertionError();
    }
    
    
    /**
     * Sets part of specified protocol message to specified Integer data.
     * @param protoMsg message to set
     * @param data source data
     * @param startIndex start index in the message
     * @param dataLength number of bytes to copy
     */
    static private void setMessageData_Int(short[] protoMsg, int data, int startIndex, 
            int dataLength) {
        logger.debug("setMessageData_Integer - start: protoMsg={}, data={}", 
                protoMsg, data
        );
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(data);
        for ( int byteId = 0; byteId < dataLength; byteId++ ) {
            protoMsg[byteId + startIndex] = (short)(byteBuffer.get(byteId) & 0xFF);
        }
        
        logger.debug("setMessageData_Integer - end");
    }
    
    /**
     * Returns Integer-typed specified part of specified protocol message.
     * @param protoMsg source message
     * @param startIndex start index in the message
     * @param dataLength number of bytes
     */
    static private int getMessageData_Int(short[] protoMsg, int startIndex, 
            int dataLength
    ) {
        logger.debug("getMessageData_Integer - start:");
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int byteId = 0; byteId < dataLength; byteId++) {
            byteBuffer.put((byte)protoMsg[startIndex + byteId]);
        }
        
        Integer data = byteBuffer.getInt(0);
        
        logger.debug("getMessageData_Integer - end: {}", data);
        return data;
    }
    
    /**
     * Returns length of tripple.
     * @return length of tripple.
     */
    static int getTrippleLength() {
        return PCMD_START + PCMD_LENGTH;
    }
    
    /**
     * Sets NADR field of specified message to specified value.
     * @param protoMsg message to set
     * @param nodeAddress value to use
     */
    static void setNodeAddress(short[] protoMsg, int nodeAddress) {
        setMessageData_Int(protoMsg, nodeAddress, NADR_START, NADR_LENGTH);
    }
    
    /**
     * Sets PNUM field of specified message to specified value.
     * @param protoMsg message to set
     * @param perNumber value to use
     */
    static void setPeripheralNumber(short[] protoMsg, int perNumber) {
        setMessageData_Int(protoMsg, perNumber, PNUM_START, PNUM_LENGTH);
    }
    
    /**
     * Sets PCmd field of specified message to specified value.
     * @param protoMsg message to set
     * @param command value to use
     */
    static void setCommand(short[] protoMsg, int command) {
        setMessageData_Int(protoMsg, command, PCMD_START, PCMD_LENGTH);
    }
    
    /**
     * Sets specified message NADDR field to specified device ID. 
     * @param protoMsg message to set
     * @param deviceId device ID
     */
    static void setNodeAddress(short[] protoMsg, String deviceId) {
        logger.debug("setAddress - start: protoMsg={}, deviceId={}", protoMsg, deviceId);
        
        int addr = Integer.parseInt(deviceId);
        setMessageData_Int(protoMsg, addr, NADR_START, NADR_LENGTH);
        
        logger.debug("setAddress - end");
    }
    
    /**
     * Sets specified message PNUM field to specified peripheral identifier.
     * @param protoMsg message to set
     * @param perId peripheral identifier
     */
    static void setPerihperalNumber(short[] protoMsg, String perId) {
        logger.debug("setPerNum - start: protoMsg={}, perNum={}", protoMsg, perId);
        
        int ifaceNum = Integer.parseInt(perId);
        setMessageData_Int(protoMsg, ifaceNum, PNUM_START, PNUM_LENGTH);
        
        logger.debug("setPerNum - end");
    }
    
    
    /** RESPONSE PROCESSING. */
    
    /**
     * Returns NADR field of specified message.
     * @param protoMsg source message
     * @return NADR field of specified message.
     */
    static int getNodeAddress(short[] protoMsg) {
        logger.debug("setUserData - start: protoMsg={}", protoMsg);
        
        int nodeAddress = getMessageData_Int(protoMsg, NADR_START, NADR_LENGTH); 
        
        logger.debug("getNodeAddress - end: {}", nodeAddress);
        return nodeAddress;
    }
    
    /**
     * Returns PNUM field of specified message.
     * @param protoMsg source message
     * @return PNUM field of specified message.
     */
    static int getPeripheralNumber(short[] protoMsg) {
        logger.debug("getPeripheralNumber - start: protoMsg={}", protoMsg);
        
        int perNumber = getMessageData_Int(protoMsg, PNUM_START, PNUM_LENGTH);
        
        logger.debug("getPeripheralNumber - end: {}", perNumber);
        return perNumber;
    }
    
    /**
     * Returns PCmd field of specified message.
     * @param protoMsg source message
     * @return PCmd field of specified message.
     */
    static int getCommand(short[] protoMsg) {
        logger.debug("getCommand - start: protoMsg={}", protoMsg);
        
        int command = getMessageData_Int(protoMsg, PCMD_START, PCMD_LENGTH);
        
        logger.debug("getCommand - end: {}", command);
        return command;
    }
    
    /**
     * Returns RESPONSE_CODE field of specified message ( must be a response ).
     * @param protoMsg source message
     * @return RESPONSE_CODE field of specified message
     * @throws ValueConversionException, if response code contains unknown value
     */
    public static DPA_ResponseCode getResponseCode(short[] protoMsg) 
            throws ValueConversionException {
        logger.debug("getResponseCode - start: protoMsg={}", protoMsg);
        
        int responseIntCode = getMessageData_Int(
                protoMsg, RESPONSE_CODE_START, RESPONSE_CODE_LENGTH
        );
        for ( DPA_ResponseCode responseCode : DPA_ResponseCode.values() ) {
            if ( responseCode.getCodeValue() == responseIntCode ) {
                logger.debug("getResponseCode - end: {}", responseCode);
                return responseCode;
            }
        }
        
        // unknown reponse code
        throw new ValueConversionException("Unknown response code: " + responseIntCode);
    }
    
    /**
     * Retruns response code length.
     * @return 
     */
    static int getResponseCodeLength() {
        return RESPONSE_CODE_LENGTH;
    }
    
    /**
     * Returns RESPONSE_DATA field of specified message ( must be a response ).
     * @param protoMsg source message
     * @return RESPONSE_DATA field of specified message
     */
    static short[] getResponseData(short[] protoMsg) {
        logger.debug("getResponseData - start: protoMsg={}", protoMsg);
        
        int responseLength = protoMsg.length - RESPONSE_DATA_START;
        short[] responseData = new short[protoMsg.length - RESPONSE_DATA_START];
        System.arraycopy(protoMsg, RESPONSE_DATA_START, responseData, 0, responseLength);
        
        logger.debug("getResponseData - end: {}", responseData);
        return responseData;
    }
    
    /**
     * Returns converted RESPONSE_DATA field.
     * @param protoMsg source message
     * @param typeConvertor type convertor to use     
     * @return RESPONSE_DATA field of specified message.
     * @throws ValueConversionException
     */
    static Object getReturnValue(short[] protoMsg, AbstractConvertor typeConvertor) 
            throws ValueConversionException {
        logger.debug("getReturnValue - start: protoMsg={}, typeConvertor={}", 
                protoMsg, typeConvertor
        );
        
        short[] retVal = new short[protoMsg.length - RESPONSE_DATA_START];
        System.arraycopy(protoMsg, RESPONSE_DATA_START, retVal, 0, retVal.length);
        
        // may throw exception
        Object retValObj = typeConvertor.toObject(retVal); 
      
        logger.debug("getReturnValue - end: {}", retValObj);
        return retValObj;
    }
}
