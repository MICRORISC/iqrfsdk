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
package com.microrisc.simply.iqrf.dpa.v22x.types;

/**
 * HWP Configuration.
 * <p>
 * @author Michal Konopa
 * @author Martin Strouhal
 */
// October 2015 - added undocumented byte property and implemented equals method
public final class HWP_Configuration {

    /** Standard peripherals. */
    private IntegerFastQueryList standardPeripherals;

    /**
     * Various DPA configuration flag bits.
     */
    public static class DPA_ConfigFlags {

        private final boolean callHandlerOnEvent;
        private final boolean controlledByLocalSPI;
        private final boolean runAutoexecOnBootTime;
        private final boolean notRouteOnBackground;
        private final boolean runIOSetupOnBootTime;
        private final boolean receivesPeerToPeer;

        public DPA_ConfigFlags(
                boolean callHandlerOnEvent, boolean controlledByLocalSPI,
                boolean runAutoexecOnBootTime, boolean notRouteOnBackground,
                boolean runIOSetupOnBootTime, boolean receivePeerToPeer
        ) {
            this.callHandlerOnEvent = callHandlerOnEvent;
            this.controlledByLocalSPI = controlledByLocalSPI;
            this.runAutoexecOnBootTime = runAutoexecOnBootTime;
            this.notRouteOnBackground = notRouteOnBackground;
            this.runIOSetupOnBootTime = runIOSetupOnBootTime;
            this.receivesPeerToPeer = receivePeerToPeer;
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
         * @return indication if Node device does not route packet on the
         * background
         */
        public boolean notRouteOnBackground() {
            return notRouteOnBackground;
        }

        /**
         * @return indication if IOSetup is run on boot time
         */
        public boolean isIOSetupRunOnBootTime() {
            return runIOSetupOnBootTime;
        }

        /**
         * @return indication if Node receives also peer-to-peer
         * (non-networking) packets and raises PeerToPeer event
         */
        public boolean isReceivesPeerToPeer() {
            return receivesPeerToPeer;
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
            strBuilder.append(" Is IO setup run on boot time: " + runIOSetupOnBootTime + NEW_LINE);
            strBuilder.append(" Node receives also peer-to-peer (non-networking) packets and raises PeerToPeer event: " + receivesPeerToPeer + NEW_LINE);
            strBuilder.append("}");

            return strBuilder.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DPA_ConfigFlags) {
                DPA_ConfigFlags flags = (DPA_ConfigFlags) obj;
                return (callHandlerOnEvent == flags.isHandlerCalledOnEvent()
                        && controlledByLocalSPI == flags.canBeControlledByLocalSPI()
                        && notRouteOnBackground == flags.notRouteOnBackground()
                        && receivesPeerToPeer == flags.isReceivesPeerToPeer()
                        && runAutoexecOnBootTime == flags.isAutoexecRunOnBootTime()
                        && runIOSetupOnBootTime == flags.isIOSetupRunOnBootTime());
            }
            return false;
        }

        public String toPrettyFormatedString() {
            StringBuilder strBuilder = new StringBuilder();
            String NEW_LINE = System.getProperty("line.separator");

            strBuilder.append("Call Custom handler on event: " + callHandlerOnEvent + NEW_LINE);
            strBuilder.append("Can DPA be controlled by local SPI: " + controlledByLocalSPI + NEW_LINE);
            strBuilder.append("Is Autoexec run on boot time: " + runAutoexecOnBootTime + NEW_LINE);
            strBuilder.append("Node does not route on background: " + notRouteOnBackground + NEW_LINE);
            strBuilder.append(" Is IO setup run on boot time: " + runIOSetupOnBootTime + NEW_LINE);
            strBuilder.append(" Node receives also peer-to-peer (non-networking) packets and raises PeerToPeer event: " + receivesPeerToPeer + NEW_LINE);

            return strBuilder.toString();
        }

    }
    
    public static class RFPGM{
       
       /** Sets, if receiving on single channel or on dual channel. */
       private boolean singleChannel;
       /** Sets, if uploaded TRs uses STD RX mode or LP RX mode */
       private boolean lpMode;
       /** Sets, if RFPGM invoking by reset. {@code true} is enabled and
        * {@code false} is disabled. This bit operates like enableRFPGM or
        * disableRFPGM functions. */
       private boolean invokeRfpgmByReset;
       /** Sets, if RFPGM is automatically terminated after ~1 minute. */
       private boolean automaticTermination;
       /** Sets, if it is enabled RFPGM termination by MCU pin RB4. {@code true}
        * is enabled (default), {@code false} is disabled. If enabled, the
        * termination is invoked by log. 0 for at least ~0.25 s for single
        * channel or ~0.5 s for dual channel on one of the dedicated pin(s):
        * <ul><li> C5 for non-SMT TR modules, e.g. TR-72D </li>
        * <li>Q12 for SMT TR modules, e.g.TR-76D </li></ul>
        * This time must be prolonged up to 2 s in case of strong RF noise. */
       private boolean terminationByPin;

