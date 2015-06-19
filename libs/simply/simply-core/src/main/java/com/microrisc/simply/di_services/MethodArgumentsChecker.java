/*
 * Copyright 2015 MICRORISC s.r.o..
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

package com.microrisc.simply.di_services;

/**
 * Checker of arguments of generic method calling.
 * The main usage of this class is intened in Device Interfaces's implementation
 * classes, which support {@link GenericAsyncCallable} interface.
 * 
 * @author Michal Konopa
 */
public final class MethodArgumentsChecker {
    /**
     * Checks specified arguments if they have specified types. 
     * Checking is done with corresponding indexes of both arrays.
     * @param args arguments to check
     * @param argTypes required data types of the arguments
     */
    public static void checkArgumentTypes(Object[] args, Class[] argTypes) {
        if ( args == null || args.length == 0 ) {
            if ( argTypes.length == 0 ) {
                return;
            } else {
                throw new IllegalArgumentException(
                        "Arguments number mismatch. "
                        + "Expected: " + argTypes.length +  ", got: 0" 
                );
            }
        }
        
        if ( args.length != argTypes.length ) {
            throw new IllegalArgumentException(
                    "Arguments number mismatch. "
                    + "Expected: " + argTypes.length + ", got: " + args.length
            );
        }
        
        for ( int argId = 0; argId < args.length; argId++ ) {
            if ( !(argTypes[argId].isAssignableFrom(args[argId].getClass())) ) {
                throw new IllegalArgumentException(
                    "Type mismatch by the " + argId + ". argument."
                    + "Expected: " + argTypes[argId].getClass().getName()
                    + ", got: " + args[argId].getClass()
                );
            }
        }
    }
}
