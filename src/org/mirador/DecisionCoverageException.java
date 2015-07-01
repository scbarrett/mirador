/* --------------------------------------------------------------------------+
   DecisionCoverageException.java - Indirectly thrown by failure to fire any
     rules of a decision table.

   Created by: Stephen Barrett
               Concordia University
               Montreal, Quebec
               ste_barr@encs.concorida.ca

   Licensed Material - Dependable Software Research Group
   --------------------------------------------------------------------------*/
package ca.dsrg.mirador;



/**                                                                       DOCDO: Provide class overview.
 *
 * @since   v0.62 - Nov 13, 2010
 * @author  Stephen Barrett
 */
public class DecisionCoverageException extends MiradorException {
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ constructors
  /**                                                                     DOCDO: Provide constructor overview.
   *
   */
  public DecisionCoverageException() {
  }


  /**                                                                     DOCDO: Provide constructor overview.
   *
   * @param  table  Purpose of the argument.
   * @param  file  Purpose of the argument.
   */
  public DecisionCoverageException(String table, String file) {
    super("No rules fired in decision table \"" + table + ((file != null)
        ? "\", as defined by file " + file + '.' : "\"."));
  }
}
