/* --------------------------------------------------------------------------+
   FujabaTransaction.java - Bundles a group of CoObRA change log records into
     a single Fujaba model transaction.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Preserves Fujaba's notion of a transaction with regard to change records.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.change.ChangeTransaction;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord.CoobraKind;
import ca.dsrg.mirador.change.fujaba.FujabaRecord.CoobraType;
import java.util.ListIterator;


/**
 * A bundle of CoObRA change log records considered by Fujaba to constitute a
 * single model transaction.<p>
 *
 * A Fujaba model file <i>is</i> a CoObRA change log. That being the case, the
 * start of a transaction is marked with a CoObRA transaction record, the end is
 * marked by either the next transaction record, or the end of the file.
 *
 * @since   v0.10 - Feb 16, 2010
 * @author  Stephen Barrett
 */
public class FujabaTransaction extends ChangeTransaction<FujabaRecord> {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Collects CoObRA change log records to represent a Fujaba model transaction.
   *
   * CTransaction objects are created in parallel with the parsing of the Fujaba
   * model file. The passed iterator actually points to the parser's current
   * location in the file.
   *
   * @param  tx_record  CoObRA record indicating start of a Fujaba transaction.
   * @param  change_it  Start of model records associated with this transaction.
   */
  public FujabaTransaction(FujabaTransactionRecord tx_record,
      ListIterator<FujabaRecord> change_it) {
    tx_name_ = tx_record.getName();
    tx_id_ = tx_record.getElementId();
    tx_over_id_ = (!tx_record.getOverTransactionId().equals("-"))
        ? tx_record.getOverTransactionId() : null;
    merge_side_ = tx_record.getMergeSide();

    initialize(tx_record, change_it);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the unmodified ID of the transaction.
   *
   * @return  ID of the transaction.
   * @category  getter
   */
  public String getRealId() {
    return tx_id_.replace('_', '#');
  }


  /**
   * Gives the ID of the over-arching transaction this transaction belongs to.
   *
   * @return  Over transaction ID of this transaction.
   * @category  getter
   */
  public String getOverTransactionId() {
    return tx_over_id_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Actually creates the transaction object, pulling in records of the Fujaba
   * model that form the transaction.
   *
   * @param  tx_record  CoObRA record indicating start of a Fujaba transaction.
   * @param  change_it  Start of model records associated with this transaction.
   */
  private void initialize(FujabaTransactionRecord tx_record,
      ListIterator<FujabaRecord> change_it) {
    // Gather model records from iterator start to end of transaction.
    for (boolean is_done = false; change_it.hasNext() && !is_done;) {
      FujabaRecord record = change_it.next();
      CoobraType ch_type = record.getRecordType();

      switch (ch_type) {
        case CHANGE:
          // Add all kinds but manage change records to the transaction.
          FujabaChangeRecord ch_record = (FujabaChangeRecord) record;
          CoobraKind ch_kind = ch_record.getChangeKind();

          if (ch_kind == CoobraKind.CREATE_OBJECT
              || ch_kind == CoobraKind.DESTROY_OBJECT
              || ch_kind == CoobraKind.ALTER_FIELD
              || ch_kind == CoobraKind.REMOVE_KEY) {
            addChange(ch_record);
            break;
          }
        break;

        case TRANSACTION:
          is_done = true;       // Beginning of next transaction...
          change_it.previous();  // ...back iterator up one so as not to lose it.
        break;
      }
    }

    Debug.dbg.println("\n\t>>> Creating Fujaba Transaction - "
        + getRealId() + ": " + tx_name_ + " <<<");
    for (change_it = changeIterator(); change_it.hasNext();)
      Debug.dbg.println(change_it.next());
  }


  // Instance data ----------------------------------------------------------
  /** Transaction that contains this transaction. */
  protected String tx_over_id_;
  // End instance data ------------------------------------------------------
}
