/* --------------------------------------------------------------------------+
   TestFujabaTransaction.java - Unit tests of transaction creation and state.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.change.fujaba.FujabaRecord;
import ca.dsrg.mirador.change.fujaba.FujabaRecordFactory;
import ca.dsrg.mirador.change.fujaba.FujabaTransaction;
import ca.dsrg.mirador.change.fujaba.FujabaTransactionRecord;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Tests CTransaction creation from the CoObRA records of a simulated model
 * file, verifies the resulting object's state, and the final read point in
 * the model file.
 *
 * @since   v0.15 - Mar 3, 2010
 * @author  Stephen Barrett
 */
public class TestFujabaTransaction {
  /**
   * Simulates a small portion of a model file containing a Fujaba transaction.
   */
  @Before public void setUp() {
    records_.add(FujabaRecordFactory.makeRecord(
        "t;CWcnD#0A;inplace editing;1254671729742;-;;",
        MergeSide.LEFT));
    records_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:CWcnD#E2;name;v::Order Product;v::Scenario;-;i:CWcnD#0A;",
        MergeSide.LEFT));
    records_.add(FujabaRecordFactory.makeRecord(
        "c3;;i:CWcnD#23;textComment;v::// Logged in;v::// start: ;-;i:CWcnD#0A;",
        MergeSide.LEFT));
    records_.add(FujabaRecordFactory.makeRecord(
        "t;CWcnD#1A;drag;1254671756877;-;;",
        MergeSide.LEFT));
  }

  /**
   * Asserts successful creation of transaction from model records.
   */
  @Test public final void testTransactionCreation() {
    java.util.ListIterator<FujabaRecord> iterator = records_.listIterator();
    FujabaTransactionRecord tx_record = (FujabaTransactionRecord) iterator.next();
    FujabaTransaction tx_ = new FujabaTransaction(tx_record, iterator);

    assertNotNull("\nCreating a Fujaba transaction", tx_);
  }

  /**
   * Asserts that transaction state information reflects model records.
   */
  @Test public final void testTransactionState() {
    java.util.ListIterator<FujabaRecord> iterator = records_.listIterator();
    FujabaTransactionRecord tx_record = (FujabaTransactionRecord) iterator.next();
    FujabaTransaction tx_ = new FujabaTransaction(tx_record, iterator);
    int size = 0;
    for (ListIterator<FujabaRecord> it = tx_.changeIterator();
        it.hasNext(); it.next(), ++size);

    assertEquals("\nTransaction ID", "CWcnD_0A",
        tx_.getId());
    assertEquals("\nTransaction name", "inplace editing",
        tx_.getName());
    assertEquals("\nOver transaction ID", "-",
        tx_.getOverTransactionId());
    assertEquals("\nModel side", MergeSide.LEFT,
        tx_.getMergeSide());
    assertEquals("\nChange records", 2, size);
  }

  /**
   * Asserts model file pointer is in proper place after creating transaction.
   */
  @Test public final void testIteratorState() {
    java.util.ListIterator<FujabaRecord> iterator = records_.listIterator();
    FujabaTransactionRecord tx_record = (FujabaTransactionRecord) iterator.next();
    new FujabaTransaction(tx_record, iterator);

    assertEquals("\nNext transaction", "CWcnD_1A",
        ((FujabaTransactionRecord) iterator.next()).getElementId());
  }

  // Instance data ----------------------------------------------------------
  /** The CoObRA change records of a simulated Fujaba transaction. */
  private List<FujabaRecord> records_ = new ArrayList<FujabaRecord>();
  // End instance data ------------------------------------------------------
}
