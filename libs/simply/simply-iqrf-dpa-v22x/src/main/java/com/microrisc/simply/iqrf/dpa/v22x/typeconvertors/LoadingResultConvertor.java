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

import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingResult;
import com.microrisc.simply.protocol.mapping.ConvertorFactoryMethod;
import com.microrisc.simply.typeconvertors.PrimitiveConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality for converting from {@link LoadingResultConvertor}
 * type values to proto values.
 *
 * @author Martin Strouhal
 */
public final class LoadingResultConvertor extends PrimitiveConvertor {

   /** Logger. */
   private static final Logger logger = LoggerFactory.getLogger(
           LoadingResultConvertor.class);

   private LoadingResultConvertor() {
   }

   /** Singleton. */
   private static final LoadingResultConvertor instance = new LoadingResultConvertor();


   /**
    * @return {@code LoadingResultConvertor} instance
    */
   @ConvertorFactoryMethod
   static public LoadingResultConvertor getInstance() {
      return instance;
   }

   /** Size of returned response. */
   static public final int TYPE_SIZE = 1;

   @Override
   public int getGenericTypeSize() {
      return TYPE_SIZE;
   }


   /**
    * Currently not supported. Throws {@code UnsupportedOperationException }.
    *
    * @throws UnsupportedOperationException
    */
   @Override
   public short[] toProtoValue(Object value) throws ValueConversionException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Object toObject(short[] protoValue) throws ValueConversionException {
      logger.debug("toObject - start: protoValue={}", protoValue);
      LoadingResult result;
      if (protoValue.length >= TYPE_SIZE) {
         if (protoValue[0] == 1) {
            result = new LoadingResult(true);
         } else {
            result = new LoadingResult(false);
         }
      } else {
         logger.warn("Length of protoValue is 0 instead of 1.");
         result = new LoadingResult(false);
      }
      logger.debug("toObject - end: {}", result);
      return result;
   }
}
