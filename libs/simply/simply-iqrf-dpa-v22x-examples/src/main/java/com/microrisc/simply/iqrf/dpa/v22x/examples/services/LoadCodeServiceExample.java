/*
 * Copyright 2016 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.examples.services;

import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.DPA_Network;
import com.microrisc.simply.iqrf.dpa.DPA_Node;
import com.microrisc.simply.iqrf.dpa.DPA_Simply;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v22x.services.node.load_code.LoadCodeProcessingInfo;
import com.microrisc.simply.iqrf.dpa.v22x.services.node.load_code.LoadCodeService;
import com.microrisc.simply.iqrf.dpa.v22x.services.node.load_code.LoadCodeServiceParameters;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingResult;
import com.microrisc.simply.services.ServiceResult;
import java.io.File;

/**
 * Usage of Load Code Service.
 * 
 * @author Michal Konopa
 */
public class LoadCodeServiceExample {
    private static DPA_Simply simply = null;
    
    // prints out specified message, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if ( simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }
    
    public static void main(String[] args) {
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }

        // getting network 1
        DPA_Network network1 = simply.getNetwork("1", DPA_Network.class);
        if ( network1 == null ) {
            printMessageAndExit("Network 1 doesn't exist");
        }

        // getting node 1
        DPA_Node node1 = network1.getNode("1");
        if ( node1 == null ) {
            printMessageAndExit("Node 1 doesn't exist.");
        }
        
        // getting Load Code Service on node 1
        LoadCodeService loadCodeService = node1.getService(LoadCodeService.class);
        if ( loadCodeService == null ) {
            printMessageAndExit("Node 1 doesn't support Load Code Service.");
        }
        
        // loading code
        
        /*
        ServiceResult<LoadingResult, LoadCodeProcessingInfo> serviceResult 
            = loadCodeService.loadCode( 
                    new LoadCodeServiceParameters(
                        "D:\\Plocha\\IQRF_OS307_7xD\\Examples\\DPA\\CustomDpaHandlerExamples\\hex\\CustomDpaHandler-FRC-Minimalistic-7xD-V224-151201.hex", 
                        0x0000,
                        LoadingCodeProperties.LoadingAction.ComputeAndMatchChecksumWithoutCodeLoading,
                        LoadingCodeProperties.LoadingContent.Hex 
                    )
            );
        */
        
        ServiceResult<LoadingResult, LoadCodeProcessingInfo> serviceResult 
            = loadCodeService.loadCode( 
                    new LoadCodeServiceParameters(
                        "D:\\Plocha\\IQRF_OS308_7xD\\Examples\\DPA\\StartUp\\DemoPlug-ins\\CustomDpaHandler-ChangeIQRFOS-7xD-V226-160303.iqrf", 
                        0x0000,
                        LoadingCodeProperties.LoadingAction.ComputeAndMatchChecksumWithoutCodeLoading,
                        LoadingCodeProperties.LoadingContent.IQRF_Plugin
                    )
            );
        
        
        // getting results
        if ( serviceResult.getStatus() == ServiceResult.Status.SUCCESSFULLY_COMPLETED ) {
            System.out.println("Code successfully loaded.");
        } else {
            System.out.println("Code load was NOT successful.");
            // find out details
            LoadCodeProcessingInfo procInfo = serviceResult.getProcessingInfo();
            System.out.println(procInfo);
            // ...
        }
        
        simply.destroy();
    }
}
