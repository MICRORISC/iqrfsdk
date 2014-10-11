
package com.microrisc.rpi.spi.iqrf;

import com.microrisc.rpi.spi.SPI_Exception;

/**
 * SPI Master.
 * 
 * @author Michal Konopa
 */
public interface SPI_Master {
    /**
     * Returns SPI status information of slave.
     * @return SPI status information of slave.
     * @throws SPI_Exception, if some communication error has encountered
     */
    SPI_Status getSlaveStatus() throws SPI_Exception;
    
    /**
     * Sends specified data to slave.
     * @param data data to be sent to slave
     * @throws SPI_Exception, if some communication error has encountered
     */
    void sendData(short[] data) throws SPI_Exception;
    
    /**
     * Reads data from slave.
     * @param dataLen length of data to read
     * @return data read from slave.
     * @throws SPI_Exception, if some communication error has encountered
     */
    short[] readData(int dataLen) throws SPI_Exception;
    
    /**
     * Terminates communication with slave and frees up used resources.
     */
    void destroy();
}
