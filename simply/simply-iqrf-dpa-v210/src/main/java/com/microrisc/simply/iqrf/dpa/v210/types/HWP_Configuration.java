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

package com.microrisc.simply.iqrf.dpa.v210.types;

/**
 * HWP Configuration.
 * 
 * @author Michal Konopa
 */
public final class HWP_Configuration {
    /** Standard peripherals. */
    private final IntegerFastQueryList standardPeripherals;

    
    /**
     * Various DPA configuration flag bits.
     */
    public static class DPA_ConfigFlags {
        private final boolean callHandlerOnEvent;
        private final boolean controlledByLocalSPI;
        private final boolean runAutoexecOnBootTime;
        private final boolean notRouteOnBackground;
        
        
        public DPA_ConfigFlags(
                boolean callHandlerOnEvent, boolean controlledByLocalSPI, 
                boolean runAutoexecOnBootTime, boolean notRouteOnBackground
        ) {
            this.callHandlerOnEvent = callHandlerOnEvent;
            this.controlledByLocalSPI = controlledByLocalSPI;
            this.runAutoexecOnBootTime = runAutoexecOnBootTime;
            this.notRouteOnBackground = notRouteOnBackground;
        }
        
        /**
         * @return indication if custom DPA handler is called in case of event
         */
        public boolean isHandlerCalledOnEvent() {
            return callHandlerOnEvent;
        }

        /**
         * @return indication if DPA can be controlled by local SPI
         */
        public boolean canBeControlledByLocalSPI() {
            return controlledByLocalSPI;
        }

        /**
         * @return indication if autoexec is run on boot time
         */
        public boolean isAutoexecRunOnBootTime() {
            return runAutoexecOnBootTime;
        }

        /**
         * @return indication if Node device does not route packet on the background
         */
        public boolean notRouteOnBackground() {
            return notRouteOnBackground;
        }
        
        @Override
        public String toString() {
            StringBuilder strBuilder = new StringBuilder();
            String NEW_LINE = System.getProperty("line.separator");

            strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
            strBuilder.append(" Call Custom handler on event: " + callHandlerOnEvent + NEW_LINE);
            strBuilder.append(" Can DPA be controlled by local SPI: " + controlledByLocalSPI + NEW_LINE);
            strBuilder.append(" Is Autoexec run on boot time: " + runAutoexecOnBootTime + NEW_LINE);
            strBuilder.append(" Node does not route on background: " + notRouteOnBackground + NEW_LINE);
            strBuilder.append("}");

            return strBuilder.toString();
        }
        
        public String toPrettyFormatedString() {
            StringBuilder strBuilder = new StringBuilder();
            String NEW_LINE = System.getProperty("line.separator");

            strBuilder.append("Call Custom handler on event: " + callHandlerOnEvent + NEW_LINE);
            strBuilder.append("Can DPA be controlled by local SPI: " + controlledByLocalSPI + NEW_LINE);
            strBuilder.append("Is Autoexec run on boot time: " + runAutoexecOnBootTime + NEW_LINE);
            strBuilder.append("Node does not route on background: " + notRouteOnBackground + NEW_LINE);

            return strBuilder.toString();
        }
        
    }
    
    /** Various DPA configuration flag bits. */
    private final DPA_ConfigFlags configFlags;
    
    /**
     * RF channel A of the optional subordinate network in case the node also 
     * plays a role of the coordinator of such network.
     */
    private final int RFChannelASubNetwork;
    
    /**
     * RF channel B of the optional subordinate network in case the node also 
     * plays a role of the coordinator of such network.
     */
    private final int RFChannelBSubNetwork;
    
    /** RF output power. Valid numbers 0-7. */
    private final int RFOutputPower;
    
    /** RF signal filter. Valid numbers 0-64. */
    private final int RFSignalFilter;
    
    /**
     * Timeout when receiving RF packets at LP or XLP modes. Unit is cycles 
     * (one cycle is 46 ms at LP, 770 ms at XLP mode). Greater values save energy 
     * but might decrease responsiveness to the master interface DPA Requests and 
     * also decrease Idle event calling frequency. Valid numbers 1-255. Also see 
     * APIvariable uns8 LP_XLP_toutRF.
     */
    private final int timeoutRecvRFPackets;
    
    /**
     * Baud rate of the UART interface if used. Uses the same coding as UART Open
     * (i.e. 0x00 = 1200 ).
     */
    private final int baudRateOfUARF;
    
    /** RF channel A of the main network. Valid numbers depend on used RF band. */
    private final int RFChannelA;
    
