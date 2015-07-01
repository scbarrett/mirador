/* --------------------------------------------------------------------------+
   AddedEReference.java - Part of the Ecore difference metamodel (MMD), used to
     represent an association that has been added to a model.

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
import org.eclipse.emf.ecore.impl.EReferenceImpl;


/**
 * MMD Metaclass to represent an added Ecore EReference object.
 *
 * @since   v0.27 - Jul 6, 2010
 * @author  Stephen Barrett
 */
public class AddedEReference extends EReferenceImpl implements AddedEElement {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public AddedEReference() {
    super();
  }
}
