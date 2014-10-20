
package com.microrisc.rpi.spi;

/**
 * Base class of exceptions, which occurs during SPI communication. 
 * 
 * @author Michal Konopa
 */
public class SPI_Exception extends java.lang.Exception {
    public SPI_Exception(String descr) {
        super(descr);
    }
}
