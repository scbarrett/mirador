/* --------------------------------------------------------------------------+
   EcoreExtra.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.model;
import ca.dsrg.mirador.difference.EcoreTyper;
import ca.dsrg.mirador.difference.EcoreTyper.EcoreType;
import ca.dsrg.mirador.difference.MiradorTyper.MiradorType;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EReference;
import java.util.Comparator;
import javax.swing.tree.TreePath;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.27 - Jul 6, 2010
 * @author  Stephen Barrett
 */
public class EcoreExtra {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  public EcoreExtra(ENamedElement element, String element_id, EcoreType etype) {
    this (element, element_id, etype, MiradorType.NONE);
  }


  public EcoreExtra(ENamedElement element, String element_id, EcoreType etype,
      MiradorType mtype) {
    setElement(element);  // Sets class name as well;
    element_id_ = element_id;
    ecore_type_ = etype;
    mirador_type_ = mtype;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public String getClassName() {
    return class_name_;
  }


  public void setClassName(String class_name) {
    class_name_ = class_name;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  What is returned by the method.
   */
  public EcoreType getEcoreType() {
    return ecore_type_;
  }


  public ENamedElement getElement() {
    return element_;
  }


  public void setElement(ENamedElement element) {
    element_ = element;

    if (class_name_ == null)
      class_name_ = (element_.getClass().getSimpleName()).replace("Impl", "");
  }


  public String getId() {
    return element_id_;
  }


  public void setId(String element_id) {
    element_id_ = element_id;
  }


  public EcoreExtra getMatch() {
    return match_;
  }


  public void setMatch(EcoreExtra match) {
    // Remove old linkage, and establish new; in both directions.
    if (match_ != match) { // Exit if link is in place.
      EcoreExtra old_match = match_;
      match_ = null;

      if (old_match != null)
        old_match.setMatch(null);

      match_ = match;

      if (match != null)
        match.setMatch(this);
    }
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  What is returned by the method.
   */
  public MiradorModel getModel() {
    return model_;
  }


  public void setModel(MiradorModel model) {
    model_ = model;
  }


  /**                                                                     DOCDO: Provide method overview.
   *
   * @return  What is returned by the method.
   */
  public MiradorType getMiradorType() {
    return mirador_type_;
  }


  public String getName() {
    if (element_ != null)
      return element_.getName();
    else
      return null;
  }


  public void setName(String name) {
    if (element_ != null)
      element_.setName(name);
  }


  public TreePath getTreePath() { // Implement viewable??3
    return tree_path_;
  }


  public void setTreePath(TreePath path) { // Implement viewable??3
    tree_path_ = path;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ predicates
  /**
   * Gives the keep match flag, and indirectly if the match was manually made.
   *
   * @return  true = keep match - manual, false = no keep match - automatic
   * @category  getter
   */
  public boolean isKeepMatch() {
    return is_keep_match_;
  }


  /**
   * Clears the keep match flag so as to enable automatic matching.
   *
   * @return  true = keep match was set, false = keep match was not set
   * @category  setter
   */
  public boolean resetKeepMatch() {
    boolean old_keep_match = is_keep_match_;
    if (getMatch() != null && is_keep_match_ == true) { // Exit if already unset.
      is_keep_match_ = false;
      getMatch().resetKeepMatch();
    }

    return old_keep_match;
  }


  /**
   * Sets the keep match flag so as to disable automatic matching.
   *
   * @return  true = keep match already set, false = keep match not already set
   * @category  setter
   */
  public boolean setKeepMatch() {
    boolean old_keep_match = is_keep_match_;
    if (getMatch() != null && is_keep_match_ == false) { // Exit if already set.
      is_keep_match_ = true;
      getMatch().setKeepMatch();
    }

    return old_keep_match;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ state accessors
  @Override public String toString() {
    if (element_ == null)
      return class_name_;

    String text;
    String name = getName();
    EcoreType typ = EcoreTyper.typeEObject(element_);

    if (typ == EcoreType.CLASS) {
      if (!((EClass) element_).getESuperTypes().isEmpty()) {
        EClass sup = ((EClass) element_).getESuperTypes().get(0);

        if (sup != null)
          name += " -> " + sup.getName();
      }
    }
    else if (typ == EcoreType.REFERENCE)
      name += ':' + ((EReference) element_).getEType().getName();

    if (class_name_.startsWith("Added"))
      text = name + " {added}";
    else if (class_name_.startsWith("Altered"))
      text = name + " {altered}";
    else if (class_name_.startsWith("Deleted"))
      text = name + " {deleted}";
    else
      text = name;

    return text;
  }


//  @Override public boolean equals(Object rhs) {
//    return false;
//  }

//  public boolean equals(EcoreExtra rhs) {
//    return element_id_.equals(rhs.element_id_);
//  }


  // Instance data ----------------------------------------------------------
  /** The wrapped Ecore model element. */
  private ENamedElement element_;
  private String element_id_;
  private String class_name_;

  /** Ecore type of the affected model element. */
  private EcoreType ecore_type_;

  /** Mirador type of the affected model element. */
  private MiradorType mirador_type_;

  private MiradorModel model_;

  private TreePath tree_path_;

  private EcoreExtra match_;

  private boolean is_keep_match_;
  // End instance data ------------------------------------------------------


  // Nested types -----------------------------------------------------------
  /**                                                                     DOCDO: Provide class overview.
   *
   * @since   v0.27 - Jul 6, 2010
   * @author  Stephen Barrett
   */
  static public class NameComparator implements Comparator<EcoreExtra> {
    @Override public int compare(EcoreExtra lhs, EcoreExtra rhs) {
      return (lhs.getName() + lhs.getId()).compareTo(rhs.getName() + rhs.getId());
    }
  }
  // End nested types -------------------------------------------------------
}
