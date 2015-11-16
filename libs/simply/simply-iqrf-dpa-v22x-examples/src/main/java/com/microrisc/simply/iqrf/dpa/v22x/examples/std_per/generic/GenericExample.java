/* 
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.examples.std_per.generic;

import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Generic;
import java.io.File;
import java.util.Arrays;
import java.util.UUID;

/**
 * Example of Generic DI. See {@link Generic} for more information.
 * <p>
 * For using Generic DI is need to specify standard peripheral number.
 * <p>
 * @author Rostislav Spinar
 */
public class GenericExample {

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

        // getting coordinator
        Node coordinator = network1.getNode("0");
        if (coordinator == null) {
            printMessageAndExit("Coordinator doesn't exist");
        }

        // getting Generic interface
        Generic generic = coordinator.getDeviceObject(Generic.class);
        if (generic == null) {
            printMessageAndExit("Error when Generic was getting from node.");
        }

        // sending LEDG pulse command and getting result
        Short[] result = generic.send((short) 0x06, (short) 0x03, new short[]{});
        // printing result
        System.out.println(Arrays.toString(result));


        // sending LEDR pulse command and getting result
        result = generic.send((short) 0x07, (short) 0x03, new short[]{});
        // printing result
        System.out.println(Arrays.toString(result));
        
        
        // sending async command
        UUID uuid = generic.async_send((short)0x06, (short) 0x03, new short[]{});
           
        // ... do some other work
        System.out.println("Sleeping ... waiting for result");
        Thread.sleep(3000);
        
        // getting result
        result = generic.getCallResultInDefaultWaitingTimeout(uuid, Short[].class);
        // printing result
        System.out.println(Arrays.toString(result));
        
        // end working with Simply
        simply.destroy();
    }
}