      public RFPGM(boolean singleChannel, boolean lpMode,
              boolean invokeRfpgmByReset, boolean automaticTermination,
              boolean terminationByPin) {
         this.singleChannel = singleChannel;
         this.lpMode = lpMode;
         this.invokeRfpgmByReset = invokeRfpgmByReset;
         this.automaticTermination = automaticTermination;
         this.terminationByPin = terminationByPin;
      }

      /** Getter for {@link RFPGM#singleChannel}.
       * @return true or false
       */
      public boolean isSingleChannel() {
         return singleChannel;
      }

      /** Setter for {@link RFPGM#singleChannel}.
       * @param singleChannel - true or false
       */
      public void setSingleChannel(boolean singleChannel) {
         this.singleChannel = singleChannel;
      }

      /** Getter for {@link RFPGM#lpMode}.
       * @return true or false
       */
      public boolean isLpMode() {
         return lpMode;
      }

      /** Setter for {@link RFPGM#lpMode}.
       * @param lpMode - true or false
       */
      public void setLpMode(boolean lpMode) {
         this.lpMode = lpMode;
      }

      /** Getter for {@link RFPGM#invokeRfpgmByReset}.
       * @return true or false
       */
      public boolean isInvokeRfpgmByReset() {
         return invokeRfpgmByReset;
      }

      /** Setter for {@link RFPGM#invokeRfpgmByReset}.
       * @param invokeRfpgmByReset - true or false
       */
      public void setInvokeRfpgmByReset(boolean invokeRfpgmByReset) {
         this.invokeRfpgmByReset = invokeRfpgmByReset;
      }

      /** Getter for {@link RFPGM#automaticTermination}.
       * @return true or false
       */
      public boolean isAutomaticTermination() {
         return automaticTermination;
      }

      /** Setter for {@link RFPGM#automaticTermination}.
       * @param automaticTermination - true or false
       */
      public void setAutomaticTermination(boolean automaticTermination) {
         this.automaticTermination = automaticTermination;
      }

      /** Getter for {@link RFPGM#terminationByPin}.
       * @return true or false
       */
      public boolean isTerminationByPin() {
         return terminationByPin;
      }

      /** Setter for {@link RFPGM#terminationByPin}.
       * @param terminationByPin - true or false
       */
      public void setTerminationByPin(boolean terminationByPin) {
         this.terminationByPin = terminationByPin;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final RFPGM other = (RFPGM) obj;
         return (this.singleChannel != other.isSingleChannel()
                 && this.lpMode != other.isLpMode()
                 && this.invokeRfpgmByReset != other.isInvokeRfpgmByReset()
                 && this.automaticTermination != other.isAutomaticTermination()
                 && this.terminationByPin != other.isTerminationByPin());
      }

      @Override
      public String toString() {
         return "RFPGM" + "\n singleChannel=" + singleChannel
                  + ",\n lpMode=" + lpMode
                  + ",\n invokeRfpgmByReset=" + invokeRfpgmByReset
                  + ",\n automaticTermination=" + automaticTermination
                  + ",\n terminationByPin=" + terminationByPin;
      }
    }

    /** Various DPA configuration flag bits. */
    private DPA_ConfigFlags configFlags;
    
    /** Settings of RFPGM and etc. */
    private RFPGM rfpgm;

    /**
     * RF channel A of the optional subordinate network in case the node also
     * plays a role of the coordinator of such network.
     */
    private int RFChannelASubNetwork;

    /**
     * RF channel B of the optional subordinate network in case the node also
     * plays a role of the coordinator of such network.
     */
    private int RFChannelBSubNetwork;

    /** RF output power. Valid numbers 0-7. */
    private int RFOutputPower;

    /** RF signal filter. Valid numbers 0-64. */
    private int RFSignalFilter;

    /**
     * Timeout when receiving RF packets at LP or XLP modes. Unit is cycles (one
     * cycle is 46 ms at LP, 770 ms at XLP mode). Greater values save energy but
     * might decrease responsiveness to the master interface DPA Requests and
     * also decrease Idle event calling frequency. Valid numbers 1-255. Also see
     * APIvariable uns8 LP_XLP_toutRF.
     */
    private int timeoutRecvRFPackets;

    /**
     * Baud rate of the UART interface if used. Uses the same coding as UART
     * Open (i.e. 0x00 = 1200 ).
     */
    private int baudRateOfUARF;

    /** RF channel A of the main network. Valid numbers depend on used RF band. */
    private int RFChannelA;

    /** RF channel B of the main network. Valid numbers depend on used RF band. */
    private int RFChannelB;

    /** Undocumented byte value, which can be read from module and written again. 
     * (It doesn't matter on module - value can be read from module 1 and and can 
     * be written into module 2) 
     */
    private short[] undocumented;

