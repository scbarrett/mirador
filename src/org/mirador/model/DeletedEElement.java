/* --------------------------------------------------------------------------+
   DeletedEElement.java - Interface for the Changed metaclasses of the Ecore
     difference metamodel (MMD).

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


/**
 * Empty interface to ease type checking of Deleted metaclasses of the MMD.
 *
 * @since   v0.65 - Nov 18, 2010
 * @author  Stephen Barrett
 */
public interface DeletedEElement extends MMDEElement {
}
