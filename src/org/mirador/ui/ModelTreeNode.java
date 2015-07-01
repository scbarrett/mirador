/* --------------------------------------------------------------------------+
   ModelTreeNode.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.ui;
import ca.dsrg.mirador.Constants;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.model.EcoreExtra;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.10 - Feb 15, 2010
 * @author  Stephen Barrett
 */
public class ModelTreeNode extends DefaultMutableTreeNode {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  public ModelTreeNode(EcoreExtra element, boolean allows_children) {
    super(element, allows_children);
    extra_ = element;
    initialize();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public String getClassName() {
    return extra_.getClassName();
  }


  public EcoreExtra getExtra() {
    return extra_;
  }


  public Icon getIconClosed() {
    if (isLeaf())
      return icon_leaf_;
    else
      return icon_closed_;
  }


  public Icon getIconLeaf() {
    return icon_leaf_;
  }


  public Icon getIconOpened() {
    return icon_opened_;
  }


  public String getId() {
    return extra_.getId();
  }


  public String getName() {
    return extra_.getName();
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ initializers
  private void initialize() {
    EcoreType typ = extra_.getEcoreType();

    switch (typ) {
      case ATTRIBUTE:
        icon_closed_ = Constants.ATTRIBUTE_CLOSED_IMG;
        icon_opened_ = Constants.ATTRIBUTE_OPENED_IMG;
        icon_leaf_ = Constants.ATTRIBUTE_LEAF_IMG;
      break;

      case CLASS:
        icon_closed_ = Constants.CLASS_CLOSED_IMG;
        icon_opened_ = Constants.CLASS_OPENED_IMG;
        icon_leaf_ = Constants.CLASS_LEAF_IMG;
      break;

      case DATATYPE:
        icon_closed_ = Constants.DATATYPE_CLOSED_IMG;
        icon_opened_ = Constants.DATATYPE_OPENED_IMG;
        icon_leaf_ = Constants.DATATYPE_LEAF_IMG;
      break;

      case NONE:
        icon_closed_ = Constants.PROJECT_CLOSED_IMG;
        icon_opened_ = Constants.PROJECT_OPENED_IMG;
        icon_leaf_ = Constants.PROJECT_LEAF_IMG;
      break;

      case OPERATION:
        icon_closed_ = Constants.OPERATION_CLOSED_IMG;
        icon_opened_ = Constants.OPERATION_OPENED_IMG;
        icon_leaf_ = Constants.OPERATION_LEAF_IMG;
      break;

      case PACKAGE:
        icon_closed_ = Constants.PACKAGE_CLOSED_IMG;
        icon_opened_ = Constants.PACKAGE_OPENED_IMG;
        icon_leaf_ = Constants.PACKAGE_LEAF_IMG;
      break;

      case PARAMETER:
        icon_closed_ = Constants.PARAMETER_CLOSED_IMG;
        icon_opened_ = Constants.PARAMETER_OPENED_IMG;
        icon_leaf_ = Constants.PARAMETER_LEAF_IMG;
      break;

      case REFERENCE:
        icon_closed_ = Constants.REFERENCE_CLOSED_IMG;
        icon_opened_ = Constants.REFERENCE_OPENED_IMG;
        icon_leaf_ = Constants.REFERENCE_LEAF_IMG;
      break;
    }
  }


  // Instance data ----------------------------------------------------------
  /** Node's associated model element. Convenient alias of userObject. */
  EcoreExtra extra_;

  private Icon icon_closed_;
  private Icon icon_opened_;
  private Icon icon_leaf_;
  // End instance data ------------------------------------------------------
}
