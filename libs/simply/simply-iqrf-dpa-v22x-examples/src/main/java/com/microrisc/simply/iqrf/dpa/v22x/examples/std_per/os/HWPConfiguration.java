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
package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.os;

import com.microrisc.simply.CallRequestProcessingState;
import static com.microrisc.simply.CallRequestProcessingState.ERROR;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.types.HWP_Configuration;
import com.microrisc.simply.iqrf.dpa.v22x.types.HWP_ConfigurationByte;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;

/**
 * Example of using OS Peripheral - synchronous version.
 * <p>
 * @author Michal Konopa
 * @author Rostislav Spinar
 * @author Martin Strouhal
 */
// October 2015 - extended example with writing HWP config
public class HWPConfiguration {

    // reference to Simply

    private static Simply simply = null;

    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if (simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }

    public static void main(String[] args) throws InterruptedException {
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch (SimplyException ex) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }

        // getting network 1
        Network network1 = simply.getNetwork("1", Network.class);
        if (network1 == null) {
            printMessageAndExit("Network 1 doesn't not exist");
        }

        // getting node 1
        Node node1 = network1.getNode("1");
        if (node1 == null) {
            printMessageAndExit("Node 1 doesn't exist");
        }

        // get access to OS peripheral
        OS os = node1.getDeviceObject(OS.class);
        if (os == null) {
            printMessageAndExit("OS doesn't exist or is not enabled");
        }

        // rewrite config flags of HWP config and allow using of Custom handler
        rewriteConfigFlagByte(os);

        // read and print HWP config setting
        HWP_Configuration config = readAndPrintHWPConfig(os);

        // edit config - disallow using of Custom handler        
        config.setConfigFlags(new HWP_Configuration.DPA_ConfigFlags(false, false, false, false, false, false));

        //write edited config
        writeHWPConfig(os, config);

        // restart of device is required for use of new HWP Configuration
        restartDevice(os);

        // read HWP config again
        readAndPrintHWPConfig(os);

        // end working with Simply
        simply.destroy();
    }

    private static HWP_Configuration readAndPrintHWPConfig(OS os) {
        // read HWP config
        HWP_Configuration hwpConfig = os.readHWPConfiguration();
        if (hwpConfig == null) {
            CallRequestProcessingState procState = os.getCallRequestProcessingStateOfLastCall();
            if (procState == ERROR) {
                CallRequestProcessingError error = os.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("HWP config reading failed: " + error);
            } else {
                printMessageAndExit("HWP config reading hasn't been processed yet: " + procState);
            }
        }

        // print HWP config
        System.out.println("HWP configuration: \n" + hwpConfig.toPrettyFormatedString());
        return hwpConfig;
    }

    private static void writeHWPConfig(OS os, HWP_Configuration config) {
        VoidType result = os.writeHWPConfiguration(config);
        if (result == null) {
            CallRequestProcessingState procState = os.getCallRequestProcessingStateOfLastCall();
            if (procState == ERROR) {
                CallRequestProcessingError error = os.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("HWP config writing failed: " + error);
            } else {
                printMessageAndExit("HWP config writing hasn't been processed yet: " + procState);
            }
        }
    }

    private static void restartDevice(OS os) throws InterruptedException{
        VoidType restartResult = os.restart();
        if (restartResult == null) {
            CallRequestProcessingState procState = os.getCallRequestProcessingStateOfLastCall();
            if (procState == ERROR) {
                CallRequestProcessingError error = os.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("Device restart wasn't succcesful " + error);
            } else {
                printMessageAndExit("Device restart hasn't been processed yet: " + procState);
            }
        }

        // waiting until module is restarted
        System.out.println("Module will be restarted...");
        Thread.sleep(1000);
        System.out.println("Module has been restarted.");
    }

    private static void rewriteConfigFlagByte(OS os) {
        // rewrites in HWP configuration value of Conflig flags, where allow using Custom handler and disallow all other
        HWP_ConfigurationByte[] configBytes = new HWP_ConfigurationByte[]{
           new HWP_ConfigurationByte(0x05, 0b00000001, 0b00000001)
        };
        VoidType result = os.writeHWPConfigurationByte(configBytes);
        if (result == null) {
            CallRequestProcessingState procState = os.getCallRequestProcessingStateOfLastCall();
            if (procState == ERROR) {
                CallRequestProcessingError error = os.getCallRequestProcessingErrorOfLastCall();
                printMessageAndExit("HWP config writing of one byte failed: " + error);
            } else {
                printMessageAndExit("HWP config writing of one byte hasn't been processed yet: " + procState);
            }
        }
    }
}
