package com.microrisc.simply;

/**
 * Base class for message, which comes from underlaying network.
 * 
 * @author Michal Konopa
 */
public abstract class AbstractMessage {
    /** Message effective main data - carrying the result. */
    protected Object mainData;
    
    /** Additional effective data. */
    protected Object additionalData;
    
    
    /**
     * Information about source of this message.
     */
    public static interface MessageSource {
        /**
         * @return network ID
         */
        public String getNetworkId();

        /**
         * @return node ID
         */
        public String getNodeId();
    }
    
    
    /** Message source. */
    protected MessageSource messageSource;
    
    
    /**
     * Protected constructor. 
     * @param mainData effective main data of this message
     * @param additionalData effective additional data of this message
     * @param messageSource source of this message
     */
    protected AbstractMessage(
            Object mainData, Object additionalData, MessageSource messageSource
    ) {
        this.mainData = mainData;
        this.additionalData = additionalData;
        this.messageSource = messageSource;
    }
    
    /**
     * Returns main data.
     * @return main data.
     */
    public Object getMainData() {
        return mainData;
    }
    
    /**
     * Returns additional data.
     * @return additional data.
     */
    public Object getAdditionalData() {
        return additionalData;
    }
    
    /**
     * Returns source of this message.
     * @return source of this message.
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }
    
    @Override
    public String toString() {
        return ("{ " +
                "main data=" + mainData +
                ", additional data=" + additionalData +
                ", source=" + messageSource +
                " }");
    }
}
