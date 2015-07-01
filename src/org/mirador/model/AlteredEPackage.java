/* --------------------------------------------------------------------------+
   AlteredEPackage.java - Part of the Ecore difference metamodel (MMD), used
     to represent a package that has been altered in a model.

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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;


/**
 * MMD Metaclass to represent an altered Ecore EPackage object.
 *
 * @since   v0.27 - Jul 6, 2010
 * @author  Stephen Barrett
 */
public class AlteredEPackage extends EPackageImpl implements AlteredEElement {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public AlteredEPackage() {
    this(null);
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  src  Purpose of the argument.
   */
  public AlteredEPackage(EPackage src) {
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
  static public EPackage snap(EPackage tar, EPackage src) {
    if (tar == null || src == null)
      return null;

    tar.setName(src.getName());

    tar.eSetDeliver(src.eDeliver());

    tar.setNsPrefix(src.getNsPrefix());
    tar.setNsURI(src.getNsURI());

    return tar;
  }


  // Instance data ----------------------------------------------------------
  private ENamedElement updated_;
  // End instance data ------------------------------------------------------
}
