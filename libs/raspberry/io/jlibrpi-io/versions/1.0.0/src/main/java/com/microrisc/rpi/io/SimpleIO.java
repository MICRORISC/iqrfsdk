package com.microrisc.rpi.io;

/**
 * Simple implementation of {@code IO} interface.
 * 
 * @author Rostislav Spinar
 */
public final class SimpleIO implements IO {
    // native methods
    private native void stub_init();
    private native void stub_set(int port, int direction);
    private native void stub_write(int port, int value);
    private native int stub_read(int port);
    private native void stub_resetTR();
    private native void stub_destroy();
    
    
    /**
     * Loads supporting Java Stub .so library.
     */
    static {
        System.loadLibrary("rpi_io_javastub");
    }
    
    public SimpleIO() throws IOException {
        stub_init();
    }
    
    public void set(IO.Port port, IO.Direction direction) throws IOException {
        stub_set(port.getIntValue(), direction.getIntValue());
    }

    public void write(IO.Port port, IO.Level value) throws IOException {
        stub_write(port.getIntValue(), value.getIntValue());
    }

    public int read(IO.Port port) throws IOException {
        return stub_read(port.getIntValue());
    }
    
    public void resetTR() throws IOException {
        stub_resetTR();
    }
    
    public void destroy() {
        try {
            stub_destroy();
        } catch (Exception e) {
            System.err.println("Error while destroying IO: " + e.getMessage());
        }
    }
}
