/* --------------------------------------------------------------------------+
   ChangeRecord.java - Abstract representation of a model change log record.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Situated at the top of the record hierarchy, a ChangeRecord provides an
   interface to the most common properties of a model change operation.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change;
import java.util.ArrayList;
import java.util.List;


/**
 * Minimal representation of a model change log record.
 *
 * @since   v0.29 - Jul 20, 2010
 * @author  Stephen Barrett
 */
abstract public class ChangeRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Stores the raw change record before parsing into individual fields. Parsing
   * is actually done by a meta-model specific derived class.
   *
   * @param  record_text  Raw change record as a string.
   * @param  merge_side  Which side of a merge the change record is from.
   */
  public ChangeRecord(String record_text, MergeSide merge_side) {
    record_text_ = new String(record_text);
    merge_side_ = merge_side;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the ID of the model element changed by the record.
   *
   * @return  ID of the affected model element.
   * @category  getter
   */
  public String getElementId() {
    return element_id_;
  }


  /**
   * Gives the type of change to be effected by the record.
   *
   * @return  What is returned by the method.
   */
  public ElementType getElementType() {
    return element_type_;
  }


  /**
   * Gives which of the models being merged the change was executed on.
   *
   * @return  Which side of a merge the change record is from.
   * @category  getter
   */
  public MergeSide getMergeSide() {
    return merge_side_;
  }


  /**
   * Assigns which of the models being merged the change was executed on.
   *
   * @param  merge_side  Which side of a merge the change record is from.
   * @category  setter
   */
  public void setMergeSide(MergeSide merge_side) {
    merge_side_ = merge_side;
  }


  /**
   * Gives a list containing the individual fields of the CoObRA record.
   *
   * @return  Fields of the raw CoObRA record.
   * @category  getter
   */
  public List<String> getRawFields() {
    return raw_fields_;
  }


  /**
   * Gives the raw text of the change record.
   *
   * @return  Raw text of the change log record.
   * @category  getter
   */
  public String getRecord() {
    return record_text_;
  }


  /**
   * Assigns the raw text of the change record.
   *
   * @param  record_text  Raw text of the change log record.
   * @category  setter
   */
  public String setRecord(String record_text) {
    return record_text_ = record_text;
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


  // Instance data ----------------------------------------------------------
  /** Container for fields of the raw change record. */
  protected List<String> raw_fields_ = new ArrayList<String>();

  /** The raw change record. */
  protected String record_text_;

  /** ID of the affected model element if any. */
  protected String element_id_ = null;

  /** Type of the affected model element if any. */
  protected ElementType element_type_ = ElementType.NONE;

  /** Side of merge the change record is from. */
  protected MergeSide merge_side_;
  // End instance data ------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**
   * Tag values to indicate which side of a three-way merge a change record
   * comes from, and therefore which model(s) it has affected.
   *
   * @since   v0.29 - Jul 20, 2010
   * @author  Stephen Barrett
   */
  static public enum MergeSide { NONE, BASE, LEFT, RIGHT, BOTH }


  /**
   * Tag values to indicate the type of element being changed.
   *
   * @since   v0.29 - Jul 20, 2010
   * @author  Stephen Barrett
   */
  static public enum ElementType { NONE, PACKAGE, CLASS, ATTRIBUTE, OPERATION,
    PARAMETER, ASSOCIATION, REFERENCE, CARDINALITY, GENERALIZATION, DATATYPE }
  // End nested types -------------------------------------------------------
}
