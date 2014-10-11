

package com.microrisc.simply.iqrf.dpa.di_services;

/**
 * Manipulation of HW profile value of requests.
 * 
 * @author Michal Konopa
 */
public interface HwProfileService {
    /**
     * Sets HW profile on specified value, which will be used for all next
     * requests.
     * @param hwProfile new HW profile value for all next requests 
     */
    void setRequestHwProfile(int hwProfile);
    
    /**
     * Returns actual HW profile value of all next requests.
     * @return actual HW profile value of all next requests
     */
    int getRequestHwProfile();
}
