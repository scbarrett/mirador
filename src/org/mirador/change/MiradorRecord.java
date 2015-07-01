/* --------------------------------------------------------------------------+
   MiradorRecord.java - Abstraction for a tool-neutral model change log record
     that documents a model change.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Situated at the top of Mirador's record hierarchy, provides an interface to
   those aspects of a model change operation most important to merging.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord;


/**
 * Minimal representation of a tool-neutral model change log record that
 * documents a change to the model.
 *
 * The class provides a dedicated constructor for each tool-specific type
 * of model change operation recorder, plus a naked constructor for when no
 * recorder is involved.
 *
 * @since   v0.29 - Jul 21, 2010
 * @author  Stephen Barrett
 */
abstract public class MiradorRecord extends ChangeRecord{
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Naked constructor for creating an empty model change record not associated
   * with any particular recorder tool.
   *
   * @param  element_id  ID of the element affected by the change operation.
   * @param  merge_side  Which side of a merge the change record is from.
   * @param  tx_action  Change transaction the operation is part of.
   */
  public MiradorRecord(String element_id, MergeSide merge_side,
      MiradorTransaction tx_action) {
    super("", merge_side);
    element_id_ = element_id;
    tx_action_ = tx_action;
  }


  /**
   * Fujaba recorder-specific constructor for creating a model change record.
   * Performs the conversion to tool-neutrality.
   *
   * @param  fujaba_record  The Fujaba model change log record.
   * @param  tx  Change transaction the operation is part of.
   */
  public MiradorRecord(FujabaChangeRecord fujaba_record,
      MiradorTransaction tx) {
    super(fujaba_record.getRecord(), fujaba_record.getMergeSide());
    element_id_ = fujaba_record.getElementId();
    tx_action_ = tx;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the containing transaction of the change record.
   *
   * @return  Transaction containing the change record.
   * @category  getter
   */
  public MiradorTransaction getTransaction() {
    return tx_action_;
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
  /** Containing transaction for this change. */
  protected MiradorTransaction tx_action_;
  // End instance data ------------------------------------------------------
}
