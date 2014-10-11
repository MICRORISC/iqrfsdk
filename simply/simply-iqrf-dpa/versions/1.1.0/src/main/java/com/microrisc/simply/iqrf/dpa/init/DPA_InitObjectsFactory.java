
package com.microrisc.simply.iqrf.dpa.init;

import com.microrisc.simply.init.AbstractInitObjectsFactory;
import com.microrisc.simply.init.InitObjects;
import com.microrisc.simply.init.InitConfigSettings;
import com.microrisc.simply.init.SimpleInitObjectsFactory;
import com.microrisc.simply.iqrf.dpa.protocol.DPA_PeripheralToDevIfaceMapperFactory;
import com.microrisc.simply.iqrf.dpa.protocol.DPA_StandardPerProtocolMappingFactory;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapper;
import com.microrisc.simply.iqrf.dpa.protocol.PeripheralToDevIfaceMapperFactory;
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
 */
public final class DPA_InitObjectsFactory 
extends AbstractInitObjectsFactory<Configuration, SimpleDPA_InitObjects>
{
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
                return null;
            }
            Class factoryClass = Class.forName(factoryClassName);
            java.lang.reflect.Constructor constructor = factoryClass.getConstructor();
            ProtocolMappingFactory protoFactory = 
                    (ProtocolMappingFactory)constructor.newInstance(); 
            return protoFactory.createProtocolMapping();   
        }
        
        @Override
        protected ProtocolMapping createProtocolMapping(Configuration configuration) 
                throws Exception {
            ProtocolMapping standardProtoMapping = 
                    (new DPA_StandardPerProtocolMappingFactory()).createProtocolMapping();
            ProtocolMapping userProtoMapping = createUserPerProtocolMapping(configuration); 
            
            CallRequestToPacketMapping userRequestMapping = null;
            PacketToCallResponseMapping userResponseMapping = null;
            if ( userProtoMapping != null ) {
                userRequestMapping = userProtoMapping.getCallRequestToPacketMapping();
                userResponseMapping = userProtoMapping.getPacketToCallResponseMapping();
            }
            
            CallRequestToPacketMapping multiRequestMapping = 
                    new MultiCallRequestToPacketMapping( new CallRequestToPacketMapping[] { 
                        standardProtoMapping.getCallRequestToPacketMapping(),
                        userRequestMapping
                    } );
            
            PacketToCallResponseMapping multiResponseMapping = 
                    new MultiPacketToCallResponseMapping( new PacketToCallResponseMapping[] { 
                        standardProtoMapping.getPacketToCallResponseMapping(),
                        userResponseMapping
                    } );
            
            return new SimpleProtocolMapping(multiRequestMapping, multiResponseMapping);
        }
    }
    
    
    /**
     * Creates user peripherals to device interface mapping.
     * @param configuration configuration to use
     * @return
     * @throws Exception 
     */
    private PeripheralToDevIfaceMapper createUserPerToDevIfaceMapper(Configuration configuration) 
           throws Exception {
       String factoryClassName = configuration.getString("dpa.perToDevIfaceMapper.factory.class", "");
       // if user hasn't defined his mapping
       if ( factoryClassName.isEmpty() ) {
           return null;
       }
       
       Class factoryClass = Class.forName(factoryClassName);
       java.lang.reflect.Constructor constructor = factoryClass.getConstructor();
       PeripheralToDevIfaceMapperFactory factory = 
               (PeripheralToDevIfaceMapperFactory)constructor.newInstance(); 
       return factory.createPeripheralToDevIfaceMapper();
    }
    
    /** Creates peripheral to device interface mapper. */
    private PeripheralToDevIfaceMapper createPeripheralToDevIfaceMapper(Configuration configuration) 
            throws Exception {
        PeripheralToDevIfaceMapper standardPerMapper = 
                (new DPA_PeripheralToDevIfaceMapperFactory()).createPeripheralToDevIfaceMapper();
        PeripheralToDevIfaceMapper userPerMapper = createUserPerToDevIfaceMapper(configuration);
                
        return new MultiPeripheralToDevIfaceMapper( new PeripheralToDevIfaceMapper[] {
            standardPerMapper, userPerMapper
        });
    }

    
    
    @Override
    public SimpleDPA_InitObjects getInitObjects(Configuration configuration) 
            throws Exception {
        DPA_UserPerMappingFactory dpaInitObjectsFactory = new DPA_UserPerMappingFactory();
        InitObjects<InitConfigSettings<Configuration, Map<String, Configuration>>> dpaInitObjects 
                = dpaInitObjectsFactory.getInitObjects(configuration);
        PeripheralToDevIfaceMapper perMapper = createPeripheralToDevIfaceMapper(configuration);
        return new SimpleDPA_InitObjects(
                dpaInitObjects.getConnectionStack(), 
                dpaInitObjects.getImplClassMapper(), 
                dpaInitObjects.getConfigSettings(),
                perMapper
        );
    }
    
}
