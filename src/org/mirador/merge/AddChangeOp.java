/* --------------------------------------------------------------------------+
   AddChangeOp.java - High-level description of module and place in system.
   DOCDO: Finish file description and details.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------+
   Design rational, and module details that need highlighting.
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador.merge;
import ca.dsrg.mirador.change.ChangeRecord.MergeSide;
import ca.dsrg.mirador.model.EcoreExtra;


/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.41 - Aug 30, 2010
 * @author  Stephen Barrett
 */
public class AddChangeOp extends AtomicChangeOp {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  target  Purpose of the argument.
   * @param  side  Purpose of the argument.
   */
  public AddChangeOp(EcoreExtra target, MergeSide side) {
    super(target, side);
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ accessors


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ collections


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ operations


  // Instance data ----------------------------------------------------------
  // End instance data ------------------------------------------------------


  // Class data -------------------------------------------------------------
  // End class data ---------------------------------------------------------


  // Nested types -----------------------------------------------------------
  // End nested types -------------------------------------------------------
}
