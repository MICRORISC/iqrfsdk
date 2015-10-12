/* 
 * Copyright 2014 - 2015 MICRORISC s.r.o.
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

import com.microrisc.simply.init.AbstractInitObjectsFactory;
import com.microrisc.simply.init.InitConfigSettings;
import com.microrisc.simply.init.InitObjects;
import com.microrisc.simply.init.SimpleInitObjectsFactory;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapperFactory;
import com.microrisc.simply.iqrf.dpa.protocol.ProtocolObjects;
import com.microrisc.simply.iqrf.dpa.v22x.protocol.CustomUserPerToDevIfaceMapperFactory;
import com.microrisc.simply.iqrf.dpa.v22x.protocol.CustomUserProtocolMappingFactory;
import com.microrisc.simply.iqrf.dpa.v22x.protocol.DPA_PeripheralToDevIfaceMapperFactory;
import com.microrisc.simply.iqrf.dpa.v22x.protocol.DPA_StandardPerProtocolMappingFactory;
import com.microrisc.simply.protocol.mapping.CallRequestToPacketMapping;
import com.microrisc.simply.protocol.mapping.PacketToCallResponseMapping;
import com.microrisc.simply.protocol.mapping.ProtocolMapping;
import com.microrisc.simply.protocol.mapping.ProtocolMappingFactory;
import com.microrisc.simply.protocol.mapping.SimpleProtocolMapping;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * DPA implementation of Simply initialization objects factory.
 * <p>
 * Associated configuration properties:
 * - protocol mapping factory class for user defined Device Interfaces: 
 * <b>protocolLayer.protocolMapping.factory.class</b>
 * 
 * - user DPA peripherals to Device Interafces factory class:
 * <b>dpa.perToDevIfaceMapper.factory.class</b>
 * 
 * 
 * @author Michal Konopa
 * @author Martin Strouhal
 */
