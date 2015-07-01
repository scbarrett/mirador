/* --------------------------------------------------------------------------+
   FujabaCommentRecord.java - Comment type of CoObRA change log record.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;


/**
 * The CoObRA change log, comment record. See "EBNF for the CoObRA Data Format."
 * <pre>#;comment</pre>
 *
 * @since   v0.8 - Feb 5, 2010
 * @author  Stephen Barrett
 */
public class FujabaCommentRecord extends FujabaRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * In addition to the common attributes set through it's super constructor,
   * sets the attributes specific to this CoObRA record type.
   *
   * @param  record_text  String of semicolon delimited fields of textual values.
   * @param  merge_side  Which model change log the record is from.
   */
  public FujabaCommentRecord(String record_text, MergeSide merge_side) {
    super(record_text, merge_side);
    setRecordType(CoobraType.COMMENT);
    parse();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the comment field from the raw record.
   *
   * @return  Comment of the CoObRA record.
   * @category  getter
   */
  public String getComment() {
    if (raw_fields_.size() > 1)
      return raw_fields_.get(1);
    else
      return "";
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  /**
   * Returns the object as a string.
   *
   * @return  String representation of object.
   */
  @Override public String toString() {
    return record_text_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Breaks the raw CoObRA record into semicolon delimited tokens. Certain
   * marker characters are stripped from the text before storing the tokens in
   * a list data structure.
   *
   * @see  FujabaRecord#raw_fields_
   */
  @Override protected void tokenizeRecord() {
    int len = record_text_.length();

    if (record_text_.indexOf(DELIMITER) == 1) {
      String token = record_text_.substring(0, 1);
      raw_fields_.add(token);  // Store record type flag.

      if (len > 2)  // Everything after first delimiter is a comment.
        token = record_text_.substring(2, len);
      else
        token = "";

      raw_fields_.add(token);
    }
  }


  // Class data -------------------------------------------------------------
  /** Record type-specific heading, for use with listings. */
  static public final String heading_ = "\nComment change records:";
  // End class data ---------------------------------------------------------
}
