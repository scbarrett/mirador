/* --------------------------------------------------------------------------+
   TestFujabaRepository.java - Unit tests of...
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.change.fujaba.FujabaRecord;
import ca.dsrg.mirador.change.fujaba.FujabaRecordFactory;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.15 - Mar 3, 2010
 * @author  Stephen Barrett
 */
public class TestFujabaRepository {
  /**
   * Simulates a small portion of two model file containing Fujaba transactions.
   */
  @Before public void setUp() {
    records_lf_.add(FujabaRecordFactory.makeRecord(
        "t;XjyOJ#03;editClass;1266087859102;-;;",
        MergeSide.LEFT));
    records_lf_.add(FujabaRecordFactory.makeRecord(
        "c1;;v::de.uni_paderborn.fujaba.uml.structure.UMLClass;i:XjyOJ#13;-;i:XjyOJ#03;",
        MergeSide.LEFT));
    records_lf_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#13;declaredInPackage;i:XjyOJ#92;-;-;i:XjyOJ#03;",
        MergeSide.LEFT));
    records_lf_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#13;name;v::Cd3C;-;-;i:XjyOJ#03;",
        MergeSide.LEFT));

    records_lf_.add(FujabaRecordFactory.makeRecord(
        "t;XjyOJ#15;inplace editing;1266088604100;-;;",
        MergeSide.LEFT));
    records_lf_.add(FujabaRecordFactory.makeRecord(
        "c1;;v::de.uni_paderborn.fujaba.uml.structure.UMLAttr;i:XjyOJ#25;-;i:XjyOJ#15;",
        MergeSide.LEFT));
    records_lf_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#13;attrs;i:XjyOJ#25;-;-;i:XjyOJ#15;",
        MergeSide.LEFT));
    records_lf_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#25;parent;i:XjyOJ#13;-;-;i:XjyOJ#15;",
        MergeSide.LEFT));


    records_rt_.add(FujabaRecordFactory.makeRecord(
        "t;XjyOJ#03;editClass;1266087859102;-;;",
        MergeSide.LEFT));
    records_rt_.add(FujabaRecordFactory.makeRecord(
        "c1;;v::de.uni_paderborn.fujaba.uml.structure.UMLClass;i:XjyOJ#13;-;i:XjyOJ#03;",
        MergeSide.LEFT));
    records_rt_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#13;declaredInPackage;i:XjyOJ#92;-;-;i:XjyOJ#03;",
        MergeSide.LEFT));
    records_rt_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#13;name;v::Cd3C;-;-;i:XjyOJ#03;",
        MergeSide.LEFT));

    records_rt_.add(FujabaRecordFactory.makeRecord(
        "t;f8q3h#D21;globalDeleteAction;1266090866267;-;;",
        MergeSide.LEFT));
    records_rt_.add(FujabaRecordFactory.makeRecord(
        "c2;;i:XjyOJ#13;-;-;-;-;i:f8q3h#D21;",
        MergeSide.LEFT));
    records_rt_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#13;name;v::deleted {Cdelete7C};v::Cdelete7C;-;i:f8q3h#D21;",
        MergeSide.LEFT));
    records_rt_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#13;diagrams;-;i:XjyOJ#E2;-;i:f8q3h#D21;",
        MergeSide.LEFT));
    records_rt_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:XjyOJ#E2;elements;-;i:XjyOJ#13;-;i:f8q3h#D21;",
        MergeSide.LEFT));
    records_rt_.add(FujabaRecordFactory.makeRecord(
        "c2;;i:XjyOJ#13;-;-;-;-;i:f8q3h#D21;",
        MergeSide.LEFT));
  }

  /**
   * Asserts successful creation of transaction from model records.
   */
  @Test public final void testRepositoryCreation() {
//    FujabaRepository repo = new FujabaRepository(lf_records_, records_rt_, null);
//    assertNotNull("\nCreating a Mirador repository", repo);
  }

  /**
   * Asserts that transaction state information reflects model records.
   */
  @Test public final void testTransactionState() {
//    java.util.ListIterator<CRecord> iterator = records_.listIterator();
//    FujabaTransactionRecord tx_record = (FujabaTransactionRecord) iterator.next();
//    tx_ = new FujabaTransaction(tx_record, iterator);
//
//    assertEquals("\nTransaction ID", "CWcnD#0A",
//        tx_.getTransactionId());
//    assertEquals("\nTransaction name", "inplace editing",
//        tx_.getTransactionName());
//    assertEquals("\nOver transaction ID", "-",
//        tx_.getOverTransactionId());
//    assertEquals("\nModel side", MSide.LEFT,
//        tx_.getSide());
//    assertEquals("\nChange records", 2,
//        tx_.getChanges().size());
  }

  /**
   * Asserts model file pointer is in proper place after creating transaction.
   */
  @Test public final void testIteratorState() {
//    java.util.ListIterator<CRecord> iterator = records_.listIterator();
//    FujabaTransactionRecord tx_record = (FujabaTransactionRecord) iterator.next();
//    tx_ = new FujabaTransaction(tx_record, iterator);
//
//    assertEquals("\nNext transaction", "CWcnD#1A",
//        ((FujabaTransactionRecord) iterator.next()).getTransactionId());
  }

  // Instance data ----------------------------------------------------------
  /** The CoObRA change records of a simulated Fujaba left model. */
  private List<FujabaRecord> records_lf_ = new ArrayList<FujabaRecord>();
  /** The CoObRA change records of a simulated Fujaba right model. */
  private List<FujabaRecord> records_rt_ = new ArrayList<FujabaRecord>();
  // End instance data ------------------------------------------------------
}
