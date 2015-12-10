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
package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.autonetwork_embedded;

import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.asynchrony.AsynchronousMessagesListener;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.EEPROM;
import com.microrisc.simply.iqrf.dpa.v22x.devices.RAM;
import java.io.File;

/**
 * Example which automatically build network from all nodes. For this example is
 * need to have CustomDpaHandler-Coordinator-AutoNetwork-Embedded in Coordinator
 * and CustomDpaHandler-AutoNetwork in Nodes.
 * <p>
 * @author Martin Strouhal
 */
public class BuildNetwork implements AsynchronousMessagesListener<DPA_AsynchronousMessage> {

    // reference to Simply
    private static DPA_Simply simply = null;
    // reference to async manager
    AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> asyncManager;

    // reference to Coordinator node
    private static Node coordinator = null;

    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if (simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }

    public static void main(String[] args) {
        // getting simply
        try {
            simply = DPA_SimplyFactory.getSimply(
                    "config" + File.separator + "Simply.properties");
        } catch (SimplyException ex) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }

        // getting network "1"
        Network network1 = simply.getNetwork("1", Network.class);
        if (network1 == null) {
            printMessageAndExit("Network 1 doesn't exist");
        }

        // getting node 1
        coordinator = network1.getNode("0");
        if (coordinator == null) {
            printMessageAndExit("Coordinator doesn't exist");
        }

        BuildNetwork networkBuilder = new BuildNetwork();
        
        networkBuilder.initAndStartAutonetwork(0x07, 0x08, 0x03, false, true);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        
        // destroy
        networkBuilder.asyncManager.unregisterAsyncMsgListener(networkBuilder);
        simply.destroy();
    }

    /**
     *
     * @param discoveryTXPower discovery TX power in interval  <0,7>
     * @param bondingTime nominal bonding time in 2.56 s units. Must be longer
     * then ( NumberOfNodes + 4 ) * 60 ms.
     * @param temporaryAddessTimeout is node temporary address timeout in 25.6 s
     * units. Must be long enough the temporary address does not timeout till
     * all previous nodes are authorized.
     * @param forwardBondedMid If is set then when MID and bonding user data are
     * read from [C] or [N] the response from the corresponding
     * CMD_???_READ_REMOTELY_BONDED_MID is forwarded to the interface and
     * interface master can (dis)approve new node with temporary address for the
     * future authorization by responding within APPROVE_TIMEOUT timeout
     * Interface master approves node by responding NADR=0, PNUM=0x20, PCMD=0x01
     * or by not responding within the timeout Interface master disapproves node
     * by responding NADR=0, PNUM=0x20, PCMD=0x00 <br>
     * !!! This feature is not implemented at DCTR5x because of free Flash
     * memory limitation !!!
     * @param unbondAndRestart If is set then all nodes with temporary address
     * 0xFE are unbonded and restarted before discovery
     */
    private void initAndStartAutonetwork(int discoveryTXPower, 
            int bondingTime, int temporaryAddessTimeout, 
            boolean forwardBondedMid, boolean unbondAndRestart) 
    {
        // checking params
        if (discoveryTXPower < 0 || discoveryTXPower > 7) {
            throw new IllegalArgumentException("TX power must be in interval <0,7>.");
        }
        if (temporaryAddessTimeout < 0 || bondingTime < 0) {
            throw new IllegalArgumentException("Node temporary address timeout and bonding time cannot be negative!");
        }
        
        // prepare config byte form booleans
        int configByte = (unbondAndRestart == true) ? 1 : 0;
        configByte <<= 1;
        configByte += (forwardBondedMid == true) ? 1 : 0;

        
        // getting RAM DI
        EEPROM eeprom = coordinator.getDeviceObject(EEPROM.class);
        if (eeprom == null) {
            printMessageAndExit("RAM doesn't exist on Coordinator!");
        }

        // writing configuration
        eeprom.write(0x0, new short[]{(short)discoveryTXPower, (short)bondingTime, 
            (short)temporaryAddessTimeout, (short) configByte});
         
        
        // getting access to asynchronous messaging manager
        asyncManager = simply.getAsynchronousMessagingManager();
        
        // register the listener of asynchronous messages
        asyncManager.registerAsyncMsgListener(this);
        
        
        // getting RAM DI
        RAM ram = coordinator.getDeviceObject(RAM.class);
        if (ram == null) {
            printMessageAndExit("RAM doesn't exist on Coordinator!");
        }
        
        // start autonetwork on Coordinator
        ram.write(0x0, new short[]{0x0A});
    }        

    @Override
    public void onAsynchronousMessage(DPA_AsynchronousMessage message) {
        // printing received async msg
        System.out.println("New autonetwork message: ");
        
        System.out.println("Message source: "
            + "network ID= " + message.getMessageSource().getNetworkId()
            + ", node ID= " + message.getMessageSource().getNodeId()
            + ", peripheral number= " + message.getMessageSource().getPeripheralNumber()
        );
        
        System.out.println("Main data: " + message.getMainData());
        System.out.println();
        
        // getting specific type once we know what message comes
        //OsInfo osi = (OsInfo)message.getMainData();
        //System.out.println("Pretty format: " + osi.toPrettyFormatedString());
        //System.out.println();
    }

    
}
