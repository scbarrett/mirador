/* --------------------------------------------------------------------------+
   AlteredEAttribute.java - Part of the Ecore difference metamodel (MMD), used
     to represent a class attribute that has been altered in a model.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   The Changed metaclasses extend their associated Impl class, contributing
   a back reference to the updated object, and the means to link changes to the
   same object together into a list.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.model;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.impl.EAttributeImpl;


/**
 * MMD Metaclass to represent an altered Ecore EAttribute object.
 *
 * @since   v0.27 - Jul 6, 2010
 * @author  Stephen Barrett
 */
public class AlteredEAttribute extends EAttributeImpl implements AlteredEElement {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public AlteredEAttribute() {
    this(null);
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  src  Purpose of the argument.
   */
  public AlteredEAttribute(EAttribute src) {
    super();
    snap(this, src);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public ENamedElement getUpdated() {
    return updated_;
  }


  public void setUpdated(ENamedElement updated) {
    updated_ = updated;
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations
  static public EAttribute snap(EAttribute tar, EAttribute src) {
    if (tar == null || src == null)
      return null;

    tar.setName(src.getName());

    tar.setDefaultValueLiteral(src.getDefaultValueLiteral());
    tar.eSetDeliver(src.eDeliver());

    tar.setLowerBound(src.getLowerBound());
    tar.setUpperBound(src.getUpperBound());

    tar.setChangeable(src.isChangeable());
    tar.setDerived(src.isDerived());
    tar.setID(src.isID());
    tar.setOrdered(src.isOrdered());
    tar.setTransient(src.isTransient());
    tar.setUnique(src.isUnique());
    tar.setUnsettable(src.isUnsettable());
    tar.setVolatile(src.isVolatile());

    return tar;
  }


  // Instance data ----------------------------------------------------------
  private ENamedElement updated_;
  // End instance data ------------------------------------------------------
}
