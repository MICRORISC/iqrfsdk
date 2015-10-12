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

package com.microrisc.simply.iqrf.dpa.v22x.autonetwork.demo;

import com.microrisc.simply.CallRequestProcessingState;
import com.microrisc.simply.Network;
import com.microrisc.simply.Node;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.errors.CallRequestProcessingError;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.broadcasting.BroadcastResult;
import com.microrisc.simply.iqrf.dpa.broadcasting.services.BroadcastServices;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.AutoNetworkAlgorithm;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.AutoNetworkAlgorithmImpl;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.AutoNetworkAlgorithmImpl.State;
import com.microrisc.simply.iqrf.dpa.v22x.autonetwork.P2PPrebonderStandardTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.devices.Coordinator;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Request;
import com.microrisc.simply.iqrf.types.VoidType;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
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
    
    // maximal running time of the algorithm [ in seconds ]
    private static long maxRunningTime = MAX_RUNNING_TIME_UNLIMITED;
    
    // removing bonded nodes for another test
    private static boolean removeBondsAtEnd = false;
    
    // reference to Simply
    private static DPA_Simply simply = null;
    
    // reference to Algorithm
    private static AutoNetworkAlgorithm algo = null;
    
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
                    .withDescription("Prints this message")
                    .create("help")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "ID of the network to run the algorithm on.\n"
                            + "Default value: " + NETWORK_ID_DEFAULT
                    )
                    .create("networkId")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Method ID transformer for P2P Prebonder.\n"
                            + "Default value: " + P2PPrebonderStandardTransformer.class.getCanonicalName()
                    )
                    .create("methodIdTransformer")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Discovery TX power\n"
                            + "Allowed range of vales: ["
                                    + AutoNetworkAlgorithmImpl.DISCOVERY_TX_POWER_MIN
                                    + ".."
                                    + AutoNetworkAlgorithmImpl.DISCOVERY_TX_POWER_MAX
                                    + "]\n"
                            + "Default value: " + AutoNetworkAlgorithmImpl.DISCOVERY_TX_POWER_DEFAULT
                    )
                    .create("discoveryTxPower")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Prebonding interval [in seconds]\n"
                            + "Allowed range of vales: ["
                                    + AutoNetworkAlgorithmImpl.PREBONDING_INTERVAL_MIN
                                    + ".."
                                    + AutoNetworkAlgorithmImpl.PREBONDING_INTERVAL_MAX
                                    + "]\n"
                            + "Default value: " + AutoNetworkAlgorithmImpl.PREBONDING_INTERVAL_DEFAULT
                    )
                    .create("prebondingInterval")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Authorize retries\n"
                            + "Default value: " + AutoNetworkAlgorithmImpl.AUTHORIZE_RETRIES_DEFAULT
                    )
                    .create("authorizeRetries")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Discovery retries\n"
                            + "Default value: " + AutoNetworkAlgorithmImpl.DISCOVERY_RETRIES_DEFAULT
                    )
                    .create("discoveryRetries")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Temporary address timeout [in tens of seconds]\n"
                            + "Allowed range of vales: ["
                                    + AutoNetworkAlgorithmImpl.TEMPORARY_ADDRESS_TIMEOUT_MIN
                                    + ".."
                                    + AutoNetworkAlgorithmImpl.TEMPORARY_ADDRESS_TIMEOUT_MAX
                                    + "]\n"
                            + "Default value: " + AutoNetworkAlgorithmImpl.TEMPORARY_ADDRESS_TIMEOUT_DEFAULT
                    )
                    .create("temporaryAddressTimeout")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Use FRC automatically in checking the accessibility of newly bonded nodes\n"
                            + "Default value: " + AutoNetworkAlgorithmImpl.AUTOUSE_FRC_DEFAULT
                    )
                    .create("autoUseFrc")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Number of nodes to bond\n"
                            + "Set the " + AutoNetworkAlgorithmImpl.NODES_NUMBER_TO_BOND_MAX + " value "
                            + " for maximal number of nodes to bond according to "
                            + " the IQRF DPA networks limitations.\n"
                            + "Default value: " + AutoNetworkAlgorithmImpl.NODES_NUMBER_TO_BOND_MAX
                    )
                    .create("nodesNumberToBond")
        );
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Maximal running time of the algorithm [in seconds]."
                            + "Set the " + MAX_RUNNING_TIME_UNLIMITED + " value "
                            + "for not to limit the maximal running time.\n"
                            + "Default value: " + MAX_RUNNING_TIME_UNLIMITED 
                    )
                    .create("maxRunningTime")
        );    
        
        options.addOption(
                OptionBuilder
                    .isRequired(false)
                    .hasArg()
                    .withDescription(
                            "Remove bonds from all nodes to enable another test.\n"
                            + "Default value: " + removeBondsAtEnd
                    )
                    .create("removeBondsAtEnd")
        );  
    }
    
    // prints help message
    private static void printHelpMessage() {
        System.out.println();
        System.out.println(
            "Demo of algorithm for automatic network creation.\n"
            + "Runs algorithm for automatic network creation. Parameters of \n"
            + "the algorithm can be specified by user. If a parameter is not \n"
            + "specified, the default one is used."    
        );
        System.out.println();
        
        PrintWriter printWriter = new PrintWriter(System.out, true);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(printWriter, 80, "", options);
        formatter.printOptions(printWriter, 80, options, 1, 1);
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
    
    // sets if the bonds are removed at the end
    private static void setRemoveBondsAtEnd(CommandLine cmdLine) {
        if ( cmdLine.hasOption("removeBondsAtEnd") ) {
            removeBondsAtEnd = Boolean.parseBoolean(cmdLine.getOptionValue("removeBondsAtEnd"));
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
    private static AutoNetworkAlgorithm createNetworkBuildingAlgorithm(
            DPA_Simply simply, CommandLine cmdLine
    ) {
        String networkId = NETWORK_ID_DEFAULT;
        if ( cmdLine.hasOption("networkId") ) {
            networkId = cmdLine.getOptionValue("networkId");
        }
        
        // getting network
        Network network = simply.getNetwork(networkId, Network.class);
        if ( network == null ) {
            printMessageAndExit("Network " + networkId + " doesn't exist");
        }
        
        int discoveryTxPower = AutoNetworkAlgorithmImpl.DISCOVERY_TX_POWER_DEFAULT;
        if ( cmdLine.hasOption("discoveryTxPower") ) {
            discoveryTxPower = Integer.parseInt(cmdLine.getOptionValue("discoveryTxPower"));
        }
        
        long prebondingInterval = AutoNetworkAlgorithmImpl.PREBONDING_INTERVAL_DEFAULT;
        if ( cmdLine.hasOption("prebondingInterval") ) {
            prebondingInterval = Long.parseLong(cmdLine.getOptionValue("prebondingInterval"));
        }
        
        int authorizeRetries = AutoNetworkAlgorithmImpl.AUTHORIZE_RETRIES_DEFAULT;
        if ( cmdLine.hasOption("authorizeRetries") ) {
            authorizeRetries = Integer.parseInt(cmdLine.getOptionValue("authorizeRetries"));
        }
        
        int discoveryRetries = AutoNetworkAlgorithmImpl.DISCOVERY_RETRIES_DEFAULT;
        if ( cmdLine.hasOption("discoveryRetries") ) {
            discoveryRetries = Integer.parseInt(cmdLine.getOptionValue("discoveryRetries"));
        }
        
        long temporaryAddressTimeout = AutoNetworkAlgorithmImpl.TEMPORARY_ADDRESS_TIMEOUT_DEFAULT;
        if ( cmdLine.hasOption("temporaryAddressTimeout") ) {
            temporaryAddressTimeout = Long.parseLong(cmdLine.getOptionValue("temporaryAddressTimeout"));
        }
        
        boolean autoUseFrc = AutoNetworkAlgorithmImpl.AUTOUSE_FRC_DEFAULT ;
        if ( cmdLine.hasOption("autoUseFrc") ) {
            autoUseFrc = Boolean.parseBoolean(cmdLine.getOptionValue("autoUseFrc"));
        }
        
        MethodIdTransformer methodIdTransformer = P2PPrebonderStandardTransformer.getInstance();
        if ( cmdLine.hasOption("methodIdTransformer") ) {
            methodIdTransformer = getMethodIdTransformer(cmdLine);
        }
        
        int numberOfNodesToBond = AutoNetworkAlgorithmImpl.NODES_NUMBER_TO_BOND_MAX;
        if ( cmdLine.hasOption("nodesNumberToBond") ) {
            numberOfNodesToBond = Integer.parseInt(cmdLine.getOptionValue("nodesNumberToBond"));
        }
        
        // get reference to algorithm object with reference to a network which
        // the algorithm will be running on
        // it is possible to set algorithm parameters or to leave theirs default values 
        return new AutoNetworkAlgorithmImpl.Builder(network, simply.getBroadcastServices())
                .discoveryTxPower(discoveryTxPower)
                .prebondingInterval(prebondingInterval)
                .authorizeRetries(authorizeRetries)
                .discoveryRetries(discoveryRetries)
                .temporaryAddressTimeout(temporaryAddressTimeout)
                .autoUseFrc(autoUseFrc)
                .p2pPrebonderMethodIdTransformer(methodIdTransformer)
                .numberOfNodesToBond(numberOfNodesToBond)
        .build();
    }
    
    public static void main(String[] args) throws InterruptedException {
        initCmdLineOptions();
        
        CommandLine cmdLine = null;
        try {
            // cmd arguments processing
            cmdLine = cmdLineParser.parse( options, args);
        } catch ( ParseException ex ) {
            printMessageAndExit("Error while parsing command line arguments: " + ex);
        }
        
        // if there is the 'help' option, print it and exit
        if ( cmdLine.hasOption("help") ) {
            printHelpMessage();
            return;
        }
 
        // set the maximal running time
        setMaxRunningTime(cmdLine);
        
        // set if the bonds are removed
        setRemoveBondsAtEnd(cmdLine);
        
        // creating the Simply instance    
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex);
        }
         
        // create object of algorithm configured via command line arguments
	algo = createNetworkBuildingAlgorithm(simply, cmdLine);

        // start the algorithm
        algo.start();
        
        // total running time [in seconds]
        long runTimeTotal = 0;
        
        while ( !algo.isFinished() ) {
            try {
                Thread.sleep(1000);
            } catch ( InterruptedException ex ) {
                printMessageAndExit("Algorithm interrupted.");
            }
            
            if ( maxRunningTime != MAX_RUNNING_TIME_UNLIMITED ) {
                runTimeTotal += 1;
                if ( runTimeTotal >= maxRunningTime ) {
                    // is algorithm finished?
                    if ( !algo.isFinished() ) {
                        // cancell the algorithm
                        // after cancellation is not possible to run the algorithm again
                        algo.cancel();
                        System.out.println("Algorithm cancelled.");
                    }
                }
            }
        }
        
        // algorithm state
        State algState = ((AutoNetworkAlgorithmImpl)(algo)).getState();
        switch ( algState ) {
            case FINISHED_OK:
                System.out.println("Algorithm succesfully finished.");
                break;
            case ERROR:
                System.out.println("Error occured during algorithm run.");
            default:
                System.out.println("Algorithm finished with state: " + algState);
        }
        
        // view the result of the algorithm run
        Network resultNetwork = ((AutoNetworkAlgorithmImpl)algo).getResultNetwork();
        
        int numberOfNodes = resultNetwork.getNodesMap().size() - 1;
        System.out.println("Number of bonded nodes in the network: " + numberOfNodes);
        
        // remove bonds
        if (removeBondsAtEnd) {
            System.out.println("Removing bonds at the end enabled");
            
            // getting network
            Network network = simply.getNetwork(NETWORK_ID_DEFAULT, Network.class);
            if (network == null) {
                printMessageAndExit("Network " + NETWORK_ID_DEFAULT + " doesn't exist");
            }

            // were there any nodes bonded
            if (numberOfNodes > 0) {
                // REMOVE BOND FROM NODES & RESET THEM
                // getting access to broadcast services
                BroadcastServices broadcast = simply.getBroadcastServices();
                
                UUID requestIdBatch = broadcast.sendRequest(
                    network.getId(),
                    OS.class,
                    OS.MethodID.BATCH,
                    new Object[]{
                        new DPA_Request[]{
                            new DPA_Request(
                                    com.microrisc.simply.iqrf.dpa.v22x.devices.Node.class,
                                    com.microrisc.simply.iqrf.dpa.v22x.devices.Node.MethodID.REMOVE_BOND,
                                    new Object[]{},
                                    0xFFFF
                            ),
                            new DPA_Request(
                                    OS.class,
                                    OS.MethodID.RESET,
                                    new Object[]{},
                                    0xFFFF
                            )
                        }
                    }
                );
                
                // check based on received confirmation
                // allow some time for result to propagete to application layer - max. 1000 ms
                BroadcastResult broadcastResult = broadcast.getBroadcastResult(requestIdBatch, 1000);
                
                // check if the broadcast was sent
                if (requestIdBatch == null || broadcastResult == BroadcastResult.ERROR ) {
                    printMessageAndExit("Error while sending request for removing bond and reseting");
                } else {
                    System.out.println("Remove bond on the nodes and reset them");
                }
            }

            // REMOVE ALL BONDS FROM COORDINATOR
            // getting a master node
            Node master = network.getNode("0");
            if (master == null) {
                printMessageAndExit("Master doesn't exist");
            }

            // getting Coordinator interface
            Coordinator coordinator = master.getDeviceObject(Coordinator.class);
            if (coordinator == null) {
                printMessageAndExit("Coordinator doesn't exist or is not enabled");
            }

            // send request
            VoidType result = coordinator.clearAllBonds();
            if (result == null) {
                CallRequestProcessingState procState = coordinator.getCallRequestProcessingStateOfLastCall();
                if (procState == CallRequestProcessingState.ERROR) {
                    CallRequestProcessingError error = coordinator.getCallRequestProcessingErrorOfLastCall();
                    printMessageAndExit("ClearAllBonds result failed: " + error);
                } else {
                    printMessageAndExit("ClearAllBonds result hasn't been processed yet: " + procState);
                }
            } else {
                System.out.println("Remove bonds on the coordinator");
            }
        }
        
        // end working with Simply
        simply.destroy();
    }
}
