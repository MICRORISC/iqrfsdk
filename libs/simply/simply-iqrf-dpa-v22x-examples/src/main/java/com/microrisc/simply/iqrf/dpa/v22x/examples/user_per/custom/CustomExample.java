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
package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.custom;

import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Custom;
import java.io.File;
import java.util.Arrays;
import java.util.UUID;

/**
 * Example of Custom peripheral. See {@link Custom} for more information.
 * <p>
 * For using Custom peripheral is need to specify peripheral number (same as
 * in Custom) in Peripheral distribution file.
 * <p>
 * @author Martin Strouhal
 */
public class CustomExample {

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
            simply = DPA_SimplyFactory.getSimply(
                    "config" + File.separator + "Simply.properties"
            );
        } catch (SimplyException ex) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }

        // getting network 1
        Network network1 = simply.getNetwork("1", Network.class);
        if (network1 == null) {
            printMessageAndExit("Network 1 doesn't exist");
        }

        // getting node 1
        Node node = network1.getNode("1");
        if (node == null) {
            printMessageAndExit("Node 1 doesn't exist");
        }

        // getting Custom interface
        Custom custom = node.getDeviceObject(Custom.class);
        if (custom == null) {
            printMessageAndExit("Error when Custom was getting from node.");
        }

        // sending command and getting result
        short[] result = custom.send((short) 0x20, (short) 0x00, new short[]{});
        // printing result
        System.out.println(Arrays.toString(result));


        // sending command and getting result on another peripheral
        result = custom.send((short) 0x21, (short) 0x00, new short[]{});
        // printing result
        System.out.println(Arrays.toString(result));
        
        
        // sending async command
        UUID uuid = custom.async_send((short)0x20, (short) 0x00, new short[]{});
           
        // ... do some other work
        System.out.println("Sleeping ... waiting for result");
        Thread.sleep(3000);
        
        // getting result
        result = custom.getCallResultInDefaultWaitingTimeout(uuid, short[].class);
        // printing result
        System.out.println(Arrays.toString(result));
        
        // end working with Simply
        simply.destroy();
    }
}
