
package com.microrisc.simply.iqrf.dpa.types;

/**
 * Encapsulates information about IO commands.
 *  
 * @author Michal Konopa
 */
public interface IO_Command {
    
    int getFirstField();
    
    int getSecondField();
    
    int getThirdField();
}
