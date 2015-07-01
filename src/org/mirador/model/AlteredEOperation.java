/* --------------------------------------------------------------------------+
   AlteredEOperation.java - Part of the Ecore difference metamodel (MMD), used
     to represent a class operation that has been altered in a model.

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
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.impl.EOperationImpl;


/**
 * MMD Metaclass to represent an altered Ecore EOperation object.
 *
 * @since   v0.27 - Jul 6, 2010
 * @author  Stephen Barrett
 */
public class AlteredEOperation extends EOperationImpl implements AlteredEElement {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public AlteredEOperation() {
    this(null);
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  src  Purpose of the argument.
   */
  public AlteredEOperation(EOperation src) {
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
  static public EOperation snap(EOperation tar, EOperation src) {
    if (tar == null || src == null)
      return null;

    tar.setName(src.getName());

    tar.eSetDeliver(src.eDeliver());

    tar.setLowerBound(src.getLowerBound());
    tar.setUpperBound(src.getUpperBound());

    tar.setOrdered(src.isOrdered());
    tar.setUnique(src.isUnique());

    return tar;
  }


  // Instance data ----------------------------------------------------------
  private ENamedElement updated_;
  // End instance data ------------------------------------------------------
}
