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

package com.microrisc.simply.iqrf.dpa.v210.network_building_algorithm.demo;

import com.microrisc.simply.Network;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.v210.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v210.network_building_algorithm.NetworkBuildingAlgorithm;
import com.microrisc.simply.iqrf.dpa.v210.network_building_algorithm.NetworkBuildingAlgorithmImpl;
import com.microrisc.simply.iqrf.dpa.v210.network_building_algorithm.NetworkBuildingAlgorithmImpl.State;
import com.microrisc.simply.iqrf.dpa.v210.network_building_algorithm.P2PPrebonderStandardTransformer;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Demo for testing the algorithm for automatic network creation.
 * 
 * @author Michal Konopa
 */
public final class AutoNetworkDemo {
    // default ID of the network to run the algorithm on
    public static final String NETWORK_ID_DEFAULT = "1";
    
    // denotes, that maximal running time of the algorithm is potentially unlimited
    public static final int MAX_RUNNING_TIME_UNLIMITED = -1;
    
    // maximal running time of the algorithm
    private static long maxRunningTime = MAX_RUNNING_TIME_UNLIMITED;
    
    
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
    
    // program command line options
    private static final  Options options = new Options();
    
    // parser of command line arguments
    private static final CommandLineParser cmdLineParser = new BasicParser();
    
