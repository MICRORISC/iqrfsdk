
package com.microrisc.simply.iqrf.dpa.v210.types;

import com.microrisc.simply.types.PrimitiveConvertor;
import com.microrisc.simply.types.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from HWP Configuration type values 
 * to {@code HWP_Configuration} objects. 
 * 
 * @author Michal Konopa
 */
public final class HWP_ConfigurationConvertor extends PrimitiveConvertor {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(HWP_ConfigurationConvertor.class);
    
    private HWP_ConfigurationConvertor() {}
    
    /** Singleton. */
    private static final HWP_ConfigurationConvertor instance = new HWP_ConfigurationConvertor();
    
    /** Size of returned response. */
    static public final int TYPE_SIZE = 0x13;
    
    /** Operand value to use for xoring values comming from protocol. */
    static private final int XOR_OPERAND = 0x34;
    
    // positions of fields
    static private final int CHECKSUM_POS = 0x00;
    static private final int STANDARD_PER_POS = 0x01;
    static private final int STANDARD_PER_LENGTH = 0x04;
    static private final int CONFIG_FLAGS_POS = 0x05;
    static private final int RFCHANNEL_A_SUB_NETWORK_POS = 0x06;
    static private final int RFCHANNEL_B_SUB_NETWORK_POS = 0x07;
    static private final int RFOUTPUT_POWER_POS = 0x08;
    static private final int RFSIGNAL_FILTER_POS = 0x09;
    static private final int TIMEOUT_RECV_RF_PACKETS = 0x0A;
    static private final int BAUD_RATE_OF_UART = 0x0B;
    static private final int RFCHANNEL_A_POS = 0x11;
    static private final int RFCHANNEL_B_POS = 0x12;
    
    
    /**
     * @return {@code HWP_ConfigurationConvertor} instance 
     */
    static public HWP_ConfigurationConvertor getInstance() {
        return instance;
    }
    
    @Override
    public int getGenericTypeSize() {
        return TYPE_SIZE;
    }
    
    
    private IntegerFastQueryList getStandardPeripherals(short[] standPerBytes) 
            throws ValueConversionException 
    { 
        return (IntegerFastQueryList)IntegerFastQueryListConvertor.getInstance().toObject(standPerBytes);
    }
    
    private HWP_Configuration.DPA_ConfigFlags getConfigFlags(short configFlagsByte) {
        boolean callHandlerOnEvent = ((configFlagsByte & 0x01) == 0x01);
        boolean controlledByLocalSPI = ((configFlagsByte & 0x02) == 0x02);
        boolean runAutoexecOnBootTime = ((configFlagsByte & 0x04) == 0x04);
        boolean notRouteOnBackground = ((configFlagsByte & 0x08) == 0x08);
        return new HWP_Configuration.DPA_ConfigFlags(
                callHandlerOnEvent, controlledByLocalSPI, runAutoexecOnBootTime, 
                notRouteOnBackground
        );
    }
    
    /**
     * Currently not supported. Throws {@code UnsupportedOperationException }.
     * @throws UnsupportedOperationException 
     */
    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);
        
        short[] standPerBytes = new short[STANDARD_PER_LENGTH];
        for ( short valueId = 0; valueId < STANDARD_PER_LENGTH; valueId++ ) {
            standPerBytes[valueId] = (short) (protoValue[valueId + STANDARD_PER_POS] ^ XOR_OPERAND); 
        }
        
        IntegerFastQueryList standardPeripherals = getStandardPeripherals(standPerBytes);
        HWP_Configuration.DPA_ConfigFlags configFlags = getConfigFlags(
                (short)( protoValue[CONFIG_FLAGS_POS] ^ XOR_OPERAND )
        );
        int RFChannelASubNetwork = protoValue[RFCHANNEL_A_SUB_NETWORK_POS] ^ XOR_OPERAND;
        int RFChannelBSubNetwork = protoValue[RFCHANNEL_B_SUB_NETWORK_POS] ^ XOR_OPERAND;
        int RFOutputPower = protoValue[RFOUTPUT_POWER_POS] ^ XOR_OPERAND;
        int RFSignalFilter = protoValue[RFSIGNAL_FILTER_POS] ^ XOR_OPERAND;
        int timeoutRecvRFPackets = protoValue[TIMEOUT_RECV_RF_PACKETS] ^ XOR_OPERAND;
        int baudRateOfUARF = protoValue[BAUD_RATE_OF_UART] ^ XOR_OPERAND;
        int RFChannelA = protoValue[RFCHANNEL_A_POS] ^ XOR_OPERAND;
        int RFChannelB = protoValue[RFCHANNEL_B_POS] ^ XOR_OPERAND;
        
        HWP_Configuration hwpConfig = new HWP_Configuration(
                standardPeripherals, configFlags, RFChannelASubNetwork, RFChannelBSubNetwork,
                RFOutputPower, RFSignalFilter, timeoutRecvRFPackets, baudRateOfUARF, 
                RFChannelA, RFChannelB
        );
        
        logger.debug("toObject - end: {}", hwpConfig);
        return hwpConfig;
    }
}
