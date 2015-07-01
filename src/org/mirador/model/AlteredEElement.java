/* --------------------------------------------------------------------------+
   AlteredEElement.java - Interface for the Changed metaclasses of the Ecore
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
import org.eclipse.emf.ecore.ENamedElement;


/**
 * Interface to add back reference and linked list capabilities to the Altered
 * metaclasses of the MMD.
 *
 * @since   v0.28 - Jul 11, 2010
 * @author  Stephen Barrett
 */
public interface AlteredEElement extends MMDEElement {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors
  public ENamedElement getUpdated();


  public void setUpdated(ENamedElement updated);
}
