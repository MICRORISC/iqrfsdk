package com.microrisc.simply.iqrf.dpa.v22x.types;

import com.microrisc.simply.Node;
import com.microrisc.simply.iqrf.dpa.v22x.typeconvertors.DPA_RequestConvertor;
import com.microrisc.simply.typeconvertors.ValueConversionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implementation of {@link AbstractFRC_Command} for the most general and
 * universal use.
 *
 * @author Martin Strouhal
 */
public final class FRC_UniversalWithBytes extends AbstractFRC_Command {

   private final int id;
   public static final int FRC_DATA_LENGTH = 64;

   /**
    * Creates new object of {@code FRC_UniversalWithBytes} with specified ID of
    * command and default user data. See the
    * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
    * constructor.
    *
    * @param frc_id ID of FRC command, it should be in range 0 - 255.
    *
    * @throws IllegalArgumentException if is FRC ID incorrect
    */
   public FRC_UniversalWithBytes(int frc_id)    {
      super();
      this.id = checkID(frc_id);
   }

   /**
    * Creates new object of {@code FRC_UniversalWithBytes} with specified ID of
    * command and specified user data.
    *
    * @param frc_id ID of FRC command, it should be in range 0 - 255
    * @param userData user data of FRC command
    * @throws IllegalArgumentException if is FRC ID incorrect or if userData are
    * incorrect {@link AbstractFRC_Command#AbstractFRC_Command(short[]) (more)}
    */
   public FRC_UniversalWithBytes(int frc_id, short[] userData) {
      super(userData);
      this.id = checkID(frc_id);
   }

   /**
    * Creates new object of {@code FRC_UniversalWithBytes} with specified user
    * data and FRC id.
    *   
    * @param frcId ID of FRC command, it should be in range 0 - 255    
    * @param dpaRequest DPA request to take as a user data
    * @throws IllegalArgumentException if an error has occured during conversion
    * of specified DPA request into the series of bytes of user data or if is
    * FRC ID incorrect
    */
   public FRC_UniversalWithBytes(int frcId, DPA_Request dpaRequest) {
      super();
      try {
         this.userData = DPA_RequestConvertor.getInstance().toProtoValue(
                 checkDpaRequest(dpaRequest));
      } catch (ValueConversionException e) {
         throw new IllegalArgumentException(
                 "Conversion of DPA request failed: " + e);
      }
      this.id = checkID(frcId);
   }

    /**
     * Creates new object of {@code FRC_UniversalWithBytes} with specified user
     * data.
     *
     * @param userData user data
     * @param selectedNodes node on which will be command processed
     * @param frcId ID of FRC command, it should be in range 0 - 255
     * @throws IllegalArgumentException if {@code userData} or
     * {@code selectedNodes} is invalid. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command(short[], Node[]) AbstractFRC_Command}
     * constructor.
     */
    public FRC_UniversalWithBytes(int frcId, short[] userData, Node[] selectedNodes) {
        super(userData, selectedNodes);
        this.id = checkID(frcId);
    }

    /**
     * Creates new object of {@code FRC_UniversalWithBytes} with default user
     * data. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     *
     * @param selectedNodes node on which will be command processed
     * @param frcId ID of FRC command, it should be in range 0 - 255
     */
    public FRC_UniversalWithBytes(int frcId, Node[] selectedNodes) {
        super(selectedNodes);
        this.id = checkID(frcId);
    }
   
   private DPA_Request checkDpaRequest(DPA_Request dpaRequest) {
      if (dpaRequest == null) {
         throw new IllegalArgumentException("DPA request cannot be null");
      }
      return dpaRequest;
   }

   private int checkID(int id) {
      if (id < 0) {
         throw new IllegalArgumentException("FRC ID cannot be negative.");
      } else if (id > 255) {
         throw new IllegalArgumentException("FRC ID cannot be greater than 255");
      } else {
         return id;
      }
   }

   public static interface Result extends FRC_CollectedBytes {
   }

   public static class ResultImpl implements Result {

      private final short byteValue;

      public ResultImpl(short byteValue) {
         this.byteValue = byteValue;
      }

      @Override
      public short getByte() {
         return byteValue;
      }
   }

   /**
    * Parses specified FRC data comming from IQRF.
    *
    * @param frcData FRC data to parse
    * @return map of results for each node. Identifiers of nodes are used as a
    * keys of the returned map.
    * @throws IllegalArgumentException if specified FRC data are not in correct
    * format
    * @throws Exception if parsing failed
    */
   public static Map<String, FRC_UniversalWithBytes.Result> parse(
           short[] frcData) throws Exception {
      checkFrcData(frcData);

      Map<String, FRC_UniversalWithBytes.ResultImpl> resultImplMap = null;
      try {
         resultImplMap = FRC_ResultParser.parseAsCollectedBytes(frcData,
                 FRC_UniversalWithBytes.ResultImpl.class);
      } catch (Exception ex) {
         throw new Exception("Parsing failed: " + ex);
      }

      Map<String, FRC_UniversalWithBytes.Result> resultMap = new HashMap<>();
      for (Map.Entry<String, FRC_UniversalWithBytes.ResultImpl> resImplEntry : resultImplMap.entrySet()) {
         resultMap.put(resImplEntry.getKey(), resImplEntry.getValue());
      }
      return resultMap;
   }

   /**
    * Parses specified FRC data comming from IQRF into easiest map in this case
    * - Map<String, Short>.
    *
    * @param frcData FRC data to parse
    * @return map of results for each node. Identifiers of nodes are used as a
    * keys of the returned map.
    * @throws IllegalArgumentException if specified FRC data are not in correct
    * format
    * @throws Exception if parsing failed
    */
   public static Map<String, Short> parseIntoShort(
           short[] frcData) throws Exception {
      checkFrcData(frcData);

      Map<String, FRC_UniversalWithBytes.Result> resultImplMap = parse(frcData);
      Map<String, Short> resultEasyMap = new HashMap<>();

      for (Entry<String, FRC_UniversalWithBytes.Result> e : resultImplMap.entrySet()) {
         String key = e.getKey();
         FRC_UniversalWithBytes.Result value = e.getValue();

         resultEasyMap.put(key, value.getByte());
      }

      return resultEasyMap;
   }

   private static short[] checkFrcData(short[] frcData) {
      if (frcData == null) {
         throw new IllegalArgumentException(
                 "FRC data to parse cannot be null");
      }

      if (frcData.length != FRC_DATA_LENGTH) {
         throw new IllegalArgumentException(
                 "Invalid length of FRC data. Expected: " + FRC_DATA_LENGTH
                 + ", got: " + frcData.length);
      }
      return frcData;
   }

   @Override
   public int getId() {
      return id;
   }

   @Override
   public short[] getUserData() {
      return userData;
   }
}
