

package com.microrisc.simply.iqrf.dpa.v201.init;

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
    private Map<String, Class> peripheralToIface; 
    private Map<Class, String> ifaceToPeripheral;
    
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
        
        for (PeripheralToDevIfaceMapper mapper : mappers) {
            Set<Class> mapperDevIfaces = mapper.getMappedDeviceInterfaces();
            for (Class mapperDevIface : mapperDevIfaces) {
                String perId = mapper.getPeripheralId(mapperDevIface);
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
    public Set<Class> getMappedDeviceInterfaces() {
        Set<Class> mappedIfaces = new HashSet<>();
        for (PeripheralToDevIfaceMapper mapper : mappers) {
            mappedIfaces.addAll(mapper.getMappedDeviceInterfaces());
        }
        return mappedIfaces;
    }
    
    @Override
    public Class getDeviceInterface(String perId) {
        return peripheralToIface.get(perId);
    }

    @Override
    public String getPeripheralId(Class devInterface) {
        return ifaceToPeripheral.get(devInterface);
    }

}
