/*
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.protocol.mapping;

import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapperFactory;
import com.microrisc.simply.protocol.mapping.ProtocolMapping;
import com.microrisc.simply.protocol.mapping.ProtocolMappingFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Provides creating of {@link ProtocolMapping} and mapping peripherals number 
 * to interfaces from file, especially for for mapping of user periherals.
 * <p>
 * Mapping from file is based on simplified xml file, which contains only
 * important information about mapping related with implementation of DPA
 * protocol.
 * <p>
 * <i>Development note:</i> methods with comment "default" are methods, which
 * are returning general and default values (mappings) of DPA protocol. This
 * mapping is (almost) always same.
 * <p>
 * @author Martin Strouhal
 */
public final class FileMapper implements ProtocolMappingFactory, PeripheralToDevIfaceMapperFactory {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(FileMapper.class);

    private ProtocolMapping protocolMapping;
    private UserPerToDevIfaceMapper mapper;
    
    /**
     * Holds mapping between my peripherals and Device Interfaces.
     */
    private class UserPerToDevIfaceMapper implements PeripheralToDevIfaceMapper {

        private final Map<Integer, Class> peripheralToIface;
        private final Map<Class, Integer> ifaceToPeripheral;

        public UserPerToDevIfaceMapper(Map<Integer, Class> peripheralToIface) {
            this.peripheralToIface = peripheralToIface;
            this.ifaceToPeripheral = new HashMap<>();

            // creating transposition
            for (Map.Entry<Integer, Class> entry : this.peripheralToIface.entrySet()) {
                this.ifaceToPeripheral.put(entry.getValue(), entry.getKey());
            }
        }

        @Override
        public Set<Class> getMappedDeviceInterfaces() {
            return ifaceToPeripheral.keySet();
        }

        @Override
        public Class getDeviceInterface(int perId) {
            return peripheralToIface.get(perId);
        }

        @Override
        public Integer getPeripheralId(Class devInterface) {
            return ifaceToPeripheral.get(devInterface);
        }

        @Override
        public Set<Integer> getMappedPeripherals() {
            return peripheralToIface.keySet();
        }
    }


    /** Name of file from which is mapping loaded. */
    private final String file_name;
    /** Default file name for file containing mapping. */
    private final static String DEFAULT_FILE_NAME = "config/mapping.xml";

    private FileMapper(Configuration config) {
        file_name = config.getString("protocolLayer.protocolMapping.mappingFile", DEFAULT_FILE_NAME);
        logger.debug("Created FileMapping with Configuration=" + config);
    }

    private static FileMapper instance;
    /**
     * Returns instance of {@link FileMapper} objects.
     * @param config from which can be used mapping file name
     * @return instance of {@link FileMapper} objects.
     */
    public static FileMapper getInstance(Configuration config) {
        if (instance == null) {
            instance = new FileMapper(config);
        }
        return instance;
    }

    @Override
    public ProtocolMapping createProtocolMapping() throws Exception {
        if (protocolMapping != null) {
            return protocolMapping;
        }

        File file = new File(file_name);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        doc.getDocumentElement().normalize();

        NodeList listInterfaces = doc.getElementsByTagName("interface");

        FileMappingObjects objects = FileMappingParser.getParser().parse(listInterfaces);
        protocolMapping = objects.getProtocolMapping();
        mapper = new UserPerToDevIfaceMapper(objects.getPeripheralToIface());

        return protocolMapping;
    }

    @Override
    public PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper() throws Exception {
        if (mapper == null) {
            createProtocolMapping();
        }
        return mapper;
    }
}
