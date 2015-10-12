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
package com.microrisc.simply.iqrf.dpa.v22x.devices;

import com.microrisc.simply.DeviceInterface;
import com.microrisc.simply.DeviceInterfaceMethodId;
import com.microrisc.simply.di_services.GenericAsyncCallable;
import com.microrisc.simply.di_services.MethodIdTransformer;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.DPA_StandardServices;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Command;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Configuration;
import com.microrisc.simply.iqrf.dpa.v22x.types.FRC_Data;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA FRC Device Interface.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if an error has occurred during processing
 * of this method.
 *
 * @author Rostislav Spinar
 */
@DeviceInterface
public interface FRC
        extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {

    /**
     * Identifiers of this device interface's methods.
     */
    enum MethodID implements DeviceInterfaceMethodId {

        SEND,
        EXTRA_RESULT,
        SEND_SELECTIVE,
        SET_FRC_PARAMS
    }

    // ASYNCHRONOUS METHODS
    /**
     * This command starts Fast Response Command (FRC) process supported by IQRF
     * OS. It allows quickly and using only one command to collect same type of
     * information from multiple nodes in the network.
     *
     * @param frcCmd FRC command to use
     * @return unique identifier of sent request
     */
    UUID async_send(FRC_Command frcCmd);

    /**
     * Reads remaining bytes of the FRC result, so the total number of bytes
     * obtained by both commands will be total 64. It is recommended to call
     * this command immediately after the FRC Send command to preserve
     * previously collected FRC data.
     *
     * @return unique identifier of sent request
     */
    UUID async_extraResult();

    /**
     * This command starts Fast Response Command (FRC) process supported by IQRF
     * OS. It allows quickly and using only one command to collect same type of
     * information from multiple nodes in the network. Nodes from which will be
     * collected information can be specified in selective map.
     *
     * @param frcCmd FRC command to use with correctl selected nodes
     * @return unique identifier of sent request
     */
    UUID async_sendSelective(FRC_Command frcCmd);
    
    /**
     * Sets global FRC parameters specified in {@link FRC_Configuration}.
     * @param config data to configuration
     * @return {@code VoidType} object, if method call has processed allright
     */
    UUID async_setFRCParams(FRC_Configuration config);

    // SYNCHRONOUS WRAPPERS
    /**
     * Synchronous wrapper for {@link
     * #async_send(com.microrisc.simply.iqrf.dpa.v220.types.FRC_Command)  async_send}
     * method.
     *
     * @param frcCmd FRC command to use
     * @return FRC data collected from nodes
     */
    FRC_Data send(FRC_Command frcCmd);

    /**
     * Synchronous wrapper for {@link #async_extraResult() async_extraResult}
     * method.
     *
     * @return remaining bytes of the FRC result
     */
    short[] extraResult();

    /**
     * Synchronous wraper for
     * {@link #async_sendSelective(com.microrisc.simply.iqrf.dpa.v220.types.FRC_Command) asnyc_sendSelective}. <br>
     * NOTE: Returned results are sorted one by one and nodes numbers are not 
     * coresponding with real numbers.
     *
     * @param frcCmd FRC command to use with correctly select nodes
     *
     * @return FRC data collected from nodes
     */
    FRC_Data sendSelective(FRC_Command frcCmd);
    
    /**
     * Synchronous wrapper for {@link #async_setFRCParams(com.microrisc.simply.iqrf.dpa.v220.types.FRC_Configuration) async_setfRCParams}
     * @param config data to configuration
     * @return {@code VoidType} object, if method call has processed allright
     */
    VoidType setFRCParams(FRC_Configuration config);
}
