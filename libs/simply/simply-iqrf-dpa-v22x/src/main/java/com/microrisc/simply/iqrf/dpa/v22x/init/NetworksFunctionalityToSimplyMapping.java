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

package com.microrisc.simply.iqrf.dpa.v22x.init;

import java.util.Map;
import java.util.Set;

/**
 * Mapping of IQRF networks functionality into Simply structural objects ( networks,
 * nodes ).   
 * 
 * @author Michal Konopa
 */
public interface NetworksFunctionalityToSimplyMapping {
    /**
     * Returns complete mapping of IQRF networks functionality into Simply
     * structural objects.
     * <p>
     * Indexes of outer mapping represent networks IDs, corresponding values
     * represent mappings of nodes functionality on that networks. <br>
     * Indexes of inner mappings represent nodes IDs, corresponding values
     * represent sets of supported periperals on that nodes.
     * @return complete mapping of IQRF network functionality into Simply
     *         structural objects
     */
    Map<String, Map<String, Set<Integer>>> getMapping();
}
