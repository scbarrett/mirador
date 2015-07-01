/* --------------------------------------------------------------------------+
   FujabaRecordFactory.java - Factory class for CoObRA change log record types.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.change.fujaba.FujabaRecord.CoobraType;


/**
 * An uninstantiateable factory class for building concrete instances of the
 * various CoObRA change log record type.
 *
 * @since   v0.8 - Feb 5, 2010
 * @author  Stephen Barrett
 */
public class FujabaRecordFactory {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   * Suppress default constructor to prevent instantiation.
   *
   */
  private FujabaRecordFactory() {}


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Examines a raw CoObRA record in order to determine its type.
   *
   * @param  record_text  String of semicolon delimited fields of textual values.
   * @return  Type of the CoObRA record.
   */
  static protected CoobraType determineRecordType(String record_text) {
    CoobraType type = null;

    if (record_text != null) {
      int pos = record_text.indexOf(FujabaRecord.DELIMITER);

      if (pos > 0) {
        char type_ch = Character.toLowerCase(record_text.charAt(0));

        if (FujabaRecord.VALID_TYPES.indexOf(type_ch) >= 0
            && (pos == 1 && type_ch != 'c') || (pos == 2 && type_ch == 'c')) {

          switch (type_ch) {
            case 'h':
              type = CoobraType.HEADER;
            break;

            case '#':
              type = CoobraType.COMMENT;
            break;

            case 't':
              type = CoobraType.TRANSACTION;
            break;

            case 'c':
              type = CoobraType.CHANGE;
            break;
          }
        }
      }
    }

    return type;
  }


  /**
   * Builds an appropriate representation of a CoObRA change log record as
   * determined by the record's text. The record text is given as one string of
   * semicolon separated fields of textual values.
   *
   * @param  record_text  String of semicolon delimited fields of textual values.
   * @param  merge_side  Which model change log the record is from.
   * @return  The newly constructed CoObRA record, or null if an invalid type.
   */
  static public FujabaRecord makeRecord(String record_text, MergeSide merge_side) {
    FujabaRecord record = null;
    CoobraType type = determineRecordType(record_text);  // Get type indicator.

    // Instantiate the proper record based on the type indicated in the text.
    if (type != null) {
      switch (type) {
        case CHANGE:
          record = new FujabaChangeRecord(record_text, merge_side);
        break;

        case COMMENT:
          record = new FujabaCommentRecord(record_text, merge_side);
        break;

        case HEADER:
          record = new FujabaHeaderRecord(record_text, merge_side);
        break;

        case TRANSACTION:
          record = new FujabaTransactionRecord(record_text, merge_side);
        break;
      }
    }

    return record;
  }
}