    /** RF channel B of the main network. Valid numbers depend on used RF band. */
    private final int RFChannelB;
    
    
    /**
     * Creates new object of HWP configuration.
     * @param standardPeripherals standard peripherals.
     * @param configFlags Various DPA configuration flag bits.
     * @param RFChannelASubNetwork RF channel A of the optional subordinate network 
     *                             in case the node also plays a role of the coordinator 
     *                             of such network.
     * @param RFChannelBSubNetwork RF channel B of the optional subordinate network 
     *                             in case the node also plays a role of the coordinator 
     *                             of such network.
     * @param RFOutputPower RF output power. Valid numbers 0-7.
     * @param RFSignalFilter RF signal filter. Valid numbers 0-64.
     * @param timeoutRecvRFPackets Timeout when receiving RF packets at LP or 
     *        XLP modes. Unit is cycles (one cycle is 46 ms at LP, 770 ms at XLP mode).
     *        Greater values save energy but might decrease responsiveness to the master 
     *        interface DPA Requests and also decrease Idle event calling frequency. 
     *        Valid numbers 1-255. Also see API variable uns8 LP_XLP_toutRF.
     * @param baudRateOfUARF Baud rate of the UART interface if used. Uses the 
     *        same coding as UART Open (i.e. 0x00 = 1200 )
     * @param RFChannelA RF channel A of the main network. Valid numbers depend on used RF band. 
     * @param RFChannelB RF channel B of the main network. Valid numbers depend on used RF band.
     */
    public HWP_Configuration(
            IntegerFastQueryList standardPeripherals, DPA_ConfigFlags configFlags, 
            int RFChannelASubNetwork, int RFChannelBSubNetwork, int RFOutputPower, 
            int RFSignalFilter, int timeoutRecvRFPackets, int baudRateOfUARF, 
            int RFChannelA, int RFChannelB
    ) {
        this.standardPeripherals = standardPeripherals;
        this.configFlags = configFlags;
        this.RFChannelASubNetwork = RFChannelASubNetwork;
        this.RFChannelBSubNetwork = RFChannelBSubNetwork;
        this.RFOutputPower = RFOutputPower;
        this.RFSignalFilter = RFSignalFilter;
        this.timeoutRecvRFPackets = timeoutRecvRFPackets;
        this.baudRateOfUARF = baudRateOfUARF;
        this.RFChannelA = RFChannelA;
        this.RFChannelB = RFChannelB;
    }
    
    /**
     * @return list of standard peripherals
     */
    public IntegerFastQueryList getStandardPeripherals() {
        return standardPeripherals;
    }

    /**
     * @return Various DPA configuration flag bits
     */
    public DPA_ConfigFlags getConfigFlags() {
        return configFlags;
    }

    /**
     * @return RF channel A of the optional subordinate network in case the node also 
     *         plays a role of the coordinator of such network.
     */
    public int getRFChannelASubNetwork() {
        return RFChannelASubNetwork;
    }

    /**
     * @return RF channel B of the optional subordinate network in case the node also 
     *         plays a role of the coordinator of such network.
     */
    public int getRFChannelBSubNetwork() {
        return RFChannelBSubNetwork;
    }

    /**
     * @return RF output power. Valid numbers 0-7.
     */
    public int getRFOutputPower() {
        return RFOutputPower;
    }

    /**
     * @return RF signal filter. Valid numbers 0-64.
     */
    public int getRFSignalFilter() {
        return RFSignalFilter;
    }

    /**
     * @return Timeout when receiving RF packets at LP or XLP modes
     */
    public int getTimeoutRecvRFPackets() {
        return timeoutRecvRFPackets;
    }

    /**
     * @return Baud rate of the UART interface if used.
     */
    public int getBaudRateOfUARF() {
        return baudRateOfUARF;
    }
    
    /**
     * @return RF channel A of the main network. Valid numbers depend on used RF band.
     */
    public int getRFChannelA() {
        return RFChannelA;
    }

    /**
     * @return RF channel B of the main network. Valid numbers depend on used RF band.
     */
    public int getRFChannelB() {
        return RFChannelB;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Standard peripherals: " + standardPeripherals.membersListToString() + NEW_LINE);
        strBuilder.append(" Configuration flags: " + configFlags + NEW_LINE);
        strBuilder.append(" RF channel A of the optional subordinate network: " + RFChannelASubNetwork + NEW_LINE);
        strBuilder.append(" RF channel B of the optional subordinate network: " + RFChannelBSubNetwork + NEW_LINE);
        strBuilder.append(" RF outuput power: " + RFOutputPower + NEW_LINE);
        strBuilder.append(" RF signal filter: " + RFSignalFilter + NEW_LINE);
        strBuilder.append(" Timeout receiving RF packets at LP or XLP modes: " + timeoutRecvRFPackets + NEW_LINE);
        strBuilder.append(" Baud rate of the UART: " + baudRateOfUARF + NEW_LINE);
        strBuilder.append(" RF channel A of the main network: " + RFChannelA + NEW_LINE);
        strBuilder.append(" RF channel B of the main network: " + RFChannelB + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormatedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("Standard peripherals: " + standardPeripherals.membersListToString() + NEW_LINE);
        strBuilder.append("Configuration flags: " + NEW_LINE);
        strBuilder.append(configFlags.toPrettyFormatedString());
        strBuilder.append("RF channel A of the optional subordinate network: " + RFChannelASubNetwork + NEW_LINE);
        strBuilder.append("RF channel B of the optional subordinate network: " + RFChannelBSubNetwork + NEW_LINE);
        strBuilder.append("RF outuput power: " + RFOutputPower + NEW_LINE);
        strBuilder.append("RF signal filter: " + RFSignalFilter + NEW_LINE);
        strBuilder.append("Timeout receiving RF packets at LP or XLP modes: " + timeoutRecvRFPackets + NEW_LINE);
        strBuilder.append("Baud rate of the UART: " + baudRateOfUARF + NEW_LINE);
        strBuilder.append("RF channel A of the main network: " + RFChannelA + NEW_LINE);
        strBuilder.append("RF channel B of the main network: " + RFChannelB + NEW_LINE);
        
        return strBuilder.toString();
    }
}

