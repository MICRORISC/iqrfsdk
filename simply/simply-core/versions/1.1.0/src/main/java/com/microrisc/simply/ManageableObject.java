

package com.microrisc.simply;

/**
 * Managing and controlling objects life cycles.
 * 
 * @author Michal Konopa
 */
public interface ManageableObject {
    /**
     * Starts object life cycle.
     * @throws SimplyException if some error has occured during starting process
     */
    void start() throws SimplyException;
    
    /**
     * Terminates object life cycle and frees up all used resources.
     */
    void destroy();
}
