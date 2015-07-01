/* --------------------------------------------------------------------------+
   DeletedEAttribute.java - Part of the Ecore difference metamodel (MMD), used
     to represent a class attribute that has been deleted from a model.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   The Deleted metaclasses extend their associated Impl class without contribut-
   ing anything new. Only their type is of importance.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.model;
import org.eclipse.emf.ecore.impl.EAttributeImpl;


/**
 * MMD Metaclass to represent a deleted Ecore EAttribute object.
 *
 * @since   v0.27 - Jul 6, 2010
 * @author  Stephen Barrett
 */
public class DeletedEAttribute extends EAttributeImpl
    implements DeletedEElement {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public DeletedEAttribute() {
    super();
  }
}