public final class DPA_InitObjectsFactory
extends AbstractInitObjectsFactory<Configuration, SimpleDPA_InitObjects>
{
    // for temporary storing of created DPA protocol mapping
    private static ProtocolMapping _protocolMapping = null;

    /**
     * Extends {@code SimpleInitObjectsFactory} by support for user
     * peripheral protocol mapping. <br>
     * Standard DPA peripherals will be added automatically without any need of
     * user intervention.
     */
    private static final class DPA_UserPerMappingFactory
            extends SimpleInitObjectsFactory {
        /**
         * Creates user peripheral protocol mapping.
         * @param configuration configuration to use
         * @return
         * @throws Exception
         */
        private ProtocolMapping createUserPerProtocolMapping(Configuration configuration)
                throws Exception {
            String factoryClassName = configuration.getString("protocolLayer.protocolMapping.factory.class", "");
            // if user hasn't defined his mapping
            if ( factoryClassName.isEmpty() ) {
                //if any mapping doesn't exist, create mapping with custom 
                // peripheral over specified peripherals in config
                return new CustomUserProtocolMappingFactory(configuration).createProtocolMapping();
            }
            Class factoryClass = Class.forName(factoryClassName);

            // if ProtocolMappingFactory support getInstance for configuration, it's given
            // recognizing of used constructor            
            ProtocolMappingFactory protoFactory;
            try {
                java.lang.reflect.Method getInstance = factoryClass.getDeclaredMethod("getInstance", Configuration.class);
                protoFactory = (ProtocolMappingFactory) getInstance.invoke(null, configuration);
            } catch (NoSuchMethodException ex) {
                java.lang.reflect.Constructor constructor = factoryClass.getConstructor();
                protoFactory = (ProtocolMappingFactory) constructor.newInstance();
            }
            return protoFactory.createProtocolMapping();
        }

        @Override
        protected ProtocolMapping createProtocolMapping(Configuration configuration)
                throws Exception {
            ProtocolMapping standardProtoMapping
                    = (new DPA_StandardPerProtocolMappingFactory()).createProtocolMapping();
            ProtocolMapping userProtoMapping = createUserPerProtocolMapping(configuration);

            CallRequestToPacketMapping userRequestMapping = null;
            PacketToCallResponseMapping userResponseMapping = null;
            if (userProtoMapping != null) {
                userRequestMapping = userProtoMapping.getCallRequestToPacketMapping();
                userResponseMapping = userProtoMapping.getPacketToCallResponseMapping();
            }

            CallRequestToPacketMapping multiRequestMapping
                    = new MultiCallRequestToPacketMapping(new CallRequestToPacketMapping[]{
                        standardProtoMapping.getCallRequestToPacketMapping(),
                        userRequestMapping
                    });

            PacketToCallResponseMapping multiResponseMapping
                    = new MultiPacketToCallResponseMapping(new PacketToCallResponseMapping[]{
                        standardProtoMapping.getPacketToCallResponseMapping(),
                        userResponseMapping
                    });
            ProtocolMapping protocolMapping = new SimpleProtocolMapping(
                    multiRequestMapping, multiResponseMapping
            );
            _protocolMapping = protocolMapping;
            return protocolMapping;
        }
    }

    /**
     * Creates user peripherals to device interface mapping.
     * @param configuration configuration to use
     * @return user peripherals to device interface mapping
     * @throws Exception
     */
    private PeripheralToDevIfaceMapper createUserPerToDevIfaceMapper(Configuration configuration)
            throws Exception {
        String factoryClassName = configuration.getString("dpa.perToDevIfaceMapper.factory.class", "");
        // if user hasn't defined his mapping
       if ( factoryClassName.isEmpty() ) {
            //if any mapping doesn't exist, create mapping with custom 
            // peripheral over specified peripherals in config
           return new CustomUserPerToDevIfaceMapperFactory(configuration).createPeripheralToDevIfaceMapper();
        }
        Class factoryClass = Class.forName(factoryClassName);
        
        // if ProtocolMappingFactory support getInstance for configuration, it's given
        // recognizing of used constructor            
        PeripheralToDevIfaceMapperFactory factory;
        try {
            java.lang.reflect.Method getInstance = factoryClass.getDeclaredMethod("getInstance", Configuration.class);
            factory = (PeripheralToDevIfaceMapperFactory) getInstance.invoke(null, configuration);
        } catch (NoSuchMethodException ex) {
            java.lang.reflect.Constructor constructor = factoryClass.getConstructor();
            factory = (PeripheralToDevIfaceMapperFactory) constructor.newInstance();
        }
                                
        return factory.createPeripheralToDevIfaceMapper();
    }    

    /** Creates peripheral to device interface mapper. */
    private PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper(Configuration configuration)
            throws Exception 
    {
        PeripheralToDevIfaceMapper standardPerMapper = 
                (new DPA_PeripheralToDevIfaceMapperFactory()).createPeripheralToDevIfaceMapper();
        PeripheralToDevIfaceMapper userPerMapper = createUserPerToDevIfaceMapper(configuration);

        return new MultiPeripheralToDevIfaceMapper( new PeripheralToDevIfaceMapper[] {
            standardPerMapper, userPerMapper
        });
    }

    @Override
    public SimpleDPA_InitObjects getInitObjects(Configuration configuration)
            throws Exception 
    {
        DPA_UserPerMappingFactory dpaInitObjectsFactory = new DPA_UserPerMappingFactory();
        InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> dpaInitObjects
                = dpaInitObjectsFactory.getInitObjects(configuration);
        PeripheralToDevIfaceMapper perMapper = createPeripheralToDevIfaceMapper(configuration);

        // initialization of ProtocolObjects class
        ProtocolObjects.init(perMapper, _protocolMapping);

        return new SimpleDPA_InitObjects(
                dpaInitObjects.getConnectionStack(),
                dpaInitObjects.getImplClassMapper(),
                dpaInitObjects.getConfigSettings(),
                perMapper
        );
    }

}
