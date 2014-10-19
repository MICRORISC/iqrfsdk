
package com.microrisc.simply.iqrf.dpa.v201.types;

/**
 * Encapsulates information about IO delay settings.
 * 
 * @author Michal Konopa
 */
public final class IO_DelaySettings implements IO_Command {
    /** Delay value [in ms]. */
    private final int delay;
    
   
    /**
     * Creates new objects encapsulating IO delay value settings.
     * @param delay delay value [in ms]
     */
    public IO_DelaySettings(int delay) {
        this.delay = delay;
    }

    /**
     * @return delay [in ms]
     */
    public int getDelay() {
        return delay;
    }

    @Override
    public int getFirstField() {
        return 0xFF;
    }

    @Override
    public int getSecondField() {
        return (delay & 0xFF);
    }

    @Override
    public int getThirdField() {
        return ((delay & 0xFF00) >> 8);
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" Delay: " + delay + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
}
