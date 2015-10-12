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

package com.microrisc.simply.iqrf.dpa.v22x.examples.asynchrony;

import com.microrisc.simply.SimplyException;
import com.microrisc.simply.asynchrony.AsynchronousMessagesListener;
import com.microrisc.simply.asynchrony.AsynchronousMessagingManager;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessage;
import com.microrisc.simply.iqrf.dpa.asynchrony.DPA_AsynchronousMessageProperties;
import java.io.File;

/**
 * Example of using asynchronous messaging.
 * Example has been tested with CustomDpaHandler-Coordinator-PullNodes.hex.
 * 
 * @author Michal Konopa, Rostislav Spinar
 */
public class AsynchronousMessages 
implements AsynchronousMessagesListener<DPA_AsynchronousMessage> 
{
    // reference to Simply
    private static DPA_Simply simply = null;
    
    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if ( simply != null ) {
            simply.destroy();
        }
        System.exit(1);
    } 
    
    public static void main(String[] args) throws InterruptedException {
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }
        
        AsynchronousMessages msgListener = new AsynchronousMessages();
        
        // getting access to asynchronous messaging manager
        AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> asyncManager 
                = simply.getAsynchronousMessagingManager();
        
        // register the listener of asynchronous messages
        asyncManager.registerAsyncMsgListener(msgListener);
        
        // sleep for some time to show the processing of incomming asynchronous messages 
        Thread.sleep(30000);
        
        // after end of work with asynchronous messages, unrergister the listener
        asyncManager.unregisterAsyncMsgListener(msgListener);
        
        // end working with Simply
        simply.destroy();
    }

    @Override
    public void onAsynchronousMessage(DPA_AsynchronousMessage message) {
        System.out.println("New asynchronous message: ");
        
        System.out.println("Message source: "
            + "network ID= " + message.getMessageSource().getNetworkId()
            + ", node ID= " + message.getMessageSource().getNodeId()
            + ", peripheral number= " + message.getMessageSource().getPeripheralNumber()
        );
        
        System.out.println("Main data: " + message.getMainData());
        System.out.println("Additional data: " + message.getAdditionalData());
        System.out.println();
        
        // getting specific type once we know what message comes
        //OsInfo osi = (OsInfo)message.getMainData();
        //System.out.println("Pretty format: " + osi.toPrettyFormatedString());
        //System.out.println();
    }
}
