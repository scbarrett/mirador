/* --------------------------------------------------------------------------+
   TestFujabaRecords.java - Unit tests for the CoObRA record class hierarchy.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.test;
import static org.junit.Assert.assertEquals;
import ca.dsrg.mirador.change.ChangeRecord.ElementType;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord;
import ca.dsrg.mirador.change.fujaba.FujabaCommentRecord;
import ca.dsrg.mirador.change.fujaba.FujabaHeaderRecord;
import ca.dsrg.mirador.change.fujaba.FujabaRecord;
import ca.dsrg.mirador.change.fujaba.FujabaTransactionRecord;
import ca.dsrg.mirador.change.fujaba.FujabaChangeRecord.CoobraKind;
import ca.dsrg.mirador.change.fujaba.FujabaRecord.CoobraType;
import org.junit.Test;


/**
 * Tests the parsing of the various CoObRA record types, and the retrieval of
 * their constituent parts.
 *
 * @since   v0.14 - Feb 28, 2010
 * @author  Stephen Barrett
 * @see  FujabaRecord
 * @see  FujabaHeaderRecord
 * @see  FujabaCommentRecord
 * @see  FujabaTransactionRecord
 * @see  FujabaChangeRecord
 */
public class TestFujabaRecords {
  /**
   * Asserts the parsing of a CoObRA comment record.
   */
  @Test public final void testCommentRecord() {
    String comment = "This is a comment;this #too!";
    String text = "#;" + comment;
    FujabaCommentRecord record =
        new FujabaCommentRecord(text, MergeSide.BASE);

    assertEquals("\nCoObRA record", text, record.getRecord());
    assertEquals("\nModel side", MergeSide.BASE, record.getMergeSide());
    assertEquals("\nType indicator", '#', record.getRawType());
    assertEquals("\nRecord type", CoobraType.COMMENT, record.getRecordType());
    assertEquals("\nComment", comment, record.getComment());

//    assertNull("\nName", record.getName());
//    assertNull("\nField key", record.getFieldKey());
//    assertNull("\nField name", record.getFieldName());
//    assertNull("\nFull name", record.getFullName());
//    assertNull("\nItem ID", record.getObjectId());
//    assertNull("\nChange kind", record.getChangeKind());
//    assertNull("\nMetadata", record.getMetadata());
//    assertNull("\nModifier source", record.getModifierSource());
//    assertNull("\nNew value", record.getNewValue());
//    assertNull("\nOld value", record.getOldValue());
//    assertNull("\nOver ID", record.getOverTransactionId());
//    assertNull("\nTransaction ID", record.getItemId());
//    assertNull("\nTransaction Time", record.getTransactionTime());
  }

  /**
   * Asserts the parsing of a CoObRA header record.
   */
  @Test public final void testHeaderRecord() {
    String metadata = "Fujaba Version;5.2.2";
    String text = "h;" + metadata;
    FujabaHeaderRecord record =
        new FujabaHeaderRecord(text, MergeSide.LEFT);

    assertEquals("\nCoObRA record", text, record.getRecord());
    assertEquals("\nModel side", MergeSide.LEFT, record.getMergeSide());
    assertEquals("\nType indicator", 'h', record.getRawType());
    assertEquals("\nRecord type", CoobraType.HEADER, record.getRecordType());
    assertEquals("\nMetadata", metadata, record.getMetadata());

//    assertNull("\nName", record.getName());
//    assertNull("\nComment", record.getComment());
//    assertNull("\nField key", record.getFieldKey());
//    assertNull("\nField name", record.getFieldName());
//    assertNull("\nFull name", record.getFullName());
//    assertNull("\nItem ID", record.getObjectId());
//    assertNull("\nChange kind", record.getChangeKind());
//    assertNull("\nModifier source", record.getModifierSource());
//    assertNull("\nNew value", record.getNewValue());
//    assertNull("\nOld value", record.getOldValue());
//    assertNull("\nOver ID", record.getOverTransactionId());
//    assertNull("\nTransaction ID", record.getId());
//    assertNull("\nTransaction Time", record.getTransactionTime());
  }

