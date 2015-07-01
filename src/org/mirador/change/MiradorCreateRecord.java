/* --------------------------------------------------------------------------+
   MiradorCreateRecord.java - Representation of a tool-neutral model change
     log record that documents the creation of a model element.


   MiradorChangeRecord.java - Abstraction for all

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord;


/**
 * Represents a tool-neutral model change log record that documents a model
 * element creation event.
 *
 * The class provides a dedicated constructor for each tool-specific type
 * of model change operation recorder, plus a naked constructor for when no
 * recorder is involved.
 *
 * @since   v0.29 - Jul 20, 2010
 * @author  Stephen Barrett
 */
public class MiradorCreateRecord extends MiradorRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Naked constructor for creating an empty creation type of model change
   * record not associated with any particular recorder tool.
   *
   * @param  element_id  ID of the element affected by the change operation.
   * @param  merge_side  Which side of a merge the change record is from.
   * @param  tx_action  Change transaction the operation is part of.
   * @param  element_type  Type of the affected model element.
   */
  public MiradorCreateRecord(String element_id, MergeSide merge_side,
      MiradorTransaction tx_action, ElementType element_type) {
    super(element_id, merge_side, tx_action);
    element_type_ = element_type;
  }


  /**
   * Fujaba recorder-specific constructor for creating a creation type of
   * model change record. Performs the conversion to tool-neutrality.
   *
   * @param  fujaba_record  The Fujaba model change log record.
   * @param  tx_action  Change transaction the operation is part of.
   */
  public MiradorCreateRecord(FujabaChangeRecord fujaba_record,
      MiradorTransaction tx_action) {
    super(fujaba_record, tx_action);
    element_type_ = fujaba_record.getElementType();
  }
}
