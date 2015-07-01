/* --------------------------------------------------------------------------+
   AlteredEClass.java - Part of the Ecore difference metamodel (MMD), used
     to represent a class that has been altered in a model.

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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.impl.EClassImpl;


/**
 * MMD Metaclass to represent an altered Ecore EClass object.
 *
 * @since   v0.27 - Jul 6, 2010
 * @author  Stephen Barrett
 */
public class AlteredEClass extends EClassImpl implements AlteredEElement {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  public AlteredEClass() {
    this(null);
  }


  public AlteredEClass(EClass src) {
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
  static public EClass snap(EClass tar, EClass src) {
    if (tar == null || src == null)
      return null;

    tar.setName(src.getName());

    tar.setInstanceClass(src.getInstanceClass());
    tar.eSetDeliver(src.eDeliver());

    tar.setAbstract(src.isAbstract());
    tar.setInterface(src.isInterface());

    return tar;
  }


  // Instance data ----------------------------------------------------------
  private ENamedElement updated_;
  // End instance data ------------------------------------------------------
}
