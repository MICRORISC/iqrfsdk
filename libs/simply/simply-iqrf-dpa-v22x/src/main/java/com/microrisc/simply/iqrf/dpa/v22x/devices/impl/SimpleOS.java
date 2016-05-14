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

package com.microrisc.simply.iqrf.dpa.v22x.devices.impl;

import com.microrisc.simply.CallRequestProcessingInfoContainer;
import com.microrisc.simply.ConnectorService;
import com.microrisc.simply.di_services.MethodArgumentsChecker;
import com.microrisc.simply.iqrf.dpa.v22x.DPA_DeviceObject;
import com.microrisc.simply.iqrf.dpa.v22x.devices.OS;
import com.microrisc.simply.iqrf.dpa.v22x.di_services.method_id_transformers.OSStandardTransformer;
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
 * Simple {@code OS} implementation.
 * 
 * @author Michal Konopa
 * @author Martin Strouhal
 */
//JUNE-2015 - implemented restart
//SEPTEMBER 2015 - implemented write HWP config, write HWP config byte
//MARCH 2016 - implemented load code
//MAY2016 - updated WriteHWPConfigByte
public final class SimpleOS 
extends DPA_DeviceObject implements OS {
    
    private static final int USER_ADDR_LOWER_BOUND = 0x00;
    private static final int USER_ADDR_UPPER_BOUND = 0xFFFF;
    
    private static int checkUserAddress(int userAddress) {
        if ( (userAddress < USER_ADDR_LOWER_BOUND) || (userAddress > USER_ADDR_UPPER_BOUND) ) {
            throw new IllegalArgumentException("User address out of bounds");
        }
        return userAddress;
    }
    
    // MID key length
    private static final int MID_KEY_LENGTH = 24; 
    
    private static void checkKey(short[] key) {
        if ( key == null ) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if ( key.length != MID_KEY_LENGTH ) {
            throw new IllegalArgumentException(
                    "Invalid key length. Expected: " + MID_KEY_LENGTH
            );
        }
    }
    
    private static void checkLoadingCodeProperties(LoadingCodeProperties prop){
       if(prop == null){
          throw new IllegalArgumentException("Properties cannot be null.");
       }
    }
    
    
    public SimpleOS(String networkId, String nodeId, ConnectorService connector, 
            CallRequestProcessingInfoContainer resultsContainer
    ) {
        super(networkId, nodeId, connector, resultsContainer);
    }
    
    @Override
    public UUID call(Object methodId, Object[] args) {
        String methodIdStr = transform((OS.MethodID) methodId);
        if ( methodIdStr == null ) {
            return null;
        }
        
        switch ( (OS.MethodID)methodId ) {
            case READ:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() },
                        getDefaultWaitingTimeout()
                );
            case RESET:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr,
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            case READ_HWP_CONFIGURATION:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            case RUN_RFPGM:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                );
            case SLEEP:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { SleepInfo.class } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (SleepInfo) args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case BATCH:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { DPA_Request[].class } );
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (DPA_Request[]) args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case SET_USEC:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { Integer.class } );
                checkUserAddress((Integer)args[0]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (Integer) args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case SET_MID:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { short[].class } );
                checkKey((short[])args[0]);
                return dispatchCall(
                        methodIdStr, 
                        new Object[] { getRequestHwProfile(), (short[]) args[0] }, 
                        getDefaultWaitingTimeout()
                );
            case RESTART:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[] { } );
                return dispatchCall(
                        methodIdStr,
                        new Object[] { getRequestHwProfile() }, 
                        getDefaultWaitingTimeout()
                ); 
            case WRITE_HWP_CONFIGURATION:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[]{HWP_Configuration.class});
                 return dispatchCall(
                        methodIdStr,
                        new Object[] { getRequestHwProfile(), (HWP_Configuration) args[0] }, 
                        getDefaultWaitingTimeout()
                ); 
            case WRITE_HWP_CONFIGURATION_BYTE:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[]{Integer.class, Integer.class});
                 return dispatchCall(
                        methodIdStr,
                        new Object[] { getRequestHwProfile(), (Integer) args[0], (Integer) args[1] }, 
                        getDefaultWaitingTimeout()
                ); 
            case LOAD_CODE:
                MethodArgumentsChecker.checkArgumentTypes(args, new Class[]{LoadingCodeProperties.class});
                 return dispatchCall(
                        methodIdStr,
                        new Object[] { getRequestHwProfile(), (LoadingCodeProperties) args[0] }, 
                        getDefaultWaitingTimeout()
                );
            default:
                throw new IllegalArgumentException("Unsupported command: " + methodId);
        }
    }
    
    @Override
    public String transform(Object methodId) {
        return OSStandardTransformer.getInstance().transform(methodId);
    }
    
    
    
    // ASYNCHRONOUS METHODS IMPLEMENTATIONS
    
    @Override
    public UUID async_read() {
        return dispatchCall(
                "1", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_reset() {
        return dispatchCall(
                "2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_readHWPConfiguration() {
        return dispatchCall(
                "3", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_runRFPGM() {
        return dispatchCall(
                "4", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_sleep(SleepInfo sleepInfo) {
        return dispatchCall(
                "5", new Object[] { getRequestHwProfile(), sleepInfo }, 
                getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_batch(DPA_Request[] requests) {
        return dispatchCall(
                "6", new Object[] { getRequestHwProfile(), requests }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_setUSEC(int value) {
        checkUserAddress(value);
        return dispatchCall(
                "7", new Object[] { getRequestHwProfile(), value }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_setMID(short[] key) {
        checkKey(key);
        return dispatchCall(
                "8", new Object[] { getRequestHwProfile(), key }, getDefaultWaitingTimeout() 
        );
    }

    @Override
    public UUID async_restart(){        
        return dispatchCall(
                "9", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_writeHWPConfiguration(HWP_Configuration configuration) {
        return dispatchCall(
                "10", new Object[] { getRequestHwProfile(), configuration }, getDefaultWaitingTimeout() 
        );
    }
    
    @Override
    public UUID async_writeHWPConfigurationByte(HWP_ConfigurationByte[] configBytes) {
        return dispatchCall(
                "11", new Object[] { getRequestHwProfile(), configBytes }, getDefaultWaitingTimeout() 
        );
    }
    
   @Override
   public UUID async_loadCode(LoadingCodeProperties properties) {
      checkLoadingCodeProperties(properties);
      return dispatchCall("12", new Object[]{getRequestHwProfile(),
         properties}, getDefaultWaitingTimeout()
      );
   }
    
    
    // SYNCHRONOUS WRAPPERS IMPLEMENTATIONS
    
    @Override
    public OsInfo read() {
        UUID uid = dispatchCall(
                "1", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, OsInfo.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public VoidType reset() {
        UUID uid = dispatchCall(
                "2", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public HWP_Configuration readHWPConfiguration() {
        UUID uid = dispatchCall(
                "3", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, HWP_Configuration.class, getDefaultWaitingTimeout() );
    }
 
    @Override
    public VoidType runRFPGM() {
        UUID uid = dispatchCall(
                "4", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }

    @Override
    public VoidType sleep(SleepInfo sleepInfo) {
        UUID uid = dispatchCall(
                "5", new Object[] { getRequestHwProfile(), sleepInfo }, 
                getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType batch(DPA_Request[] requests) {
        UUID uid = dispatchCall(
                "6", new Object[] { getRequestHwProfile(), requests }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType setUSEC(int value) {
        checkUserAddress(value);
        UUID uid = dispatchCall(
                "7", new Object[] { getRequestHwProfile(), value }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType setMID(short[] key) {
        checkKey(key);
        UUID uid = dispatchCall(
                "8", new Object[] { getRequestHwProfile(), key }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType restart(){
        UUID uid = dispatchCall(
                "9", new Object[] { getRequestHwProfile() }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout());
    }
    
    @Override
    public VoidType writeHWPConfiguration(HWP_Configuration configuration) {
        UUID uid = dispatchCall(
                "10", new Object[] { getRequestHwProfile(), configuration }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }
    
    @Override
    public VoidType writeHWPConfigurationByte(HWP_ConfigurationByte[] configBytes) {
        UUID uid = dispatchCall(
                "11", new Object[] { getRequestHwProfile(), configBytes }, getDefaultWaitingTimeout() 
        );
        if ( uid == null ) {
            return null;
        }
        return getCallResult(uid, VoidType.class, getDefaultWaitingTimeout() );
    }

   @Override
   public LoadingResult loadCode(LoadingCodeProperties properties) {
      checkLoadingCodeProperties(properties);
      UUID uid = dispatchCall("12", new Object[]{getRequestHwProfile(),
         properties}, getDefaultWaitingTimeout()
      );
      if (uid == null) {
         return null;
      }
      return getCallResult(uid, LoadingResult.class, getDefaultWaitingTimeout());
   }
}
