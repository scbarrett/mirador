/* --------------------------------------------------------------------------+
   MiradorAlterRecord.java - Representation of a tool-neutral model change
     log record that documents the alteration of a model element.

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
 * element alteration event.
 *
 * The class provides a dedicated constructor for each tool-specific type
 * of model change operation recorder, plus a naked constructor for when no
 * recorder is involved.
 *
 * @since   v0.29 - Jul 20, 2010
 * @author  Stephen Barrett
 */
public class MiradorAlterRecord extends MiradorRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Naked constructor for creating an empty alteration type of model change
   * record not associated with any particular recorder tool.
   *
   * @param  element_id  ID of the element affected by the change operation.
   * @param  merge_side  Which side of a merge the change record is from.
   * @param  tx_action  Change transaction the operation is part of.
   * @param  field_name  Name of affected element attribute.
   * @param  new_value  To be assigned value of affected element attribute.
   * @param  old_value  Previously assigned value of affected element attribute.
   */
  public MiradorAlterRecord(String element_id, MergeSide merge_side,
      MiradorTransaction tx_action, String field_name, String new_value,
      String old_value ) {
    super(element_id, merge_side, tx_action);
    field_name_ = field_name;
    new_value_ = new_value;
    old_value_ = old_value;
  }


  /**
   * Fujaba recorder-specific constructor for creating a alteration type of
   * model change record. Performs the conversion to tool-neutrality.
   *
   * @param  fujaba_record  The Fujaba model change log record.
   * @param  tx_action  Change transaction the operation is part of.
   */
  public MiradorAlterRecord(FujabaChangeRecord fujaba_record,
      MiradorTransaction tx_action) {
    super(fujaba_record, tx_action);
    field_name_ = fujaba_record.getFieldName();
    new_value_ = !fujaba_record.getNewValue().equals("-")
        ? fujaba_record.getNewValue() : null;
    old_value_ = !fujaba_record.getOldValue().equals("-")
        ? fujaba_record.getOldValue() : null;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the name of the affected model element attribute.
   *
   * @return  Name of affected element attribute.
   * @category  getter
   */
  public String getFieldName() {
    return field_name_;
  }


  /**
   * Gives the to be assigned value of the affected model element attribute.
   *
   * @return  To be assigned value of affected element attribute.
   * @category  getter
   */
  public String getNewValue() {
    return new_value_;
  }


  /**
   * Gives the previously assigned value of the affected model element attribute.
   *
   * @return  Previously assigned value of affected element attribute.
   * @category  getter
   */
  public String getOldValue() {
    return old_value_;
  }


  // Instance data ----------------------------------------------------------
  /** Name of affected model element attribute. */
  private String field_name_;

  /** To be assigned value of affected model element attribute. */
  private String new_value_;

  /** Previously assigned value of affected model element attribute. */
  private String old_value_;
  // End instance data ------------------------------------------------------
}
