/*
 * Copyright 2015 MICRORISC s.r.o.
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
package com.microrisc.simply.iqrf.dpa.v22x.examples.user_per.autonetwork_embedded.def;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Strouhal
 */
public class AutonetworkState {
    
    private AutonetworkStateType type;
    private List<Integer> additionalData = new LinkedList<>();
    
    public AutonetworkState(AutonetworkStateType type){
        this.type = type;
    }
    
    public void addAditionalData(int dataToAdd){
        additionalData.add(dataToAdd);
    }

    @Override
    public String toString() {
        int countOfDataInInfo = type.getInfo().length() - type.getInfo().compareTo("{");
        String newString = "";
        
        if(additionalData.size() != countOfDataInInfo){
            newString += "Warn: Count of parameters in info isn't equals with count of parameters in Autnetworkstate \n";
        }
        
        newString += type.getInfo();
        while(newString.contains("{") && !additionalData.isEmpty()){
            newString = newString.replaceFirst("{}", additionalData.remove(0).toString());
        }
        
        return newString;
    }
    
    
}
