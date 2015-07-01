/* --------------------------------------------------------------------------+
   FujabaChangeRecord.java - Change type of CoObRA change log record.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.change.fujaba;


/**
 * The CoObRA change log, change record. See "EBNF for the CoObRA Data Format."
 * <pre>c1;modifier;class-name;element-id;field-key;tx-id;</pre>
 * <pre>c?;modifier;element-id;field-name;new-value;old-value;field-key;tx-id</pre>
 * where '?' is a value from 2 to 5.
 *
 * @since   v0.8 - Feb 5, 2010
 * @author  Stephen Barrett
 */
public class FujabaChangeRecord extends FujabaRecord {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**
   * In addition to the common attributes set through it's super constructor,
   * sets the attributes specific to this CoObRA record type.
   *
   * @param  record_text  String of semicolon delimited fields of textual values.
   * @param  merge_side  Which model change log the record is from.
   */
  public FujabaChangeRecord(String record_text, MergeSide merge_side) {
    super(record_text, merge_side);
    setRecordType(CoobraType.CHANGE);
    parse();

    if (change_kind_ == CoobraKind.CREATE_OBJECT) {
      element_id_  = (!raw_fields_.get(3).equals("-"))
        ? raw_fields_.get(3) : null;

      element_type_ = determineChangeType(raw_fields_.get(2));
    }
    else {
      element_id_  = (!raw_fields_.get(2).equals("-"))
        ? raw_fields_.get(2) : null;
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  /**
   * Gives the change kind of the record.
   *
   * @return  Change kind of the CoObRA record.
   * @category  getter
   */
  public CoobraKind getChangeKind() {
    return change_kind_;
  }


  /**
   * Gives the key for the affected field of the record.
   *
   * @return  Field key of the CoObRA record.
   * @category  getter
   */
  public String getFieldKey() {
    String key = (change_kind_ == CoobraKind.CREATE_OBJECT )
        ? raw_fields_.get(4) : raw_fields_.get(6);

    if (key != null) {
      int pos = key.lastIndexOf(':');

      if (pos >= 0)
        key = key.substring(pos + 1);
    }

    return key;
  }


  /**
   * Gives the name for the affected field of the record.
   *
   * @return  Field name of the CoObRA record.
   * @category  getter
   */
  public String getFieldName() {
    return (change_kind_ != CoobraKind.CREATE_OBJECT)
        ? raw_fields_.get(3) : null;
  }


  /**
   * Gives the versioning modifier source of the record.
   *
   * @return  Modifier source of the CoObRA record.
   * @category  getter
   */
  public String getModifier() {
    return raw_fields_.get(1);
  }


  /**
   * Gives the new value for the affected field of the record.
   *
   * @return  New value of the CoObRA record.
   * @category  getter
   */
  public String getNewValue() {
    return (change_kind_ != CoobraKind.CREATE_OBJECT )
        ? raw_fields_.get(4) : null;
  }


  /**
   * Gives the old value for the affected field of the record.
   *
   * @return  Old value of the CoObRA record.
   * @category  getter
   */
  public String getOldValue() {
    return (change_kind_ != CoobraKind.CREATE_OBJECT )
        ? raw_fields_.get(5) : null;
  }


  /**
   * Gives the unmodified ID of the model element changed by the record.
   *
   * @return  ID of the affected model element.
   * @category  getter
   */
  public String getRealElementId() {
    return element_id_.replace('_', '#');
  }


  /**
   * Gives the ID of the transaction the record belongs to.
   *
   * @return  Transaction ID of the CoObRA record.
   * @category  getter
   */
  public String getTransactionId() {
    return (change_kind_ == CoobraKind.CREATE_OBJECT )
        ? raw_fields_.get(5) : raw_fields_.get(7);
  }


  /**
   * Gives the fully qualified name for the created class of the record.
   *
   * @return  Qualified name of the CoObRA record class.
   * @category  getter
   */
  public String getTypeName() {
    return (change_kind_ == CoobraKind.CREATE_OBJECT)
        ? raw_fields_.get(2) : null;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  /**
   * Returns the object as a string.
   *
   * @return  String representation of object.
   */
  @Override public String toString() {
    return record_text_;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  /**
   * Examines a raw CoObRA record in order to determine its type.
   *
   * @param  type_name  String of semicolon delimited fields of textual values.
   * @return  Type of the CoObRA record.
   */
  static protected ElementType determineChangeType(String type_name) {
    // Strip off everything before the last dot in full name.
    int pos = type_name.lastIndexOf('.');
    if (pos >= 0)
      type_name = type_name.substring(pos + 1);

    ElementType type;
    if (type_name.equals("UMLProject"))
      type = ElementType.PACKAGE;
    else if (type_name.equals("UMLPackage"))
      type = ElementType.PACKAGE;
    else if (type_name.equals("UMLClass"))
      type = ElementType.CLASS;
    else if (type_name.equals("UMLAttr"))
      type = ElementType.ATTRIBUTE;
    else if (type_name.equals("UMLMethod"))
      type = ElementType.OPERATION;
    else if (type_name.equals("UMLParam"))
      type = ElementType.PARAMETER;
    else if (type_name.equals("UMLAssoc"))
      type = ElementType.ASSOCIATION;
    else if (type_name.equals("UMLRole"))
      type = ElementType.REFERENCE;
    else if (type_name.equals("UMLCardinality"))
      type = ElementType.CARDINALITY;
    else if (type_name.equals("UMLGeneralization"))
      type = ElementType.GENERALIZATION;
    else if (type_name.equals("UMLBaseType"))
      type = ElementType.DATATYPE;
    else
      type = ElementType.NONE;

    return type;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  /**
   * Breaks the raw CoObRA record into semicolon delimited tokens. Certain
   * marker characters are stripped from the text before storing the tokens in
   * a list data structure.
   *
   * @see  FujabaRecord#raw_fields_
   */
  @Override protected void tokenizeRecord() {
    int ch_value = Character.getNumericValue(record_text_.charAt(1));
    change_kind_ = null;

    for (CoobraKind ch_kind : CoobraKind.values()) {
      if (ch_kind.ordinal() == ch_value) {
        change_kind_ = ch_kind;
        break;
      }
    }

    if (change_kind_ != null)
      super.tokenizeRecord();
  }


  // Instance data ----------------------------------------------------------
  /** Change kind of the record. */
  private CoobraKind change_kind_;
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  /** Record type-specific heading, for use with listings. */
  static public final String heading_ = "\nChange model records:";
  // End class data ---------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**
   * Tag values to indicate the change kind of a CoObRA record. The names
   * match those found in the CoObRA source code.
   *
   * @since   v0.7 - Feb 2, 2010
   * @author  Stephen Barrett
   */
  static public enum CoobraKind { UNDEFINED, CREATE_OBJECT, DESTROY_OBJECT,
      ALTER_FIELD, REMOVE_KEY, MANAGE }
  // End nested types -------------------------------------------------------
}
