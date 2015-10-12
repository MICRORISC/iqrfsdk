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

package com.microrisc.simply.iqrf.dpa.v22x.init;

import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Peripheral to Device Interface mapper which puts together other peripheral
 * to Device Interface mappers.
 * Each of the 'source' mappers must support disjunctive set of 
 * Device Interface <-> DPA Peripherls mappings from each other mappers.
 * 
 * @author Michal Konopa
 */
final class MultiPeripheralToDevIfaceMapper implements PeripheralToDevIfaceMapper {
    private PeripheralToDevIfaceMapper[] mappers;
    private Map<Integer, Class> peripheralToIface; 
    private Map<Class, Integer> ifaceToPeripheral;
    
    private PeripheralToDevIfaceMapper[] checkPeripheralToDevIfaceMappers( 
            PeripheralToDevIfaceMapper[] mappers 
    ) {
        if ( mappers == null ) {
            throw new IllegalArgumentException("Mappers cannot be null");
        }
        
        if ( mappers.length == 0 ) {
            throw new IllegalArgumentException("Mappers cannot be empty");
        }
        return mappers;
    }
    
    // creates union mapper
    private void createUnionMapper(PeripheralToDevIfaceMapper[] mappers) {
        peripheralToIface = new HashMap<>();
        ifaceToPeripheral = new HashMap<>();
        
        for ( PeripheralToDevIfaceMapper mapper : mappers ) {
            Set<Class> mapperDevIfaces = mapper.getMappedDeviceInterfaces();
            for (Class mapperDevIface : mapperDevIfaces) {
                int perId = mapper.getPeripheralId(mapperDevIface);
                if ( ifaceToPeripheral.containsKey(mapperDevIface) ) {
                    throw new IllegalArgumentException(
                            "Mappers haven't discjunctive"
                            + " mapped sets of Device Interfaces: " + mapperDevIface
                    );
                }
                
                if ( peripheralToIface.containsKey(perId) ) {
                    throw new IllegalArgumentException(
                            "Mappers haven't discjunctive"
                            + " mapped sets of Peripherals: " + perId
                    );
                }
                
                ifaceToPeripheral.put(mapperDevIface, perId);
                peripheralToIface.put(perId, mapperDevIface);
            }
        }
    } 
    
    private void copyMappers(PeripheralToDevIfaceMapper[] mappers) {
        List<PeripheralToDevIfaceMapper> mappersList = new LinkedList<>();
        for ( PeripheralToDevIfaceMapper mapper : mappers ) {
            if ( mapper != null ) {
                mappersList.add(mapper);
            }
        }
        this.mappers = mappersList.toArray(new PeripheralToDevIfaceMapper[] {} );
    }
    
    
    /**
     * Creates new multi peripherals to Device Interface mapper constitued from 
     * specified mappers. 
     * @param mappers source mappers
     */
    public MultiPeripheralToDevIfaceMapper(PeripheralToDevIfaceMapper[] mappers) {
        mappers = checkPeripheralToDevIfaceMappers(mappers);
        copyMappers(mappers);
        createUnionMapper(this.mappers);
    }
    
    @Override
    public Set<Integer> getMappedPeripherals() {
        Set<Integer> mappedPeripherals = new HashSet<>();
        for ( PeripheralToDevIfaceMapper mapper : mappers ) {
            mappedPeripherals.addAll(mapper.getMappedPeripherals());
        }
        return mappedPeripherals;
    }
    
    @Override
    public Set<Class> getMappedDeviceInterfaces() {
        Set<Class> mappedIfaces = new HashSet<>();
        for ( PeripheralToDevIfaceMapper mapper : mappers ) {
            mappedIfaces.addAll(mapper.getMappedDeviceInterfaces());
        }
        return mappedIfaces;
    }
    
    @Override
    public Class getDeviceInterface(int perId) {
        return peripheralToIface.get(perId);
    }

    @Override
    public Integer getPeripheralId(Class devInterface) {
        return ifaceToPeripheral.get(devInterface);
    }

}
