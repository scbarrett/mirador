/* --------------------------------------------------------------------------+
   MiradorTransaction.java - Representation of a tool-neutral transaction
     of model change log records.


   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord;
import ca.dsrg.mirador.change.fujaba.FujabaRecord;
import ca.dsrg.mirador.change.fujaba.FujabaTransaction;
import ca.dsrg.mirador.change.fujaba.FujabaRecord.CoobraType;
import java.util.ListIterator;


/**
 * Tool-neutral version of a named container for the model change operations
 * effecting a single high level change against one model element.
 *
 * The class provides a dedicated constructor and initializer for each tool-
 * specific type of model change operation recorder.
 *
 * @since   v0.29 - Jul 20, 2010
 * @author  Stephen Barrett
 */
public class MiradorTransaction extends ChangeTransaction<MiradorRecord>{
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Fujaba recorder-specific constructor for creating a transaction of model
   * change records. Performs the conversion to tool-neutrality.
   *
   * @param  fujaba_tx  The Fujaba model change transaction.
   */
  public MiradorTransaction(FujabaTransaction fujaba_tx) {
    tx_name_ = fujaba_tx.getName();
    tx_id_ = fujaba_tx.getId();
    merge_side_ = fujaba_tx.getMergeSide();
    initialize(fujaba_tx);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Fujaba recorder-specific initializer for the transaction. Performs the
   * conversion to tool-neutrality.
   *
   * @param  fujaba_tx  The Fujaba model change transaction.
   */
  private void initialize(FujabaTransaction fujaba_tx) {
    for (FujabaRecord  f_rec : fujaba_tx.getChanges()) {
      if (f_rec.getRecordType() == CoobraType.CHANGE) {
        FujabaChangeRecord fc_rec = (FujabaChangeRecord) f_rec;

        switch (fc_rec.getChangeKind()) {
          case CREATE_OBJECT:
            changes_.add(new MiradorCreateRecord(fc_rec, this));
          break;

          case DESTROY_OBJECT:
//            changes_.add(new MiradorDestroyRecord(fc_rec, this));
          break;

          case ALTER_FIELD:
            changes_.add(new MiradorAlterRecord(fc_rec, this));
          break;
        }
      }
    }

    Debug.dbg.println("\n\t>>> Creating Mirador Transaction - "
        + tx_id_ + ": " + tx_name_ + " <<<");
    for (ListIterator<MiradorRecord> it = changeIterator(); it.hasNext();)
      Debug.dbg.println(it.next());
  }
}
