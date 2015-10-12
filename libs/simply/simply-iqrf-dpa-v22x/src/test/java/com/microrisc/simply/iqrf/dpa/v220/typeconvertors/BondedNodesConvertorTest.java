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

import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.BondedNodesConvertor;
import com.microrisc.simply.iqrf.dpa.v22x.types.BondedNodes;
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
public class BondedNodesConvertorTest {
    
    public BondedNodesConvertorTest() {
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
     * Test 1 of toObject method, of class BondedNodesConvertor.
     */
    @Test
    public void test1_ToObject() throws Exception {
        BondedNodesConvertor instance = BondedNodesConvertor.getInstance();
        
        short[] protoValue = new short[] { 
            254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 127, 
            0, 0, 0, 0
        };
        BondedNodes result = (BondedNodes)instance.toObject(protoValue);
        
        List<Integer> expBondedNodesList = new LinkedList<>();
        for ( int nodeId = 1; nodeId <= 222; nodeId++ ) {
            expBondedNodesList.add(nodeId);
        }
        
        List<Integer> resultBondedNodesList = result.getList();
        
        assertTrue(resultBondedNodesList.equals(expBondedNodesList));
    }
    
    @Test
    public void test2_ToObject() throws Exception {
        BondedNodesConvertor instance = BondedNodesConvertor.getInstance();
        
        short[] protoValue = new short[] { 
            254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 127, 0, 0
        };
        BondedNodes result = (BondedNodes)instance.toObject(protoValue);
        
        List<Integer> expBondedNodesList = new LinkedList<>();
        for ( int nodeId = 1; nodeId < 239; nodeId++ ) {
            expBondedNodesList.add(nodeId);
        }
        
        List<Integer> resultBondedNodesList = result.getList();
        
        //System.out.println("Result: " + result.bondedNodesListToString());
        assertTrue(resultBondedNodesList.equals(expBondedNodesList));
    }
    
    @Test
    public void test3_ToObject() throws Exception {
        BondedNodesConvertor instance = BondedNodesConvertor.getInstance();
        
        short[] protoValue = new short[] { 
            254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 
            255, 255, 0, 0
        };
        BondedNodes result = (BondedNodes)instance.toObject(protoValue);
        
        List<Integer> expBondedNodesList = new LinkedList<>();
        for ( int nodeId = 1; nodeId < 240; nodeId++ ) {
            expBondedNodesList.add(nodeId);
        }
        
        List<Integer> resultBondedNodesList = result.getList();
        
        //System.out.println("Result: " + result.bondedNodesListToString());
        assertTrue(resultBondedNodesList.equals(expBondedNodesList));
    }
}