  /**
   * Asserts the parsing of a CoObRA transaction record.
   */
  @Test public final void testTransactionRecord() {
    String text = "t;6XoO6#;newUMLProject;1266256822756;-;4;";
    FujabaTransactionRecord record =
        new FujabaTransactionRecord(text, MergeSide.RIGHT);

    assertEquals("\nCoObRA record", text, record.getRecord());
    assertEquals("\nModel side", MergeSide.RIGHT, record.getMergeSide());
    assertEquals("\nType indicator", 't', record.getRawType());
    assertEquals("\nRecord type", CoobraType.TRANSACTION, record.getRecordType());

    assertEquals("\nName", "newUMLProject", record.getName());
    assertEquals("\nFull name", "newUMLProject", record.getName());
    assertEquals("\nModifier source", "4", record.getModifier());
    assertEquals("\nOver ID", "-", record.getOverTransactionId());
    assertEquals("\nTransaction ID", "6XoO6_", record.getElementId());
    assertEquals("\nTransaction Time", "1266256822756",
        record.getTransactionTime());

//    assertNull("\nComment", record.getComment());
//    assertNull("\nField key", record.getFieldKey());
//    assertNull("\nField name", record.getFieldName());
//    assertNull("\nItem ID", record.getObjectId());
//    assertNull("\nChange kind", record.getChangeKind());
//    assertNull("\nMetadata", record.getMetadata());
//    assertNull("\nNew value", record.getNewValue());
//    assertNull("\nOld value", record.getOldValue());
  }

  /**
   * Asserts the parsing of a CoObRA change create record.
   */
  @Test public final void testChangeCreateRecord() {
    String full_name = "de.uni_paderborn.fujaba.uml.structure.UMLClass";
    String text = "c1;;v::" + full_name + ";i:MG-MV#9C;-;i:MG-MV#8C;";
    FujabaChangeRecord record =
        new FujabaChangeRecord(text, MergeSide.LEFT);

    assertEquals("\nCoObRA record", text, record.getRecord());
    assertEquals("\nModel side", MergeSide.LEFT, record.getMergeSide());
    assertEquals("\nType indicator", 'c', record.getRawType());
    assertEquals("\nRecord type", CoobraType.CHANGE, record.getRecordType());
    assertEquals("\nChange kind", CoobraKind.CREATE_OBJECT, record.getChangeKind());

    assertEquals("\nName", ElementType.CLASS, record.getElementType());
    assertEquals("\nField key", "-", record.getFieldKey());
    assertEquals("\nFull name", full_name, record.getFieldName());
    assertEquals("\nItem ID", "MG-MV_9C", record.getElementId());
    assertEquals("\nModifier source", "", record.getModifier());
    assertEquals("\nTransaction ID", "MG-MV_8C", record.getTransactionId());

//    assertNull("\nComment", record.getComment());
//    assertNull("\nField name", record.getFieldName());
//    assertNull("\nMetadata", record.getMetadata());
//    assertNull("\nNew value", record.getNewValue());
//    assertNull("\nOld value", record.getOldValue());
//    assertNull("\nOver ID", record.getOverTransactionId());
//    assertNull("\nTransaction Time", record.getTransactionTime());
  }

  /**
   * Asserts the parsing of a CoObRA change destroy record.
   */
  @Test public final void testChangeDestroyRecord() {
    String text = "c2;;i:WTTuE#5F1;-;-;-;-;i:WTTuE#8F1;";
    FujabaChangeRecord record =
        new FujabaChangeRecord(text, MergeSide.RIGHT);

    assertEquals("\nCoObRA record", text, record.getRecord());
    assertEquals("\nModel side", MergeSide.RIGHT, record.getMergeSide());
    assertEquals("\nType indicator", 'c', record.getRawType());
    assertEquals("\nRecord type", CoobraType.CHANGE, record.getRecordType());
//    assertEquals("\nChange kind", CoobraKind.DESTROY_OBJECT, record.getChangeType());
//
//    assertEquals("\nField name", "-", record.getFieldName());
//    assertEquals("\nField key", "-", record.getFieldKey());
//    assertEquals("\nItem ID", "WTTuE_5F1", record.getObjectId());
//    assertEquals("\nModifier source", "", record.getModifierSource());
//    assertEquals("\nNew value", "-", record.getNewValue());
//    assertEquals("\nOld value", "-", record.getOldValue());
//    assertEquals("\nTransaction ID", "WTTuE_8F1", record.getId());
//
//    assertNull("\nName", record.getName());
//    assertNull("\nFull name", record.getFullName());
//    assertNull("\nComment", record.getComment());
//    assertNull("\nMetadata", record.getMetadata());
//    assertNull("\nOver ID", record.getOverTransactionId());
//    assertNull("\nTransaction Time", record.getTransactionTime());
  }

