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

package com.microrisc.simply.typeconvertors;

/**
 * Abstract base class for conversion data between generic types and Java types 
 * via used application protocol. Conversion to generic is done by encoding 
 * required Java value to sequence of bytes of the protocol. This sequence will 
 * be then after delivering decoded to the corresponing generic type value 
 * on network node. <br>
 * Conversion from generic value to the Java one is done by decoding incomming 
 * byte sequence, which represents that encoded generic value. 
 * 
 * @author Michal Konopa
 */
abstract public class AbstractConvertor {
    /**
     * Converts specified Java-type value to the sequence of bytes for sending 
     * it to underlaying network via used application protocol. On the network 
     * side will be the sequence converted to corresponding generic type value.
     * @param value object value to convert
     * @return application protocol representation of converted value
     * @throws ValueConversionException if the specified value has not appropriate type
     */
    abstract public short[] toProtoValue(Object value) throws ValueConversionException;
    
    /**
     * Converts specified application protocol sequence of bytes, which represents 
     * generic type value, to corresponding Java type value.
     * @param protoValue application protocol value to convert
     * @return Java-type value of converted value
     * @throws ValueConversionException if an error is encountered inside input sequence
     */
    abstract public Object toObject(short[] protoValue) throws ValueConversionException;
}
