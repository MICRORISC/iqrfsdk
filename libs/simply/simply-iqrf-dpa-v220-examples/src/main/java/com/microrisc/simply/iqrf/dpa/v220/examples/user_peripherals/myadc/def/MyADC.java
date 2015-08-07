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
package com.microrisc.simply.iqrf.dpa.v220.examples.user_peripherals.myadc.def;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.di_services.DPA_StandardServices;

/**
 * MyADC Device Interface providing reading a value from ADC convertor on
 * module. This example is recommended use with DK-EVAL and DDC-SE. Get returns
 * in this case value from potentionmeter and Get2 returns value from
 * photoresistor.
 *
 * @author Martin Strouhal
 */
@DeviceInterface
public interface MyADC
        extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {

    /**
     * Identifiers of this Device Interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {

        GET,
        GET2
    }

    /**
     * Gets first actual value of MyADC. In case use with DDC-SE value from
     * potentionmeter.
     *
     * @return first actual value of MyADC <br> {@code Integer.MAX_VALUE} if an
     * error has occurred during processing
     */
    int get();

    /**
     * Gets second actual value of MyADC. In case use with DDC-SE value from
     * photoresistor.
     *
     * @return second actual value of MyADC <br> {@code Integer.MAX_VALUE} if an
     * error has occurred during processing
     */
    int get2();

}