  /**
   * Asserts the parsing of a CoObRA change alter record.
   */
  @Test public final void testChangeAlterRecord() {
    String text = "c3;;i:_TVLu#3A1;diagrams;i:_TVLu#691;-;-;i:_TVLu#2A1;";
    FujabaChangeRecord record =
        new FujabaChangeRecord(text, MergeSide.RIGHT);

    assertEquals("\nCoObRA record", text, record.getRecord());
    assertEquals("\nModel side", MergeSide.RIGHT, record.getMergeSide());
    assertEquals("\nType indicator", 'c', record.getRawType());
    assertEquals("\nRecord type", CoobraType.CHANGE, record.getRecordType());
    assertEquals("\nChange kind", CoobraKind.ALTER_FIELD, record.getChangeKind());

    assertEquals("\nField name", "diagrams", record.getFieldName());
    assertEquals("\nField key", "-", record.getFieldKey());
    assertEquals("\nItem ID", "_TVLu_3A1", record.getElementId());
    assertEquals("\nModifier source", "", record.getModifier());
    assertEquals("\nNew value", "_TVLu_691", record.getNewValue());
    assertEquals("\nOld value", "-", record.getOldValue());
    assertEquals("\nTransaction ID", "_TVLu_2A1", record.getTransactionId());

//    assertNull("\nName", record.getName());
//    assertNull("\nFull name", record.getFullName());
//    assertNull("\nComment", record.getComment());
//    assertNull("\nMetadata", record.getMetadata());
//    assertNull("\nOver ID", record.getOverTransactionId());
//    assertNull("\nTransaction Time", record.getTransactionTime());
  }

  /**
   * Asserts the parsing of a CoObRA change remove record.
   */
  @Test public final void testChangeRemoveRecord() {
    String text = "c4;;i:_TVLu#3A1;diagrams;i:-;-;Activity;i:_TVLu#2A1;";
    FujabaChangeRecord record =
        new FujabaChangeRecord(text, MergeSide.LEFT);

    assertEquals("\nCoObRA record", text, record.getRecord());
    assertEquals("\nModel side", MergeSide.LEFT, record.getMergeSide());
    assertEquals("\nType indicator", 'c', record.getRawType());
    assertEquals("\nRecord type", CoobraType.CHANGE, record.getRecordType());
    assertEquals("\nChange kind", CoobraKind.REMOVE_KEY, record.getChangeKind());

    assertEquals("\nField name", "diagrams", record.getFieldName());
    assertEquals("\nField key", "Activity", record.getFieldKey());
    assertEquals("\nItem ID", "_TVLu_3A1", record.getElementId());
    assertEquals("\nModifier source", "", record.getModifier());
    assertEquals("\nNew value", "-", record.getNewValue());
    assertEquals("\nOld value", "-", record.getOldValue());
    assertEquals("\nTransaction ID", "_TVLu_2A1", record.getTransactionId());

//    assertNull("\nName", record.getName());
//    assertNull("\nFull name", record.getFullName());
//    assertNull("\nComment", record.getComment());
//    assertNull("\nMetadata", record.getMetadata());
//    assertNull("\nOver ID", record.getOverTransactionId());
//    assertNull("\nTransaction Time", record.getTransactionTime());
  }

  /**
   * Asserts the parsing of a CoObRA change manage record.
   */
  @Test public final void testChangeManageRecord() {
    String text = "c5;4;-;-;v:java.lang.Long:100;-;v::IDSUFFIX;-;";
    FujabaChangeRecord record =
        new FujabaChangeRecord(text, MergeSide.BASE);

    assertEquals("\nCoObRA record", text, record.getRecord());
    assertEquals("\nModel side", MergeSide.BASE, record.getMergeSide());
    assertEquals("\nType indicator", 'c', record.getRawType());
    assertEquals("\nRecord type", CoobraType.CHANGE, record.getRecordType());
    assertEquals("\nChange kind", CoobraKind.MANAGE, record.getChangeKind());

    assertEquals("\nField name", "-", record.getFieldName());
    assertEquals("\nField key", "IDSUFFIX", record.getFieldKey());
    assertEquals("\nItem ID", "-", record.getElementId());
    assertEquals("\nModifier source", "4", record.getModifier());
    assertEquals("\nNew value", "java.lang.Long:100", record.getNewValue());
    assertEquals("\nOld value", "-", record.getOldValue());
    assertEquals("\nTransaction ID", "-", record.getTransactionId());

//    assertNull("\nName", record.getName());
//    assertNull("\nFull name", record.getFullName());
//    assertNull("\nComment", record.getComment());
//    assertNull("\nMetadata", record.getMetadata());
//    assertNull("\nOver ID", record.getOverTransactionId());
//    assertNull("\nTransaction Time", record.getTransactionTime());
  }
}
