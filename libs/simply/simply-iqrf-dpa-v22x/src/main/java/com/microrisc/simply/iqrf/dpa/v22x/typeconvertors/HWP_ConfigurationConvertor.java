/* 
 * Copyright 2014-2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v22x.types.HWP_Configuration;
import com.microrisc.simply.iqrf.dpa.v22x.types.IntegerFastQueryList;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from HWP Configuration type values to
 * {@code HWP_Configuration} objects.
 * <p>
 * @author Michal Konopa
 * @author Martin Strouhal
 */
// October 2015 - implemented toProtoValue and added conversion of undocumented byte
// May 2016 - updated tp DPA 2.27, added RFPGM
public final class HWP_ConfigurationConvertor extends PrimitiveConvertor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(HWP_ConfigurationConvertor.class);

    private HWP_ConfigurationConvertor() {
    }

    /** Singleton. */
    private static final HWP_ConfigurationConvertor instance = new HWP_ConfigurationConvertor();

    /** Size of returned response. */
    static public final int REQUEST_TYPE_SIZE = 0x21;
    static public final int RESPONSE_TYPE_SIZE = 0x21;

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
    static private final int RFPGM_POS = 0x20;
    /** Only in response (ReadHWP) */
    static private final int UNDOCUMENTED_POS = 0x21;
    /** Only in response (ReadHWP) */
    static private final int UNDOCUMENTED_RESPONSE_LENGTH = 1;
    
    static private final int RFPGM_SINGLE_CHANNEL_MASK = 0b00000011;
    static private final int RFPGM_LP_MODE_MASK = 0b00000100;
    static private final int RFPGM_INVOKE_BY_RESET_MASK = 0b00010000;
    static private final int RFPGM_AUTOMATIC_TERMINATION_MASK = 0b01000000;
    static private final int RFPGM_TERMINATION_BY_PIN_MASK = 0b10000000;

    /**
     * @return {@code HWP_ConfigurationConvertor} instance
     */
    @ConvertorFactoryMethod
    static public HWP_ConfigurationConvertor getInstance() {
        return instance;
    }

    @Override
    public int getGenericTypeSize() {
        return REQUEST_TYPE_SIZE;
    }

    private IntegerFastQueryList getStandardPeripherals(short[] standPerBytes)
            throws ValueConversionException {
        return (IntegerFastQueryList) IntegerFastQueryListConvertor.getInstance().toObject(standPerBytes);
    }

    /**
     * Puts speicified standard peripherals into spciefied proto value.
     */
    private void putStandardPeriperals(short[] protoValue, IntegerFastQueryList peripherals)
            throws ValueConversionException {
        short[] shortPeripherals = IntegerFastQueryListConvertor.getInstance().toProtoValue(peripherals);

        if (shortPeripherals.length > STANDARD_PER_LENGTH) {
            throw new ValueConversionException("IntegerQueryList size is greater than is allowed length of peripherals!");
        }

        for (int i = 0; i < STANDARD_PER_LENGTH; i++) {
            protoValue[i + STANDARD_PER_POS] += i < shortPeripherals.length ? shortPeripherals[i] : 0;
        }
    }

    private HWP_Configuration.DPA_ConfigFlags getConfigFlagsAsObject(short configFlagsByte) {
        boolean callHandlerOnEvent = ((configFlagsByte & 0x01) == 0x01);
        boolean controlledByLocalSPI = ((configFlagsByte & 0x02) == 0x02);
        boolean runAutoexecOnBootTime = ((configFlagsByte & 0x04) == 0x04);
        boolean notRouteOnBackground = ((configFlagsByte & 0x08) == 0x08);
        boolean runIOSetupOnBootTime = ((configFlagsByte & 0x10) == 0x10);
        boolean receivePeerToPeer = ((configFlagsByte & 0x20) == 0x20);
        return new HWP_Configuration.DPA_ConfigFlags(
                callHandlerOnEvent, controlledByLocalSPI, runAutoexecOnBootTime,
                notRouteOnBackground, runIOSetupOnBootTime, receivePeerToPeer
        );
    }

    /**
     * Returns protoValue of specified config flags in proto value.
     */
    private short getConfigFlagsToProtoValue(HWP_Configuration.DPA_ConfigFlags configFlags) {
        short flags = 0;
        flags += configFlags.isReceivesPeerToPeer() ? 1 : 0;
        flags <<= 1;
        flags += configFlags.isIOSetupRunOnBootTime() ? 1 : 0;
        flags <<= 1;
        flags += configFlags.notRouteOnBackground() ? 1 : 0;
        flags <<= 1;
        flags += configFlags.isAutoexecRunOnBootTime() ? 1 : 0;
        flags <<= 1;
        flags += configFlags.canBeControlledByLocalSPI() ? 1 : 0;
        flags <<= 1;
        flags += configFlags.isHandlerCalledOnEvent() ? 1 : 0;

        return flags;
    }

    private HWP_Configuration.RFPGM getRFPGMAsObject(short rfpgmByte){
       boolean singleChannel = (rfpgmByte & RFPGM_SINGLE_CHANNEL_MASK) == RFPGM_SINGLE_CHANNEL_MASK ? true : false;
       boolean lpMode = (rfpgmByte & RFPGM_LP_MODE_MASK) == RFPGM_LP_MODE_MASK ? true : false;
       boolean invokeByReset = (rfpgmByte & RFPGM_INVOKE_BY_RESET_MASK) == RFPGM_INVOKE_BY_RESET_MASK ? true : false;
       boolean automaticTermination = (rfpgmByte & RFPGM_AUTOMATIC_TERMINATION_MASK) == RFPGM_AUTOMATIC_TERMINATION_MASK ? true : false;
       boolean terminationByPin = (rfpgmByte & RFPGM_TERMINATION_BY_PIN_MASK) == RFPGM_TERMINATION_BY_PIN_MASK ? true : false;
       
       return new HWP_Configuration.RFPGM(singleChannel, lpMode, invokeByReset,
               automaticTermination, terminationByPin);
    }
    
    private short getRFPGMToProtoValue(HWP_Configuration.RFPGM rfpgm){
      short rfpgmByte = 0;
      rfpgmByte += rfpgm.isSingleChannel() ? (0xFF & RFPGM_SINGLE_CHANNEL_MASK) : 0;
      rfpgmByte += rfpgm.isLpMode() ? (0xFF & RFPGM_LP_MODE_MASK) : 0;
      rfpgmByte += rfpgm.isInvokeRfpgmByReset() ? (0xFF & RFPGM_INVOKE_BY_RESET_MASK) : 0;
      rfpgmByte += rfpgm.isAutomaticTermination() ? (0xFF & RFPGM_AUTOMATIC_TERMINATION_MASK) : 0;
      rfpgmByte += rfpgm.isTerminationByPin() ? (0xFF & RFPGM_TERMINATION_BY_PIN_MASK) : 0;
      
      return rfpgmByte;
    }
    
    /**
     * Get undocumented byte from protoValue.
     * <p>
     * @param protoValue from which will be getted undocumented byte
     * @return undocumented byte, see {@link HWP_Configuration#undocumented}
     */
    private short[] getUndocumentedByte(short[] protoValue) {
        short[] undocumented = new short[UNDOCUMENTED_RESPONSE_LENGTH];
        System.arraycopy(protoValue, UNDOCUMENTED_POS, undocumented, 0, UNDOCUMENTED_RESPONSE_LENGTH);
        return undocumented;
    }

    /**
     * Checks, if specified object contains allowable value of HWP configuration
     * and returns {@link HWP_Configuration} object. Otherwise is
     * {@link IllegalArgumentException} thrown.
     * <p>
     * @param config object to check and convert
     * @return converted {@link HWP_Configuration} object
     */
    private HWP_Configuration checkHWPConfig(Object config) {
        if (!(config instanceof HWP_Configuration)) {
            throw new IllegalArgumentException("Object to convert must be type of HWP configuration.");
        }
        if (config == null) {
            throw new IllegalArgumentException("HWP configuration cannot be null.");
        }
        HWP_Configuration hwp_config = (HWP_Configuration) config;
        if (hwp_config.getUndocumented() == null) {
            throw new IllegalArgumentException("Undocumented byte cannot be null.");
        }
        if (hwp_config.getUndocumented().length < 1) {
            throw new IllegalArgumentException("Undocumented byte value cannot be smaller than 1.");
        }
        return hwp_config;
    }

    @Override
    public short[] toProtoValue(Object value) throws ValueConversionException {
        logger.debug("toProtoValue - start: value={}", value);

        HWP_Configuration config = checkHWPConfig(value);
        short[] protoValue = new short[REQUEST_TYPE_SIZE];

        // put stanrd peripherals in protoValue to HWP config's protoValue
        putStandardPeriperals(protoValue, config.getStandardPeripherals());
        // config flags
        protoValue[CONFIG_FLAGS_POS] = getConfigFlagsToProtoValue(config.getConfigFlags());

        // number properties
        protoValue[RFCHANNEL_A_SUB_NETWORK_POS] = (short) (config.getRFChannelASubNetwork());
        protoValue[RFCHANNEL_B_SUB_NETWORK_POS] = (short) (config.getRFChannelBSubNetwork());
        protoValue[RFOUTPUT_POWER_POS] = (short) (config.getRFOutputPower());
        protoValue[RFSIGNAL_FILTER_POS] = (short) (config.getRFSignalFilter());
        protoValue[TIMEOUT_RECV_RF_PACKETS] = (short) (config.getTimeoutRecvRFPackets());
        protoValue[BAUD_RATE_OF_UART] = (short) (config.getBaudRateOfUARF());
        protoValue[RFCHANNEL_A_POS] = (short) (config.getRFChannelA());
        protoValue[RFCHANNEL_B_POS] = (short) (config.getRFChannelB());
        protoValue[RFPGM_POS] = getRFPGMToProtoValue(config.getRfpgm());
        

        // calculate checksum
        protoValue[CHECKSUM_POS] = 0x5F;
        for (int i = 1; i < RFPGM_POS; i++) {
            protoValue[CHECKSUM_POS] ^= protoValue[i];
        }

        logger.debug("toProtoValue - end: {}", protoValue);
        return protoValue;
    }

    @Override
    public Object toObject(short[] protoValue) throws ValueConversionException {
        logger.debug("toObject - start: protoValue={}", protoValue);

        short[] standPerBytes = new short[STANDARD_PER_LENGTH];
        for (short valueId = 0; valueId < STANDARD_PER_LENGTH; valueId++) {
            standPerBytes[valueId] = (short) (protoValue[valueId + STANDARD_PER_POS] ^ XOR_OPERAND);
        }

        IntegerFastQueryList standardPeripherals = getStandardPeripherals(standPerBytes);
        HWP_Configuration.DPA_ConfigFlags configFlags = getConfigFlagsAsObject(
                (short) (protoValue[CONFIG_FLAGS_POS] ^ XOR_OPERAND)
        );
        int RFChannelASubNetwork = protoValue[RFCHANNEL_A_SUB_NETWORK_POS] ^ XOR_OPERAND;
        int RFChannelBSubNetwork = protoValue[RFCHANNEL_B_SUB_NETWORK_POS] ^ XOR_OPERAND;
        int RFOutputPower = protoValue[RFOUTPUT_POWER_POS] ^ XOR_OPERAND;
        int RFSignalFilter = protoValue[RFSIGNAL_FILTER_POS] ^ XOR_OPERAND;
        int timeoutRecvRFPackets = protoValue[TIMEOUT_RECV_RF_PACKETS] ^ XOR_OPERAND;
        int baudRateOfUARF = protoValue[BAUD_RATE_OF_UART] ^ XOR_OPERAND;
        int RFChannelA = protoValue[RFCHANNEL_A_POS] ^ XOR_OPERAND;
        int RFChannelB = protoValue[RFCHANNEL_B_POS] ^ XOR_OPERAND;
        HWP_Configuration.RFPGM rfpgm = getRFPGMAsObject(protoValue[RFPGM_POS]);
        
        short[] undocumentedByte = getUndocumentedByte(protoValue);

        HWP_Configuration hwpConfig = new HWP_Configuration(
                standardPeripherals, configFlags, RFChannelASubNetwork, 
                RFChannelBSubNetwork, RFOutputPower, RFSignalFilter, 
                timeoutRecvRFPackets, baudRateOfUARF, RFChannelA, RFChannelB, 
                rfpgm, undocumentedByte
        );

        logger.debug("toObject - end: {}", hwpConfig);
        return hwpConfig;
    }
}