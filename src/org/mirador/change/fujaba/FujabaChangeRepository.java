/* --------------------------------------------------------------------------+
   FujabaChangeRepository.java - Concrete deposit point for the salient items of a
     merge session as drawn from Fujaba base, left, and right models.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Though a base change log is part of the repository, by their nature,
   replicated Fujaba models always contain this common ancestor model.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;
import ca.dsrg.mirador.Debug;
import ca.dsrg.mirador.change.ChangeRepository;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord.CoobraKind;
import ca.dsrg.mirador.change.fujaba.FujabaRecord.CoobraType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;


/**
 * Overall repository for the model change records, and transactional groupings
 * of the models involved in a merge. Components are identified by their "side"
 * in the merge: BASE, LEFT, or RIGHT.
 *
 * @since   v0.10 - Feb 15, 2010
 * @author  Stephen Barrett
 */
public class FujabaChangeRepository
    extends ChangeRepository<FujabaRecord, FujabaTransaction> {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * Creates a snapshot of the change history of the models involved in a merge
   * for later use by Mirador. These Fujaba-specific logs are then converted
   * into the common Mirador change record format.
   *
   * A null base log implies a two-way merge.
   *
   * @param  change_log_bs  Change log for the "base" model.
   * @param  change_log_lf  Change log for the "left" model.
   * @param  change_log_rt  Change log for the "right" model.
   */
  public FujabaChangeRepository(File change_log_bs, File change_log_lf,
      File change_log_rt) {
    super(change_log_bs, change_log_lf, change_log_rt);
    initialize(change_log_bs, change_log_lf, change_log_rt);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  static private void cleanTransaction(FujabaTransaction tx,
      List<FujabaTransaction> removed_txs, Set<String> removed_ids) {
    List<FujabaChangeRecord> removed_recs = new ArrayList<FujabaChangeRecord>();

    // Guard against processing non-useful transactions.
    String tx_name = tx.getName();
    if (tx_name != null
        && (tx_name.equals("drag")
        ||  tx_name.equals("newClassDiagram")
        ||  tx_name.equals("editImports"))) {
      removed_txs.add(tx);  // Not useful action, cache.

      // Cache IDs of objects created in the removed transaction.
      for (ListIterator<FujabaRecord> it = tx.changeIterator();
          it.hasNext();) {
        FujabaChangeRecord rec = (FujabaChangeRecord) it.next();
        if (rec.getChangeKind() == CoobraKind.CREATE_OBJECT)
          removed_ids.add(rec.getElementId());  // Cache ID for comparisons.
      }
      return;
    }


    for (ListIterator<FujabaRecord> outer_it = tx.changeIterator();
        outer_it.hasNext();) {
      FujabaChangeRecord outer = (FujabaChangeRecord) outer_it.next();
      if (outer.getRecordType() != CoobraType.CHANGE)
        continue;

      // Screen creation changes for non-useful types.
      if (outer.getChangeKind() == CoobraKind.CREATE_OBJECT) {
        String type_name = outer.getTypeName();

        if (type_name != null
            && (type_name.endsWith(".UMLClassDiagram")
            ||  type_name.endsWith(".UMLStereotype")
            ||  type_name.endsWith(".UMLFile")
            ||  type_name.endsWith(".ASGInformation")
            ||  type_name.endsWith(".ASGUnparseInformation"))) {
          removed_recs.add(outer);  // Not useful type, cache for removal.
          removed_ids.add(outer.getElementId());  // Cache ID for comparisons.
        }
        continue;
      }


      // Screen for changes that target object of a canceled creation.
      if (removed_ids.contains(outer.getElementId())
          || removed_ids.contains(outer.getNewValue())) {
        removed_recs.add(outer);  // Targets a removed type, cache.
        continue;
      }


      // Screen alteration changes for non-useful fields.
      String field_name = outer.getFieldName();
      if (field_name != null  // Comment line to force check of feature.
          && (field_name.equals("progLangType")
          ||  field_name.equals("modelRootNodes")
//        ||  field_name.equals("name")
          ||  field_name.equals("contains")       // Opposite is "file".
          ||  field_name.equals("file")           // Opposite is "contains".
          ||  field_name.equals("elements")       // Opposite is "diagrams".
          ||  field_name.equals("diagrams")       // Opposite is "elements".
          ||  field_name.equals("parent")         // Opposite is "attrs" or "methods" or "packages".
//        ||  field_name.equals("packages")       // Opposite is "parent".
//        ||  field_name.equals("attrs")          // Opposite is "parent".
//        ||  field_name.equals("methods")        // Opposite is "parent".
//        ||  field_name.equals("param")          // Opposite is "revParam".
          ||  field_name.equals("revParam")       // Opposite is "param".
//        ||  field_name.equals("roles")          // Opposite is "target".
          ||  field_name.equals("target")         // Opposite is "roles".
//        ||  field_name.equals("leftRole")       // Opposite is "revLeftRole".
          ||  field_name.equals("revLeftRole")    // Opposite is "leftRole".
//        ||  field_name.equals("rightRole")      // Opposite is "revRightRole".
          ||  field_name.equals("revRightRole")   // Opposite is "rightRole".
//        ||  field_name.equals("subclass")       // Opposite is "revSubclass".
          ||  field_name.equals("revSubclass")    // Opposite is "subclass".
//        ||  field_name.equals("superclass")     // Opposite is "revSuperclass".
          ||  field_name.equals("revSuperclass")  // Opposite is "superclass".
//        ||  field_name.equals("attrType")       // Must be an EDataType.
//        ||  field_name.equals("card")
          ||  field_name.equals("sortedComparator")
          ||  field_name.equals("increments")
          ||  field_name.equals("defaultIcon")
          ||  field_name.equals("unparseInformations")
          ||  field_name.equals("aSGInformation")
          ||  field_name.equals("information")))
      {
        removed_recs.add(outer);  // Not useful field, cache.
        continue;
      }


      String val = outer.getNewValue();
      if (val.equals("-")
          && outer.getChangeKind() != CoobraKind.DESTROY_OBJECT) {
        removed_recs.add(outer);
        continue;
      }


      // See if acceptable change is overwritten by succeeding changes.
      String id = outer.getElementId();
      for (ListIterator<FujabaRecord> inner_it =
          tx.changeIterator(outer_it.nextIndex()); inner_it.hasNext();) {
        FujabaChangeRecord inner = (FujabaChangeRecord) inner_it.next();
        if (inner.getRecordType() != CoobraType.CHANGE)
          continue;

        if (inner.getChangeKind() == CoobraKind.CREATE_OBJECT
            || inner.getFieldName() == null)
          continue;

        if (   (inner.getElementId().equals(id)
            &&  inner.getFieldName().equals(field_name))
            && (inner.getNewValue().equals(val)
            ||  (!field_name.equals("packages")
            &&   !field_name.equals("attrs")
            &&   !field_name.equals("methods")
            &&   !field_name.equals("param")
            &&   !field_name.equals("roles")))) {
          removed_recs.add(outer);  // Redundant change, cache.
          break;
        }
      }
    }


    // Remove cached non-useful and redundant changes.
    for (ListIterator<FujabaChangeRecord> remove_it =
        removed_recs.listIterator(); remove_it.hasNext();)
      tx.removeChange(remove_it.next());

    if (!tx.isEmpty()) {
      Debug.dbg.println("\n\t>>> Cleaning Transaction - "
          + tx.getRealId() + ": " + tx.getName() + " <<<");
      for (ListIterator<FujabaRecord> tx_it = tx.changeIterator();
          tx_it.hasNext();) {
        Debug.dbg.println(tx_it.next());
      }
    }
    else
      removed_txs.add(tx);  // Not useful action, cache.
  }


  /**
   * Groups a model's CoObRA record representations into CTransaction objects
   * as delimited in the Fujaba model by CoObRA transaction records.
   *
   * @param  changes
   * @param  txs
   */
  static private void gatherTransactions(List<FujabaRecord> changes,
      List<FujabaTransaction>txs) {
    for (ListIterator<FujabaRecord> change_it = changes.listIterator();
        change_it.hasNext();) {
      FujabaRecord change = change_it.next();

      if (change.getRecordType() == CoobraType.TRANSACTION) {
        FujabaTransaction tx =
            new FujabaTransaction((FujabaTransactionRecord) change, change_it);
        txs.add(tx);
      }
    }

    List<FujabaTransaction> removed_txs = new ArrayList<FujabaTransaction>();
    Set<String> removed_ids = new HashSet<String>();
    for (FujabaTransaction tx : txs)
      cleanTransaction(tx, removed_txs, removed_ids);

    // Remove cached non-useful transactions.
    for (ListIterator<FujabaTransaction> remove_it = removed_txs.listIterator();
        remove_it.hasNext();)
      txs.remove(remove_it.next());
  }


  /**
   * Reads through the left and right model files gathering the change records
   * into transactions.
   *
   * Note that Fujaba models replicated from the same base model will each
   * hold a common set ancestor records.
   */
  protected void initialize(File change_log_bs, File change_log_lf,
      File change_log_rt) {
    boolean is_debug = Debug.dbg.resetDebug();
    int cnt = 0;  // Count of records read from base model (base one).

    if (change_log_bs != null) {  // TODO:3 Extract base model from 2-way merge.
      cnt = readFileLines(change_log_bs, cnt, changes_bs_, MergeSide.BASE);
      Debug.dbg.println("\n\n\n\t    --- BASE FUJABA TRANSACTIONS ---");
      gatherTransactions(changes_bs_, txs_bs_);
    }

    if (change_log_lf != null) {
      readFileLines(change_log_lf, cnt, changes_lf_, MergeSide.LEFT);
      Debug.dbg.println("\n\n\n\t    --- LEFT FUJABA TRANSACTIONS ---");
      gatherTransactions(changes_lf_, txs_lf_);
    }

    if (change_log_rt != null) {
      readFileLines(change_log_rt, cnt, changes_rt_, MergeSide.RIGHT);
      Debug.dbg.println("\n\n\n\t    --- RIGHT FUJABA TRANSACTIONS ---");
      gatherTransactions(changes_rt_, txs_rt_);
    }

    Debug.dbg.setDebug(is_debug);
  }


  /**
   * Creates a CRecord representation of the proper type for each CoObRA record
   * in a model file, adding them to the model's storage area.
   *
   * @param  log_file  CoObRA change log for the model.
   * @param  rec_count  Log record count at which the model begins (base one).
   * @param  changes  Collection for caching change records.
   * @param  side  Which model the changes are from.
   * @return  Count of records read from log file.
   */
  static private int readFileLines(File log_file, int rec_count,
      List<FujabaRecord> changes, MergeSide side) {
    String line;
    int rec_cnt = 0;

    try {
      BufferedReader fin = new BufferedReader(new FileReader(log_file));

      do {
        line = fin.readLine();

        if (line != null && !line.trim().isEmpty() && ++rec_cnt > rec_count) {
          // Make a record object of the proper type.
          FujabaRecord record = FujabaRecordFactory.makeRecord(line, side);

          // Stash legitimate record object into collection.
          if (record != null && record.getRecordType() != null
              && (record.getRecordType() == CoobraType.TRANSACTION
              || record.getRecordType() == CoobraType.CHANGE)) {
            changes.add(record);
          }
        }
      } while (line != null);
    } catch (IOException ex) {
      System.err.println("!!! Mirador - failure !!!\n");  // TODO:3 Replace catch stub.
      ex.printStackTrace();
    }

    return rec_cnt;
  }
}
