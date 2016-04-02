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
package com.microrisc.simply.iqrf.dpa.v22x.typeconvertors;

import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties.LoadingAction;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties.LoadingContent;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@link LoadingCodeProperties} type
 * values to proto values.
 *
 * @author Martin Strouhal
 */
public final class LoadingCodePropertiesConvertor extends PrimitiveConvertor {

   /** Logger. */
   private static final Logger logger = LoggerFactory.getLogger(
           LoadingCodePropertiesConvertor.class);

   private LoadingCodePropertiesConvertor() {
   }

   /** Singleton. */
   private static final LoadingCodePropertiesConvertor instance = new LoadingCodePropertiesConvertor();


   /**
    * @return {@code LoadingCodePropertiesConvertor} instance
    */
   @ConvertorFactoryMethod
   static public LoadingCodePropertiesConvertor getInstance() {
      return instance;
   }

   /** Size of returned response. */
   static public final int TYPE_SIZE = 7;

   @Override
   public int getGenericTypeSize() {
      return TYPE_SIZE;
   }


   // postitions of fields
   static private final int FLAGS_VALUE_POS = 0;
   static private final int ADDRES_VALUE_POS = 1;
   static private final int LENGTH_VALUE_POS = 3;
   static private final int CHECKSUM_VALUE_POS = 5;

   
   @Override
   public short[] toProtoValue(Object value) throws ValueConversionException {
      logger.debug("toProtoValue - start: value={}", value);

      if (!(value instanceof LoadingCodeProperties)) {
         throw new ValueConversionException(
                 "Value to convert has not proper type.");
      }
      
      LoadingCodeProperties codeProp = (LoadingCodeProperties)value;
      
      short[] protoValue = new short[TYPE_SIZE];
      
      // FLAGS
      if(codeProp.getContent() == LoadingContent.IQRF_Plugin){
         protoValue[FLAGS_VALUE_POS] = 1;
      }else{
         protoValue[FLAGS_VALUE_POS] = 0;
      }
      protoValue[FLAGS_VALUE_POS] <<= 1;
      if(codeProp.getAction() == LoadingAction.ComputeAndMatchChecksumWithCodeLoading){
         protoValue[FLAGS_VALUE_POS]++;
      }
      
      // ADDRESS
      int address = codeProp.getAddress();
      protoValue[ADDRES_VALUE_POS] = (short) (address & 0xFF); 
      address >>= 8;
      protoValue[ADDRES_VALUE_POS+1] = (short)address; 
      
      // LENGTH
      int length = codeProp.getLength();
      protoValue[LENGTH_VALUE_POS] = (short) (length & 0xFF); 
      length >>= 8;
      protoValue[LENGTH_VALUE_POS+1] = (short)length; 
      
      // CHECKSUM
      int checksum = codeProp.getChecksum();
      protoValue[CHECKSUM_VALUE_POS] = (short) (checksum & 0xFF); 
      checksum >>= 8;
      protoValue[CHECKSUM_VALUE_POS+1] = (short)checksum; 
      
      logger.debug("toProtoValue - end: " + Arrays.toString(protoValue));
      return protoValue;
   }

   /**
    * Currently not supported. Throws {@code UnsupportedOperationException }.
    *
    * @throws UnsupportedOperationException
    */
   @Override
   public Object toObject(short[] protoValue) throws ValueConversionException {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