    // inits command line options
    private static void initCmdLineOptions() {
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription("ID of the network to run the algorithm on")
                    .create("networkId")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription("Method ID transformer for P2P Prebonder")
                    .create("methodIdTransformer")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription("Discovery TX power")
                    .create("discoveryTxPower")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription("Prebonding interval")
                    .create("prebondingInterval")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription("Authorize retries")
                    .create("authorizeRetries")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription("Discovery retries")
                    .create("discoveryRetries")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription("Temporary address timeout")
                    .create("temporaryAddressTimeout")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription("Use FRC automatically in checking the accessibility of newly bonded nodes")
                    .create("autoUseFrc")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Maximal running time of the algorithm [in ms]."
                            + "Set the " + MAX_RUNNING_TIME_UNLIMITED + " value "
                            + "for not to limit the maximal running time."
                    )
                    .create("maxRunningTime")
        );
        
    }
    
    // sets the maximal running time according to the command line
    private static void setMaxRunningTime(CommandLine cmdLine) {
        if ( cmdLine.hasOption("maxRunningTime") ) {
            maxRunningTime = Long.parseLong(cmdLine.getOptionValue("maxRunningTime"));
        }
        
        if ( maxRunningTime != MAX_RUNNING_TIME_UNLIMITED && maxRunningTime < 0 ) {
            throw new IllegalArgumentException(
                    "Maximal running time cannot be negative "
                    + "and different from " + MAX_RUNNING_TIME_UNLIMITED
            );
        }
    }
    
    // creates and returns Method ID Transdformer
    private static MethodIdTransformer getMethodIdTransformer(CommandLine cmdLine) {
        MethodIdTransformer methodIdTransformer = null;
        
        String transformerClassName = cmdLine.getOptionValue("methodIdTransformer");
        try {
            Class transformerClass = Class.forName(transformerClassName);
            Class[] implIfaces = transformerClass.getInterfaces();

            boolean isMethodIdTransformer = false;
            for ( Class implIface : implIfaces ) {
                if ( implIface == MethodIdTransformer.class ) {
                    isMethodIdTransformer = true;
                    break;
                }
            }

            if ( !isMethodIdTransformer ) {
                printMessageAndExit(
                    "Specified Method ID Transformer doesn't implement the "
                    +  MethodIdTransformer.class.getCanonicalName() + " interface"
                );
            }

            // find no-parametric constructor, if exists
            Constructor transformerConstr = null;
            try {
                transformerConstr = transformerClass.getConstructor();
            } catch ( SecurityException ex ) {
                printMessageAndExit(
                    "Error while getting acces to no-arg constructor of Method ID Transformer: " + ex
                );
            }

            if ( transformerConstr != null ) {
                methodIdTransformer = (MethodIdTransformer)(transformerConstr.newInstance());
            } else {
                Method[] methods = transformerClass.getMethods();
                for ( Method method : methods ) {
                    if ( method.getAnnotation(MethodIdTransformerCreator.class) != null ) {
                        methodIdTransformer = (MethodIdTransformer)(method.invoke(null));
                        break;
                    }
                }
                if ( methodIdTransformer == null ) {
                    printMessageAndExit("Method for creation of Method ID Transformer not found.");
                }
            }
        } catch ( Exception ex ) {
            printMessageAndExit("Error while getting Method ID Transformer: " + ex);
        }
        
        return methodIdTransformer;
    }
    
    // creates instance of algorithm according to specified command line arguments
    private static NetworkBuildingAlgorithm createNetworkBuildingAlgorithm(
            DPA_Simply simply, CommandLine cmdLine
    ) {
        String networkId = NETWORK_ID_DEFAULT;
        if ( cmdLine.hasOption("networkId") ) {
            networkId = cmdLine.getOptionValue("discoveryTxPower");
        }
        
        // getting network
        Network network = simply.getNetwork(networkId, Network.class);
        if ( network == null ) {
            printMessageAndExit("Network " + networkId + " doesn't exist");
        }
        
        int discoveryTxPower = NetworkBuildingAlgorithmImpl.DISCOVERY_TX_POWER_DEFAULT;
        if ( cmdLine.hasOption("discoveryTxPower") ) {
            discoveryTxPower = Integer.parseInt(cmdLine.getOptionValue("discoveryTxPower"));
        }
        
        long prebondingInterval = NetworkBuildingAlgorithmImpl.PREBONDING_INTERVAL_DEFAULT;
        if ( cmdLine.hasOption("prebondingInterval") ) {
            prebondingInterval = Long.parseLong(cmdLine.getOptionValue("prebondingInterval"));
        }
        
        int authorizeRetries = NetworkBuildingAlgorithmImpl.AUTHORIZE_RETRIES_DEFAULT;
        if ( cmdLine.hasOption("authorizeRetries") ) {
            authorizeRetries = Integer.parseInt(cmdLine.getOptionValue("authorizeRetries"));
        }
        
        int discoveryRetries = NetworkBuildingAlgorithmImpl.DISCOVERY_RETRIES_DEFAULT;
        if ( cmdLine.hasOption("discoveryRetries") ) {
            discoveryRetries = Integer.parseInt(cmdLine.getOptionValue("discoveryRetries"));
        }
        
        long temporaryAddressTimeout = NetworkBuildingAlgorithmImpl.TEMPORARY_ADDRESS_TIMEOUT_DEFAULT;
        if ( cmdLine.hasOption("temporaryAddressTimeout") ) {
            temporaryAddressTimeout = Long.parseLong(cmdLine.getOptionValue("temporaryAddressTimeout"));
        }
        
        boolean autoUseFrc = NetworkBuildingAlgorithmImpl.AUTOUSE_FRC_DEFAULT ;
        if ( cmdLine.hasOption("autoUseFrc") ) {
            autoUseFrc = Boolean.parseBoolean(cmdLine.getOptionValue("autoUseFrc"));
        }
        
        MethodIdTransformer methodIdTransformer = P2PPrebonderStandardTransformer.getInstance();
        if ( cmdLine.hasOption("methodIdTransformer") ) {
            methodIdTransformer = getMethodIdTransformer(cmdLine);
        }
        
        // get reference to algorithm object with reference to a network which
        // the algorithm will be running on
        // it is possible to set algorithm parameters or to leave theirs default values 
        return new NetworkBuildingAlgorithmImpl.Builder(network, simply.getBroadcastServices())
                .discoveryTxPower(discoveryTxPower)
                .prebondingInterval(prebondingInterval)
                .authorizeRetries(authorizeRetries)
                .discoveryRetries(discoveryRetries)
                .temporaryAddressTimeout(temporaryAddressTimeout)
                .autoUseFrc(autoUseFrc)
                .p2pPrebonderMethodIdTransformer(methodIdTransformer)
        .build();
    }
    
    
    public static void main(String[] args) {
        initCmdLineOptions();
        
        CommandLine cmdLine = null;
        try {
            // cmd arguments processing
            cmdLine = cmdLineParser.parse( options, args);
        } catch ( ParseException ex ) {
            printMessageAndExit("Error while parsing command line arguments: " + ex);
        }
        
        // set the maximal running time
        setMaxRunningTime(cmdLine);
        
        // creating the Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex);
        }
        
        // create object of algorithm configured via command line arguments
	NetworkBuildingAlgorithm algo = createNetworkBuildingAlgorithm(simply, cmdLine);

        // start the algorithm
        algo.start();
        
        if ( maxRunningTime == MAX_RUNNING_TIME_UNLIMITED ) {
            while ( !algo.isFinished() ) {
                try {
                    Thread.sleep(1000);
                } catch ( InterruptedException ex ) {
                    printMessageAndExit("Algorithm interrupted.");
                }
            }
            State algState = ((NetworkBuildingAlgorithmImpl)(algo)).getState();
            switch ( algState ) {
                case FINISHED_OK:
                    System.out.println("Algorithm succesfully finished.");
                    break;
                case ERROR:
                    System.out.println("Error occured during algorithm run.");
                default:
                    System.out.println("Algorithm succesfully finished with state: " + algState);
            }
        } else {
            try {
                Thread.sleep(maxRunningTime);
            } catch ( InterruptedException ex ) {
                printMessageAndExit("Algorithm interrupted.");
            }
            
            // is algorithm finished?
            if ( algo.isFinished() ) {
                System.out.println("Algorithm succesfully finished.");
            } else {
                // cancell the algorithm
                // after cancellation is not possible to run the algorithm again
                algo.cancel();
                System.out.println("Algorithm cancelled.");
            }
        }
        
        // view the result of the algorithm run
        Network resultNetwork = ((NetworkBuildingAlgorithmImpl)algo).getResultNetwork();
        System.out.println("Number of nodes in the network: " + resultNetwork.getNodesMap().size());
        
        // end working with Simply
        simply.destroy();
    }
}
