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

package com.microrisc.simply.iqrf.dpa.v22x.types;

/**
 * Encapsulates information about routing hops in IQMESH network.
 * 
 * @author Michal Konopa
 */
public final class RoutingHops {
    /** Request hops. */
    private final int requestHops;
    
    /** Response hops. */
    private final int responseHops;
    
    /** Hops lower bound. */
    public static final int HOPS_LOWER_BOUND = 0x00; 
    
    /** Hops upper bound. */
    public static final int HOPS_UPPER_BOUND = 0xFF; 
    
    private static int checkHops(int hops) {
        if ( (hops < HOPS_LOWER_BOUND) || (hops > HOPS_UPPER_BOUND) ) {
            throw new IllegalArgumentException("Request hops out of bounds");
        }
        return hops;
    }
    
    /**
     * Creates new object encapsulating routing hops informations.
     * @param requestHops request hops
     * @param responseHops response hops
     * @throws IllegalArgumentException if specified request hops or response hops
     *         are out of [{@code HOPS_LOWER_BOUND}..{@code HOPS_UPPER_BOUND}] interval
     */
    public RoutingHops(int requestHops, int responseHops) {
        this.requestHops = checkHops(requestHops);
        this.responseHops = checkHops(responseHops);
    }

    /**
     * @return the request hops
     */
    public int getRequestHops() {
        return requestHops;
    }

    /**
     * @return the response hops
     */
    public int getResponseHops() {
        return responseHops;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Request hops: " + requestHops + NEW_LINE);
        strBuilder.append(" Response hops: " + responseHops + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormattedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("Request hops: " + requestHops + NEW_LINE);
        strBuilder.append("Response hops: " + responseHops + NEW_LINE);
        
        return strBuilder.toString();
    }
}
