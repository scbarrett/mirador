/* --------------------------------------------------------------------------+
   FujabaTransactionRecord.java - Transaction type of CoObRA change log record.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;


/**
 * The CoObRA change log, transaction record. See "EBNF for the CoObRA Data
 * Format."
 * <pre>t;tx-id;tx-name;tx-time;tx-over;(modifier);</pre>
 *
 * @since   v0.8 - Feb 5, 2010
 * @author  Stephen Barrett
 */
public class FujabaTransactionRecord extends FujabaRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * In addition to the common attributes set through it's super constructor,
   * sets the attributes specific to this CoObRA record type.
   *
   * @param  record_text  String of semicolon delimited fields of textual values.
   * @param  merge_side  Which model change log the record is from.
   */
  public FujabaTransactionRecord(String record_text, MergeSide merge_side) {
    super(record_text, merge_side);
    setRecordType(CoobraType.TRANSACTION);
    parse();
    element_id_ = raw_fields_.get(1);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the versioning modifier source of the record.
   *
   * @return  Modifier source of the CoObRA record.
   * @category  getter
   */
  public String getModifier() {
    return raw_fields_.get(5);
  }


  /**
   * Gives the transaction name of the record.
   *
   * @return  Transaction name of the CoObRA record.
   * @category  getter
   */
  public String getName() {
    return raw_fields_.get(2);
  }


  /**
   * Gives the ID of the over-arching transaction the record belongs to.
   *
   * @return  Over transaction ID of the CoObRA record.
   * @category  getter
   */
  public String getOverTransactionId() {
    return raw_fields_.get(4);
  }


  /**
   * Gives the time of the transaction the record belongs to.
   *
   * @return  Transaction time of the CoObRA record.
   * @category  getter
   */
  public String getTransactionTime() {
    return raw_fields_.get(3);
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


  // Class data -------------------------------------------------------------
  /** Record type-specific heading, for use with listings. */
  static public final String heading_ = "\nTransaction model records:";
  // End class data ---------------------------------------------------------
}
