

package com.microrisc.simply.iqrf.dpa.v210.types;

import com.microrisc.simply.iqrf.dpa.v210.DPA_ResponseCode;

/**
 * Encapsulates additional information comming from DPA network messages.  
 * 
 * @author Michal Konopa
 */
public final class DPA_AdditionalInfo {
    /** HW profile. */
    private final int hwProfile;
    
    /** Response code. */
    private final DPA_ResponseCode responseCode;
    
    /** DPA value. */
    private final int dpaValue;
    
    
    /**
     * Creates new additional info encapsulating specified informations.
     * @param hwProfile HW profile
     * @param responseCode response code
     * @param dpaValue DPA value
     */
    public DPA_AdditionalInfo(int hwProfile, DPA_ResponseCode responseCode, int dpaValue) 
    {
        this.hwProfile = hwProfile;
        this.responseCode = responseCode;
        this.dpaValue = dpaValue;
    }
    
    /**
     * Returns HW profile relating to the last incomming result.
     * @return HW profile relating to the last incomming result.
     */
    public int getHwProfile() {
        return hwProfile;
    }
    
    /**
     * Returns DPA value relating to the last incomming result.
     * @return DPA value relating to the last incomming result.
     */
    public int getDPA_Value() {
        return dpaValue;
    }
    
    /**
     * Returns response code relating to the last incomming result.
     * @return response code relating to the last incomming result.
     */
    public DPA_ResponseCode getResponseCode() {
        return responseCode;
    }
    
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append(this.getClass().getSimpleName() + " { " + NEW_LINE);
        strBuilder.append(" HW profile ID: " + hwProfile + NEW_LINE);
        strBuilder.append(" Response code: " + responseCode + NEW_LINE);
        strBuilder.append(" DPA value: " + dpaValue + NEW_LINE);
        strBuilder.append("}");
        
        return strBuilder.toString();
    }
    
    public String toPrettyFormattedString() {
        StringBuilder strBuilder = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        
        strBuilder.append("HW profile ID: " + hwProfile + NEW_LINE);
        strBuilder.append("Response code: " + responseCode + NEW_LINE);
        strBuilder.append("DPA value: " + dpaValue + NEW_LINE);
        
        return strBuilder.toString();
    }
    
}
