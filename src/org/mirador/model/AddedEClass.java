/* --------------------------------------------------------------------------+
   AddedEClass.java - Part of the Ecore difference metamodel (MMD), used to
     represent a class that has been added to a model.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   The Added metaclasses extend their associated Impl class without contribut-
   ing anything new. Only their type is of importance.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.model;
import org.eclipse.emf.ecore.impl.EClassImpl;


/**
 * MMD Metaclass to represent an added Ecore EClass object.
 *
 * @since   v0.27 - Jul 6, 2010
 * @author  Stephen Barrett
 */
public class AddedEClass extends EClassImpl implements AddedEElement {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public AddedEClass() {
    super();
  }
}
