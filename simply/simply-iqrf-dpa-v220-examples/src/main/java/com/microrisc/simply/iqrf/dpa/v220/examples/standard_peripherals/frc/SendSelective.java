/*
 * Copyright 2015 Martin Strouhal.
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
package com.microrisc.simply.iqrf.dpa.v220.examples.standard_peripherals.frc;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.v220.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v220.devices.FRC;
import com.microrisc.simply.iqrf.dpa.v220.devices.LEDG;
import com.microrisc.simply.iqrf.dpa.v220.types.FRC_Data;
import com.microrisc.simply.iqrf.dpa.v220.types.FRC_LedgOn;
import com.microrisc.simply.iqrf.dpa.v220.types.FRC_Temperature;
import com.microrisc.simply.iqrf.dpa.v220.types.LED_State;
import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Martin Strouhal
 */
public class SendSelective {
// reference to Simply

    private static Simply simply = null;

    // prints out specified error description, destroys the Simply and exits
    private static void printMessageAndExit(String errorDescr) {
        System.out.println(errorDescr);
        if (simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }

    // processes NULL result
    private static void processNullResult(FRC frc, String errorMsg, String notProcMsg) {
        CallRequestProcessingState procState = frc.getCallRequestProcessingStateOfLastCall();
        if (procState == CallRequestProcessingState.ERROR) {
            CallRequestProcessingError error = frc.getCallRequestProcessingErrorOfLastCall();
            printMessageAndExit(errorMsg + ": " + error);
        } else {
            printMessageAndExit(notProcMsg + ": " + procState);
        }
    }

    // puts together both parts of incomming FRC data
    private static short[] getCompleteFrcData(short[] firstPart, short[] extraData) {
        short[] completeData = new short[firstPart.length + extraData.length];
        System.arraycopy(firstPart, 0, completeData, 0, firstPart.length);
        System.arraycopy(extraData, 0, completeData, firstPart.length, extraData.length);
        return completeData;
    }

    private static class NodeIdComparator implements Comparator<String> {

        @Override
        public int compare(String nodeIdStr1, String nodeIdStr2) {
            int nodeId_1 = Integer.decode(nodeIdStr1);
            int nodeId_2 = Integer.decode(nodeIdStr2);
            return Integer.compare(nodeId_1, nodeId_2);
        }
    }

    // Node Id comparator
    private static final NodeIdComparator nodeIdComparator = new NodeIdComparator();

    // sorting specified results according to node ID in ascendent manner
    private static SortedMap<String, FRC_Temperature.Result> sortResult(
            Map<String, FRC_Temperature.Result> result
    ) {
        TreeMap<String, FRC_Temperature.Result> sortedResult = new TreeMap<>(nodeIdComparator);
        sortedResult.putAll(result);
        return sortedResult;
    }

    public static void main(String[] args) {
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch (SimplyException ex) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }

        // getting network 1
        Network network1 = simply.getNetwork("1", Network.class);
        if (network1 == null) {
            printMessageAndExit("Network 1 doesn't exist");
        }

        // getting a master node
        Node master = network1.getNode("0");
        if (master == null) {
            printMessageAndExit("Master doesn't exist");
        }

        // getting FRC interface
        FRC frc = master.getDeviceObject(FRC.class);
        if (frc == null) {
            printMessageAndExit("FRC doesn't exist or is not enabled");
        }

        //choose nodes on which will be FRC executed
        Node[] selectedNodes = new Node[]{network1.getNode("1"), network1.getNode("2")};

        //send selective FRC
        FRC_Data frcData = frc.sendSelective(new FRC_Temperature(selectedNodes));

        if (frcData == null) {
            processNullResult(frc, "Sending FRC command failed",
                    "Sending FRC command hasn't been processed yet"
            );
        }

        short[] frcExtraData = frc.extraResult();
        if (frcExtraData == null) {
            processNullResult(frc, "Setting FRC extra result failed",
                    "Setting FRC extra result hasn't been processed yet"
            );
        }

        //parse FRC result
        Map<String, FRC_Temperature.Result> result = null;
        try {
            result = FRC_Temperature.parse(getCompleteFrcData(frcData.getData(), frcExtraData));
        } catch (Exception ex) {
            printMessageAndExit("Parsing result data failed: " + ex);
        }

        // sort the results
        SortedMap<String, FRC_Temperature.Result> sortedResult = sortResult(result);
      
        // printing temperature on each node
        for (Map.Entry<String, FRC_Temperature.Result> dataEntry : sortedResult.entrySet()) {
            System.out.println("Node: " + dataEntry.getKey()
                    + ", temperature: " + dataEntry.getValue().getTemperature());
        }

        // end working with Simply
        simply.destroy();
    }
}
