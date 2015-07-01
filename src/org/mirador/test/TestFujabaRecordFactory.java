/* --------------------------------------------------------------------------+
   TestFujabaRecordFactory.java - Unit tests for the CoObRA record class factory.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord;
import ca.dsrg.mirador.change.fujaba.FujabaCommentRecord;
import ca.dsrg.mirador.change.fujaba.FujabaHeaderRecord;
import ca.dsrg.mirador.change.fujaba.FujabaRecord;
import ca.dsrg.mirador.change.fujaba.FujabaRecordFactory;
import ca.dsrg.mirador.change.fujaba.FujabaTransactionRecord;
import ca.dsrg.mirador.change.fujaba.FujabaRecord.CoobraType;
import org.junit.Test;


/**
 * Tests the factory construction of the various CoObRA record types.
 *
 * @since   v0.15 - Mar 3, 2010
 * @author  Stephen Barrett
 * @see  FujabaRecord
 * @see  FujabaHeaderRecord
 * @see  FujabaCommentRecord
 * @see  FujabaTransactionRecord
 * @see  FujabaChangeRecord
 */
public class TestFujabaRecordFactory {
  /**
   * Asserts factory construction of a CoObRA comment record.
   */
  @Test public final void testMakeCommentRecord() {
    String comment = "This is a comment;this #too!";
    String text = "#;" + comment;
    FujabaRecord record = FujabaRecordFactory.makeRecord(text, MergeSide.BASE);
    assertEquals("\nCoObRA record type", CoobraType.COMMENT, record.getRecordType());
  }

  /**
   * Asserts factory construction of a CoObRA header record.
   */
  @Test public final void testMakeHeaderRecord() {
    String metadata = "Fujaba Version;5.2.2";
    String text = "h;" + metadata;
    FujabaRecord record = FujabaRecordFactory.makeRecord(text, MergeSide.LEFT);
    assertEquals("\nCoObRA record type", CoobraType.HEADER, record.getRecordType());
  }

  /**
   * Asserts factory construction of a CoObRA transaction record.
   */
  @Test public final void testMakeTransactionRecord() {
    String text = "t;6XoO6#;newUMLProject;1266256822756;-;4;";
    FujabaRecord record = FujabaRecordFactory.makeRecord(text, MergeSide.RIGHT);
    assertEquals("\nCoObRA record type", CoobraType.TRANSACTION, record.getRecordType());
  }

  /**
   * Asserts factory construction of a CoObRA change record.
   */
  @Test public final void testMakeChangeRecord() {
    String text = "c3;;i:_TVLu#3A1;diagrams;i:_TVLu#691;-;-;i:_TVLu#2A1;";
    FujabaRecord record = FujabaRecordFactory.makeRecord(text, MergeSide.BASE);
    assertEquals("\nCoObRA record type", CoobraType.CHANGE, record.getRecordType());
  }

  /**
   * Asserts factory construction of an invalid CoObRA record.
   */
  @Test public final void testMakeInvalidRecord() {
    String text = "x;Invalid;record;type;!";
    FujabaRecord record = FujabaRecordFactory.makeRecord(text, MergeSide.LEFT);
    assertNull("\nCoObRA record type", record);
  }
}
