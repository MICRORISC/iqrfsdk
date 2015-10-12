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

package com.microrisc.simply.iqrf.dpa.v22x.examples.autonetwork;

import com.microrisc.simply.Network;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.AutoNetworkAlgorithm;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.AutoNetworkAlgorithmImpl;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.P2PPrebonderStandardTransformer;
import java.io.File;

/**
 * Simple example of usage of algorithm for network creation.
 * 
 * @author Michal Konopa
 */
public final class AutoNetwork {
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
        // creating the Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex);
        }
        
        // getting network 1
        Network network1 = simply.getNetwork("1", Network.class);
        if ( network1 == null ) {
            printMessageAndExit("Network 1 doesn't exist");
        }
        
		
        // get reference to algorithm object with reference to a network which
        // the algorithm will be running on
        // it is possible to set algorithm parameters or to leave theirs default values 
        AutoNetworkAlgorithm algo 
                = new AutoNetworkAlgorithmImpl.Builder(network1, simply.getBroadcastServices())
                    .discoveryTxPower(7)
                    .prebondingInterval(10)
                    .authorizeRetries(1)
                    .discoveryRetries(1)
                    .temporaryAddressTimeout(10)
                    .autoUseFrc(true)
                    .p2pPrebonderMethodIdTransformer(P2PPrebonderStandardTransformer.getInstance())
                    .numberOfNodesToBond(5)
                .build();

        // start the algorithm
        algo.start();

        // do some other work for some time ...
        Thread.sleep(80000);

        // is algorithm finished?
        if ( algo.isFinished() ) {
            System.out.println("Algorithm succesfully finished.");
        } else {
            // cancell the algorithm
            // after cancellation is not possible to run the algorithm again
            algo.cancel();
            System.out.println("Algorithm cancelled.");
        }
        
        // view the result of the algorithm run
        Network resultNetwork = ((AutoNetworkAlgorithmImpl)algo).getResultNetwork();
        System.out.println("Number of nodes in the network: " + resultNetwork.getNodesMap().size());
        
        // end working with Simply
        simply.destroy();
    }
}
