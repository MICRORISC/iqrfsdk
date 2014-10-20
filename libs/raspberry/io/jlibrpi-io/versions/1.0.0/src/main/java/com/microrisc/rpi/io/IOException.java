
package com.microrisc.rpi.io;

/**
 * Exceptions, which occurs during IO communication. 
 * 
 * @author Michal Konopa
 */
public class IOException extends Exception {
    public IOException(String descr) {
        super(descr);
    }
}
