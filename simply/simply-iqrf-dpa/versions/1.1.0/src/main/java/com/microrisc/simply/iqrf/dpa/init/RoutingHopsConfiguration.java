
package com.microrisc.simply.iqrf.dpa.init;

/**
 * Configuration of routing hops.
 * 
 * @author Michal Konopa
 */
public class RoutingHopsConfiguration {
    /** Default value of request hops number. */
    public static int DEFAULT_REQUEST_HOPS = 0;
    
    /** Default value of response hops number. */
    public static int DEFAULT_RESPONSE_HOPS = 0;
    
    
    /** Request hops number. */
    private int requestHops;
    
    /** Request hops number. */
    private int responseHops;
    
    
    private int checkRequestHops(int requestHops) {
        if ( (requestHops < 0) || (requestHops > 0xFF) ) {
            throw new IllegalArgumentException(
                "Request hops must be in interval [0x00 - 0xFF]"
            );
        }
        return requestHops;
    }
    
    private int checkResponseHops(int responseHops) {
        if ( (responseHops < 0) || (responseHops > 0xFF) ) {
            throw new IllegalArgumentException(
                "Response hops must be in interval [0x00 - 0xFF]"
            );
        }
        return responseHops;
    }
    
    
    
    /**
     * Creates new object of routing hops configuration.
     * @param requestHops number of request hops
     * @param responseHops number of response hops
     */
    public RoutingHopsConfiguration(int requestHops, int responseHops) {
        this.requestHops = checkRequestHops(requestHops);
        this.responseHops = checkResponseHops(responseHops);
    }

    /**
     * @return number of request hops
     */
    public int getRequestHops() {
        return requestHops;
    }

    /**
     * @return number of response hops
     */
    public int getResponseHops() {
        return responseHops;
    }
    
}
