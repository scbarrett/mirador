/* --------------------------------------------------------------------------+
   MiradorChangeRecord.java - Abstraction for a tool-neutral model change log
     record that documents an actual model change.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord;


/**
 * Represents a tool-neutral model change log record that documents an actual
 * change to the model.
 *
 * The class provides a dedicated constructor for each tool-specific type
 * of model change operation recorder, plus a naked constructor for when no
 * recorder is involved.
 *
 * @since   v0.29 - Jul 21, 2010
 * @author  Stephen Barrett
 */
abstract public class MiradorChangeRecord extends MiradorRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Naked constructor for creating an empty model change record not associated
   * with any particular recorder tool.
   *
   * @param  element_id  ID of the element affected by the change operation.
   * @param  merge_side  Which side of a merge the change record is from.
   * @param  tx_action  Change transaction the operation is part of.
   */
  public MiradorChangeRecord(String element_id, MergeSide merge_side,
      MiradorTransaction tx_action) {
    super(element_id, merge_side, tx_action);
  }


  /**
   * Fujaba recorder-specific constructor for creating a model change record.
   * Performs the conversion to tool-neutrality.
   *
   * @param  fujaba_record  The Fujaba model change log record.
   * @param  tx_action  Change transaction the operation is part of.
   */
  public MiradorChangeRecord(FujabaChangeRecord fujaba_record,
      MiradorTransaction tx_action) {
    super(fujaba_record, tx_action);
  }
}
