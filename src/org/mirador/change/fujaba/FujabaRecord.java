/* --------------------------------------------------------------------------+
   FujabaRecord.java - Abstract representation of a CoObRA change log record.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Accessors for the fields of every record type are defined here, though most
   are non-functioning. This allows for dynamic binding, alleviating the need
   to determine the record type and typecast it before usage.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;
import ca.dsrg.mirador.change.ChangeRecord;


/**
 * Representation of the common aspects of a CoObRA change log record, becoming
 * the base class for a family of such record types. A record may have from two
 * to eight fields, which in raw form are separated by semicolons.<p>
 *
 * CoObRA (Concurrent Object Replication frAmework) is a framework used by
 * Fujaba for edit undo/redo, object persistency, and model versioning. The
 * various CRecord classes perform the mappings to the raw fields as appropriate
 * for their type. Though accessors for every field appear here, most are non-
 * functioning and are overridden in the relevant derived classes.
 *
 * @since   v0.7 - Feb 2, 2010
 * @author  Stephen Barrett
 */
abstract public class FujabaRecord extends ChangeRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Sets the CoObRA record's common attributes, i.e., the actual change record,
   * which of the models being merged is the record's source, and its type.
   *
   * @param  record_text  String of semicolon delimited fields of textual values.
   * @param  merge_side  Which model change log the record is from.
   */
  public FujabaRecord(String record_text, MergeSide merge_side) {
    super(record_text, merge_side);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the type indicator character of the record.
   *
   * @return  Type of the CoObRA record.
   * @category  getter
   */
  public char getRawType() {
    return raw_type_;
  }


  /**
   * Assigns the type indicator character of the record.
   *
   * @param  raw_type  The type of the CoObRA record.
   * @category  setter
   */
  protected void setRawType(char raw_type) {
    raw_type_ = raw_type;
  }


  /**
   * Gives the type of the record.
   *
   * @return  Type of the CoObRA record.
   * @category  getter
   */
  public CoobraType getRecordType() {
    return record_type_;
  }


  /**
   * Assigns the type of the record.
   *
   * @param  record_type  The type of the CoObRA record.
   * @category  setter
   */
  protected void setRecordType(CoobraType record_type) {
    record_type_ = record_type;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Tokenizes the raw CoObRA record before determining its type.
   */
  protected void parse() {
    raw_fields_.clear();
    tokenizeRecord();
    raw_type_ = parseType();
  }


  /**
   * Extracts the record's type indicator and validates it.
   *
   * @return  Record type indicator.
   */
  protected char parseType() {
    String raw_type = raw_fields_.get(0);
    int raw_len = raw_type.length();
    char type = Character.toLowerCase(raw_type.charAt(0));

    // Assure record is well formed.
    if (VALID_TYPES.indexOf(type) >= 0
        && (raw_len == 1 && type != 'c') || (raw_len == 2 && type == 'c'))
      return type;
    else
      return 0;
  }


  /**
   * Breaks the raw CoObRA record into semicolon delimited tokens. Certain
   * marker characters are stripped from the text before storing the tokens in
   * a list data structure.
   *
   * @see  FujabaRecord#raw_fields_
   */
  protected void tokenizeRecord() {
    int len = record_text_.length();
    int beg = 0;

    while (beg < len) {
      int end = record_text_.indexOf(DELIMITER, beg);
      if (end == -1)
        end = len;

      String token = record_text_.substring(beg, end);
      if (token.contains("#"))
        token = token.replace('#', '_');

      // Remove field value marker.
      if (token.indexOf("v:") == 0) { // Both "v:"...
        token = token.substring(2);

        if (token.indexOf(':') == 0)  // ...and "v::" tags.
          token = token.substring(1);
      }
      // Remove element ID marker.
      else if (token.indexOf("i:") == 0)
        token = token.substring(2);

      raw_fields_.add(token);
      beg = end + 1;
    }
  }


  // Instance data ----------------------------------------------------------
  /** Record type indicator. */
  private char raw_type_ = 0;

  /** Change record type. */
  private CoobraType record_type_;
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  /** Record field separation character. */
  static public final char DELIMITER = ';';

  /** The group of valid record type indicator characters. */
  static public final String VALID_TYPES = "chtuxv#<=>]";

  /** Record type-specific heading, for use with listings. */
  static public final String heading_ = "\nModel change records:";
  // End class data ---------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**
   * Tag values to indicate the type of a CoObRA record.
   *
   * @since   v0.7 - Feb 2, 2010
   * @author  Stephen Barrett
   */
  static public enum CoobraType { HEADER, COMMENT, TRANSACTION, CHANGE }
  // End nested types -------------------------------------------------------
}
