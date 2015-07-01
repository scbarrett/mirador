/* --------------------------------------------------------------------------+
   FujabaHeaderRecord.java - Header type of CoObRA change log record.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;


/**
 * The CoObRA change log, header record. See "EBNF for the CoObRA Data Format."
 * <pre>h;metadata</pre>
 *
 * @since   v0.8 - Feb 5, 2010
 * @author  Stephen Barrett
 */
public class FujabaHeaderRecord extends FujabaRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * In addition to the common attributes set through it's super constructor,
   * sets the attributes specific to this CoObRA record type.
   *
   * @param  record_text  String of semicolon delimited fields of textual values.
   * @param  merge_side  Which model change log the record is from.
   */
  public FujabaHeaderRecord(String record_text, MergeSide merge_side) {
    super(record_text, merge_side);
    setRecordType(CoobraType.HEADER);
    parse();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the metadata of the record.
   *
   * @return  Metadata of the CoObRA record.
   * @category  getter
   */
  public String getMetadata() {
    StringBuilder meta_string = new StringBuilder();

    // Every field after record type flag is metadata.
    for (int i = 1, sz = raw_fields_.size(); i < sz; ++i)
      meta_string.append(raw_fields_.get(i) + ';');

    // Strip away last delimiter.
    return (meta_string.lastIndexOf(";") >= 0)
        ? meta_string.substring(0, meta_string.length() - 1)
        : meta_string.toString();
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
  static public final String heading_ = "\nHeader model records:";
  // End class data ---------------------------------------------------------
}
