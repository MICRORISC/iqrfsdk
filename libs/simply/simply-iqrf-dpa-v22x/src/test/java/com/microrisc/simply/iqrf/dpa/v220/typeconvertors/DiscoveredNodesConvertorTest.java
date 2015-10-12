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

package com.microrisc.simply.iqrf.dpa.v220.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DiscoveredNodesConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.types.DiscoveredNodes;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michal Konopa
 */
public class DiscoveredNodesConvertorTest {
    
    public DiscoveredNodesConvertorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test 1 of toObject method, of class DiscoveredNodesConvertor.
     */
    @Test
    public void test1_ToObject() throws Exception {
        DiscoveredNodesConvertor instance = DiscoveredNodesConvertor.getInstance();
        
        short[] protoValue = new short[] { 
            254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 127, 
            0, 0, 0, 0
        };
        DiscoveredNodes result = (DiscoveredNodes)instance.toObject(protoValue);
        
        List<Integer> expDiscoveredNodesList = new LinkedList<>();
        for ( int nodeId = 1; nodeId <= 222; nodeId++ ) {
            expDiscoveredNodesList.add(nodeId);
        }
        
        List<Integer> resultDsicoveredNodesList = result.getList();
        
        assertTrue(resultDsicoveredNodesList.equals(expDiscoveredNodesList));
    }
    
    @Test
    public void test2_ToObject() throws Exception {
        DiscoveredNodesConvertor instance = DiscoveredNodesConvertor.getInstance();
        
        short[] protoValue = new short[] { 
            254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 127, 0, 0
        };
        DiscoveredNodes result = (DiscoveredNodes)instance.toObject(protoValue);
        
        List<Integer> expDiscoveredNodesList = new LinkedList<>();
        for ( int nodeId = 1; nodeId < 239; nodeId++ ) {
            expDiscoveredNodesList.add(nodeId);
        }
        
        List<Integer> resultDiscoveredNodesList = result.getList();
        
        //System.out.println("Result: " + result.bondedNodesListToString());
        assertTrue(resultDiscoveredNodesList.equals(expDiscoveredNodesList));
    }
    
    @Test
    public void test3_ToObject() throws Exception {
        DiscoveredNodesConvertor instance = DiscoveredNodesConvertor.getInstance();
        
        short[] protoValue = new short[] { 
            254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 255, 0, 0
        };
        DiscoveredNodes result = (DiscoveredNodes)instance.toObject(protoValue);
        
        List<Integer> expDiscoveredNodesList = new LinkedList<>();
        for ( int nodeId = 1; nodeId < 240; nodeId++ ) {
            expDiscoveredNodesList.add(nodeId);
        }
        
        List<Integer> resultDiscoveredNodesList = result.getList();
        
        //System.out.println("Result: " + result.bondedNodesListToString());
        assertTrue(resultDiscoveredNodesList.equals(expDiscoveredNodesList));
    }
    
}
