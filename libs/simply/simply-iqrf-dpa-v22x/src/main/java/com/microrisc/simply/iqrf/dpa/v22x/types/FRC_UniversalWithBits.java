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
public final class FRC_UniversalWithBits extends AbstractFRC_Command {

   private final int id;
   public static final int FRC_DATA_LENGTH = 64;

   /**
    * Creates new object of {@code FRC_UniversalWithBits} with specified ID of
    * command and default user data. See the
    * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
    * constructor.
    *
    * @param frc_id ID of FRC command, it should be in range 0 - 255.
    *
    * @throws IllegalArgumentException if is FRC ID incorrect
    */
   public FRC_UniversalWithBits(int frc_id) {
      super();
      this.id = checkID(frc_id);
   }

   /**
    * Creates new object of {@code FRC_UniversalWithBits} with specified ID of
    * command and specified user data.
    *
    * @param frc_id ID of FRC command, it should be in range 0 - 255
    * @param userData user data of FRC command
    * @throws IllegalArgumentException if is FRC ID incorrect or if userData are
    * incorrect {@link AbstractFRC_Command#AbstractFRC_Command(short[]) (more)}
    */
   public FRC_UniversalWithBits(int frc_id, short[] userData) {
      super(userData);
      this.id = checkID(frc_id);
   }

   /**
    * Creates new object of {@code FRC_UniversalWithBits} with specified user
    * data and FRC id.
    *
    * @param dpaRequest DPA request to take as a user data
    * @param id ID of FRC command, it should be in range 0 - 255
    * @throws IllegalArgumentException if an error has occured during conversion
    * of specified DPA request into the series of bytes of user data or if is
    * FRC ID incorrect
    */
   public FRC_UniversalWithBits(DPA_Request dpaRequest, int id) {
      super();
      try {
         this.userData = DPA_RequestConvertor.getInstance().toProtoValue(
                 checkDpaRequest(dpaRequest));
      } catch (ValueConversionException e) {
         throw new IllegalArgumentException(
                 "Conversion of DPA request failed: " + e);
      }
      this.id = checkID(id);
   }

    /**
     * Creates new object of {@code FRC_UniversalWithBits} with specified user
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
    public FRC_UniversalWithBits(int frcId, short[] userData, Node[] selectedNodes) {
        super(userData, selectedNodes);
        this.id = checkID(frcId);
    }

    /**
     * Creates new object of {@code FRC_UniversalWithBits} with default user
     * data. See the
     * {@link AbstractFRC_Command#AbstractFRC_Command() AbstractFRC_Command}
     * constructor.
     *
     * @param selectedNodes node on which will be command processed
     * @param frcId ID of FRC command, it should be in range 0 - 255
     */
    public FRC_UniversalWithBits(int frcId, Node[] selectedNodes) {
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

   /**
    * Provides acces to parsed FRC data comming from IQRF.
    */
   public static interface Result extends FRC_CollectedBits {

      /**
       * Return all bits merged into one byte.
       *
       * @return all bits in 1 byte
       */
      byte getBitsInByte();
   }

   /** Parsed FRC data comming from IQRF. */
   public static class ResultImpl implements Result {

      private final byte bit0;
      private final byte bit1;
      
      public ResultImpl(byte bit0, byte bit1) {
         this.bit0 = bit0;
         this.bit1 = bit1;
      }

      @Override
      public byte getBit0() {
         return bit0;
      }

      @Override
      public byte getBit1() {
         return bit1;
      }

      @Override
      public byte getBitsInByte() {
         byte resultByte = bit1;
         resultByte <<= 1;
         resultByte += bit0;
         return resultByte;
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
   public static Map<String, FRC_UniversalWithBits.Result> parse(
           short[] frcData) throws Exception {
      checkFrcData(frcData);

      Map<String, ResultImpl> resultImplMap = null;
      try {
         resultImplMap = FRC_ResultParser.parseAsCollectedBits(frcData, ResultImpl.class);
      } catch (Exception ex) {
         throw new Exception("Parsing failed: " + ex);
      }

      Map<String, FRC_UniversalWithBits.Result> resultMap = new HashMap<>();
      for (Map.Entry<String, ResultImpl> resImplEntry : resultImplMap.entrySet()) {
         resultMap.put(resImplEntry.getKey(), resImplEntry.getValue());
      }
      return resultMap;
   }

   /**
    * Parses specified FRC data comming from IQRF into easiest map in this case
    * - Map<String, Byte>. For info about merging bits see {@link Result#getBitsInByte()
    * } which is used in this case.
    *
    * @param frcData FRC data to parse
    * @return map of results for each node. Identifiers of nodes are used as a
    * keys of the returned map.
    * @throws IllegalArgumentException if specified FRC data are not in correct
    * format
    * @throws Exception if parsing failed
    */
   public static Map<String, Byte> parseIntoByte(
           short[] frcData) throws Exception {
      checkFrcData(frcData);

      Map<String, FRC_UniversalWithBits.Result> resultImplMap = parse(frcData);
      Map<String, Byte> resultEasyMap = new HashMap<>();

      for (Entry<String, FRC_UniversalWithBits.Result> e : resultImplMap.entrySet()) {
         String key = e.getKey();
         FRC_UniversalWithBits.Result value = e.getValue();

         resultEasyMap.put(key, value.getBitsInByte());
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
