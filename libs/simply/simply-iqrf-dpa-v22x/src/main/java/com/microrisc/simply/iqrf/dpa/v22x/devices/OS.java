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
import com.microrisc.simply.iqrf.dpa.v22x.types.DPA_Request;
import com.microrisc.simply.iqrf.dpa.v22x.types.HWP_Configuration;
import com.microrisc.simply.iqrf.dpa.v22x.types.HWP_ConfigurationByte;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties;
import com.microrisc.simply.iqrf.dpa.v22x.types.LoadingResult;
import com.microrisc.simply.iqrf.dpa.v22x.types.OsInfo;
import com.microrisc.simply.iqrf.dpa.v22x.types.SleepInfo;
import com.microrisc.simply.iqrf.types.VoidType;
import java.util.UUID;

/**
 * DPA OS Device Interface.
 * <p>
 * IMPORTANT NOTE: <br>
 * Every method returns {@code NULL}, if an error has occurred during processing
 * of this method.
 *
 * @author Michal Konopa
 */
@DeviceInterface
public interface OS
        extends DPA_StandardServices, GenericAsyncCallable, MethodIdTransformer {

   /**
    * Identifiers of this device interface's methods.
    */
   enum MethodID implements DeviceInterfaceMethodId {
      READ,
      RESET,
      READ_HWP_CONFIGURATION,
      RUN_RFPGM,
      SLEEP,
      BATCH,
      SET_USEC,
      SET_MID,
      RESTART,
      WRITE_HWP_CONFIGURATION,
      WRITE_HWP_CONFIGURATION_BYTE,
      LOAD_CODE
   }


   // ASYNCHRONOUS METHODS

   /**
    * Sends method call request for reading some useful system information about
    * the node.
    *
    * @return unique identifier of sent request
    */
   UUID async_read();

   /**
    * Sends method call request for forcing (DC)TR transceiver module to carry
    * out reset.
    *
    * @return unique identifier of sent request
    */
   UUID async_reset();

   /**
    * Sends method call request for reading a raw HWP configuration memory.
    *
    * @return unique identifier of sent request
    */
   UUID async_readHWPConfiguration();

   /**
    * Sends method call request for putting device into RFPGM mode configured at
    * HWP Configuration. The device is reset when RFPGM process is finished or
    * if it ends due to timeout. RFPGM runs at same channels (configured at HWP
    * configuration) the network is using.
    *
    * @return unique identifier of sent request
    */
   UUID async_runRFPGM();

   /**
    * Sends method call request for putting device into sleep (power saving)
    * mode. This command is not implemented at the device having coordinator
    * functionality i.e. [C] and [CN].
    *
    * @param sleepInfo information about sleeping
    * @return unique identifier of sent request
    */
   UUID async_sleep(SleepInfo sleepInfo);

   /**
    * Sends method call request for allowing to execute more individual DPA
    * requests within one original DPA request. It is not allowed to embed Batch
    * command itself within series of individual DPA requests. Using Run
    * discover is not allowed inside batch command list too.
    *
    * @param requests DPA requests to be executed
    * @return unique identifier of sent request
    */
   UUID async_batch(DPA_Request[] requests);

   /**
    * Sends method call request for setting value of User Security Code (USEC).
    * USEC is used for an additional authorization to enter maintenance DPA
    * Service Mode.
    *
    * @param value USEC value. The initial value for a new device is 0xFFFF
    * (65,535 decimal). Value is coded using little-endian style..
    * @return unique identifier of sent request
    */
   UUID async_setUSEC(int value);

   /**
    * Sends method call request for setting a unique device Module ID (MID).
    * This can be useful for creating a backup HW of the coordinator device
    * (also see coordinator Backup and Restore). A special encrypted 24 byte
    * long key obtained from device manufacturer is needed. Nevertheless the
    * very last 4 bytes equal to the current MID, and the previous 4 bytes equal
    * to the new MID to be set.
    *
    * @param key value to set
    * @return unique identifier of sent request
    */
   UUID async_setMID(short[] key);

   /**
    * Sends method call request for restarting of tranreceiver module. <br>
    * It is similar to reset (the device starts, RAM and global variables are
    * cleared) except MCU is not reset from the HW point of view (MCU
    * peripherals are not initialized) and RFPGM on reset (when it is enabled)
    * is always skipped.
    *
    * @return unique identifier of sent request
    */
   UUID async_restart();

   /**
    * Sends method call request for writing HWP configuration into module
    * memory.
    *
    * @param configuration configuration memory data to write
    * @return unique identifier of sent request
    */
   UUID async_writeHWPConfiguration(HWP_Configuration configuration);

   /**
    * Sends method call request for writing one byte value to HWP configuration
    * into module memory.
    *
    * @param configBytes config bytes to write
    * @return unique identifier of sent request
    */
   UUID async_writeHWPConfigurationByte(HWP_ConfigurationByte[] configByte);

   /**
    * Loads previously stored code in external memory to MCU Flash memory.
    *
    * @param properties describing loading operation, see
    * {@link LoadingCodeProperties}
    * @return unique identifier of sent request
    */
   UUID async_loadCode(LoadingCodeProperties properties);

   // SYNCHRONOUS WRAPPERS

   /**
    * Synchronous wrapper for {@link #async_read() async_read} method.
    *
    * @return information about module and OS
    */
   OsInfo read();

   /**
    * Synchronous wrapper for {@link #async_reset() async_reset} method.
    *
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType reset();

   /**
    * Synchronous wrapper for {@link
    * #async_readHWPConfiguration() async_readHWPConfiguration} method.
    *
    * @return configuration memory data read.
    */
   HWP_Configuration readHWPConfiguration();

   /**
    * Synchronous wrapper for {@link #async_runRFPGM() async_runRFPGM} method.
    *
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType runRFPGM();

   /**
    * Synchronous wrapper for {@link
    * #async_sleep(com.microrisc.simply.iqrf.dpa.v220.types.SleepInfo) async_sleep
    * }
    * method
    *
    * @param sleepInfo information about sleeping
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType sleep(SleepInfo sleepInfo);

   /**
    * Synchronous wrapper for {@link
    * #async_batch(com.microrisc.simply.iqrf.dpa.v220.types.DPA_Request[])
    * async_runRFPGM} method.
    *
    * @param requests DPA requests to be executed
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType batch(DPA_Request[] requests);

   /**
    * Synchronous wrapper for {@link #async_setUSEC(int) async_setUSEC} method.
    *
    * @param value USEC value. The initial value for a new device is 0xFFFF
    * (65,535 decimal). Value is coded using little-endian style..
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType setUSEC(int value);

   /**
    * Synchronous wrapper for {@link #async_setMID(short[]) async_setMID}
    * method.
    *
    * @param key value to set
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType setMID(short[] key);


   /**
    * Synchronous wrapper for {@link #async_restart() async_restart} method.
    *
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType restart();

   /**
    * Synchronous wrapper for {@link
    * #async_readHWPConfiguration() async_writeHWPConfiguration} method.
    *
    * @param configuration configuration memory data to write
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType writeHWPConfiguration(HWP_Configuration configuration);

   /**
    * Synchronous wrapper for {@link
    * #async_readHWPConfigurationByte() async_writeHWPConfiguration} method.
    *
    * @param configBytes config bytes to write
    * @return {@code VoidType} object, if method call has processed allright
    */
   VoidType writeHWPConfigurationByte(HWP_ConfigurationByte[] configBytes);

   /**
    * Synchronous wrapper for {@link
    * #async_loadCode(com.microrisc.simply.iqrf.dpa.v22x.types.LoadingCodeProperties) async_LoadCode}
    * method.
    *
    * @param properties describing loading operation, see
    * {@link LoadingCodeProperties}
    * @return {@link LoadingResult}
    */
   LoadingResult loadCode(LoadingCodeProperties properties);
}
