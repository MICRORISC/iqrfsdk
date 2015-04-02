/*
 * Copyright 2015 MICRORISC s.r.o..
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

package com.microrisc.simply.iqrf.dpa.v210.devices;

import com.microrisc.simply.Network;
import com.microrisc.simply.Simply;
import com.microrisc.simply.SimplyException;
import com.microrisc.simply.iqrf.dpa.v210.DPA_SimplyFactory;
import com.microrisc.simply.iqrf.dpa.v210.types.AddressingInfo;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration tests for coordinator.
 * ATTENTION: This is only the first sketch !!! It is intended mainly for drawing
 * basic principles. For real usage, high improvements are unavoidable.
 * 
 * @author Michal Konopa
 */
public class CoordinatorIT {
    // reference to Simply object
    private static Simply simply = null;
    
    // reference to coordinator to test
    private static Coordinator coordinator = null;
    
    // testing data
    private static Map<String, Object> testingData = null;
    
            
    // prints out specified error description, destroys the Simply and exits
    private static void printMessageAndExit(String message) {
        System.out.println(message);
        if ( simply != null) {
            simply.destroy();
        }
        System.exit(1);
    }
    
    public CoordinatorIT() {
    }
    
    // sets up Simply and tested coordinator object
    // TODO: read configuration settings for:
    //  - location of main configuration file of Simply
    //  - source network ID
    //  - source node ID
    private static void setUpSimply() {
                
        // creating Simply instance
        try {
            simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");
        } catch ( SimplyException ex ) {
            printMessageAndExit("Error while creating Simply: " + ex.getMessage());
        }
        
        // getting network 1
        Network network1 = simply.getNetwork("1", Network.class);
        if ( network1 == null ) {
            printMessageAndExit("Network 1 doesn't exist");
        }
        
        // getting a master node
        com.microrisc.simply.Node master = network1.getNode("0");
        if ( master == null ) {
            printMessageAndExit("Master doesn't exist");
        }
        
        // getting Coordinator interface
        coordinator = master.getDeviceObject(Coordinator.class);
        if ( coordinator == null ) {
            printMessageAndExit("Coordinator doesn't exist");
        }
    }
    
    private static void tearDownSimply() {
        if ( simply != null ) {
            simply.destroy();
            simply = null;
        }
    }
    
    // sets up testing data
    // TODO: read testing data from external source
    private static void setUpTestingData() {
        testingData = new HashMap<>();
        
        // testing data for testAsync_getAddressingInfo test
        testingData.put("testAsync_getAddressingInfo.result.bondedNodesNum", 5);
        testingData.put("testAsync_getAddressingInfo.result.did", 4);
    }
    
    private static void tearDownTestingData() {
        testingData.clear();
        testingData = null;
    }
    
    
    @BeforeClass
    public static void setUpClass() {
        setUpSimply();
        setUpTestingData();
    }
    
    @AfterClass
    public static void tearDownClass() {
        tearDownTestingData();
        tearDownSimply();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of the 
     * {@link Coordinator#async_getAddressingInfo() async_getAddressingInfo } method.
     */
    @Test
    public void testAsync_getAddressingInfo() {
        System.out.println("async_getAddressingInfo");
        
        UUID requestId = coordinator.async_getAddressingInfo();
        assertNotNull("Request ID null", requestId);
        
        AddressingInfo result = coordinator.getCallResultInDefaultWaitingTimeout(
                requestId, AddressingInfo.class
        );
        assertNotNull("Result not available", result);
        
        assertEquals(result.getBondedNodesNum(), testingData.get("testAsync_getAddressingInfo.result.bondedNodesNum"));
        assertEquals(result.getDid(), testingData.get("testAsync_getAddressingInfo.result.did"));
    }
}
