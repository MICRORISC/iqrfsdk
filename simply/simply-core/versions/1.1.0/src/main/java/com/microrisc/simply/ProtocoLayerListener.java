package com.microrisc.simply;

/**
 * Listener of data, which comes from protocol layer.
 * 
 * @author Michal Konopa
 */
public interface ProtocoLayerListener {
    /**
     * Will be called, when specified message comes from protocol layer.
     * @param message message, which comes from protocol layer
     */
    public void onGetMessage(AbstractMessage message);
}