    /**
     * Creates new object of HWP configuration.
     * <p>
     * @param standardPeripherals standard peripherals.
     * @param configFlags Various DPA configuration flag bits.
     * @param RFChannelASubNetwork RF channel A of the optional subordinate
     * network in case the node also plays a role of the coordinator of such
     * network.
     * @param RFChannelBSubNetwork RF channel B of the optional subordinate
     * network in case the node also plays a role of the coordinator of such
     * network.
     * @param RFOutputPower RF output power. Valid numbers 0-7.
     * @param RFSignalFilter RF signal filter. Valid numbers 0-64.
     * @param timeoutRecvRFPackets Timeout when receiving RF packets at LP or
     * XLP modes. Unit is cycles (one cycle is 46 ms at LP, 770 ms at XLP mode).
     * Greater values save energy but might decrease responsiveness to the
     * master interface DPA Requests and also decrease Idle event calling
     * frequency. Valid numbers 1-255. Also see API variable uns8 LP_XLP_toutRF.
     * @param baudRateOfUARF Baud rate of the UART interface if used. Uses the
     * same coding as UART Open (i.e. 0x00 = 1200 )
     * @param RFChannelA RF channel A of the main network. Valid numbers depend
     * on used RF band.
     * @param RFChannelB RF channel B of the main network. Valid numbers depend
     * on used RF band.
     * @param rfpgm rfpgm setting
     * @param undocumented undocumented byte value (it has occurred while 
     * reading hwp configuration)
     */
    public HWP_Configuration(
            IntegerFastQueryList standardPeripherals, DPA_ConfigFlags configFlags,
            int RFChannelASubNetwork, int RFChannelBSubNetwork, int RFOutputPower,
            int RFSignalFilter, int timeoutRecvRFPackets, int baudRateOfUARF,
            int RFChannelA, int RFChannelB, RFPGM rfpgm, short[] undocumented
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
        this.rfpgm = rfpgm;
        this.undocumented = undocumented;
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
     * @return RF channel A of the optional subordinate network in case the node
     * also plays a role of the coordinator of such network.
     */
    public int getRFChannelASubNetwork() {
        return RFChannelASubNetwork;
    }

    /**
     * @return RF channel B of the optional subordinate network in case the node
     * also plays a role of the coordinator of such network.
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
     * @return RF channel A of the main network. Valid numbers depend on used RF
     * band.
     */
    public int getRFChannelA() {
        return RFChannelA;
    }

    /**
     * @return RF channel B of the main network. Valid numbers depend on used RF
     * band.
     */
    public int getRFChannelB() {
        return RFChannelB;
    }

   /**
     * @return rpfgm value, see {@link HWP_Configuration#rfpgm}
     */
   public RFPGM getRfpgm() {
      return rfpgm;
   }

    /**
     * @return undocumented byte value, which must be same for write HWP config
     * as was while reading HWP config
     */
    public short[] getUndocumented() {
        return undocumented;
    }

    public void setStandardPeripherals(IntegerFastQueryList standardPeripherals) {
        this.standardPeripherals = standardPeripherals;
    }

    public void setConfigFlags(DPA_ConfigFlags configFlags) {
        this.configFlags = configFlags;
    }

    public void setRFChannelASubNetwork(int RFChannelASubNetwork) {
        this.RFChannelASubNetwork = RFChannelASubNetwork;
    }

    public void setRFChannelBSubNetwork(int RFChannelBSubNetwork) {
        this.RFChannelBSubNetwork = RFChannelBSubNetwork;
    }

    public void setRFOutputPower(int RFOutputPower) {
        this.RFOutputPower = RFOutputPower;
    }

    public void setRFSignalFilter(int RFSignalFilter) {
        this.RFSignalFilter = RFSignalFilter;
    }

    public void setTimeoutRecvRFPackets(int timeoutRecvRFPackets) {
        this.timeoutRecvRFPackets = timeoutRecvRFPackets;
    }

    public void setBaudRateOfUARF(int baudRateOfUARF) {
        this.baudRateOfUARF = baudRateOfUARF;
    }

    public void setRFChannelA(int RFChannelA) {
        this.RFChannelA = RFChannelA;
    }

    public void setRFChannelB(int RFChannelB) {
        this.RFChannelB = RFChannelB;
    }

   public void setRfpgm(RFPGM rfpgm) {
      this.rfpgm = rfpgm;
   }

    public void setUndocumented(short[] undocumented) {
        this.undocumented = undocumented;
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
        strBuilder.append(" RFPGM: " + rfpgm + NEW_LINE);
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
        strBuilder.append("RFPGM: " + rfpgm + NEW_LINE);

        return strBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HWP_Configuration) {
            HWP_Configuration hwp = (HWP_Configuration) obj;
            return (RFChannelA == hwp.getRFChannelA()
                    && RFChannelASubNetwork == hwp.getRFChannelASubNetwork()
                    && RFChannelB == hwp.getRFChannelB()
                    && RFChannelBSubNetwork == hwp.getRFChannelBSubNetwork()
                    && RFOutputPower == hwp.getRFOutputPower()
                    && RFSignalFilter == hwp.getRFSignalFilter()
                    && baudRateOfUARF == hwp.getBaudRateOfUARF()
                    && configFlags.equals(hwp.getConfigFlags())
                    && standardPeripherals.getList().equals(hwp.getStandardPeripherals().getList())
                    && timeoutRecvRFPackets == hwp.getTimeoutRecvRFPackets()
                    && rfpgm.equals(hwp.getRfpgm()));
        }
        return false;
    }
}
